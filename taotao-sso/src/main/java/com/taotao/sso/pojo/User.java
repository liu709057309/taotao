package com.taotao.sso.pojo;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author liuhongbao
 * @date 2017年4月9日 下午10:09:48
 * 
 */
@Table(name = "tb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Length(min=6,max=12,message="用户名最小为6位，最大为12位")
    private String username;

    //生成Json的时候忽略该字段
    @JsonIgnore
    @Length(min=6,max=11,message="用户密码最小为6位，最大为12位")
    private String password;
    
    @Length(min=11,max=11,message="电话号码为11位")
    private String phone;

    @Email(message="邮箱格式不正确！")
    private String email;

    private Date created;

    private Date updated;

    //数据库表字段忽略该属性
    @Transient
    private Boolean flag;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date update) {
        this.updated = update;
    }
    
    
    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", password=" + password + ", phone=" + phone
                + ", email=" + email + ", created=" + created + ", updated=" + updated + "]";
    }
    
    
}
