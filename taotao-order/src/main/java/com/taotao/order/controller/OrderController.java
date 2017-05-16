package com.taotao.order.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.order.pojo.Order;
import com.taotao.order.service.OrderService;

/**
 * @author liuhongbao
 * @date 2017年4月13日 下午11:23:35
 * 
 */
@RequestMapping(value = "order")
@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Order order) {

        // 创建订单并返回订单号
        try {
            String orderId = orderService.createOrder(order);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("status", 200);
            map.put("msg", "OK");
            map.put("data", orderId);
            map.put("ok", true);
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 通过订单id查询订单
     * 
     * @param orderId
     * @return
     */
    @RequestMapping(value = "query/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Order> queryOrderById(@PathVariable("orderId") String orderId) {
        Order order = this.orderService.queryOrderById(orderId);
        try {
            if (null != order) {
                return ResponseEntity.status(HttpStatus.OK).body(order);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    /**
     * 根据 用户名分页查询订单
     * @param buyerNick
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping(value = "query/{buyerNick}/{page}/{rows}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Order>> queryOrderByUserId(@PathVariable("buyerNick") String buyerNick,
            @PathVariable("page") Integer page, @PathVariable("rows") Integer rows) {

        try {
            //查询订单
            List<Order> list = this.orderService.queryOrderByUserId(buyerNick, page, rows);
            //查询结果不为null或者list元素>0
            if (list != null && list.size() > 0) {
                return ResponseEntity.ok(list);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
    
    /**
     * 更新订单状态
     * @param order
     * @return
     */
    @RequestMapping(value = "changeOrderStatus",method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Void> changeOrderStatus(@RequestBody Order order){
        try {
            this.orderService.changeOrderStatus(order);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        
       
    }
}
