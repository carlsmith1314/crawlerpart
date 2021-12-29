package cn.edu.bistu.cs.bigdata.crawlerpart.baicdata;
import java.util.ArrayList;


public class BasicInformation {
    //文献名称
    String ArticleName;
    //作者名称
    ArrayList<String> AuthorName = new ArrayList<>();
    //文献摘要
    String Abstract;
    //关键词
    ArrayList<String> KeyWord = new ArrayList<>();

    public BasicInformation(){

    }

    public String getArticleName() {
        return ArticleName;
    }

    public ArrayList<String> getAuthorName() {
        return AuthorName;
    }

    public String getAbstract() {
        return Abstract;
    }

    public ArrayList<String> getKeyWord() {
        return KeyWord;
    }

    public void setArticleName(String articleName) {
        ArticleName = articleName;
    }

    public void setAuthorName(ArrayList<String> authorName) {
        AuthorName = authorName;
    }

    public void setAbstract(String anAbstract) {
        Abstract = anAbstract;
    }

    public void setKeyWord(ArrayList<String> keyWord) {
        KeyWord = keyWord;
    }
}