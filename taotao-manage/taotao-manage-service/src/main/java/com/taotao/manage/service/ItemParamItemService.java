package com.taotao.manage.service;

import org.springframework.stereotype.Service;

import com.taotao.manage.pojo.ItemParamItem;

@Service
public class ItemParamItemService extends BaseService<ItemParamItem> {
    /**
     * 根据商品id查询商品参数数据
     * 
     * @param itemId
     * @return
     */
    public ItemParamItem queryItemParamItem(Long itemId) {
        ItemParamItem itemParamItem = new ItemParamItem();
        itemParamItem.setItemId(itemId);
        return super.queryOne(itemParamItem);
    }

}
