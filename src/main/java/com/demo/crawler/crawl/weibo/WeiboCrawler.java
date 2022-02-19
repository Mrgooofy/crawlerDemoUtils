package com.demo.crawler.crawl.weibo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.demo.crawler.crawl.ip.dto.IPDto;
import com.demo.crawler.crawl.utils.crawler.HttpCrawlerUtils;
import com.demo.crawler.crawl.utils.crawler.SendSpriderDto;
import com.demo.crawler.crawl.utils.crawler.SendToServerUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 个人信息的用户的  前缀 containerId 100505
 * 个人的微博的 前缀 containerId 107603
 *
 *
 */
@Slf4j
public class WeiboCrawler {

    private static String WEIBO_CN_IP_PRE = "https://m.weibo.cn";

    private static String WEIBO_RM = "https://m.weibo.cn/api/container/getIndex?containerid=102803&openApp=0";

    private static String WEIBO_PERSON_INDEX_URL = "https://m.weibo.cn/api/container/getIndex";

    public static String WEIBO_PERSON_DETAIL_PRE = "https://m.weibo.cn/detail/";

    public static String WEIBO_PERSON_HOME_PRE = "https://m.weibo.cn/u/";

    private static HttpUtils httpUtils = new HttpUtils();

    private static  ScriptEngine engine;         //获取js引擎实例


    public static void main(String[] args) {
        //WeiboCrawler.listIP(0,1);

        String nickname = "人民日报";

        String uid = "2803301701";
        String weiboContainerid = "1076032803301701";

        //getDetail("4545084231779111", "w");

        List list  = listCards(uid, 1);
        TypeUtils.compatibleWithJavaBean = true;

        Map<String, Object> resultMap = (Map<String, Object>) list.get(0);
        List<SendSpriderDto> sendSpriderDtoList = (List<SendSpriderDto>) resultMap.get("sendList");
        for (SendSpriderDto sendSpriderDto : sendSpriderDtoList) {
            SendToServerUtils.sendToServer(SendToServerUtils.pushUrl, sendSpriderDto);
        }

        System.out.println(JSON.toJSONString(list));
        //list.forEach(System.out::println);



/*        list.forEach(System.out::println);

        System.out.println();*/

        /*String data = getInfo("2803301701", "1005052803301701");

        JSONObject dataJson = JSON.parseObject(data);
        JSONObject tabsInfo = JSON.parseObject(dataJson.get("tabsInfo").toString());
        //
        JSONArray tabs = tabsInfo.getJSONArray("tabs");
        for (int i = 0; i < tabs.size(); i++){
            String s = tabs.getJSONObject(i).toString();
            System.out.println(s);
        }*/


    }


    /**
     * 通过uid, 获取个人的用户信息和标签的信息
     * @param uid
     * @return
     */
    public static String getInfo(String uid, String containerid) {

        String url = "https://m.weibo.cn/api/container/getIndex?type=uid&value=" + uid + "&containerid=" + containerid;
        String html = httpUtils.doGetHtml(url);
        JSONObject jsonObject = JSON.parseObject(html);
        log.info(jsonObject.get("data").toString());

        return jsonObject.get("data").toString();
    }


    /**
     * 通过 uid 获取个人的微博列表
     *
     * @param uid
     * @param pageNum 1起 ， 页码
     * @return  List中的每一项 中有hashMap : cardList 和 sendList
     */
    @SneakyThrows
    public static List listCards(String uid, int pageNum) {
        String containerid = uidToContainerId(uid);

        String url = WEIBO_PERSON_INDEX_URL;

        String userAgent = HttpCrawlerUtils.getRandomOneUAs();
        String referer = WEIBO_CN_IP_PRE + "/u/" + uid;
        String host = WEIBO_CN_IP_PRE;


        /**
         * 分页的相关信息，相当第几页
         */
        String sinceId = "";

        //请求参数
        Map<String, String> paramData = new HashMap<>();
        paramData.put("type", "uid");
        paramData.put("value", uid);
        paramData.put("containerid", containerid);
        paramData.put("since_id", sinceId);

        /** 头部信息 */
        Map<String, String> headers = HttpCrawlerUtils.getHeader(host, referer, userAgent);


        Connection connection = Jsoup.connect(url)
                .headers(headers)
                .userAgent(userAgent) // searchOA
                .timeout(5000)
                .ignoreContentType(true)
                .method(Connection.Method.GET);

        List cardList = new ArrayList();

        for(int i = 0; i < pageNum; i ++ ) {
            paramData.put("since_id", sinceId);
            connection.data(paramData);
            Connection.Response res = connection.execute();
            //Document doc = res.parse();

            String jsonStr =  res.body();

            Map<String, String> map =  parseIndexJson(jsonStr);

            if(null != map) {
                sinceId = String.valueOf(map.get("sinceId"));

                log.info("执行每一个帖子");
                JSONArray cardJsonArray =  JSON.parseArray(map.get("cardList"));
                List sendSpriderList = getDetailList(cardJsonArray, userAgent);


                TypeUtils.compatibleWithJavaBean = true;
                TypeUtils.compatibleWithFieldName = true;
                String sendJson = JSON.toJSONString(sendSpriderList);
                log.info("********send Json *******");
                //log.info(sendJson);


                Map<String, Object> mapPage = new HashMap<>();
                mapPage.put("sendList", sendSpriderList);
                mapPage.put("cardList", cardJsonArray);

                cardList.add(mapPage);

                log.info("map page ");
                //log.info(JSON.toJSONString(mapPage));
                //cardList.forEach(System.out::println);
            }
            //System.out.println(cardList.toString());

            HttpCrawlerUtils.sleepRandom(3, 7);
            log.info("第 " + (i+1) + "页");
        }

        return cardList;
    }


