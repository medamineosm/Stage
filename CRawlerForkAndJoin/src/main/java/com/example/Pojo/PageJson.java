package com.example.Pojo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by OUASMINE Mohammed Amine on 11/05/2016.
 */
@Document(collection="WebPage")
public class PageJson {


    @Id
    private String url;
    private String Content;

    public PageJson() {
    }

    public PageJson(String url, String content) {
        this.url = url;
        Content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    @Override
    public String toString() {
        return "PageJson{" +
                "url='" + url + '\'' +
                ", Content='" + Content + '\'' +
                '}';
    }
}
