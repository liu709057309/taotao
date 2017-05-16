package com.taotao.manage.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.taotao.manage.pojo.ContentCategory;

/**
 * @author liuhongbao
 * @date 2017年4月4日 下午2:51:35
 * 
 */
@Service
public class ContentCategoryService extends BaseService<ContentCategory>{

    /**
     * 根据父id查询数据
     * @param parentId
     * @return
     */
    public List<ContentCategory> queryContentCategoryByParentId(Long parentId) {
        ContentCategory param = new ContentCategory();
        param.setParentId(parentId);
        return super.queryListByWhere(param);
    }

    /**
     * 保存新增的内容分类 
     * @param contentCategory
     * @return
     */
    public ContentCategory saveContentCategory(ContentCategory contentCategory) {
        System.out.println("保存id:" + contentCategory.getId());
        System.out.println("保存父Id:" + contentCategory.getParentId());
        System.out.println("保存分类名称:" + contentCategory.getName());
        contentCategory.setId(null);
        contentCategory.setIsParent(false);
        contentCategory.setCreated(new Date());
        super.saveSelective(contentCategory);
       //查询isParent是否为false，如果是false，则修改为true.
        ContentCategory parent = super.queryById(contentCategory.getParentId());
        if(!parent.getIsParent()){
            parent.setIsParent(true);
            super.updateByIdSelective(parent);
        }
        return contentCategory;
    }


    /**
     * 删除内容分类节点
     * 
     * @param parentId
     * @param id
     */
    public void deleteContentCategory(Long parentId, Long id) {
        // 获取所有需要删除的id，使用递归获取所有子节点的id
        List<Object> ids = new ArrayList<Object>();
        ids.add(id);
        this.getDelIds(ids, id);
        // 把获取到的所有id进行删除
        super.deleteByIds(ids);

        // 判断父节点是否还有其他的子节点
        ContentCategory param = new ContentCategory();
        param.setParentId(parentId);
        // 查询该节点的所有兄弟节点
        List<ContentCategory> list = super.queryListByWhere(param);
        if (list.size() == 0) {
            // 如果没有兄弟节点，则父节点的isParent为false
            ContentCategory parent = new ContentCategory();
            parent.setId(parentId);
            parent.setIsParent(false);
            super.updateByIdSelective(parent);
        }

    }

    /**
     * 递归获取该节点的所有字节点
     * 
     * @param ids
     * @param id
     */
    private void getDelIds(List<Object> ids, Long id) {
        // 声明查询参数，传递进来的id为parentId
        ContentCategory param = new ContentCategory();
        param.setParentId(id);

        // 根据条件查询
        List<ContentCategory> list = super.queryListByWhere(param);
        for (ContentCategory contentCategory : list) {
            // 把查询到的id，add到ids中，准备删除
            ids.add(contentCategory.getId());
            // 对查询的id进行递归
            this.getDelIds(ids, contentCategory.getId());
        }

    }
    
}
