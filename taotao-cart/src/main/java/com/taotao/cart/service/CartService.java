package com.taotao.cart.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.cart.mapper.CartMapper;
import com.taotao.cart.pojo.Cart;

/**
 * @author liuhongbao
 * @date 2017年4月30日 下午10:36:52
 * 
 */
@Service
public class CartService {
    
    @Autowired
    private CartMapper cartMapper;

    /**
     * 保存购物车到数据库
     * @param cart
     * @return
     */
    public Boolean saveCart(Cart cart) {
        //从数据库查询购物车商品数据，如果有则做更新操作，如果没有则做新增操作。
        Cart param = new Cart();
        param.setItemId(cart.getItemId());
        param.setUserId(cart.getUserId());
        
        Cart result = this.cartMapper.selectOne(param);
        
        if(result == null){
            //查询结果为Null 则做新增操作
            cart.setId(null);
            cart.setCreated(new Date());
            cart.setUpdated(cart.getCreated());
            this.cartMapper.insertSelective(cart);
            //新增返回true
            return true;
        } else {
            //查询结果不为null，则做修改操作，需要对商品数量进行合并
            result.setNum(result.getNum() + cart.getNum());
            result.setUpdated(new Date());
            this.cartMapper.updateByPrimaryKeySelective(result);
            //更新返回false
            return false;
        }
    }

    /**
     * 根据用户Id查询购物车数据
     * @param userId
     * @return
     */
    public List<Cart> queryCartByUserId(Long userId) {
        Cart param = new Cart();
        param.setUserId(userId);
        List<Cart> list = this.cartMapper.select(param);
        
        return list;
    }

    
    /**
     * 根据useId和itemId修改购物车数量
     * @param userId
     * @param itemId
     * @param num
     */
    public void updateItemNum(Long userId, Long itemId, Integer num) {
        //查询购物车商品数据
        Cart param = new Cart();
        param.setUserId(userId);
        param.setItemId(itemId);
        Cart cart = this.cartMapper.selectOne(param);
        //如果查询到，则修改，如果查询不到则什么也不做
        if(cart != null){
            cart.setNum(num);
            cart.setUpdated(new Date());
            this.cartMapper.updateByPrimaryKeySelective(cart);
        }
    }

    /**
     * 删除购物车商品
     * @param userId
     * @param itemId
     */
    public void deleteCart(Long userId, Long itemId) {
        Cart param = new Cart();
        param.setUserId(userId);
        param.setItemId(itemId);
        this.cartMapper.delete(param);
    }

}
