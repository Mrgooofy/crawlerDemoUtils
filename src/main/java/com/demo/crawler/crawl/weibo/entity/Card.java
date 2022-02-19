package com.demo.crawler.crawl.weibo.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;


public class Card implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @JSONField(name="card_type")
    private Integer cardType;

    private String itemid;

    private String mblog;

    private String scheme;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getMblog() {
        return mblog;
    }

    public void setMblog(String mblog) {
        this.mblog = mblog;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", cardType=" + cardType +
                ", itemid='" + itemid + '\'' +
                ", mblog='" + mblog + '\'' +
                ", scheme='" + scheme + '\'' +
                '}';
    }
}
