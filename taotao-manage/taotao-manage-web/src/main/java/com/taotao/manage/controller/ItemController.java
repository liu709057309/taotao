package com.taotao.manage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.manage.pojo.Item;
import com.taotao.manage.service.ItemService;

/**
 * @author liuhongbao
 * @date 2017年3月7日 下午2:03:16
 * 
 */
@RequestMapping("item")
@Controller
public class ItemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private ItemService itemService;

    /**
     * 添加商品
     * @param item
     * @param desc
     * @param itemParams
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> saveItem(Item item, @RequestParam("desc") String desc,
            @RequestParam("itemParams") String itemParams) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("开始保存商品数据，传入的参数是：item = {},desc={}", item, desc);
            }
            this.itemService.saveItem(item, desc, itemParams);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("保存商品数据成功，itemId = {}", item.getId());
            }

            return ResponseEntity.status(HttpStatus.OK).build();
            // return ResponseEntity.ok().build();两种作用是一样的。
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("保存商品数据出现异常！item = " + item, e);
            }

        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 
     * 分页查询
     * 
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<EasyUIResult> queryListByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "30") Integer rows) {
        try {
//            Item item = new Item();
//            item.setStatus(1);
//            item.setCid(76l);
            PageInfo<Item> pageInfo = this.itemService.queryListByOrder(null, page, rows, "updated desc");
            return ResponseEntity.ok(new EasyUIResult(pageInfo.getTotal(), pageInfo.getList()));
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("分页查询商品数据出现异常！" + e);
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 更新商品
     * 
     * @param item
     * @param desc
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Void> updateItem(Item item, @RequestParam("desc") String desc) {
        try {
            this.itemService.updateItem(item, desc);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

    }
    
    /**
     * 根据 Id查询商品
     * @param itemId
     * @return
     */
    @RequestMapping(value = "{itemId}",method = RequestMethod.GET)
    public ResponseEntity<Item> queryItemById(@PathVariable("itemId")Long itemId){
        try {
            Item item = this.itemService.queryById(itemId);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        
    }
}
