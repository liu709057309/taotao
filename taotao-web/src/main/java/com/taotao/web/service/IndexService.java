package com.taotao.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.common.service.ApiService;
import com.taotao.common.service.RedisService;
import com.taotao.manage.pojo.Content;

/**
 * @author liuhongbao
 * @date 2017年4月4日 下午10:55:52
 * 
 */
@Service
public class IndexService {
    private String MANAGE_TAOTAO_AD1 = "MANAGE_TAOTAO_AD1";
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    @Autowired
    private  ApiService apiService;

    @Value(value = "${MANAGE_TAOTAO_URL}")
    private String MANAGE_TAOTAO_URL;
    
    @Autowired
    private RedisService redisService;
    /*
     
      // 原始HttpClient的实现方法。供参考 
      public String getAD1() {
         CloseableHttpClient httpClient = HttpClients.createDefault();
         //创建一个http GET请求
         HttpGet httpGet = new HttpGet("http://manage.taotao.com/rest/content?categoryId=51&page=1&rows=20");
         CloseableHttpResponse response = null;
         try {
             //执行请求
            response = httpClient.execute(httpGet);
         // 判断返回状态是否为200
            if(response.getStatusLine().getStatusCode() == 200){
                //得到响应内容
                String jsonData = EntityUtils.toString(response.getEntity(),"UTF-8");
                //判断得到的jasonData是否为空
                if(StringUtils.isNotBlank(jsonData)){
                    //采用EasyUiResult的formatToList方法将jsonData格式数据 转换为EasyUIResult对象。
                    EasyUIResult easyUIResult = EasyUIResult.formatToList(jsonData,Content.class);
                    //得到List<Content>
                    List<Content> list = (List<Content>) easyUIResult.getRows();
                    
                    List<Map<String,Object>> contents = new ArrayList<Map<String,Object>>();
                    
                    for(Content content : list){
                        Map<String,Object> map = new HashMap<String,Object>();
                        map.put("srcB",content.getPic());
                        map.put("height",240);
                        map.put("alt","");
                        map.put("width",670);
                        map.put("src",content.getPic());
                        map.put("widthB",550);
                        map.put("href",content.getPic());
                        map.put("heightB",240);
                        contents.add(map);
                    }
                    
                    String json = MAPPER.writeValueAsString(contents);
                    return json;
                }
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    */
    /**
     * 增加了httpclient的方法和redis功能的方法
     * @return
     */
    public Object getAD1(){
        //1.先从缓存中命中，如果命中，则直接返回数据。
        String redisJson = this.redisService.get(this.MANAGE_TAOTAO_AD1);
        if(StringUtils.isNotBlank(redisJson)){
            return redisJson;
        }
       
        //2.如果没有命中，需要从后台系统的接口获取
        String url = this.MANAGE_TAOTAO_URL + "rest/content?categoryId=51&page=1&rows=20";
        try {
            String jsonData = this.apiService.doGet(url);
            if(StringUtils.isNotBlank(jsonData)){
                //采用EasyUiResult的formatToList方法将jsonData格式数据 转换为EasyUIResult对象。
                EasyUIResult easyUIResult = EasyUIResult.formatToList(jsonData,Content.class);
                //得到List<Content>
                List<Content> list = (List<Content>) easyUIResult.getRows();
                
                List<Map<String,Object>> contents = new ArrayList<Map<String,Object>>();
                
                for(Content content : list){
                    Map<String,Object> map = new HashMap<String,Object>();
                    map.put("srcB",content.getPic());
                    map.put("height",240);
                    map.put("alt","");
                    map.put("width",670);
                    map.put("src",content.getPic());
                    map.put("widthB",550);
                    map.put("href",content.getPic());
                    map.put("heightB",240);
                    contents.add(map);
                }
                
                String json = MAPPER.writeValueAsString(contents);
                
                //查询到就保存到缓存中
                if(StringUtils.isNotBlank(json)){
                    this.redisService.set(MANAGE_TAOTAO_AD1,json,60*60*3);
                }
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        
    }
}
