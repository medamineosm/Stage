package com.pixalione.maillageinterne.semanticproximity;

import com.pixalione.maillageinterne.semanticproximity.Services.HtmlProcessing;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by OUASMINE Mohammed Amine on 09/05/2016.
 */
public class Extraction {

    public static void main(String[] args) throws IOException, URISyntaxException {
        /*
        String name="Extracted_Text";
        String path =  "C:\\Users\\mpoko\\Desktop\\stage";
        Text_Extraction_ text = new Text_Extraction_("http://www.jardiland.com/", name, path);
        */
        String url = "http://127.0.0.1/test/";
        System.out.println(HtmlProcessing.getHtmlText(url));
    }
}
