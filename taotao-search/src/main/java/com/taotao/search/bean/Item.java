package com.taotao.search.bean;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * @author xiaobao
 *
 */
//忽略未定义的字段
@JsonIgnoreProperties(ignoreUnknown=true)
public class Item {
    @Field("id")
    private Long id;
    
    @Field("title")
    private String title;
    
    @Field("sellPoint")
    private String sellPoint;

    @Field("price")
    private Long price;

    @Field("image")
    private String image;
    
    @Field("cid")
    private Long cid;
    
    @Field("status")
    private Integer status;
    
    public String[] getImages(){
        if(StringUtils.isNotBlank(this.getImage())){
            return StringUtils.split(this.getImage(),",");
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSellPoint() {
        return sellPoint;
    }

    public void setSellPoint(String sellPoint) {
        this.sellPoint = sellPoint;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


}
