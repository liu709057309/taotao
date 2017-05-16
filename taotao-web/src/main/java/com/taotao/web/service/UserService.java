package com.taotao.web.service;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.ApiService;
import com.taotao.web.bean.User;

/**
 * @author liuhongbao
 * @date 2017年4月15日 下午11:20:11
 * 
 */
@Service
public class UserService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    @Value("${SSO_TAOTAO_URL}")
    public String SSO_TAOTAO_URL;
    
    @Autowired
    private ApiService apiService;

    public User queryUserByTicket(String ticket) {
        //拼接单点登录系统，通过ticket查询用户信息的接口
        String url = this.SSO_TAOTAO_URL + "user/" + ticket;
        
        try {
            System.out.println("---------------" + url);
            //请求接口，获取user的json格式数据
            String jsonData = this.apiService.doGet(url);
            System.out.println("----------------json" + jsonData);
            if(StringUtils.isNotBlank(jsonData)){
                //序列化为user对象
                User user = this.MAPPER.readValue(jsonData,User.class);
                return user;
            }
        } catch (Exception e) {
        }
        //查询不到返回Null
        return null;
    }

}
