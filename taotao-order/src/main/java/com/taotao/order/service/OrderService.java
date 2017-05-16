package com.taotao.order.service;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.abel533.entity.Example;
import com.github.abel533.entity.Example.Criteria;
import com.github.pagehelper.PageHelper;
import com.taotao.order.mapper.OrderItemMapper;
import com.taotao.order.mapper.OrderMapper;
import com.taotao.order.mapper.OrderShippingMapper;
import com.taotao.order.pojo.Order;
import com.taotao.order.pojo.OrderItem;
import com.taotao.order.pojo.OrderShipping;

/**
 * @author liuhongbao
 * @date 2017年4月13日 下午10:36:56
 * 
 */
@Service
public class OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private OrderShippingMapper orderShippingMapper;

    
    /**
     * 创建订单,返回订单号
     * @param order
     * @return
     */
    public String createOrder(Order order) {
        //根据系统时间生成订单id
        String orderId = String.valueOf(System.currentTimeMillis()) + order.getBuyerNick();
        //保存订单
        order.setOrderId(orderId);
        this.orderMapper.insertSelective(order);
        //保存订单商品
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(orderId);
            this.orderItemMapper.insertSelective(orderItem);
        }
        //保存物流信息
        OrderShipping orderShipping = order.getOrderShipping();
        orderShipping.setOrderId(orderId);
        this.orderShippingMapper.insertSelective(orderShipping);
        return orderId;
    }


    /**
     * 根据订单Id查询订单
     * @param orderId
     * @return
     */
    public Order queryOrderById(String orderId) {
        //查询订单
        Order order = this.orderMapper.selectByPrimaryKey(orderId);
        
        if(null != order){
          //查询订单商品，加载到订单
            //声明查询条件
            OrderItem param = new OrderItem();
            param.setOrderId(orderId);
            //根据条件查询订单商品的数据 
            List<OrderItem> select = orderItemMapper.select(param);
            order.setOrderItems(select);
            
            //查询订单物流数据，加载到订单中
            OrderShipping orderShipping = this.orderShippingMapper.selectByPrimaryKey(orderId);
            
            order.setOrderShipping(orderShipping);
            return order;
        }
        return null;
    }


    /**
     * 按用户名称分页查询订单
     * @param buyerNick
     * @param page
     * @param rows
     * @return
     */
    public List<Order> queryOrderByUserId(String buyerNick, Integer page, Integer rows) {
        //设置分页
        PageHelper.startPage(page,rows);
        
        //设置查询条件
        Order param = new Order();
        param.setBuyerNick(buyerNick);
        //查询订单。按分页查询出结果后，在加载订单商品数据和物流数据 进来没有问题。
        List<Order> orders = this.orderMapper.select(param);
        //遍历订单，并把订单数据和订单物流数据加载进来
        for (int i = 0; i < orders.size(); i++) {
            //根据订单Id查询订单（已加载订单商品和订单物流信息）
            //查询出来的结果带有OrderItem和OrderShipping等信息，
            Order order = this.queryOrderById(orders.get(i).getOrderId());
            //将查询出来的带有OrderItem和OrderShipping的Order再设置回list
            orders.set(i,order);
        }
        return orders;
    }


    /**
     * 修改订单状态
     * @param order
     */
    public void changeOrderStatus(Order order) {
        this.orderMapper.updateByPrimaryKeySelective(order);
        
    }
    
    /**
     * 清理无效订单
     */
    public void cleanInvalidOrder(){
        //声明查询条件
        Example example = new Example(Order.class);
        Criteria criteria = example.createCriteria();
        
        //订单的支付类型是在线支付
        criteria.andEqualTo("paymentType",1);
        //订单的状态是未付款
        criteria.andEqualTo("status",1);
        //订单创建时间是在两天前
        criteria.andLessThan("createTime",new DateTime().minusDays(2).toDate());
        
        //需要更新的数据
        Order order = new Order();
        order.setStatus(6);
        order.setCloseTime(new Date());
        
        //开始关闭订单
        
        this.orderMapper.updateByExampleSelective(order, example);
    }
}
