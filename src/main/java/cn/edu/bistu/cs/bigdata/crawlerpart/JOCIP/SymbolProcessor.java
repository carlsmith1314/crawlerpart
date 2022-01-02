package cn.edu.bistu.cs.bigdata.crawlerpart.JOCIP;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class SymbolProcessor implements PageProcessor {
    private static final Site site = Site.me()
            .setRetryTimes(5)
            .setRetrySleepTime(3000)
            .setSleepTime(5000)
            .setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");


    @SneakyThrows
    @Override

    public void process(Page page) {
        //文献DOI

        //期刊名称
        String JOPIC_add = page.getRequest().getUrl();
        Document D_JOPIC = Jsoup.connect(JOPIC_add).get();
        Elements e_JOPIC = D_JOPIC.getElementsByAttributeValue("name", "citation_journal_title");
        String JOPIC = e_JOPIC.attr("content");
        page.putField("JOPIC", JOPIC);

        //数据源内唯一ID
        String basicAdd = page.getRequest().getUrl();
        StringBuffer JOCIP_ID = new StringBuffer();
        for(int i = basicAdd.length() - 10; i < basicAdd.length() - 6; i++){
            JOCIP_ID.append(basicAdd.charAt(i));
        }
        page.putField("JOPIC_ID", JOCIP_ID);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args){
        Spider.create(new SymbolProcessor())
                .addUrl("http://jcip.cipsc.org.cn/CN/abstract/abstract3009.shtml")
                .run();
    }
}
