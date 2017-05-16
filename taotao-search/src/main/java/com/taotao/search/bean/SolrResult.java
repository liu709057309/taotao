package com.taotao.search.bean;

import java.util.List;

/**
 * @author liuhongbao
 * @date 2017年4月20日 下午11:15:59
 * 
 */
public class SolrResult {
    
    //数据总条数
    private Long total;
    
    //查询的结果
    private List<?> list;

    
    public SolrResult() {
        super();
    }
    
    public SolrResult(Long total, List<?> list) {
        super();
        this.total = total;
        this.list = list;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
    
    
}
