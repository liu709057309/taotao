package com.taotao.sso.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.taotao.sso.pojo.User;
import com.taotao.sso.service.UserService;
import com.taotao.sso.util.CookieUtils;

/**
 * @author liuhongbao
 * @date 2017年4月10日 下午10:26:23
 * 
 */
@RequestMapping("/service/user")
@Controller
public class UserLoginController {
    
    private String TT_TICKET = "TT_TICKET";

    @Autowired
    private UserService userService;

    /**
     * 用户登录 并将ticket存入cookie
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "doLogin", method = RequestMethod.POST)
    public ResponseEntity<Map<String,String>> doLogin(@RequestParam("username") String username,
            @RequestParam("password") String password, HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String ticket = this.userService.login(username, password);
            //ticket存入cookie
            CookieUtils.setCookie(request,response,this.TT_TICKET,ticket,true);
            Map<String,String> map = new HashMap<String,String>();
            map.put("status","200");
            return ResponseEntity.ok().body(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

    }
    
    
    /**
     * 用户注册返回状态200
     * @param user
     * @return
     */
    @RequestMapping(value = "doRegister", method = RequestMethod.POST)
    public ResponseEntity<Map<String,String>> doRegister(User user) {
        try {
            this.userService.register(user);
            Map<String,String> map = new HashMap<String,String>();
            map.put("status","200");
            return ResponseEntity.ok().body(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
}
