package com.taotao.web.bean;

import org.apache.commons.lang3.StringUtils;

import com.taotao.manage.pojo.Item;

/**
 * @author liuhongbao
 * @date 2017年4月8日 下午4:55:24
 * 
 */
public class ItemInfo extends Item{
    public String[] getImages(){
        if(StringUtils.isNotBlank(super.getImage())){
            String[] images = StringUtils.split(super.getImage(),",");
            return images;
        }
        return null;
    }

}
