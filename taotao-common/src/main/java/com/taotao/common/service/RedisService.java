package com.taotao.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @author liuhongbao
 * @date 2017年4月6日 下午9:37:15
 * 
 */
/**
 * @author xiaobao
 *
 */
@Service
public class RedisService {
    //如果容器中存在就注入，不存在就不注入
    @Autowired(required = false)
    private ShardedJedisPool shardedJedisPool;

    private <T> T execute(Function<T,ShardedJedis> fun){
        ShardedJedis shardedJedis = null;
        try {
            //从连接池中获取jedis分片对象
            shardedJedis = shardedJedisPool.getResource();
            return fun.callback(shardedJedis);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != shardedJedis){
                //关闭，检查链接是否有效，有效则放回到链接池中，无效则重置状态
                shardedJedis.close();
            }
        }
        return null;
        
    }
    
    /**
     * 往redis添加数据
     * @param key
     * @param value
     * @return
     */
    public String set(final String key,final String value){
        return execute(new Function<String,ShardedJedis>(){

            @Override
            public String callback(ShardedJedis e) {
                return e.set(key,value);
            }
            
        });
        
    }
    
    
    /**
     * 从redis中获取数据 
     * @param key
     * @return String
     */
    public String get(final String key){
        return execute(new Function<String,ShardedJedis>(){

            @Override
            public String callback(ShardedJedis e) {
                return e.get(key);
            }
            
        });
        
    }
    
    /**
     * 从redis删除数据
     * @param key
     * @return
     */
    public Long del(final String key){
        return execute(new Function<Long,ShardedJedis>(){

            @Override
            public Long callback(ShardedJedis e) {
                return e.del(key);
            }
            
        });
        
    }
    
    /**
     * 保存数据到redis中，并设置生存时间
     * @param key 
     * @param value
     * @param time 设置生存时间，单位是秒
     * @return
     */
    public Long set(final String key,final String value,final Integer time){
        return execute(new Function<Long,ShardedJedis>(){

            @Override
            public Long callback(ShardedJedis e) {
                e.set(key,value);
                return e.expire(key,time);
            }
            
        });
        
    }
    
    /**
     * 根据key，设置数据的生存时间
     * @param key 
     * @param value
     * @param time 设置生存时间，单位是秒
     * @return
     */
    public Long set(final String key,final Integer time){
        return execute(new Function<Long,ShardedJedis>(){

            @Override
            public Long callback(ShardedJedis e) {
                return e.expire(key,time);
            }
            
        });
        
    }
//    /**
//     * 插入数据到redis
//     * 
//     * @param key
//     * @param value
//     * @return
//     */
//    public String set(String key, String value) {
//        ShardedJedis shardedJedis = null;
//        try {
//            // 从连接池中获取到jedis分片对象
//            shardedJedis = shardedJedisPool.getResource();
//            return shardedJedis.set(key, value);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (null != shardedJedis) {
//                // 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
//                shardedJedis.close();
//            }
//        }
//        return null;
//
//    }
//----------第一次重构---------------
    
    
//    public String set(final String key,final String value){
//        RedisFunction rsfun = new RedisFunction(this.shardedJedisPool) {
//            
//            @Override
//            public String callback(ShardedJedis shardedJedis) {
//                return shardedJedis.set(key,value);
//            }
//        };
//        return rsfun.execute();
//    }

    
}

//-----------第一次重构----------------
//abstract class RedisFunction{
//    private ShardedJedisPool shardedJedisPool;
//
//    public RedisFunction(ShardedJedisPool shardedJedisPool) {
//        super();
//        this.shardedJedisPool = shardedJedisPool;
//    }
//    
//    public abstract String callback(ShardedJedis shardedJedis);
//    
//    public String execute(){
//        ShardedJedis shardedJedis = null;
//        try {
//            // 从连接池中获取到jedis分片对象
//            shardedJedis = shardedJedisPool.getResource();
//            return this.callback(shardedJedis);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (null != shardedJedis) {
//                // 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
//                shardedJedis.close();
//            }
//        }
//        return null;
//
//    }
    
//}
