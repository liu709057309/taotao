package com.taotao.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.web.service.ItemService;

/**
 * @author liuhongbao
 * @date 2017年4月9日 下午1:47:38
 * 
 */
@RequestMapping("item/cache")
@Controller
public class ItemCacheController {
    
    @Autowired
    private ItemService itemService;
    
    @RequestMapping(value = "{itemId}" ,method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Void> delItemCache(@PathVariable("itemId")Long itemId){
        
        try {
            this.itemService.delItemCache(itemId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
