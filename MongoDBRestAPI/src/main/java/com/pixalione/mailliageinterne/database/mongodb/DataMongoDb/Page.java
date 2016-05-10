package com.pixalione.mailliageinterne.database.mongodb.DataMongoDb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by AmYné on 29/04/2016.
 */
@Document(collection = "Pages")
public class Page {

    @Id
    private String id;
    @Indexed
    private String urlPage;
    @DBRef
    private List<Link> list_liens;

    public Page(String urlPage, List<Link> list_liens) {
        this.urlPage = urlPage;
        this.list_liens = list_liens;
    }

    public String getUrlPage() {
        return urlPage;
    }

    public void setUrlPage(String urlPage) {
        urlPage = urlPage;
    }

    public List<Link> getList_liens() {
        return list_liens;
    }

    public void setList_liens(List<Link> list_liens) {
        this.list_liens = list_liens;
    }

    @Override
    public String toString() {
        return "Page{" +
                "id='" + id + '\'' +
                ", UrlPage='" + urlPage + '\'' +
                ", list_liens=" + list_liens +
                '}';
    }
}
