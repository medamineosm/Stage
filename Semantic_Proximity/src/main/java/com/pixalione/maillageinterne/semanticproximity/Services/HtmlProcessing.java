package com.pixalione.maillageinterne.semanticproximity.Services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by OUASMINE Mohammed Amine on 09/05/2016.
 */
public class HtmlProcessing {
    public static String getHtmlText(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String body = doc.head().text();
        String bodyChildren = doc.body().children().text();

        return bodyChildren;
    }


}
