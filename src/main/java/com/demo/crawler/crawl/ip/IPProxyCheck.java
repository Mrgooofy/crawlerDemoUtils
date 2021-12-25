package com.demo.crawler.crawl.ip;

import com.demo.crawler.crawl.ip.dto.IPDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.*;

public class IPProxyCheck {

    //public static String VALID_DEFAULT_URL = "https://www.kuaidaili.com/free/inha/";
    public static String VALID_DEFAULT_URL = "https://www.baidu.com";

    /**
     * check ip collection valid， return ip list
     * @param list
     * @return
     */
    public static HashSet<IPDto> checkIP(List<IPDto> list) {

        HashSet<IPDto> ipDtoValidList = new HashSet<>();

        for(IPDto ipDto : list) {
           boolean checkRes =  IPProxyCheck.check(ipDto.getIP(), ipDto.getIPPort());
           if(checkRes) {
               ipDtoValidList.add(ipDto);
           }
        }

        return ipDtoValidList;
    }

    /**
     * check ip and port valid
     * @param ip
     * @param port
     * @return
     */
    public static boolean check(String ip, String port) {
        Random r = new Random();
        String[] ua = {"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36 OPR/37.0.2178.32",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586",
                "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko",
                "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0)",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 BIDUBrowser/8.3 Safari/537.36",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36 Core/1.47.277.400 QQBrowser/9.4.7658.400",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 UBrowser/5.6.12150.8 Safari/537.36",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36 SE 2.X MetaSr 1.0",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36 TheWorld 7",
                "Mozilla/5.0 (Windows NT 6.1; W…) Gecko/20100101 Firefox/60.0"};
        int i = r.nextInt(14);


        Map<String, String> map = new HashMap<String, String>();
        map.put("anythin","232323");
        try {
            long a = System.currentTimeMillis();

            Document doc = Jsoup.connect(IPProxyCheck.VALID_DEFAULT_URL)
                    .timeout(5000)
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, Integer.parseInt(port))))
                    //.proxy(ip, port, null)
                    //.data(map)
                    .ignoreContentType(true)
                    .userAgent(ua[i])
                    .header("referer", IPProxyCheck.VALID_DEFAULT_URL)
                    .get();

            System.out.println(ip+":"+port+" time:"+(System.currentTimeMillis() -a) + "   access result: "+doc.text());
            Thread.sleep(3000);
            return true;
        } catch (IOException | InterruptedException e) {
            System.out.println("Error ip : " + ip + ":" + port);
            System.out.println(e.getMessage());
            //e.printStackTrace();
            return false;
        }

    }


}
