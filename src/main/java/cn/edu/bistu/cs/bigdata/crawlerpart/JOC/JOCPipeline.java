package cn.edu.bistu.cs.bigdata.crawlerpart.JOC;

import cn.edu.bistu.cs.bigdata.crawlerpart.JDBC;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import cn.edu.bistu.cs.bigdata.crawlerpart.baicdata.BasicInformation;

import java.util.ArrayList;


public class JOCPipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        String title = resultItems.get("title");
        ArrayList<String> name = new ArrayList<>(resultItems.get("name"));
        String abs = resultItems.get("abstract");
        ArrayList<String> keyword = new ArrayList<>(resultItems.get("keyword"));
        String DOI = resultItems.get("DOI");
        String joc = resultItems.get("kan_name");
        String joc_id = resultItems.get("joc_id");

        String sql =
                "INSERT INTO articleInformation VALUES('" + DOI + "','" + title + "','" + name + "','"
                        + abs + "','" + keyword +"', '" + joc + "','" + joc_id + "')";
        JDBC jdbc = new JDBC();
        jdbc.insert(sql);
    }
}