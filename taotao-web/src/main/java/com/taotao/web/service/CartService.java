package com.taotao.web.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.HttpResult;
import com.taotao.common.service.ApiService;
import com.taotao.web.bean.Cart;
import com.taotao.web.bean.ItemInfo;

/**
 * @author liuhongbao
 * @date 2017年5月1日 下午3:59:32
 * 
 */
@Service
public class CartService {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    @Value("${CART_TAOTAO_URL}")
    public String CART_TAOTAO_URL;
    
    @Autowired
    private ApiService apiService;
    
    @Autowired
    private ItemService itemService;

    /**
     * 添加商品到购物车
     * @param userId
     * @param itemId
     * @param num
     */
    public void addItemToCart(Long userId,Long itemId, Integer num) {
     
        //根据 商品Id查询商品数据 
        ItemInfo itemInfo = this.itemService.queryItemById(itemId);
        //拼接post请求参数
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("userId",userId);
        map.put("itemId",itemId);
        map.put("itemTitle",itemInfo.getTitle());
        if(StringUtils.isNotBlank(itemInfo.getImage())){
            map.put("itemImage",itemInfo.getImages()[0]);
        } else {
            map.put("itemImage","");
        }
        map.put("itemPrice",itemInfo.getPrice());
        map.put("num",num);
        String url = CART_TAOTAO_URL + "rest/cart";
        //发起post请求
        try {
            HttpResult httpResult = this.apiService.doPost(url,map);
            if(httpResult.getCode() == 201 || httpResult.getCode() == 204){
                //TODO
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    /**
     * 要用用户Id查询购物车
     * @param userId
     * @return
     */
    public List<Cart> queryCartByUserId(Long userId) {
        String url = CART_TAOTAO_URL + "rest/cart/" + userId;
        try {
            String jsonData = this.apiService.doGet(url);
            if(StringUtils.isNotBlank(jsonData)){
                List<Cart> list = MAPPER.readValue(jsonData,MAPPER.getTypeFactory().constructCollectionType(List.class,Cart.class));
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新购物车商品数量
     * @param userId
     * @param itemId
     * @param num
     */
    public void updateCartItemNum(Long userId, Long itemId, Integer num) {
        String url = CART_TAOTAO_URL + "rest/cart/" + userId + "/" + itemId + "/" + num;
        try {
            String jsonData = this.apiService.doPut(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除购物车数据
     * @param itemId
     */
    public void deleteCart(Long userId,Long itemId) {
        String url = CART_TAOTAO_URL + "rest/cart/" + userId + "/" + itemId;
        try {
            String jsonData = this.apiService.doDelete(url);
            System.out.println("-----------------------------------------:"+jsonData);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
}
