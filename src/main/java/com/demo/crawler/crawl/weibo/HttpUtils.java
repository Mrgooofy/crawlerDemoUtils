package com.demo.crawler.crawl.weibo;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Component
public class HttpUtils {

    private PoolingHttpClientConnectionManager cm;

    public HttpUtils() {
        this.cm = new PoolingHttpClientConnectionManager();

        //设置最大连接数
        this.cm.setMaxTotal(100);
        //设置每个主机得最大连接数
        this.cm.setDefaultMaxPerRoute(10);
    }

    /**
     * 根据请求获取页面数据
     * @param url
     * @return
     */
    public String doGetHtml(String url){

        //获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(this.cm).build();

        //创建httpGet请求对象，设置url地址
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(this.getConfig());

        CloseableHttpResponse response = null;
        try {
            //使用HttpClient发起请求，获取响应
            response = httpClient.execute(httpGet);

            //解析响应，返回结果
            if (response.getStatusLine().getStatusCode() == 200){
                //判断响应体Entity是否不为空，如果不为空就可以使用EntityUtils
                if (response.getEntity() != null){
                    String content = EntityUtils.toString(response.getEntity(), "utf8");
                    return content;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 下载图片
     * @param url
     * @return
     */
    public String daGetImage(String url){

        //获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(this.cm).build();

        //创建httpGet请求对象，设置url地址
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(this.getConfig());

        CloseableHttpResponse response = null;
        try {
            //使用HttpClient发起请求，获取响应
            response = httpClient.execute(httpGet);

            //解析响应，返回结果
            if (response.getStatusLine().getStatusCode() == 200){
                //判断响应体Entity是否不为空
                if (response.getEntity() != null){
                    //下载图片
                    //获取文件后缀
                    String extName = url.substring(url.lastIndexOf("."));
                    //获取文件名
                    String picName = UUID.randomUUID().toString()+extName;
                    //写入到本地
                    FileOutputStream outputStream = new FileOutputStream(new File("D:\\yunyan\\uploadPath\\image\\" + picName));
                    response.getEntity().writeTo(outputStream);
                    //返回文件名
                    return picName;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //失败返回空字符串
        return "";
    }

    /**
     * 设置请求信息
     * @return
     */
    private RequestConfig getConfig(){
        RequestConfig build = RequestConfig.custom()
                .setConnectTimeout(1000)    //创建连接最长时间
                .setConnectionRequestTimeout(500)   //获取连接超时时间
                .setSocketTimeout(10000)    //数据传输超时时间
                .build();
        return build;
    }
}
