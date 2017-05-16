package com.taotao.manage.service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.abel533.entity.Example;
import com.github.abel533.entity.Example.Criteria;
import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.manage.pojo.BasePojo;

/**
 * 通用BaseService方法
 * 
 * @author liuhongbao
 * @date 2017年1月4日 下午9:01:36
 * 
 */
public abstract class BaseService<T extends BasePojo> {
    @Autowired
    private Mapper<T> mapper;
    public  Mapper<T> getMapper(){
        return this.mapper;
    }
    private Class<T> clazz;
    
    @SuppressWarnings("unchecked")
    public BaseService(){
        super();
        Type type = this.getClass().getGenericSuperclass();
        ParameterizedType ptype = (ParameterizedType) type;
        this.clazz = (Class<T>) ptype.getActualTypeArguments()[0];
    }
    /**
     * 根据主键查询
     * 
     * @param id
     * @return
     */
    public T queryById(Long id) {
        return this.getMapper().selectByPrimaryKey(id);
    }

    /**
     * 查询所有数据
     * 
     * @return
     */
    public List<T> queryAll() {
        return this.getMapper().select(null);
    }

    /**
     * 根据条件查询
     * 
     * @param t
     * @return
     */
    public List<T> queryListByWhere(T t) {
        return this.getMapper().select(t);
    }

    /**
     * 查询数据总条数
     * 
     * @return
     */
    public Integer queryCount() {
        return this.getMapper().selectCount(null);
    }

    /**
     * 根据条件分页查询
     * 
     * @param t
     * @param page
     * @param rows
     * @return
     */
    public PageInfo<T> queryPageByWhere(T t, Integer page, Integer rows) {
        // 第一个参数是起始页，第二个参数是页面显示的数据条数。
        PageHelper.startPage(page, rows);
        List<T> list = this.getMapper().select(t);
        return new PageInfo<T>(list);
    }

    /**
     * 根据条件查询一条数据
     * 
     * @param t
     * @return
     */
    public T queryOne(T t) {
        // 查询多个会报错
        return this.getMapper().selectOne(t);
    }

    /**
     * 保存数据
     * 
     * @param t
     */
    public void save(T t) {
        if(t.getCreated() == null){
            t.setCreated(new Date());
            //性能上考虑少创建了对象，业务上 要求，更新时间 和创建时间必须一致。new的话造成业务上的不一致。
            t.setUpdated(t.getCreated());
        } else {
            t.setUpdated(t.getCreated());
        }
        this.getMapper().insert(t);
    }

    /**
     * 保存数据（忽略空字段）
     * 
     * @param t
     */
    public void saveSelective(T t) {
        this.getMapper().insertSelective(t);
    }
    
    /**
     * 更新
     * @param t
     */
    public void updateById(T t){
        t.setUpdated(new Date());
        this.getMapper().updateByPrimaryKey(t);
    }
    
    /**
     * 更新（忽略空字段）
     * @param t
     */
    public void updateByIdSelective(T t){
        this.getMapper().updateByPrimaryKeySelective(t);
    }
    
    /**
     * 根据id删除
     * @param id
     */
    public void deleteById(Long id){
        this.getMapper().deleteByPrimaryKey(id);
    }
    
    /**
     * 根据ids批量删除
     * @param ids
     */
    public void deleteByIds(List<Object> ids){
        //设置条件
        Example example = new Example(clazz);
        example.createCriteria().andIn("id",ids);
        //根据条件删除
        this.getMapper().deleteByExample(example);
        
    }
    
    /**
     * 按条件分页查询
     * @param t
     * @param page
     * @param rows
     * @param order
     * @return
     * @throws Exception
     */
    public PageInfo<T> queryListByOrder(T t,Integer page,Integer rows,String order) throws Exception{
        PageHelper.startPage(page,rows);
        Example example = new Example(this.clazz);
        example.setOrderByClause(order);
      
        if(t == null){
            return new PageInfo<T>(this.mapper.selectByExample(example));
        }
        //声明条件
        Criteria criteria = example.createCriteria();
        Field[] fields = t.getClass().getDeclaredFields();//反射的形式获取t类型的所有属性 并返回数组。     
        for (Field field : fields) {
            field.setAccessible(true);//设置为true提高性能，可以获取私有字段的值。
            if(field.get(t) != null){
                criteria.andEqualTo(field.getName(),field.get(t));//field.get(t)返回指定对象上此 Field 表示的字段的值。
            }
        }
        //example.or(criteria);
        
        
        
        return new PageInfo<T>(this.mapper.selectByExample(example));
    }
}
