package com.pixalione.mailliageinterne.database.mongodb.DataMongoDb.MetricsModel;

import com.pixalione.mailliageinterne.database.mongodb.DataMongoDb.Link;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

/**
 * Created by OUASMINE Mohammed Amine on 09/05/2016.
 */
@Document(collection="LinkMetrics")
public class LinkMetrics {

    @Id
    private String id;
    private Date currentDate;
    @DBRef
    private Link link;
    private int anchorLength;
    private Set<String> keyWords;
    private long AnchorText_SemanticProximity;
    private long AnchorDestinationPage_SemanticProximity;
    public LinkMetrics(){}

    public LinkMetrics(Date currentDate, Link link, int anchorLength, Set<String> keyWords) {
        this.currentDate = currentDate;
        this.link = link;
        this.anchorLength = anchorLength;
        this.keyWords = keyWords;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        currentDate = currentDate;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public int getAnchorLength() {
        return anchorLength;
    }

    public void setAnchorLength(int anchorLength) {
        anchorLength = anchorLength;
    }

    public Set<String> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(Set<String> keyWords) {
        keyWords = keyWords;
    }

    public long getAnchorText_SemanticProximity() {
        return AnchorText_SemanticProximity;
    }

    public void setAnchorText_SemanticProximity(long anchorText_SemanticProximity) {
        AnchorText_SemanticProximity = anchorText_SemanticProximity;
    }

    public long getAnchorDestinationPage_SemanticProximity() {
        return AnchorDestinationPage_SemanticProximity;
    }

    public void setAnchorDestinationPage_SemanticProximity(long anchorDestinationPage_SemanticProximity) {
        AnchorDestinationPage_SemanticProximity = anchorDestinationPage_SemanticProximity;
    }

    @Override
    public String toString() {
        return "LinkMetrics{" +
                "link=" + link +
                ", AnchorLength=" + anchorLength +
                ", KeyWords=" + keyWords +
                ", AnchorText_SemanticProximity=" + AnchorText_SemanticProximity +
                ", AnchorDestinationPage_SemanticProximity=" + AnchorDestinationPage_SemanticProximity +
                '}';
    }
}
