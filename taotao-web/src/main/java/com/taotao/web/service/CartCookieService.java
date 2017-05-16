package com.taotao.web.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.web.bean.Cart;
import com.taotao.web.bean.ItemInfo;
import com.taotao.web.util.CookieUtils;

/**
 * @author liuhongbao
 * @date 2017年5月1日 下午10:03:29
 * 
 */
@Service
public class CartCookieService {
    
    private final static ObjectMapper MAPPER = new ObjectMapper();
    
    private String WEB_TAOTAO_CART_COOKIE = "WEB_TAOTAO_CART_COOKIE";
    
    @Autowired
    private ItemService itemService;

    /**
     * 在cookie中查询购物车数据
     * @param request
     * @return
     */
    public List<Cart> queryCartByCookie(HttpServletRequest request) {
        try {
            //从cookie中获取购物车json数据
            String jsonData = CookieUtils.getCookieValue(request,this.WEB_TAOTAO_CART_COOKIE,true);
            if(StringUtils.isNotBlank(jsonData)){
                //将json数据转换为list集合
                List<Cart> list = MAPPER.readValue(jsonData,MAPPER.getTypeFactory().constructCollectionType(List.class,Cart.class));
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new ArrayList<Cart>();
    }

    /**
     * 保存商品到购物车，保存到cookie中
     * @param itemId
     * @param num
     * @param request
     * @param response
     */
    /**
     * @param itemId
     * @param num
     * @param request
     * @param response
     */
    public void addItemToCookie(Long itemId, Integer num, HttpServletRequest request,
            HttpServletResponse response) {
       
        //查询cookie中的购物车数据
        List<Cart> list = this.queryCartByCookie(request);
        Cart cart = null;
        //遍历购物车数据，找到和itemId一致的商品
        for(Cart param : list){
            if(param.getItemId().longValue() == itemId.longValue()){
                cart = param;
            }
        }
        //如果商品存在，对商品数据数量进行合并
        if(cart != null){
            cart.setNum(cart.getNum() + num);
            cart.setUpdated(new Date());
        } else {
            //根据商品id查询商品数据
            ItemInfo itemInfo = this.itemService.queryItemById(itemId);
          //如果不存在就添加商品
            cart = new Cart();
            cart.setItemId(itemId);
            cart.setItemTitle(itemInfo.getTitle());
            if(StringUtils.isNoneBlank(itemInfo.getImage())){
                cart.setItemImage(itemInfo.getImages()[0]);
            } else {
                cart.setItemImage("");
            }
            cart.setItemPrice(itemInfo.getPrice());
            cart.setNum(num);
            cart.setCreated(new Date());
            cart.setUpdated(cart.getCreated());
            list.add(cart);
        }
        try {
            CookieUtils.setCookie(request,response,this.WEB_TAOTAO_CART_COOKIE,MAPPER.writeValueAsString(list),true);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    
    /**
     * 更新cookie中的商品数据
     * @param itemId
     * @param num
     * @param request
     * @param response
     */
    public void updateCartItemNum(Long itemId, Integer num, HttpServletRequest request,
            HttpServletResponse response) {
        //从cookie中查询购物车商品数据
        List<Cart> list = this.queryCartByCookie(request);
        //判断该商品是否存在，如果存在更新商品数量，不存在什么都不做
        for(Cart cart : list){
            if(cart.getItemId().longValue() == itemId.longValue()){
                cart.setNum(num);
                cart.setUpdated(new Date());
                break;
            }
        }
        
        try {
          CookieUtils.setCookie(request,response,this.WEB_TAOTAO_CART_COOKIE,MAPPER.writeValueAsString(list),true);  
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    /**
     * 删除缓存当中商品数据 
     * @param itemId
     * @param request
     * @param response
     */
    public void deleteCart(Long itemId, HttpServletRequest request,
            HttpServletResponse response) {
        //从cookie中寻找出所有的商品
        List<Cart> list = this.queryCartByCookie(request);
        //for循环找出对应的list数据删除之
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getItemId().longValue() == itemId.longValue()){
                list.remove(i);
                break;
            }
        }
        
        //将商品在cookie中
        try {
            CookieUtils.setCookie(request,response,this.WEB_TAOTAO_CART_COOKIE,MAPPER.writeValueAsString(list),true);  
          } catch (Exception e) {
              e.printStackTrace();
          }
    }

}
