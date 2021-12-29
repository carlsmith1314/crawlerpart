package cn.edu.bistu.cs.bigdata.crawlerpart;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import cn.edu.bistu.cs.bigdata.crawlerpart.baicdata.BasicInformation;


public class ArticlePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        String title = resultItems.get("title");
        String name = resultItems.get("name");
        String abs = resultItems.get("abstract");
        String keyword = resultItems.get("keyword");


    }
}
