package com.taotao.cart.controller;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysql.fabric.Response;
import com.taotao.cart.pojo.Cart;
import com.taotao.cart.service.CartService;

/**
 * @author liuhongbao
 * @date 2017年4月30日 下午10:37:13
 * 
 */
@RequestMapping("cart")
@Controller
public class CartController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    /**
     * 保存购物车数据
     * 
     * @param cart
     * @return 201新建成功 204修改成功 500服务器内部错误
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> saveCart(Cart cart) {
        try {
            Boolean bool = this.cartService.saveCart(cart);
            if (bool) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 根据用户id查询购物车数据
     * 
     * @param userId
     * @return
     */
    @RequestMapping(value = "{userId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Cart>> queryCartByUserId(@PathVariable("userId") Long userId) {

        try {
            List<Cart> list = this.cartService.queryCartByUserId(userId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

    }

    /**
     * 更新商品数量
     * 
     * @param userId
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping(value = "{userId}/{itemId}/{num}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Void> updateItemNum(@PathVariable("userId") Long userId,
            @PathVariable("itemId") Long itemId, @PathVariable("num") Integer num) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("商品更新接收的参数是：userId={},itemId={}", userId, itemId);
            LOGGER.debug("商品更新接收的数量是：num={}", num);
        }

        try {
            this.cartService.updateItemNum(userId, itemId, num);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    
    /**
     * 删除购物车商品数据 
     * @param userId
     * @param itemId
     * @return
     */
    @RequestMapping(value = "{userId}/{itemId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteCart(@PathVariable("userId") Long userId,
            @PathVariable("itemId") Long itemId) {
        try {
            this.cartService.deleteCart(userId,itemId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

}
