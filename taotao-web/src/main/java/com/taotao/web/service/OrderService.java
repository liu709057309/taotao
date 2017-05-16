package com.taotao.web.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.HttpResult;
import com.taotao.common.service.ApiService;
import com.taotao.web.bean.Order;
import com.taotao.web.bean.User;
import com.taotao.web.threadLocal.UserThreadLocal;

/**
 * @author liuhongbao
 * @date 2017年4月15日 下午9:10:43
 * 
 */
@Service
public class OrderService {
    
    private final static ObjectMapper MAPPER = new ObjectMapper();
    
    @Value("${ORDER_TAOTAO_URL}")
    private String ORDER_TAOTAO_URL;
    
    @Autowired
    private ApiService apiService;
    
    /**
     * 调用订单系统接口，创建订单。
     * @param order
     * @return
     */
    public String createOrder(Order order) {
        //从ThreadLocal中获取user
        User user = UserThreadLocal.get();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        //创建要访问的订单系统接口的URL
        String url = this.ORDER_TAOTAO_URL + "order/create";
        
        try {
            //把order对象序列化为json格式的数据 
            String json = MAPPER.writeValueAsString(order);
            if(StringUtils.isNotBlank(json)){
              //發起post请求，调用 订单系统的创建订单接口
              HttpResult httpResult = this.apiService.doPostJson(url,json);
              //如果请求为200，表示请求成功
              if(httpResult.getCode() == 200){
                  
                  JsonNode jsonNode = MAPPER.readTree(httpResult.getBody());
                  //如果status为200，则表示订单创建成功，返回订单号
                  if(jsonNode.get("status").asInt() == 200){
                      return jsonNode.get("data").asText();
                  }
                  
              }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //订单创建失败返回null
        return null;
    }

    /**
     * 根据 订单接口查询订单
     * @param orderId
     * @return
     */
    public Order queryOrderById(String orderId) {
        //拼接访问订单系统 
        String url = this.ORDER_TAOTAO_URL + "order/query/" + orderId;
        try {
           
            String jsonData = this.apiService.doGet(url);
            if(StringUtils.isNotBlank(jsonData)){
                Order order = MAPPER.readValue(jsonData,Order.class);
                return order;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

}
