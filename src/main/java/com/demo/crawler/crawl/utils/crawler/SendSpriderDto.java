package com.demo.crawler.crawl.utils.crawler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.demo.crawler.crawl.weibo.WeiboCrawler;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 发送到 爬虫分析的数据对象
 *
 *  json 转换时 的首字母变成小写 TypeUtils.compatibleWithJavaBean = true;
 *  （首字母第二个不是大写问题)  TypeUtils.compatibleWithFieldName = true;
 *
 */
@Data
public class SendSpriderDto implements Serializable {

    /** URL地址 */
    private String URL;

    /**标题 */
    private String TITLE;

    /** 发帖人 */
    private String AUTHOR;

    /** 发帖人头像 */
    @JSONField(name = "A_ICON_URL")
    @JsonProperty(value = "A_ICON_URL")
    private String A_ICON_URL;

    /**UTF8 4000长度 正文*/
    private String MAIN_TEXT;

    /** 发帖时间(varchar格式)*/
    //@JSONField(serialize = false)
    private Timestamp MAIN_DATE;

    /** 主要图片地址 */
    private String MAIN_PIC_URL;

    /** 总回帖数*/
    private Integer HOTDEGREE;

    /** 全部html(大小8m以内)*/
    private String ALL_TEXT;

    /** 发帖客户端（APPLE/ANDROID/PC等）*/
    private String FROMCLIENT = "PC";

    /** "百度贴吧"/"微信公众号"/"微博" */
    private String FROMSITE;

    /** 'yyyy-mm-dd hh24:mi:ss'格式--收集时间 ?? (format = "yyyy-MM-dd HH:mm:ss")*/
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Timestamp GATHER_TIME = Timestamp.valueOf(LocalDateTime.now());

    /** 父链接URL*/
    private String PARENT_URL;

    /** 'yyyy-mm-dd hh24:mi:ss'格式--最后跟帖时间   ?? (format = "yyyy-MM-dd HH:mm:ss")*/
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Timestamp UPD_DATE;

    /**
     * 返回时间字符串
     * @return
     */
    public String getMAIN_DATE() {
        return null != this.MAIN_DATE ?  String.valueOf(this.MAIN_DATE.getTime()) : String.valueOf(this.MAIN_DATE);
    }



    /**
     * 通过微博的mblogjson对象复制数据
     *  status_title, MAIN_DATE，MAIN_TEXT， all_TEXT, upd_DATE 用其他方式注入
     * @param jsonObject
     * @return
     */
    public static SendSpriderDto copyWeiboJSONObj(JSONObject jsonObject) {
        SendSpriderDto sendSpriderDto = new SendSpriderDto();
        String url = WeiboCrawler.WEIBO_PERSON_DETAIL_PRE + jsonObject.getString("id");
        sendSpriderDto.setURL(url);

        /*String rawText = jsonObject.getString("raw_text");
        int subTitleLength = rawText.length() > 30 ? 30 : rawText.length();
        sendSpriderDto.setTITLE(rawText.substring(0,subTitleLength));*/

        sendSpriderDto.setAUTHOR(jsonObject.getJSONObject("user").getString("screen_name"));
        sendSpriderDto.setA_ICON_URL(jsonObject.getJSONObject("user").getString("avatar_hd"));

        /**
         * 设置主要图片
         */
        JSONObject pageInfo = jsonObject.getJSONObject("page_info");
        String mainPicUrl = StringUtils.isNotEmpty(jsonObject.getString("original_pic"))
                ? jsonObject.getString("original_pic")
                    : ( null != pageInfo && StringUtils.isNotEmpty(pageInfo.getString("page_pic"))
                        ? pageInfo.getString("page_pic") : "" );

        sendSpriderDto.setMAIN_PIC_URL(mainPicUrl);
        sendSpriderDto.setHOTDEGREE(jsonObject.getInteger("comments_count"));
        sendSpriderDto.setFROMCLIENT(jsonObject.getString("source"));
        sendSpriderDto.setFROMSITE("新浪微博");
        //sendSpriderDto.setGATHER_TIME(Timestamp.valueOf(LocalDateTime.now()));
        sendSpriderDto.setPARENT_URL(WeiboCrawler.WEIBO_PERSON_HOME_PRE + jsonObject.getJSONObject("user").getString("id"));

        return sendSpriderDto;
    }

}
