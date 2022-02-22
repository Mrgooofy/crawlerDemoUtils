package com.demo.crawler.crawl.sougouweixin.function;

import com.demo.crawler.crawl.sougouweixin.entity.Article;
import com.demo.crawler.crawl.sougouweixin.entity.LoginInfo;
import com.demo.crawler.crawl.sougouweixin.entity.OfficialAccount;
import com.demo.crawler.crawl.sougouweixin.entity.PageData;
import com.demo.crawler.crawl.sougouweixin.utils.SearchManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class SogouWechatApi {

    public static void main(String[] args) {
        try {
            List<OfficialAccount> accountList = searchOfficialAccount("nnnews2008");
            accountList.forEach(System.out::println);


            //List<Article> articleList =  searchArticle("nnxy1985", 1);
            //articleList.forEach(System.out::println);

            //testSearch(null, null);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取微信号的code
     * @param keyword
     * @return
     */
    public static OfficialAccount getOfficialAccountCode(String keyword) throws IOException, InterruptedException {

        List<OfficialAccount> list =  searchOfficialAccount(keyword);
        if(list.isEmpty()) {
            return null;
        }

        for (OfficialAccount officialAccount: list) {
            if(officialAccount.getTitle().equals(keyword) || officialAccount.getAccount().equals(keyword)) {
                //返回 account 微信号
                return officialAccount;
            }
        }

        return null;
    }

    /**
     * 获取微信公众号的信息
     * @param scanner 微信公众号的名称或者微信号
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static List<OfficialAccount> searchOfficialAccount(String scanner) throws IOException, InterruptedException {
        return SogouWechatApi.searchOfficialAccount(scanner, null);
    }


    /**
     *
     * @param scanner 公众号名
     * @param info 登录信息
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private static List<OfficialAccount> searchOfficialAccount(String scanner, LoginInfo info) throws IOException, InterruptedException {

        System.out.println("公众号关键词： " + scanner);
        String keywords = scanner;

        PageData<OfficialAccount> res = SearchManager.searchOA(info == null ?
                new PageData<OfficialAccount>(keywords).setMaxPage(3) :
                new PageData<OfficialAccount>(keywords, info.getUa(), info.getCallBackCookies()).setMaxPage(3));
        checkOfficialAccount(res);
        return res.getRecords();
    }

    /**
     * 查找微信的文章列表
     * @param scanner 微信公众号的id 或者关键词
     * @throws IOException
     * @throws InterruptedException
     */
    public static List<Article> searchArticle(String scanner, int pageNum) throws IOException, InterruptedException {
        return SogouWechatApi.searchArticle(scanner, null, pageNum);
    }

    /**
     * 查找微信的文章列表
     * @param scanner 微信公众号的id 或者关键词
     * @param info
     * @throws IOException
     * @throws InterruptedException
     */
    private static List<Article> searchArticle(String scanner, LoginInfo info, int pageNum) throws IOException, InterruptedException {
        {
            log.info("文章关键词 " + scanner);
            String keywords = scanner;

            PageData<Article> pageData = info == null ? new PageData<Article>(keywords)
                    : new PageData<Article>(keywords, info.getUa(), info.getCallBackCookies());
            pageData.setPageNum(pageNum);

            PageData<Article> res = SearchManager.searchArticle(pageData);

            checkArticle(res);

            return res.getRecords();
        }
    }



    private static void checkOfficialAccount( PageData<OfficialAccount> res) throws IOException {
        System.out.println("是否登录：" + res.isHasLogin());
        System.out.println(res.getCookies());
        List<OfficialAccount> oas = res.getRecords();
        oas.forEach(officialAccount -> System.out.println(officialAccount.toString()));
    }

    private static void checkArticle(PageData<Article> res) throws IOException, InterruptedException {
        System.out.println("是否登录：" + res.isHasLogin());
        System.out.println(res.getCookies());
        System.out.println("当前页：" + res.getPageNum());
        System.out.println("当前页记录数：" + res.getPageSize());
        System.out.println("总记录数：" + res.getTotalRecords());
        System.out.println("总页数" + res.getTotalPage());
        System.out.println("内容: ");
        System.out.println(res.getRecords());
        /*if (res.getPageNum() < res.getTotalPage()) {
            System.out.println("输入页数");
            int page = input.nextInt();

            input.nextLine(); // **** 接收"\n"

            if (page > 0) {
                res.setPageNum(page);
                PageData<Article> resp = SearchManager.searchArticle(res);
                checkRes(input, resp);
            }
        }*/
    }

}
