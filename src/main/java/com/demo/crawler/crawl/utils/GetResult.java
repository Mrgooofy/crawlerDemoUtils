package com.demo.crawler.crawl.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class GetResult {
    public static String getResult(String url) throws Exception {
        //这里用了try-with-resource语法，在try()括号中的资源会在try语句块执行完之后自动释放
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(new HttpGetConfig(url)))
        {
            String result = EntityUtils.toString(response.getEntity());
            return result;
        } catch (Exception e) {
            System.out.println("获取失败");
            return "";
        }
        //所以不需要再finally中释放资源。
    }
}
//内部类，继承HttpGet，为了设置请求超时的参数
class HttpGetConfig extends HttpGet {
    public HttpGetConfig(String url) {
        super(url);
        setDefaulConfig();
    }

    private void setDefaulConfig() {
        this.setConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(10000)
                .setConnectTimeout(10000)
                .setSocketTimeout(10000).build());
        this.setHeader("User-Agent", "spider");
    }
}