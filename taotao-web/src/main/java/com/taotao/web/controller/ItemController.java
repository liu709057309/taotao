package com.taotao.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.web.service.ItemService;

/**
 * @author liuhongbao
 * @date 2017年4月8日 下午4:15:46
 * 
 */
@RequestMapping(value = "item")
@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @RequestMapping(value = "{itemId}", method = RequestMethod.GET)
    public ModelAndView toIndex(@PathVariable("itemId") Long itemId) {
        ModelAndView modelAndView = new ModelAndView("item");
        // 商品数据保存到模型中。
        modelAndView.addObject("item", this.itemService.queryItemById(itemId));
        // 将商品的描述数据保存到作用域中
        modelAndView.addObject("itemDesc", this.itemService.queryItemDescItemId(itemId));
        // 将商品参数数据保存到数据模型中（作用域）
        modelAndView.addObject("itemParam",this.itemService.queryItemParamById(itemId));
        return modelAndView;
    }
}
