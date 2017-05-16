package com.taotao.manage.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.ItemCatResult;
import com.taotao.manage.pojo.ItemCat;
import com.taotao.manage.service.ItemCatService;

/**
 * @author liuhongbao
 * @date 2017年2月9日 下午1:48:30
 * 
 */
@RequestMapping("item/cat")
@Controller
public class ItemCatController {
    @Autowired
    private ItemCatService itemCatService;
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ItemCat>> queryListItemCat(@RequestParam(value = "id",defaultValue = "0") Long parentId) {
        try {
            List<ItemCat> list = this.itemCatService.queryListItemCatByParentId(parentId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 实现前台系统 左侧菜单栏接口功能。
     * @return
     */
    @RequestMapping(value = "all",method = RequestMethod.GET)
    public ResponseEntity<ItemCatResult> queryItemCatAll(){
        try {
            ItemCatResult itemCatResult = this.itemCatService.queryItemCatAll();
            return ResponseEntity.ok(itemCatResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
//    /**
//     * 实现前台系统 左侧菜单栏接口功能。解决乱码问题，专用方法。主要与ajax的jsonp有关。
//     * @return
//     */
//    @RequestMapping(value = "all",method = RequestMethod.GET)
//    public ResponseEntity<String> queryItemCatAll(HttpServletRequest request){
//        try {
//            ItemCatResult itemCatResult = this.itemCatService.queryItemCatAll();
//            String json = MAPPER.writeValueAsString(itemCatResult);
//            String callback = request.getParameter("callback");
//            String result = callback + "(" + json + ")";
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//    }
}
