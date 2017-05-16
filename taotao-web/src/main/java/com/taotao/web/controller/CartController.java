package com.taotao.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.web.bean.Cart;
import com.taotao.web.bean.User;
import com.taotao.web.service.CartCookieService;
import com.taotao.web.service.CartService;
import com.taotao.web.threadLocal.UserThreadLocal;

/**
 * @author liuhongbao
 * @date 2017年5月1日 下午3:49:17
 * 
 */
@RequestMapping("cart")
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartCookieService cartCookieService;

    /**
     * 添加商品到购物车
     * 
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping(value = "{itemId}", method = RequestMethod.GET)
    public String addItemToCart(@PathVariable("itemId") Long itemId, @RequestParam("num") Integer num, HttpServletRequest request, HttpServletResponse response) {
        User user = UserThreadLocal.get();
        if (user != null) {
            // 用户已登录
            Long userId = user.getId();
            this.cartService.addItemToCart(userId, itemId, num);
        } else {
            // 用户未登录
            this.cartCookieService.addItemToCookie(itemId,num,request,response);
        }

        // 跳转到显示购物车页面
        return "redirect:/cart/show.html";

    }

    /**
     * 跳转到购物车显示页面
     * 
     * @return
     */
    @RequestMapping(value = "show", method = RequestMethod.GET)
    public ModelAndView show(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("cart");
        User user = UserThreadLocal.get();
        List<Cart> list = null;
        if (user != null) {

            // 用户已登录
            list = this.cartService.queryCartByUserId(user.getId());
        } else {
            // 用户未登录
            list = this.cartCookieService.queryCartByCookie(request);
        }
        mv.addObject("cartList", list);
        return mv;
    }

    /**
     * 更新购物车商品数量
     * 
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping(value = "update/num/{itemId}/{num}", method = RequestMethod.POST)
    public ResponseEntity<Void> updateCartItemNum(@PathVariable("itemId") Long itemId,
            @PathVariable("num") Integer num,HttpServletRequest request,HttpServletResponse response) {
        try {
            User user = UserThreadLocal.get();
            if (user != null) {
                // 用户已登录
                this.cartService.updateCartItemNum(user.getId(), itemId, num);
            } else {
                // 用户未登录
                this.cartCookieService.updateCartItemNum(itemId,num,request,response);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 删除购物车数据方法
     * 
     * @param itemId
     * @return
     */
    @RequestMapping(value = "delete/{itemId}", method = RequestMethod.GET)
    public String deleteCart(@PathVariable("itemId") Long itemId,HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserThreadLocal.get();
        if (user != null) {
            // 用户已登录
            this.cartService.deleteCart(user.getId(), itemId);
        } else {
            // 用户未登录
            this.cartCookieService.deleteCart(itemId,request,response);
        }
        return "redirect:/cart/show.html";

    }
}
