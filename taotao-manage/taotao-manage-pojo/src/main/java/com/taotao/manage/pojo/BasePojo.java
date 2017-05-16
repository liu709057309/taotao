package com.taotao.manage.pojo;

import java.util.Date;


//创建时间和修改时间，由于所有表当中都有，抽取出来进行继承。
public abstract class BasePojo {
    
    private Date created;
    private Date updated;
    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
    public Date getUpdated() {
        return updated;
    }
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
    
    

}
