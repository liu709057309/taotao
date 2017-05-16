package com.taotao.search.mq;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.search.service.SearchService;

/**
 * @author liuhongbao
 * @date 2017年4月29日 下午9:01:14
 * 
 */
@Component
public class SearchMQHandler {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    @Autowired
    private SearchService searchService;
    /**
     * 定义消费者，接受消息并执行清除商品缓存数据
     * @param msg
     */
    public void excute(String msg){
        try {
            JsonNode jsonNode = MAPPER.readTree(msg);
            long itemId = jsonNode.get("itemId").asLong();
            this.searchService.updateItemByMQ(itemId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