    /**
     * 获取每一个主题帖子的列表
     * @param cardListArr
     * @param userAgent
     * @return
     */
    public static List getDetailList(JSONArray cardListArr, String userAgent) {

        int cardSize = cardListArr.size();
        if(cardSize < 0) {
            return null;
        }

        JSONObject cardJson;
        SendSpriderDto sendSpriderDto;
        List<SendSpriderDto> listSendSprider = new ArrayList<>();

        for(int i = 0; i < cardSize; i++) {
            cardJson = cardListArr.getJSONObject(i);
            if(cardJson.getIntValue("card_type") !=9 ) {
                log.info("不是 微博正文 类型 ");
                continue;
            }

            if(i > 2) {
                break;
            }

            JSONObject mblogJson = cardJson.getJSONObject("mblog");
            sendSpriderDto = SendSpriderDto.copyWeiboJSONObj(mblogJson);


            Map<String, String> detailMap =  doDetailHtml(mblogJson.getString("id"), userAgent);
            if(!detailMap.isEmpty()) {
                String renderData = detailMap.get("renderData");
                JSONObject renderJson =  JSON.parseObject(renderData);

                sendSpriderDto.setTITLE(renderJson.getJSONObject("status").getString("status_title"));
                sendSpriderDto.setMAIN_TEXT(renderJson.getJSONObject("status").getString("text"));
                sendSpriderDto.setMAIN_DATE(parseWeiboTime(renderJson.getJSONObject("status").getString("created_at")));

                sendSpriderDto.setALL_TEXT(detailMap.get("ALL_TEXT"));
            }
            //log.info(sendSpriderDto.toString());
            log.info("sendSpriderDto save");
            listSendSprider.add(sendSpriderDto);
        }

        return listSendSprider;
    }



    /**
     * 微博详情页的信息查询
     *
     * @param mid
     * @param userAgent
     * @return 返回  MAIN_TEXT, ALL_TEXT
     */
    @SneakyThrows
    public static Map<String, String> doDetailHtml(String mid, String userAgent) {

        // mid 4545084231779111

        String url = WEIBO_PERSON_DETAIL_PRE + mid;

        //userAgent = HttpCrawlerUtils.getRandomOneUAs();
        String referer = url;
        String host = WEIBO_CN_IP_PRE;

        //请求参数
        Map<String, String> paramData = new HashMap<>();

        /** 头部信息 */
        Map<String, String> headers = HttpCrawlerUtils.getHeader(host, referer, userAgent);

        HttpCrawlerUtils.sleepRandom(3, 7);
        Connection connection = Jsoup.connect(url)
                .headers(headers)
                .userAgent(userAgent) // searchOA
                .timeout(5000)
                .ignoreContentType(true)
                .method(Connection.Method.GET);

        List cardList = new ArrayList();

            connection.data(paramData);
            Connection.Response res = connection.execute();
            Document doc = res.parse();

        //System.out.println("doc ");

        Map<String, String> map =  new HashMap<>();
        map.put("ALL_TEXT", doc.toString());

        /*通过 js 获取正文*/
        Element element =  doc.body().selectFirst("script");
        if(element != null) {
            String str = element.data();

            ScriptEngineManager sem = new ScriptEngineManager();
            engine=sem.getEngineByName("javascript");
            engine.eval(str);

            String rendaData = JSON.toJSONString(engine.get("$render_data"));

            //ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) engine.get("$render_data");
            /*ScriptObjectMirror jsonStatu = (ScriptObjectMirror)  scriptObjectMirror.get("status");
            String jsonText = (String) jsonStatu.get("text");
            map.put("MAIN_TEXT", jsonText);*/

            map.put("renderData", rendaData);
            log.info("获取正文内容");
            //log.info(jsonText);
        }

        return map;
    }

