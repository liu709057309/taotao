package com.taotao.sso.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taotao.sso.pojo.User;
import com.taotao.sso.service.UserService;

/**
 * @author liuhongbao
 * @date 2017年4月9日 下午10:15:08
 * 
 */
@RequestMapping("user")
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 检查数据是否可用
     * 
     * @param param
     * @param type
     * @return true or false
     */
    @RequestMapping(value = "check/{param}/{type}", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkParam(@PathVariable("param") String param,
            @PathVariable("type") Integer type) {
        System.out.println("进入系统---------------------------------------------");
        try {
            if (type > 0 && type < 4) {
                Boolean b = this.userService.checkParam(param, type);
                // 如果type是1，2，3则返回200，并且返回true或false
                return ResponseEntity.status(HttpStatus.OK).body(b);

            } else {
                // 如果type不是1，2，3则返回400
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 500 服务器内部错误
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

    }

    /**
     * 用户注册
     * 
     * @param user
     * @return
     */
    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> register(@Valid User user,BindingResult result) {
        //判断是否有错误信息
        if(result.hasErrors()){
           List<String> errors = new ArrayList<String>();
           List<ObjectError> allErrors = result.getAllErrors();
           
           for (ObjectError objectError : allErrors) {
               //把错误信息添加到errors中
               errors.add(objectError.getDefaultMessage());
           }
           
           return ResponseEntity.status(HttpStatus.OK).body(StringUtils.join(errors,"|"));
        }
        try {
            this.userService.register(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 用户登录
     * 
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestParam("u") String username, @RequestParam("p") String password) {
        
        
        try {
            String ticket = this.userService.login(username,password);
            if(StringUtils.isNotBlank(ticket)){
                return ResponseEntity.ok(ticket);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
       
        // 500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    
    }
    
    /**
     * 通用ticket查询用户信息
     * @param ticket
     * @return
     */
    @RequestMapping(value = "{ticket}",method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<User> queryUserByTicket(@PathVariable("ticket")String ticket){
        try {
            User user = this.userService.queryUserByTicket(ticket);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        
    }
}
