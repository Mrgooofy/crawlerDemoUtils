package com.demo.crawler.crawl.weibo.entity;

import lombok.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PageData<T> {
    /**
     * 当前页数
     */
    private int pageNum;

    /**
     * 当页记录数
     */
    private int pageSize;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 总记录数
     */
    private int totalRecords;

    /**
     * 查询用户的page 用户信息
     */
    private String pageContainerid;

    /**
     * 分页的数据 查询 since_id
     */
    private String sinceId;

    /**
     * 当前页记录的数据
     */
    private List<T> records;
    /**
     * 最大页数，登录后可以访问的页数很多，可以通过设置最大页数来限制
     */
    private int maxPage;

    /**
     * 是否登录
     */
    private boolean hasLogin;
    /**
     * 关键词，记录下，翻页使用
     */
    private String keywords;
    /**
     * UA，记录下，所有接口调用统一使用
     */
    private String userAgent;
    /**
     * cookies，记录下，所有接口调用统一使用
     */
    private Map<String, String> cookies;

    // 以下参数不同页数不一致
    /**
     * 来源，翻页需要注明来源，这里处理为每次翻页的来源都为上一页
     */
    private String referer;
    /**
     * 参数，记录下，方便一些接口调用
     */
    private Map<String, String> uigsPara;
    /**
     * 参数，记录下，方便一些接口调用
     */
    private String token;

    public PageData(String keywords) {
        this.keywords = keywords;
    }

    public PageData(String userAgent, Map<String, String> cookies) {
        this.hasLogin = true;
        this.keywords = keywords;
        this.userAgent = userAgent;
        this.cookies = cookies;
    }


    public String getUserAgent() {
        if (userAgent == null) {
            //userAgent = CommonUtil.getRandomOne(CommonUtil.getUAs());
        }
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Map<String, String> getCookies() {
        if (cookies == null) {
            cookies = new HashMap<>();
        }
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
        setPageSize(records.size());
    }

    public int getPageNum() {
        if (pageNum == 0) {
            pageNum = 1;
        }
        return pageNum;
    }


    public int getTotalPage() {
        return Math.min(totalPage, getMaxPage());
//        return totalPage;
    }


    private static final int PAGE_SIZE = 10;

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
        int totalPage = (totalRecords + PAGE_SIZE - 1) / PAGE_SIZE;
        if (!isHasLogin()) {
            totalPage = totalPage <= 10 ? totalPage : 10;
        }
        setTotalPage(totalPage);
    }


    public PageData<T> setMaxPage(int maxPage) {
        this.maxPage = maxPage;
        return this;
    }

    public String getReferer() {
        if (getPageNum() > 1) {
            try {
                referer = "https://weixin.sogou.com/weixin?type=2&s_from=input&query="
                        + URLEncoder.encode(getKeywords(), "UTF-8")
                        + "&ie=utf8&_sug_=n&_sug_type_=&page=" + (getPageNum() - 1);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return referer;
    }


}
