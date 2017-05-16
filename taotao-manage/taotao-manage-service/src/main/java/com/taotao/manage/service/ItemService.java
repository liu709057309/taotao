package com.taotao.manage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.abel533.entity.Example;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.service.ApiService;
import com.taotao.manage.mapper.ItemMapper;
import com.taotao.manage.pojo.Item;
import com.taotao.manage.pojo.ItemDesc;
import com.taotao.manage.pojo.ItemParamItem;

/**
 * @author liuhongbao
 * @date 2017年2月16日 下午3:30:07
 * 
 */
@Service
public class ItemService extends BaseService<Item> {
    private  static final Logger LOGGER = LoggerFactory.getLogger(ItemService.class);

    private final static ObjectMapper MAPPER = new ObjectMapper();

    @Value("${SORL_TAOTAO_URL}")
    private String SORL_TAOTAO_URL;

    @Value("${WEB_TAOTAO_URL}")
    private String WEB_TAOTAO_URL;

    @Autowired
    private ItemDescService itemDescService;

    @Autowired
    private ItemParamItemService itemParamItemService;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ApiService apiService;

    // 注入rabbitMQ
    @Autowired
    private RabbitTemplate template;

    /**
     * 保存商品数据
     * 
     * @param item
     * @param desc
     */
    public void saveItem(Item item, String desc, String itemParams) {
        // 保存商品数据
        item.setId(null);
        item.setStatus(1);
        super.save(item);
        // 保存描述数据
        ItemDesc itemDesc = new ItemDesc();
        itemDesc.setItemId(item.getId());
        itemDesc.setItemDesc(desc);
        this.itemDescService.save(itemDesc);

        // 保存商品规格数据
        ItemParamItem itemParamItem = new ItemParamItem();
        itemParamItem.setId(null);
        itemParamItem.setItemId(item.getId());
        itemParamItem.setParamData(itemParams);
        this.itemParamItemService.save(itemParamItem);
    }

    // /**
    // * 分页查询
    // * @param page
    // * @param rows
    // * @return
    // */
    // public PageInfo<Item> queryListByPage(Integer page, Integer rows) {
    // PageHelper.startPage(page,rows);//设置分页数据page是第几页开始，rows是条数。
    // Example example = new Example(Item.class);
    // example.setOrderByClause("updated DESC");
    // example.createCriteria().andEqualTo("status",1);
    // //List<Item> list = super.queryAll();//这个通用mapper是如何确定是从item表里进行查询的？
    // List<Item> list = super.getMapper().selectByExample(example);
    // return new PageInfo<Item>(list);
    // }

    public PageInfo<Item> queryListByOrder(Item item, Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        Example example = new Example(Item.class);
        example.createCriteria().andEqualTo("status", item.getStatus());
        example.setOrderByClause("updated DESC");
        List<Item> list = this.itemMapper.selectByExample(example);
        return new PageInfo<Item>(list);
    }

    /**
     * 更新商品
     * 
     * @param item
     * @param desc
     */
    public void updateItem(Item item, String desc) {
        // 商品数据更新
        this.updateByIdSelective(item);
        // 商品描述保存
        ItemDesc itemDesc = this.itemDescService.queryById(item.getId());
        itemDesc.setItemDesc(desc);
        this.itemDescService.updateByIdSelective(itemDesc);

        // //调用前台系统的接口，清理redis缓存中的商品数据
        //
        // String url = this.WEB_TAOTAO_URL + "/item/cache/" + item.getId() + ".html";
        // //http://www.taotao.com/item/cache/45.html
        // try {
        // this.apiService.doGet(url);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        //
        // try {
        //
        // //请求solr接口的请求链接
        // String solrUrl = this.SORL_TAOTAO_URL + "/search/update/item.html";
        // //拿到需要修改的商品对象
        // Item solrItem = super.queryById(item.getId());
        // //转换为json格式数据
        // String jsonData = MAPPER.writeValueAsString(solrItem);
        // //调用API接口携带Json数据发起请求
        // this.apiService.doPostJson(solrUrl,jsonData);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        //修改商品发送消息到MQ
        this.sendMQ(item.getId(),"update");

    }

    // 采用通配符的形式,左边为key，右边为发送的消息，抽取一个方法。
    /**
     * 发送消息
     * @param itemId
     * @param type
     */
    private void sendMQ(Long itemId, String type) {

        try {
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("发送消息，接收的参数是：itemId={},type={}",itemId,type);
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("itemId", itemId);
            map.put("type", type);

            String json = ItemService.MAPPER.writeValueAsString(map);
            // 采用通配符的形式,左边为key，右边为发送的消息，抽取一个方法。
            this.template.convertAndSend("item." + type, json);
        } catch (Exception e) {
            if(LOGGER.isErrorEnabled()){
                LOGGER.error("发送消息失败，itemId="+itemId,e);
            }
            
        }

    }

}
