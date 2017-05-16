package com.taotao.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.web.bean.User;
import com.taotao.web.service.UserService;
import com.taotao.web.threadLocal.UserThreadLocal;
import com.taotao.web.util.CookieUtils;

/**
 * @author liuhongbao
 * @date 2017年4月16日 上午12:18:57
 * 
 */
public class UserCartHandlerInterceptor implements HandlerInterceptor{
    
    
    @Autowired
    private UserService userService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //true表示 是否使用utf-8编码。
        String ticket = CookieUtils.getCookieValue(request,"TT_TICKET",true);
        if(StringUtils.isBlank(ticket)){
            //用户未登录,转发到登录页面
            return true;
        }
        //放到缓存中的ticket有存在时间，时间到表示用户登录超时。
        User user = this.userService.queryUserByTicket(ticket);
        if(user == null){
            //用户登录 超时
            return true;
        }
//        //用户登录成功
        //放入到线程
        UserThreadLocal.set(user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {
        // TODO Auto-generated method stub
        
    }

}
