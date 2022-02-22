package com.demo.crawler.crawl.sougouweixin.entity;

import com.demo.crawler.crawl.utils.crawler.SendSpriderDto;

public class Article {
    /**
     * 初始链接
     */
    private String originalUrl;
    /**
     * 标题
     */
    private String title;
    /**
     * 公众号名称
     */
    private String oaName;
    /**
     * 拼接k ,h
     */
    private String urlWithSuffix;
    /**
     * 转换后的链接
     */
    private String realUrl;

    /**
     * 简介
     */
    private String textBox;

    /** 推送的信息 */
    private SendSpriderDto sendSprider;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOaName() {
        return oaName;
    }

    public void setOaName(String oaName) {
        this.oaName = oaName;
    }

    public void setUrlWithSuffix(String urlWithSuffix) {
        this.urlWithSuffix = urlWithSuffix;
    }

    public String getUrlWithSuffix() {
        return urlWithSuffix;
    }

    public void setRealUrl(String realUrl) {
        this.realUrl = realUrl.replace("http://","https://");
    }

    public String getRealUrl() {
        return realUrl;
    }

    public String getTextBox() {
        return textBox;
    }

    public void setTextBox(String textBox) {
        this.textBox = textBox;
    }

    public SendSpriderDto getSendSprider() {
        return sendSprider;
    }

    public void setSendSprider(SendSpriderDto sendSprider) {
        this.sendSprider = sendSprider;
    }

    @Override
    public String toString() {
        return "Article{" +
                "originalUrl='" + originalUrl + '\'' +
                ", title='" + title + '\'' +
                ", oaName='" + oaName + '\'' +
                ", urlWithSuffix='" + urlWithSuffix + '\'' +
                ", realUrl='" + realUrl + '\'' +
                ", textBox='" + textBox + '\'' +
                ", sendSprider='" + sendSprider + '\'' +
                '}';
    }
}
