package cn.edu.bistu.cs.bigdata.crawlerpart.JOCIP;

import cn.edu.bistu.cs.bigdata.crawlerpart.ArticlePipeline;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.server.PathContainer;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ArticleInformation implements PageProcessor {

    private static final Logger logger = Logger.getLogger(ArticleInformation.class);

    private static final Site site = Site.me()
            .setRetryTimes(5)
            .setRetrySleepTime(3000)
            .setSleepTime(5000)
            .setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
    @Override
    public void process(Page page) {
        String basicAdd = page.getRequest().getUrl();
        Document document = null;
        try {
            document = Jsoup.connect(basicAdd).get();
        }catch(IOException e){
            e.printStackTrace();
        }
        //文献名称
        assert document != null;
        Elements title = document.getElementsByAttributeValue("class", "J_biaoti");
        String t = String.valueOf(title.get(0).html())
                .replace("\n", "")
                .replace("<i>", "")
                .replace("</i>","");
        page.putField("title", t);

        //文献作者
        Elements name = document.getElementsByAttributeValue("name", "DC.Contributor");
        ArrayList<String> author = new ArrayList<>();
        for(Element a: name){
            String ant = a.attr("content");
            author.add(ant);
        }
        page.putField("name", author);
        //文献摘要
        String te = page.getHtml().xpath("//*[@id=\"abstract_tab_content\"]/table[1]/tbody/tr[2]/td[1]/span/text()").toString();
        //Elements abs = document.getElementsByAttributeValue("class", "J_zhaiyao");
        //String a = String.valueOf(abs.get(0).html());
        page.putField("abstract", te);

        //文献关键词
        //page.putField("keyword", page.getHtml().xpath("/html/head/meta[2]/@content").toString());
        String[] keyword = page.getHtml().xpath("/html/head/meta[2]/@content").toString().split(",");
        ArrayList<String> keyValue = new ArrayList<>(Arrays.asList(keyword).subList(0, keyword.length - 1));
        page.putField("keyword", keyValue);

        //文献数据源唯一标识
        StringBuffer JOCIP_ID = new StringBuffer();
        for(int i = basicAdd.length() - 10; i < basicAdd.length() - 6; i++){
            JOCIP_ID.append(JOCIP_ID.charAt(i));
        }
        page.putField("JOPIC_ID", JOCIP_ID);


        //换页爬取
        for(int i = 3001; i <= 3099; i++){
            String temp = "http://jcip.cipsc.org.cn/CN/abstract/abstract" + i + ".shtml";
            //Request有自动去重的机制
            page.addTargetRequests(Collections.singletonList(temp));
        }

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args){
        Spider.create(new ArticleInformation())
                .addUrl("http://jcip.cipsc.org.cn/CN/abstract/abstract3009.shtml")
                //.addPipeline(new JsonFilePipeline("/home/carlsmith-wuzhuo/Desktop/crawlerpart"))
                //自定义pipeline
                .addPipeline(new ArticlePipeline())
                .run();
    }
}
