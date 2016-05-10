package com.pixalione.mailliageinterne.database.mongodb.DataMongoDb.MetricsModel;

import com.pixalione.mailliageinterne.database.mongodb.DataMongoDb.Page;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by AmYné on 29/04/2016.
 */
@Document(collection="PageMetrics")
public class PageMetrics{
    @Id
    private String id;
    private Date CurrentDate;
    @DBRef
    private Page page;
    private long nombreDeLienUniqueDePage;

    public PageMetrics(Date CurrentDate,Page page, long nombreDeLienUniqueDePage)
    {
        this.page = page;
        this.CurrentDate = CurrentDate;
        this.nombreDeLienUniqueDePage = nombreDeLienUniqueDePage;
    }

    public long getNombreDePageUnique() {
        return nombreDeLienUniqueDePage;
    }

    public void setNombreDePageUnique(long nombreDePageUnique) {
        nombreDeLienUniqueDePage = nombreDePageUnique;
    }

    @Override
    public String toString() {
        return "PageMetrics{" +
                "NombreDeLienUniqueDePage=" + nombreDeLienUniqueDePage +
                ", page=" + page +
                '}';
    }
}
