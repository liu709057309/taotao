package com.taotao.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 通用跳转controller类
 * @author liuhongbao
 * @date 2017年1月4日 下午9:01:36
 * 
 */
@RequestMapping("page")
@Controller
public class PageControler {
    
    /**
     * 通用跳转controller
     * @param pageName
     * @return
     */
    @RequestMapping(value = "{pageName}",method = RequestMethod.GET)
    public String toPage(@PathVariable("pageName")String pageName){
        System.out.println(pageName);
        return pageName;
    }
}
