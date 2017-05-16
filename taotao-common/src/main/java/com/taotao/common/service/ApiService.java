package com.taotao.common.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.bean.HttpResult;

/**
 * 抽取的service方法。
 * 
 * @author liuhongbao
 * @date 2017年4月5日 下午9:33:59
 * 
 */
@Service
public class ApiService {

    @Autowired(required = false)
    private CloseableHttpClient httpClient;

    @Autowired(required = false)
    private RequestConfig config;

    /**
     * 带参数的get请求 抽取的带参数的doGet方法，对于抽取的通用方法而言一般都是要抛出异常由业务层去处理异常 如果返回状态码为200则返回body,如果不为200则返回null.
     * 
     * @param url
     * @param map
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public String doGet(String url, Map<String, Object> map) throws URISyntaxException,
            ClientProtocolException, IOException {
        // 声明URIBuilder,准备进行请求属性的拼接
        URIBuilder uriBuilder = new URIBuilder(url);
        if (map != null) {
            // 遍历map，拼接请求属性
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
            }
        }
        // 声明http get请求
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        // 加载配置信息
        httpGet.setConfig(config);
        // 发出请求
        CloseableHttpResponse response = this.httpClient.execute(httpGet);
        // 对响应进行判断，如果响应码为200,则返回null
        if (response.getStatusLine().getStatusCode() == 200) {
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        }

        return null;

    }

    /**
     * 不带参数的get请求
     * 
     * @param url
     * @return
     * @throws Exception
     */
    public String doGet(String url) throws Exception {
        return this.doGet(url, null);

    }

    /**
     * 带参数的post请求 抽取的带参数的post方法，返回的状态码和响应体交由Httpresult处理。code,body
     * 
     * @param url
     * @param map
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResult doPost(String url, Map<String, Object> map) throws ClientProtocolException, IOException {

        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        if (map != null) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            // 将list集合构造一个form表单的实体
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, "UTF-8");
            // 发出请求
            httpPost.setEntity(urlEncodedFormEntity);
        }
        // 发出请求
        CloseableHttpResponse response = this.httpClient.execute(httpPost);
        // 对响应进行判断，如果响应码为200，则返回响应体的内容
        return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(
                response.getEntity(), "UTF-8"));

    }

    /**
     * 不带参数的post请求
     * 
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public HttpResult doPost(String url) throws ClientProtocolException, IOException {
        return this.doPost(url, null);
    }

    /**
     * 带json的参数的post请求
     * 
     * @param url
     * @param json
     * @return
     * @throws Exception
     */
    public HttpResult doPostJson(String url, String json) throws Exception {
        // 声明http post
        HttpPost httpPost = new HttpPost(url);
        // 加载配置信息
        httpPost.setConfig(config);

        StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);

        // 把StringEntity放到httpPost
        httpPost.setEntity(stringEntity);

        // 执行请求
        CloseableHttpResponse response = this.httpClient.execute(httpPost);

        return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(
                response.getEntity(), "UTF-8"));
    }

    /**
     * 不带json的put方法
     * 
     * @param url
     * @return
     * @throws Exception
     */
    public String doPut(String url) throws Exception {
        HttpPut httpPut = new HttpPut(url);
        httpPut.setConfig(config);
        CloseableHttpResponse response = this.httpClient.execute(httpPut);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    /**
     * 带参数的put方法
     * 
     * @param url
     * @param map
     * @return
     * @throws Exception 
     * @throws ClientProtocolException 
     */
    public HttpResult doPut(String url, Map<String, Object> map) throws Exception {
        HttpPut httpPut = new HttpPut(url);
        httpPut.setConfig(config);
        if (map != null) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(list, "UTF-8");
            httpPut.setEntity(urlEncodedFormEntity);
        }

        CloseableHttpResponse response = this.httpClient.execute(httpPut);
        return new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(
                response.getEntity(), "UTF-8"));

    }

    /**
     * 不带参数的delete方法
     * 
     * @param url
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public String doDelete(String url) throws Exception {
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.setConfig(config);
        CloseableHttpResponse response = this.httpClient.execute(httpDelete);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

}
