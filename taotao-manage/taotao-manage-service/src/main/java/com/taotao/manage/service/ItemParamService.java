package com.taotao.manage.service;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.taotao.manage.pojo.ItemParam;

/**
 * @author liuhongbao
 * @date 2017年3月26日 下午1:52:39
 * 
 */
@Service
public class ItemParamService extends BaseService<ItemParam>{
    /**
     * 根据itemCatId查询模板数据
     * 
     * @param itemCatId
     * @return
     */
    public ItemParam queryItemParamByItemCatId(Long itemCatId) {
        ItemParam itemParam = new ItemParam();
        itemParam.setItemCatId(itemCatId);
        return super.queryOne(itemParam);
    }

    /**
     * 新增itemParam
     * 
     * @param itemCatId
     * @param paramData
     */
    public void saveItemParam(Long itemCatId, String paramData) {
        ItemParam itemParam = new ItemParam();
        itemParam.setId(null);
        itemParam.setItemCatId(itemCatId);
        itemParam.setParamData(paramData);
        super.save(itemParam);
    }

}
