package com.demo.crawler.crawl.utils.file;


import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *
 * 爬虫的文件工具类
 */
@Slf4j
public class FileCrawlerUtils {

    static String fileDirectroy= "D:\\testFile";
    static String fileStr = "D:\\testFile\\filetest.txt";

    public static void main(String[] args) {
        //checkDirectory(fileDirectroy);
        //checkFile(fileStr);

        String title = "标题";
        saveInfoToFile(fileDirectroy, "test01.txt", "hello world !111 ", title);




    }


    public static boolean saveInfoToFile(String directroyStr, String fileNameStr, String data, String title) {
        String fileStrTmp = directroyStr + "\\" + fileNameStr;

        boolean checkDirectoryRes =  checkDirectory(directroyStr);
        boolean checkFileRes = checkFile(fileStrTmp);

        if(!(checkDirectoryRes && checkFileRes)) {
            log.error("写入文件数据失败");
            return false;
        }

        Path path = Paths.get(fileStrTmp);

        try (BufferedWriter writer = Files.newBufferedWriter(path,  Charset.forName("UTF-8"), StandardOpenOption.APPEND))
        {
            writer.write("\n\n\n");
            //writer.write(DateUtils.dateTimeNow(DateUtils.YYYY_MM_DD_HH_MM_SS));
            writer.write("\n");
            writer.write(title);
            writer.write("\n");
            writer.write("\n");
            writer.write(data);
            writer.write("\n");
            writer.write("\n");
            writer.write("\n** END **********************************************************");
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public static boolean checkDirectory(String fileDirectory) {
        File file =new File(fileDirectory);
        //如果文件夹不存在则创建
        if  (!file .exists()  && !file .isDirectory())
        {
            log.warn("目录不存在， 创建目录");
            file .mkdirs();
        } else
        {
            //log.info("//目录存在");
        }
        return true;
    }

    public static boolean checkFile(String fileStr) {
        File file=new File(fileStr);
        if(!file.exists())
        {
            try {
                file.createNewFile();
                log.warn("文件不存在， 创建文件");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }



}
