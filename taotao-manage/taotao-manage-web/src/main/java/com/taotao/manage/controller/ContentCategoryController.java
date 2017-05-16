package com.taotao.manage.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysql.fabric.Response;
import com.taotao.manage.pojo.ContentCategory;
import com.taotao.manage.service.ContentCategoryService;

/**
 * @author liuhongbao
 * @date 2017年4月4日 下午3:36:26
 * 
 */

@RequestMapping("content/category")
@Controller
public class ContentCategoryController {

    @Autowired
    private ContentCategoryService contentCategoryService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ContentCategory>> queryContentCategoryByParentId(
            @RequestParam(value = "id", defaultValue = "0") Long parentId) {
        try {
            List<ContentCategory> list = this.contentCategoryService.queryContentCategoryByParentId(parentId);
            for (ContentCategory contentCategory : list) {
                System.out.println("查询父Id：" + contentCategory.getParentId());
                System.out.println("查询Id：" + contentCategory.getId());
                System.out.println("查询分类名称：" + contentCategory.getName());
                System.out.println("查询更新时间：" + contentCategory.getUpdated());

            }
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 保存新增的内容分类
     * 
     * @param contentCategory
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ContentCategory> saveContentCategory(ContentCategory contentCategory) {
        try {
            ContentCategory result = this.contentCategoryService.saveContentCategory(contentCategory);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

    }

    /**
     * 更新内容分类数据
     * 
     * @param contentCategory
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Void> updateContentCategory(ContentCategory contentCategory) {
        // 带selective的方法，只更新有数据的字段，
        try {
            contentCategory.setUpdated(new Date());
            this.contentCategoryService.updateByIdSelective(contentCategory);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

    }

    /**
     * 删除节点
     * @param parentId
     * @param id
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteContentCategory(@RequestParam("parentId") Long parentId,
            @RequestParam("id") Long id) {
        
        try {
            this.contentCategoryService.deleteContentCategory(parentId,id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
