/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixalione.mailliageinterne.Semantic_Proximity;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author Fabrice
 */
public class Language_Detection {

    /**
     * @param args the command line arguments
     */
    public boolean Language_Detection(String NameFile1, String namefile2,String path) throws LangDetectException, FileNotFoundException, IOException {
//        // TODO code application logic here
        String fichier = (path+NameFile1+".txt");
        String fichier2 = ( path+namefile2+".txt");
        

        String profileDirectory = "profiles";
        DetectorFactory.loadProfile(profileDirectory);
        Detector detector = DetectorFactory.create();

        InputStream ips = new FileInputStream(fichier);
        InputStreamReader ipsr = new InputStreamReader(ips);
        BufferedReader br = new BufferedReader(ipsr);
        String ligne;
        while ((ligne = br.readLine()) != null) {
            detector.append(ligne);
        }
        ArrayList<Language> langlist = detector.getProbabilities();
        String Lang = langlist.toString();
        Lang = Lang.substring(1, 3);
        System.out.println(NameFile1+" page language is : "+Lang);
        
        InputStream ips2 = new FileInputStream(fichier2);
        InputStreamReader ipsr2 = new InputStreamReader(ips2);
        BufferedReader br2 = new BufferedReader(ipsr2);
        String ligne2;
        while ((ligne2 = br2.readLine()) != null) {
            detector.append(ligne2);
        }
        
        ArrayList<Language> langlist2 = detector.getProbabilities();
        String Lang2 = langlist.toString();
        Lang2 = Lang2.substring(1, 3);
        System.out.println(namefile2+" language is : "+Lang2);
        
        if (Lang.equals(Lang2)){
        return true;
        }else{
            return false;
    }
    
    }

//    public static void main(String[] args) throws LangDetectException, FileNotFoundException, IOException {
//
//        Language_Detection lan = new Language_Detection();
//        boolean Lang1 = lan.Language_Detection("Backlink","TaggedPage");
//
//        Language_Detection lan2 = new Language_Detection();
////        String Lang2 = lan2.Language_Detection();
////        System.out.println(Lang1 + "  " + Lang2);

//    }

}