    /**
     * 解析首页的json数据
     * @param jsonStr
     * @return
     */
    public static Map<String, String> parseIndexJson(String jsonStr) {
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        if(1 != jsonObject.getIntValue("ok")) {
            return null;
        }

        JSONObject jsonData = jsonObject.getJSONObject("data");

        //更新 since_id， 下一页
        JSONObject cardlistInfo = jsonData.getJSONObject("cardlistInfo");
        String sinceId = cardlistInfo.getString("since_id");

        String cardsStr  = jsonData.getString("cards");
        //System.out.println(cardsStr);

        //4：将字符串转成list集合
        //List<Card> cardList =JSONObject.parseArray(cardsStr, Card.class);


        Map<String, String> map = new HashMap<>();
        map.put("sinceId", sinceId);
        map.put("cardList", cardsStr);

        return map;
    }

    public static List<IPDto> listIP(int pageNum, int pageCount) {
        List<String> addrs = new LinkedList<String>();
        Map<String,Integer> addr_map = new HashMap<String,Integer>();
        Map<String,String> ipmap = new HashMap<String,String>();
        ExecutorService exe = Executors.newFixedThreadPool(10);

        List<IPDto> list = new LinkedList<>();
        IPDto ipDto = null;


        String doc = null;
        try {
            doc = Jsoup.connect(WEIBO_RM)
                    .userAgent(HttpCrawlerUtils.getRandomOneUAs())
                    .ignoreContentType(true).execute().body();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        log.info(doc);

        //System.out.println(doc.baseUri());

        return null;

        /*Elements items = doc.select(".table").get(0).select("tbody").get(0).select("tr");
        System.out.println("*********** item ***************");
        //System.out.println(items);

        System.out.println(UserAgentUtil.getRandomOne(UserAgentUtil.getUAs()));

        for (int j = 0; j < items.size(); j++) {//每页显示20条数据  从第一条开始
            Elements elements = items.get(j).select("td");

            ipDto = new IPDto();
            ipDto.setIP(elements.get(0).text());
            ipDto.setIPPort(elements.get(1).text());
            ipDto.setIPServerType(elements.get(3).text());
            ipDto.setIPSpeed(elements.get(5).text());

            System.out.println(" td  *************");
            //System.out.printf("ip %s ,  port is %s, server type is %s, speed is %s", elements.get(0).text(), elements.get(1).text(), elements.get(3).text(),elements.get(5).text());
            list.add(ipDto);

            System.out.println(list.toString());

        }

        System.out.println("End of " +  " page ");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/


        //return list;
    }


    /**
     * 昵称转 uid
     * @throws IOException
     * @throws ClientProtocolException
     * */
    /*public static String nicknameToUid(String nickname) throws ClientProtocolException, IOException{
        String url = "https://m.weibo.cn/n/"+nickname;
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("User-Agent", HttpCrawlerUtils.getRandomOneUAs());
        HttpResponse response = httpClient.execute(post);
        post.abort();
        if(response.getStatusLine().getStatusCode()==302){
            String cid = response.getLastHeader("Location").getValue().substring(3);
            return cid;
        }
        return null;
    }*/


    /**
     * 昵称转contianerId
     * @throws IOException
     * @throws ClientProtocolException
     * */
    /*static String nicknameToContainerId(String nickname) throws ClientProtocolException, IOException{
        String url = "https://m.weibo.cn/n/"+nickname;
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        post.setHeader("User-Agent", HttpCrawlerUtils.getRandomOneUAs());
        HttpResponse response = httpClient.execute(post);
        post.abort();
        if(response.getStatusLine().getStatusCode()==302){
            String cid = response.getLastHeader("Location").getValue().substring(3);
            return "100505" + cid;
        }
        return null;
    }*/



    /**
     * uid转 个人微博列表的containerId, 100505为个人信息的containerId
     *
     * */
    static String uidToContainerId(String uid){
        if(uid==null) {
            throw new IllegalArgumentException("uid is null");
        }

        return "107603"+uid;
    }

    /**
     * contianerId转uid
     * */
    static String containerIdToUid(String contianerId){
        if(contianerId==null) {
            throw new IllegalArgumentException("contianerId is null");
        }

        return contianerId.substring(6);
    }

    /**
     *
     * "Thu Sep 03 14:53:06 +0800 2020"
     * @param dtime
     * @return
     */
    static Timestamp parseWeiboTime(String dtime) {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy", Locale.US);
        Date createDate = null;

        try {
            createDate = format.parse(dtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Timestamp createTime =  new Timestamp(createDate.getTime());
        return  createTime;
    }


}
