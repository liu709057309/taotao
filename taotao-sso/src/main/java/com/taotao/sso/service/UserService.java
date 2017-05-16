package com.taotao.sso.service;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.RedisService;
import com.taotao.sso.mapper.UserMapper;
import com.taotao.sso.pojo.User;

/**
 * @author liuhongbao
 * @date 2017年4月9日 下午10:14:28
 * 
 */
@Service
public class UserService {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    private static final String TT_TICKET = "SSO_TAOTAO_USER_TICKET_";
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RedisService redisService;

    /**
     * 检查数据是否可用
     * @param param
     * @param type
     * @return true or false true则数据不存在可用，false，则存在不可用
     */
    public Boolean checkParam(String param, Integer type) {
        User user = new User();
        switch(type){
        case 1:
            user.setUsername(param);
            break;
        case 2:
            user.setPhone(param);
            break;
        case 3:
            user.setEmail(param);
            break;
        default:
            break;
        }
        //从数据库中查询该数据是否被使用
        int count = this.userMapper.selectCount(user);
        //如果count为0，表示 该数据不存在没有被使用,则返回true
        //反之 则存在,返回false。
        return count == 0 ? true : false;
    }

    /**
     * 用户注册
     * @param user
     */
    public void register(User user) {
        user.setId(null);
        user.setCreated(new Date());
        Date date = user.getCreated();
        
        user.setUpdated(date);
        //需要对密码进行加密，用户数据安全
        user.setPassword(user.getPassword());
        
        this.userMapper.insert(user);
    }

    /**
     * 用户登录 
     * @param username
     * @param password
     * @return ticket 格式：SSO_TAOTAO_USER_TICKET + md5加密
     * @throws JsonProcessingException
     */
    @SuppressWarnings("static-access")
    public String login(String username, String password) throws JsonProcessingException {
        User param = new User();
        param.setUsername(username);//只查username会走索引。数据库查询速度会很快，优化点。
        User user = this.userMapper.selectOne(param);
        if(StringUtils.equals(user.getPassword(),password)){
            
            
            
            //生成ticket
            String ticket = DigestUtils.md5Hex(System.currentTimeMillis() + username);//md5加密
            //将ticket放置到redis缓存中
           
            this.redisService.set(this.TT_TICKET + ticket,MAPPER.writeValueAsString(user),60*30);
            //登录成功返回ticket
            return ticket;
        }
        //登录失败返回null
        return null;
    }

    /**
     * 通过 ticket查询用户信息，在redis缓存中查询
     * @param ticket
     * @return
     * @throws Exception
     */
    public User queryUserByTicket(String ticket) throws Exception {
        
        String key = this.TT_TICKET + ticket;
        //查询用户一次，就需要对保存在用户的存活时间进行重置。
        this.redisService.set(key,60*30);
        String jsonData = this.redisService.get(key);
        if(StringUtils.isNotBlank(jsonData)){
            User user = MAPPER.readValue(jsonData,User.class);
            return user;
        }
        return null;
    }
    
  
}
