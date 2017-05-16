package com.taotao.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.web.bean.Cart;
import com.taotao.web.bean.Order;
import com.taotao.web.bean.User;
import com.taotao.web.service.ItemService;
import com.taotao.web.service.OrderService;
import com.taotao.web.service.UserService;
import com.taotao.web.threadLocal.UserThreadLocal;

/**
 * @author liuhongbao
 * @date 2017年4月15日 下午9:07:25
 * 
 */
@RequestMapping("order")
@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;
//    /**
//     * 跳转到订单确认页面 从item.jsp页面，点击添加购物车跳转到order.jsp页面，携带item数据。
//     * 
//     * @param itemId
//     * @return
//     */
//    @RequestMapping(value = "{itemId}", method = RequestMethod.GET)
//    public ModelAndView toOrder(@PathVariable("itemId") Long itemId) {
//        ModelAndView mv = new ModelAndView("order");
//        mv.addObject("item", this.itemService.queryItemById(itemId));
//        return mv;
//
//    }
    /**
     * 跳转到订单确认页面
     * @return
     */
    @RequestMapping(value="create",method = RequestMethod.GET)
    public ModelAndView create(){
        //根据用户查询购物车商品
        User user = UserThreadLocal.get();
        //根据 用户Id查询购物车信息
        List<Cart> list = this.itemService.queryCartByUserId(user.getId());
        ModelAndView mv = new ModelAndView("order-cart");
        mv.addObject("carts",list);
        return mv;
    }

    /**
     * 创建订单
     * 获取用用户，并添加到订单
     * @param order
     * @param TT_TICKET
     * @return
     */
    @RequestMapping(value = "submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createOrder(Order order) {
        // 判断 orderNumber是否为空，不为空则表示 订单创建成功
        try {
            //获取登录的用户信息，将用户信息保存到order中。
//            User user = this.userService.queryUserByTicket(ticket);
            //在线程中获取user
//            User user = UserThreadLocal.get();
//            order.setUserId(user.getId());
//            order.setBuyerNick(user.getUsername());
            
            
            String orderNumber = this.orderService.createOrder(order);
            if (StringUtils.isNotBlank(orderNumber)) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("status", 200);
                map.put("data", orderNumber);
                return ResponseEntity.ok(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 500);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);

    }
    /**
     * 订单保存成功
     * @param orderId
     * @return
     */
    @RequestMapping(value= "success",method= RequestMethod.GET)
    public ModelAndView success(@RequestParam("id")String orderId){
        ModelAndView mv = new ModelAndView("success");
        //把订单数据保存在模型中
        mv.addObject("order",this.orderService.queryOrderById(orderId));
        
        //把送达时间保存到模型中
        mv.addObject("date",new DateTime().plusDays(3).toString("yyyy年MM月dd日"));
        return mv;
        
    }

}
