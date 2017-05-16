package com.taotao.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.web.service.IndexService;

/**
 * @author liuhongbao
 * @date 2017年3月26日 下午11:22:13
 * 
 */
@Controller
public class IndexController {
    
    @Autowired
    private IndexService indexService;
    

    @RequestMapping(value = "{index}", method = RequestMethod.GET)
    public ModelAndView toIndex(@PathVariable("index")String index) {
        ModelAndView mv = new ModelAndView(index);
        
        mv.addObject("AD1",indexService.getAD1());
        return mv;
    }
    
}
