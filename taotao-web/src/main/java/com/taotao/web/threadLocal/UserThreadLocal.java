package com.taotao.web.threadLocal;

import com.taotao.web.bean.User;

/**
 * @author liuhongbao
 * @date 2017年4月16日 下午3:16:39
 * 
 */
public class UserThreadLocal {
    
    //声明一个ThreadLocal
    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<User>();
    
    //将User放入到ThreadLocal
    public static void set(User user){
        userThreadLocal.set(user);
    }
    //从threadLocal中获取User
    public static User get(){
        return userThreadLocal.get();
    }
}
