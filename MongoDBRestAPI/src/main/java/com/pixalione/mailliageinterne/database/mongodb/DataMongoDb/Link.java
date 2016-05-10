package com.pixalione.mailliageinterne.database.mongodb.DataMongoDb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URL;
import java.util.Set;

/**
 * Created by AmYné on 02/05/2016.
 */
@Document(collection = "Links")
public class Link {

    @Id
    private String  id;
    @Indexed
    private URL url;
    private String title;
    @Indexed
    private String anchor;
    private String host;
    private String path;
    private String source;
    private Set<String> positons;

    private int numberOfINLink;

    public Link(){}
    public Link(URL url)
    {
        this.url = url;
    }

    public Link(URL url,String title) {
        this.url = url;
        this.title = title;
    }

    public Link(URL url, String title, String anchor) {
        this.url = url;
        this.title = title;
        this.anchor = anchor;
    }

    public Link(URL url, String title, String anchor, String host, String path) {
        this.url = url;
        this.title = title;
        this.anchor = anchor;
        this.host = host;
        this.path = path;
    }
    public Link(URL url, String title, String anchor, String host, String path,Set<String> positions) {
        this.url = url;
        this.title = title;
        this.anchor = anchor;
        this.host = host;
        this.path = path;
        this.positons = positions;
    }
    public Link(URL url, String title, String anchor, String host, String path, String source,Set<String> positions, int numberOfINLink) {
        this.url = url;
        this.title = title;
        this.anchor = anchor;
        this.host = host;
        this.path = path;
        this.source = source;
        this.positons = positions;
        this.numberOfINLink = numberOfINLink;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        source = source;
    }

    public int getNumberOfINLink() {
        return numberOfINLink;
    }

    public void setNumberOfINLink(int numberOfINLink) {
        this.numberOfINLink = numberOfINLink;
    }


    @Override
    public String toString() {
        return "Link{" +
                "numberOfINLink=" + numberOfINLink +
                ", positons=" + positons +
                ", source='" + source + '\'' +
                ", path='" + path + '\'' +
                ", host='" + host + '\'' +
                ", anchor='" + anchor + '\'' +
                ", title='" + title + '\'' +
                ", url=" + url +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link link = (Link) o;

        if (!url.equals(link.url)) return false;
        return anchor != null ? anchor.equals(link.anchor) : link.anchor == null;

    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + (anchor != null ? anchor.hashCode() : 0);
        return result;
    }


}
