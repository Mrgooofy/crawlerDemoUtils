package com.demo.crawler.crawl.ip;

import com.demo.crawler.crawl.ip.dto.IPDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * kuaidaili ip crawl
 */
public class KuaidailiIP {

    private final static String GET_IP_URL = "https://www.kuaidaili.com/free/intr/";
    //private final static String GET_IP_URL = "https://www.kuaidaili.com/free/inha/";


    /**
     * get ip list
     * @param pageNum 0 start，
     * @param pageCount page count
     * @return
     */
    public static List<IPDto> listIP(int pageNum, int pageCount) {
        List<String> addrs = new LinkedList<String>();
        Map<String, Integer> addr_map = new HashMap<String, Integer>();
        Map<String, String> ipmap = new HashMap<String, String>();
        ExecutorService exe = Executors.newFixedThreadPool(10);

        List<IPDto> list = new LinkedList<>();
        IPDto ipDto = null;
        for (int i= pageNum; i< pageCount;i++) {
            Document doc = null;
            try {
                doc = Jsoup.connect(GET_IP_URL + (i+1)).get();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                continue;
            }

            System.out.println(doc.baseUri());
            Elements items = doc.select(".table").get(0).select("tbody").get(0).select("tr");
            System.out.println("*********** item ***************");
            //System.out.println(items);



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


            /*JSONObject jsonObject = JSONObject.fromObject(doc.text());
            List<Map<String,Object>> list = (List<Map<String,Object>>) jsonObject.get("msg");
            int count = list.size();

            for (Map<String,Object> map : list ) {
                String ip = (String)map.get("ip");
                String port = (String)map.get("port") ;
                ipmap.put(ip,"1");
                checkIp2 a = new checkIp2(ip, new Integer(port),count);
                exe.execute(a);
            }
            exe.shutdown();*/
            System.out.println("End of " + (i+1) + " page ");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return list;
    }



    public static void main(String[] args) throws InterruptedException {

        List<IPDto> ipDtoList =  KuaidailiIP.listIP(0,3);

        ipDtoList.stream().forEach(System.out::println);
        System.out.println(ipDtoList.stream().count());

        HashSet<IPDto> validList =  IPProxyCheck.checkIP(ipDtoList);

        System.out.println("valid ip : ");
        validList.stream().forEach(System.out::println);
        System.out.println(validList.stream().count());

    }
}
