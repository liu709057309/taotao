package com.taotao.common.service;
/**
 * @author liuhongbao
 * @date 2017年4月6日 下午10:37:40
 * 
 */
public interface Function<T,E> {
    public T callback(E e);
}
