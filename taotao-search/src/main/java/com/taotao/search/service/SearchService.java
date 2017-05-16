package com.taotao.search.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.service.ApiService;
import com.taotao.search.bean.Item;
import com.taotao.search.bean.SolrResult;


/**
 * @author liuhongbao
 * @date 2017年4月20日 下午9:54:32
 * 
 */
@Service
public class SearchService {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    @Value("${SOLR_ROWS}")
    public Integer SOLR_ROWS;
    
    @Value("${MANAGE_TAOTAO_URL}")
    public String MANAGE_TAOTAO_URL;
    // 定义http的solr服务
    @Autowired
    private HttpSolrServer httpSolrServer;
    
    @Autowired
    private ApiService apiService;
    /**
     * 在索引库中查询商品数据
     * @param q
     * @param page
     * @return
     * @throws Exception
     */
    public SolrResult search(String q, Integer page) {
     
        
        SolrQuery solrQuery = new SolrQuery(); //构造搜索条件
        solrQuery.setQuery("text:" + q + "AND status:1"); //搜索关键词关雎状态为1的
        // 设置分页 start=0就是从0开始，，rows=5当前返回5条记录，第二页就是变化start这个值为5就可以了。
        solrQuery.setStart((Math.max(page, 1) - 1) * SOLR_ROWS);
        solrQuery.setRows(SOLR_ROWS);

        //是否需要高亮
        boolean isHighlighting = !StringUtils.equals("*", q) && StringUtils.isNotEmpty(q);

        if (isHighlighting) {
            // 设置高亮
            solrQuery.setHighlight(true); // 开启高亮组件
            solrQuery.addHighlightField("title");// 高亮字段
            solrQuery.setHighlightSimplePre("<em>");// 标记，高亮关键字前缀
            solrQuery.setHighlightSimplePost("</em>");// 后缀
        }

        // 执行查询
        try {
            QueryResponse queryResponse = this.httpSolrServer.query(solrQuery);
            List<Item> items = queryResponse.getBeans(Item.class);
            if (isHighlighting) {
                // 将高亮的标题数据写回到数据对象中
                Map<String, Map<String, List<String>>> map = queryResponse.getHighlighting();
                for (Map.Entry<String, Map<String, List<String>>> highlighting : map.entrySet()) {
                    for (Item item : items) {
                        if (!highlighting.getKey().equals(item.getId().toString())) {
                            continue;
                        }
                        item.setTitle(StringUtils.join(highlighting.getValue().get("title"), ""));
                        break;
                    }
                }
            }
            //获取查询到的数据总条数
            long total = queryResponse.getResults().getNumFound();
            SolrResult solrResult = new SolrResult(total,items);
            return solrResult;
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 更新solr索引的商品数据 
     * @param item
     */
    public void updateIndexItem(Item item) {
        try {
            this.httpSolrServer.addBean(item);
            this.httpSolrServer.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }



    /**
     * 根据itemId更新solr索引数据
     * @param itemId
     */
    public void updateItemByMQ(long itemId) {
        //1.根据 id查询商品
        String url = this.MANAGE_TAOTAO_URL + "rest/item/" + itemId;
        try {
            String jsonData = this.apiService.doGet(url);
            if(StringUtils.isNotBlank(jsonData)){
               Item item =  this.MAPPER.readValue(jsonData,Item.class);
               //2.调用updateIndexItem方法更新solr
               this.updateIndexItem(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        
    }

}
