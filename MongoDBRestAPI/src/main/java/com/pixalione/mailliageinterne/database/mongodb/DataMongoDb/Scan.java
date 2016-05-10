package com.pixalione.mailliageinterne.database.mongodb.DataMongoDb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by AmYné on 29/04/2016.
 */
@Document(collection = "Scans")
public class Scan {

    @Id
    private String id;
    @Indexed
    private Date date;
    @Indexed
    private String website;
    @DBRef
    private List<Page> pageList;

    public Scan(){}
    public Scan(Date date){
        this.date = new Date();
        this.pageList = new ArrayList<>();
    }

    public Date getDate() {
        return date;
    }

    public List<Page> getPageList() {
        return pageList;
    }

    public String getId() {
        return id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setPageList(List<Page> pageList) {
        this.pageList = pageList;
    }

    public void addPageToList(Page page) {
        this.pageList.add(page);
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
