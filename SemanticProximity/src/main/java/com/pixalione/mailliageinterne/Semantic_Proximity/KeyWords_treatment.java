/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixalione.mailliageinterne.Semantic_Proximity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.annolab.tt4j.TreeTaggerException;

/**
 *
 * @author Fabrice
 */
public class KeyWords_treatment {

    public String KeyWords_treatment(String FileName, String path) throws IOException, TreeTaggerException {
        String fichier = path + FileName + ".txt\\";

        InputStream ips = new FileInputStream(fichier);
        InputStreamReader ipsr = new InputStreamReader(ips);
        BufferedReader br = new BufferedReader(ipsr);
        String ligne;
        ArrayList<String> arra = new ArrayList<String>();
        String name2 = path+ FileName + "_final.txt\\";
        File ff = new File(name2); // définir l'arborescence
        ff.createNewFile();
        FileWriter ffw = new FileWriter(ff);

        while ((ligne = br.readLine()) != null) {
            ligne=ligne.toLowerCase();
            String[] Words = ligne.split(" ");
            for (String word : Words) {
                arra.add(word);
//                ffw.write(word + "\r\n");
            }
        }
        String mot = arra.get(0);
        for (int i=1 ; i<arra.size() ; i++){
            for (int j=i ; j<arra.size() ; j++)
            if ((arra.get(j).matches(mot))|(arra.get(j).matches("à"))|(arra.get(j).matches("de")) |(arra.get(j).matches("en"))){
                arra.remove(j);
            }
            mot=arra.get(i);
        }
        for (int i=0 ; i<arra.size() ; i++){
        ffw.write(arra.get(i)+"\r\n");
        }        
        br.close();
        ffw.close();
        return name2;
    }

   
//    public static void main(String[] args) throws IOException, MalformedURLException, URISyntaxException, TreeTaggerException {
//        KeyWords_treatment a = new KeyWords_treatment("KeyWords");
//    }
}
