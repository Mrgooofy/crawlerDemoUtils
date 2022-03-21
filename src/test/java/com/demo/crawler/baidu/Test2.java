package com.demo.crawler.baidu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

class Test2{
    public static void main(String args[]){
        //设置需要爬取的网页，这里为方便起见就直接用Jsoup自带的api来爬取网页了
        String url = "http://www.guit.edu.cn/xwzx/mtxk.htm";
        //声明Document类，来存储爬取到的html文档
        Document doc = null;
        try {
            doc = Jsoup.connect(url).timeout(2000).get();
            //调用Jsoup类中的connect()方法，url为需要爬取的页面
            //timeout()来设置超时时间，get()方法来获取响应页面
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(doc);//测试用

        //使用select选择器
        Elements elements = doc.select(".box-list").select(".oh").select("a");

        //System.out.println(elements);//测试用

        for(Element e:elements){
            if(e.text().length()>8){
                //逐条输出新闻信息
                System.out.println(e.text());
            }
        }

    }
}
