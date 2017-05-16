package com.taotao.manage.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.ItemCatData;
import com.taotao.common.bean.ItemCatResult;
import com.taotao.common.service.RedisService;
import com.taotao.manage.pojo.ItemCat;

/**
 * @author liuhongbao
 * @date 2017年2月9日 下午1:42:29
 * 
 */
@Service
public class ItemCatService extends BaseService<ItemCat> {
    
    private final static ObjectMapper MAPPER = new ObjectMapper();
//    @Autowired
//    private ItemCatMapper itemCatMapper;
//    @Override
//    public Mapper<ItemCat> getMapper() {
//        return this.itemCatMapper;
//    }
    private static String MANAGE_TAOTAO_ITEM_CAT = "MANAGE_TAOTAO_ITEM_CAT";
    
    @Autowired
    private RedisService redisService;
    /**
     * 根据id查询该节点的所有子节点
     * @param parentId
     * @return
     */
    public List<ItemCat> queryListItemCatByParentId(Long parentId) {
        ItemCat itemCat = new ItemCat();
        itemCat.setParentId(parentId);
        return this.queryListByWhere(itemCat);
    }

    /**
     * 首页三级类目json格式数据 的封装。
     * @return
     */
    public ItemCatResult queryItemCatAll() {
        //从缓存中命中
        try {
            String redisJson = this.redisService.get(this.MANAGE_TAOTAO_ITEM_CAT);
            if(StringUtils.isNotBlank(redisJson)){
                return MAPPER.readValue(redisJson,ItemCatResult.class);
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //如果没有命中，从数据库中查询
        /**
         * 全部查询，并且生成树状结构
         * @return
         */
                ItemCatResult result = new ItemCatResult();
                // 全部查出，并且在内存中生成树形结构
                List<ItemCat> cats = super.queryAll();
                
                // 转为map存储，key为父节点ID，value为数据集合
                Map<Long, List<ItemCat>> itemCatMap = new HashMap<Long, List<ItemCat>>();
                for (ItemCat itemCat : cats) {
                        if(!itemCatMap.containsKey(itemCat.getParentId())){
                                itemCatMap.put(itemCat.getParentId(), new ArrayList<ItemCat>());
                        }
                        itemCatMap.get(itemCat.getParentId()).add(itemCat);
                }
                
                // 封装一级对象
                List<ItemCat> itemCatList1 = itemCatMap.get(0L);
                for (ItemCat itemCat : itemCatList1) {
                        ItemCatData itemCatData = new ItemCatData();
                        itemCatData.setUrl("/products/" + itemCat.getId() + ".html");
                        itemCatData.setName("<a href='"+itemCatData.getUrl()+"'>"+itemCat.getName()+"</a>");
                        result.getItemCats().add(itemCatData);
                        if(!itemCat.getIsParent()){
                                continue;
                        }
                        
                        // 封装二级对象
                        List<ItemCat> itemCatList2 = itemCatMap.get(itemCat.getId());
                        List<ItemCatData> itemCatData2 = new ArrayList<ItemCatData>();
                        itemCatData.setItems(itemCatData2);
                        for (ItemCat itemCat2 : itemCatList2) {
                                ItemCatData id2 = new ItemCatData();
                                id2.setName(itemCat2.getName());
                                id2.setUrl("/products/" + itemCat2.getId() + ".html");
                                itemCatData2.add(id2);
                                if(itemCat2.getIsParent()){
                                        // 封装三级对象
                                        List<ItemCat> itemCatList3 = itemCatMap.get(itemCat2.getId());
                                        List<String> itemCatData3 = new ArrayList<String>();
                                        id2.setItems(itemCatData3);
                                        for (ItemCat itemCat3 : itemCatList3) {
                                                itemCatData3.add("/products/" + itemCat3.getId() + ".html|"+itemCat3.getName());
                                        }
                                }
                        }
                        if(result.getItemCats().size() >= 14){
                                break;
                        }
                }
                
                //查询到数据，则加入到缓存中
                try {
                    String jsonResult = MAPPER.writeValueAsString(result);
                    this.redisService.set(MANAGE_TAOTAO_ITEM_CAT,jsonResult,60*60*24*3);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return result;
        }
    

}
