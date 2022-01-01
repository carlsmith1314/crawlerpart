package cn.edu.bistu.cs.bigdata.crawlerpart.JOC;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;



/*
 *本程序仅为单部分摘出使用，仅为调试参考
 *author: CarlSmith
 */

public class SysmbolProcess implements PageProcessor {
    private static final Site site = Site.me()
            .setRetryTimes(3)
            .setRetrySleepTime(3000)
            .setSleepTime(5000)
            .setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
    @lombok.SneakyThrows
    @Override
    public void process(Page page) {
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
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args){
        Spider.create(new SysmbolProcess())
                .addUrl("http://www.jos.org.cn/jos/article/abstract/5012")
                .run();
    }
}
