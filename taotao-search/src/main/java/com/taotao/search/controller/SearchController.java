package com.taotao.search.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.search.bean.Item;
import com.taotao.search.bean.SolrResult;
import com.taotao.search.service.SearchService;

/**
 * @author liuhongbao
 * @date 2017年4月20日 下午9:44:52
 * 
 */
@RequestMapping("search")
@Controller
public class SearchController {
    
    @Autowired
    private SearchService searchService;

    
    /**
     * 实现搜索功能
     * 搜索时传递的参数是查询关键词和page
     * @param q
     * @param page
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView search(@RequestParam("q") String q, @RequestParam(value = "page",defaultValue = "1")Integer page) {
        ModelAndView mv = new ModelAndView("search");
        
        String query = null;
            //解决乱码问题
            try {
                query= new String(q.getBytes("ISO-8859-1"),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
       
        SolrResult solrResult = searchService.search(query,page);
        
        //查询的关键词，保存在模型中
        mv.addObject("q",query);
        //商品的集合数据，保存在模型中
        mv.addObject("itemList",solrResult.getList());
        //分页的页码数据
        mv.addObject("page",page);
        
       
        //查询的总条数
        Long queryTotal = solrResult.getTotal();
        //设置的每页记录数
        Integer rows = this.searchService.SOLR_ROWS;
        
        //分页的总页数
        mv.addObject("totalPages",queryTotal/rows == 0 ? queryTotal/rows : queryTotal/rows + 1);
        
        return mv;

    }
    
    /**
     * @param item
     * @return
     */
    @RequestMapping(value="update/item",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> updateIndexItem(@RequestBody Item item){
        
        try {
            this.searchService.updateIndexItem(item);
            
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        
    }

}
