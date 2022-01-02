package cn.edu.bistu.cs.bigdata.crawlerpart.JOC;
import cn.edu.bistu.cs.bigdata.crawlerpart.ArticlePipeline;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import cn.edu.bistu.cs.bigdata.crawlerpart.baicdata.BasicInformation;

public class ArticleInformation implements PageProcessor{
    private static Logger logger = Logger.getLogger(ArticleInformation.class);
    private final Site site = Site.me()
            .setRetrySleepTime(3)
            .setRetrySleepTime(3000)
            .setSleepTime(5000)
            .setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");

    @Override
    @lombok.SneakyThrows
    public void process(Page page) {
        String BasicAddress = page.getRequest().getUrl();
        Document document = null;
        try {
            document = Jsoup.connect(BasicAddress).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BasicInformation basicInformation = new BasicInformation();

        //文献名称
        //page.putField("title", page.getHtml().xpath("//[@class=\"title\"]/text()").toString());
        //basicInformation.setArticleName(page.getHtml().xpath("/html/body/div[1]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/text()").toString());
        //String title = page.getHtml().xpath("//[@class=\"title\"]").toString();

        assert document != null;
        Elements t = document.getElementsByAttributeValue("class", "title");
        String te = String.valueOf(t.get(0).html()
                .replace("\n","")
                .replace("<i>", "")
                .replace("</i>", ""));
        page.putField("title", te);
        //文献作者
        //page.putField("name", page.getHtml().xpath("//*[@id=\"CnAuthorList\"]/li[2]/a/text()").toString());
        /*
        文献作者提取采用Jsoup较为方便
         */
        ArrayList<String> author = new ArrayList<>();
        Elements autElement = document.getElementsByAttributeValue("name", "citation_author");
        for(Element aut: autElement){
            String a = aut.attr("content");
            if(aut.attr("content").matches("[\\u4E00-\\u9FFF]+")){
                author.add(aut.attr("content"));
                //logger.info(aut.attr("content"));
            }
        }
        page.putField("name", author);
        basicInformation.setAuthorName(author);

        //论文摘要
        page.putField("abstract", page.getHtml().xpath("//*[@id=\"CnAbstractValue\"]/text()").toString());
        basicInformation.setAbstract(page.getHtml().xpath("//*[@id=\"CnAbstractValue\"]/text()").toString());
        //论文关键词
        /*
        //使用jsoup抽取JavaScript
        Elements element = document.getElementsByAttributeValue("language", "javascript").eq(6);
        //eq()方法的含义
        for(Element e:element){
            String[] data = e.data().split("var");
            for (String datum : data) {
                if (datum.contains("strCnKeyWord")) {
                    if(datum.contains("<i></i>")){
                        datum = datum.replace("<i></i>", "");
                    }
                    keyword.add(datum.replace("strCnKeyWord", ""));
                }
            }
        }
        */

        /*
        因为网页的特殊原因，关键词隐藏在meta中，所以只需要提取meta中content的内容即可。
         */
        page.putField("keyword", page.getHtml().xpath("/html/head/meta[5]/@content").toString());
        String[] keyword = page.getHtml().xpath("/html/head/meta[5]/@content").toString().split(";");
        ArrayList<String> abs = new ArrayList<>(Arrays.asList(keyword).subList(0, keyword.length / 2));
        page.putField("keyword", abs);
        basicInformation.setKeyWord(abs);

        //文献DOI（文献的唯一标识，相当于文献的身份证）
        String DOI = page.getHtml().xpath("//*[@id=\"DOIValue\"]/text()").toString();
        page.putField("DOI", DOI);

        //期刊名称
        String add_of_introduce = page.getHtml().xpath("/html/body/div[1]/div[1]/div[1]/div[2]/ul/li[2]/a/@href").toString();
        //Request temporary = page.getRequest().setUrl(add_of_introduce);
        Document joc_document = Jsoup.connect("http://www.jos.org.cn/" + add_of_introduce).get();
        Elements joc_name = joc_document.getElementsByAttributeValue("name", "KeyWords");
        String joc = joc_name.get(0).attr("content");
        page.putField("kan_name", joc);

        //数据源内唯一标识
        StringBuffer joc_id = new StringBuffer();
        for(int i = BasicAddress.length()-5; i < BasicAddress.length(); i++){
            joc_id.append(joc_id.charAt(i));
        }
        page.putField("joc_id", joc_id);

        //后续再连接数据类型时，只需要将相应的字符串split()即可
        //*[@id="CnKeyWord"]/a[2]

        //将下一篇文献加入爬取队列
        for(int i = 13; i <= 62; i++){
            String temp = "http://www.jos.org.cn/jos/article/abstract/50" + i;
            //Request有自动去重的机制
            page.addTargetRequests(Collections.singletonList(temp));
        }

        //page.addTargetRequests(page.getHtml().links().regex("http://www.jos.org.cn/jos/article/abstract/5012").all());
    }

    @Override
    public Site getSite() {
        return this.site;
    }

    public static void main(String[] args){
        //logger.info("This is info message!");
        Spider.create(new ArticleInformation())
                .addUrl("http://www.jos.org.cn/jos/article/abstract/5012")
                //.addPipeline(new JsonFilePipeline("F:\\Project\\CrawlerInformation\\src"))
                //.addPipeline(new JsonFilePipeline("/home/carlsmith-wuzhuo/Desktop/CrawlerInformation"))
                //webmagic本身就包含有输出到控制台的效果
                //.addPipeline(new ConsolePipeline())
                .addPipeline(new JOCPipeline())
                //.addPipeline(new JsonFilePipeline("/home/carlsmith-wuzhuo/Desktop/crawlerpart"))
                .run();
    }
}
