package cn.edu.bistu.cs.bigdata.crawlerpart;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.net.URL;
import java.util.ArrayList;

import cn.edu.bistu.cs.bigdata.crawlerpart.baicdata.BasicInformation;


public class ArticleInformationApproach implements PageProcessor{
    private static Logger logger = Logger.getLogger(ArticleInformation.class);
    private final Site site = Site.me().setRetrySleepTime(3).setRetrySleepTime(3000).setSleepTime(5000);

    @Override
    public void process(Page page) {
        String BasicAddress = page.getRequest().getUrl();
        Document document = Jsoup.parse(BasicAddress);

        //文献名称
        page.putField("title", page.getHtml().xpath("/html/body/div[1]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/text()").toString());


        //文献作者
        //page.putField("name", page.getHtml().xpath("//*[@id=\"CnAuthorList\"]/li[2]/a/text()").toString());
        /*
        文献作者提取采用Jsoup较为方便
         */
        ArrayList<String> author = new ArrayList<>();
        Elements autElement = document.getElementsByAttributeValue("name", "citation_author");
        for(Element aut: autElement){
            if(aut.attr("content").matches("\u4e00-\u9fa5")){
                author.add(aut.attr("content"));
            }
        }
        autElement.attr("content");

        //论文摘要
        page.putField("abstract", page.getHtml().xpath("//*[@id=\"CnAbstractValue\"]/text()").toString());
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
        //后续再连接数据类型时，只需要将相应的字符串split()即可
        //*[@id="CnKeyWord"]/a[2]

        //将下一篇文献加入爬取队列
        /*
        for(int i = 12; i <= 62; i++){
            String temp = "http://www.jos.org.cn/jos/article/abstract/50" + i;
            //Request有自动去重的机制
            page.addTargetRequests((Request) page.getHtml().links().regex(temp).all());
        }
         */

        //page.addTargetRequests(page.getHtml().links().regex("http://www.jos.org.cn/jos/article/abstract/5012").all());
    }

    @Override
    public Site getSite() {
        return this.site;
    }

    public static void main(String[] args){
        logger.info("This is info message!");
        Spider.create(new ArticleInformationApproach())
                .addUrl("http://www.jos.org.cn/jos/article/abstract/5012")
                //.addPipeline(new JsonFilePipeline("F:\\Project\\CrawlerInformation\\src"))
                //.addPipeline(new JsonFilePipeline("/home/carlsmith-wuzhuo/Desktop/CrawlerInformation"))
                //webmagic本身就包含有输出到控制台的效果
                //.addPipeline(new ConsolePipeline())
                .run();
    }
}
