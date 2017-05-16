package com.taotao.web.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.taotao.common.service.ApiService;
import com.taotao.common.service.RedisService;
import com.taotao.manage.pojo.Item;
import com.taotao.manage.pojo.ItemDesc;
import com.taotao.manage.pojo.ItemParamItem;
import com.taotao.web.bean.Cart;
import com.taotao.web.bean.ItemInfo;

/**
 * @author liuhongbao
 * @date 2017年4月8日 下午4:46:56
 * 
 */
@Service
public class ItemService {
    // 保存在缓存中的key的前缀，有了这个前缀，方便我们进行查询和管理
    private String ITEM_KEY = "WEB_TAOTAO_ITEM_INFO_";

    private final static ObjectMapper MAPPER = new ObjectMapper();

    @Value("${MANAGE_TAOTAO_URL}")
    private String MANAGE_TAOTAO_URL;

    @Autowired
    private ApiService apiService;

    @Autowired
    private RedisService redisService;
    
    @Autowired
    private CartService cartService;

    /**
     * 使用httpclient访问mangae后台的方法得到item数据。并返回到页面。显示商品详情
     * 
     * @param itemId
     * @return
     */
    public ItemInfo queryItemById(Long itemId) {
        // 1.从缓存中命中，命中则返回
        String json = this.redisService.get(this.ITEM_KEY + itemId);

        try {
            if (StringUtils.isNotBlank(json)) {
                ItemInfo itemInfo = MAPPER.readValue(json, ItemInfo.class);
                return itemInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 2.没有从缓存中命中，则通过后台系统接口查询数据
        String url = this.MANAGE_TAOTAO_URL + "rest/item/" + itemId;
        try {
            String jsonData = this.apiService.doGet(url);

            if (StringUtils.isNotBlank(jsonData)) {
                
                // 3.查询到的数据，放到缓存中
                try {
                    this.redisService.set(this.ITEM_KEY + itemId, jsonData, 60 * 60 * 24 * 3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                
                
                ItemInfo itemInfo = MAPPER.readValue(jsonData, ItemInfo.class);
                return itemInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用后台系统提供的返回json数据的接口，查询商品详情数据
     * 
     * @param itemId
     * @return
     */
    public ItemDesc queryItemDescItemId(Long itemId) {
        String url = this.MANAGE_TAOTAO_URL + "rest/item/desc/" + itemId;

        try {
            String jsonData = this.apiService.doGet(url);
            if (StringUtils.isNotBlank(jsonData)) {
                // 序列化为对象。
                ItemDesc itemDesc = MAPPER.readValue(jsonData, ItemDesc.class);
                return itemDesc;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用后台manage-web系统提供的接口，根据itemid查询商品参数信息,返回html代码。
     * 
     * @param itemId
     * @return
     */
    public String queryItemParamById(Long itemId) {
        String url = this.MANAGE_TAOTAO_URL + "rest/item/param/item/" + itemId;

        try {
            // 使用后台系统获取ItemParamItem的json格式数据。
            String jsonData = apiService.doGet(url);
            if (StringUtils.isNotBlank(jsonData)) {
                ItemParamItem itemParamItem = MAPPER.readValue(jsonData, ItemParamItem.class);
                String json = itemParamItem.getParamData();
                // 使用ArrayNode操作json
                ArrayNode arrayNode = (ArrayNode) MAPPER.readTree(json);

                // 线程不安全，速度更
                StringBuilder a = new StringBuilder();
                a.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"100%\" border=\"0\"  class=\"Ptable\">");
                for (JsonNode jsonNode : arrayNode) {
                    // 获取组名称
                    String group = jsonNode.get("group").asText();
                    // 拼接html代码
                    a.append("<tr><th class=\"tdTitle\" colspan=\"2\">" + group + "</th></tr>");
                    // 获取商品规格组内子元素的数据
                    ArrayNode array = (ArrayNode) jsonNode.get("params");
                    for (JsonNode node : array) {
                        // 获取子元素的名称
                        String key = node.get("k").asText();
                        // 获取子元素的值
                        String value = node.get("v").asText();
                        // 对商品组内的子元素拼接
                        a.append("<tr><td class=\"tdTitle\">" + key + "</td><td>" + value + "</td></tr>");
                    }

                }

                a.append("</table>");
                return a.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 删除redis缓存当中的数据
     * @param itemId
     */
    public void delItemCache(Long itemId) {
        this.redisService.del(this.ITEM_KEY + itemId);
    }

    /**
     * 根据用户id查询所有的购物车数据
     * @param userId
     * @return
     */
    public List<Cart> queryCartByUserId(Long userId) {
        String url = this.cartService.CART_TAOTAO_URL + "rest/cart/" + userId;
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


}
