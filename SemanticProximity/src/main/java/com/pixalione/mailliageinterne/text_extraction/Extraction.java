/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixalione.mailliageinterne.text_extraction;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 *
 * @author Fabrice Modified By OUASMINE Mohammed Amine
 */
public class Extraction {

    public static void testExtraction(String outputFileName,String PathFile,String url) throws IOException, URISyntaxException {
        // TODO code application logic here
        Text_Extraction_ text = new Text_Extraction_(url, outputFileName, PathFile);
    }
}
