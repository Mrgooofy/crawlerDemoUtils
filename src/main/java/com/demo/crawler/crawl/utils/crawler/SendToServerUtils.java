package com.demo.crawler.crawl.utils.crawler;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.demo.crawler.crawl.utils.BeanUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.sql.Timestamp;
import java.util.Map;

@Slf4j
public class SendToServerUtils {

    public static String pushUrl = " http://111.229.147.252/eagle/sqlphp/inputSpiderData.php?ACTION_TYPE=ADD&API_SERIAL=b1c3k2_CMBJX123_SPI";

    public static void main(String[] args) throws Exception {

        SendSpriderDto sendSpriderDto = new SendSpriderDto();
        sendSpriderDto.setTITLE("标题");
        sendSpriderDto.setAUTHOR("作者");
        sendSpriderDto.setA_ICON_URL("https://wx3.sinaimg.cn/orj480/a716fd45ly8gdijd1zmonj20sa0saaby.jpg");
        sendSpriderDto.setMAIN_DATE(new Timestamp(System.currentTimeMillis()));
        sendSpriderDto.setGATHER_TIME(new Timestamp(System.currentTimeMillis()));
        sendSpriderDto.setALL_TEXT("测试");

        String mainT = StringEscapeUtils.escapeEcmaScript("<a  href=\"https://m.weibo.cn/search?containerid=231522type%3D1%26t%3D10%26q%3D%23%E5%86%9B%E8%AE%AD%E7%85%A7%E6%8C%91%E6%88%98%23&extparam=%23%E5%86%9B%E8%AE%AD%E7%85%A7%E6%8C%91%E6%88%98%23&luicode=20000061&lfid=4545461957954574\" data-hide=\"\"><span class=\"surl-text\">#军训照挑战#</span></a>【<a  href=\"https://m.weibo.cn/search?containerid=231522type%3D1%26t%3D10%26q%3D%23%E6%95%99%E5%AE%98%E6%8A%8A%E6%89%8B%E6%9C%BA%E6%94%BE%E5%AD%A6%E7%94%9F%E8%84%9A%E4%B8%8B%E6%A3%80%E9%AA%8C%E5%86%9B%E5%A7%BF%23&extparam=%23%E6%95%99%E5%AE%98%E6%8A%8A%E6%89%8B%E6%9C%BA%E6%94%BE%E5%AD%A6%E7%94%9F%E8%84%9A%E4%B8%8B%E6%A3%80%E9%AA%8C%E5%86%9B%E5%A7%BF%23&luicode=20000061&lfid=4545461957954574\" data-hide=\"\"><span class=\"surl-text\">#教官把手机放学生脚下检验军姿#</span></a>】军训时总想着玩手机？军姿站得不标准？“手机瘾“这样来戒！近日，四川文化传媒职业学院军训时，杨教官让每名同学把手机放置在自己脚下，并保持着后脚跟抬起的动作。上一秒还高喊着“真想”玩手机的同学们都使出全身力气，保证“心肝宝贝”不受破坏。<span class=\"url-icon\"><img alt=[笑cry] src=\"https://h5.sinaimg.cn/m/emoticon/icon/default/d_xiaoku-f2bd11b506.png\" style=\"width:1em; height:1em;\" /></span><a data-url=\"http://t.cn/A64c255z\" href=\"https://video.weibo.com/show?fid=1034:4545117600546828\" data-hide=\"\"><span class='url-icon'><img style='width: 1rem;height: 1rem' src='https://h5.sinaimg.cn/upload/2015/09/25/3/timeline_card_small_video_default.png'></span><span class=\"surl-text\">四川日报的微博视频</span></a>");
        log.info(mainT);
        log.info(StringEscapeUtils.escapeEcmaScript(mainT));
        sendSpriderDto.setMAIN_TEXT(mainT);

        sendSpriderDto.setURL("http://www.test.com");

        sendToServer(pushUrl, sendSpriderDto);
    }


    /**
     * @param data
     * @throws Exception
     */
    @SneakyThrows
    public static boolean sendToServer(String url, SendSpriderDto data) {
        HttpCrawlerUtils.sleepRandom(2, 4);

        TypeUtils.compatibleWithJavaBean = true;
        //请求参数
        Map<String, String> paramData = BeanUtils.beanToMap(data);

        paramData.put("A_ICON_URL", data.getA_ICON_URL());
        paramData.put("MAIN_TEXT", StringEscapeUtils.escapeEcmaScript(data.getMAIN_TEXT()));
        paramData.put("ALL_TEXT", StringEscapeUtils.escapeHtml3(data.getALL_TEXT()));

        log.info("传输的数据对象: ");
        //log.info(JSON.toJSONString(paramData));

        String userAgent = HttpCrawlerUtils.getRandomOneUAs();

        /** 头部信息 */
        Map<String, String> headers = HttpCrawlerUtils.getHeader("", "", userAgent);
        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        Connection connection = Jsoup.connect(url)
                .headers(headers)
                .userAgent(userAgent) // searchOA
                .timeout(10000)
                .ignoreContentType(true)
                .method(Connection.Method.POST);
        connection.data(paramData);
        Connection.Response res = connection.execute();
        //Document doc = res.parse();

        String jsonStr = res.body();

        if (jsonStr.equals("[{\"RT_STATUS\":OK}]")) {
            log.info(data.getAUTHOR() + " - " + data.getTITLE() + " 传输成功\n\n");
            return true;
        } else {
            log.error(data.getAUTHOR() + " - " + data.getTITLE() + " 传输失败\n\n");
            log.error(jsonStr);
        }

        return false;
    }


    /**
     * 暴力解析:Alibaba fastjson
     *
     * @param test
     * @return
     */
    public final static boolean isJSONValid(String test) {
        try {
            JSONObject.parseObject(test);
        } catch (JSONException ex) {
            try {
                JSONObject.parseArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }


}
