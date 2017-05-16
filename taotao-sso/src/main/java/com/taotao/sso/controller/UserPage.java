package com.taotao.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 通用跳转
 * @author liuhongbao
 * @date 2017年4月10日 下午9:43:56
 * 
 */

@RequestMapping("page")
@Controller
public class UserPage {
    
    /**
     * 通用跳转方法
     * @param pageName
     * @return
     */
    @RequestMapping(value = "{pageName}",method = RequestMethod.GET)
    public String toPage(@PathVariable("pageName")String pageName){
        return pageName;
        
    }
}
