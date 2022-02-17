package com.demo.crawler.crawl.domain;

import lombok.Data;

/**
 * 微博爬虫日志对象 crawler_job_weibo_log
 * 
 *
 *
 */
@Data
public class CrawlerJobWeiboLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务日志ID */
    private Long jobLogId;

    /** 任务名称 */
    private String jobName;

    /** 爬取账号名 */
    private String accountName;

    private String accountCode;

    /** 执行状态（0-失败， 1-正常） */
    private String status;

    /** 异常信息 */
    private String exceptionInfo;

    /** 爬取的内容(正文+回复) json格式  拼接 */
    private String crawlContent;

    /** 耗时时间 */
    private String takeTime;

    /** 推送状态 （0-失败， 1-正常） */
    private Integer pushState;



}
