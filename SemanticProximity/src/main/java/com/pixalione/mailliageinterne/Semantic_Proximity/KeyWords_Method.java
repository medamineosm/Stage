/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixalione.mailliageinterne.Semantic_Proximity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Ahlam
 */
public class KeyWords_Method {
    
        public static Double KeyWords_Method(String fileName, String KeyWordsFile, String path) throws IOException {
        Double comp1 = 0.0;
        Double comp2 = 0.0;
        boolean exist;
        String fichier1 = (path+fileName+".txt");
        String fichier2 = (path+KeyWordsFile+".txt");
        Process procCourant;	        
        try {

            InputStream ips2 = new FileInputStream(fichier2);
            InputStreamReader ipsr2 = new InputStreamReader(ips2);
            BufferedReader br2 = new BufferedReader(ipsr2);

            String ligne1, ligne2;

            while ((ligne2 = br2.readLine()) != null) {
                ligne2 = ligne2.toLowerCase();
                String[] Words2 = ligne2.split(" ");

                for (String word2 : Words2) {
                    InputStream ips1 = new FileInputStream(fichier1);
                    InputStreamReader ipsr1 = new InputStreamReader(ips1);
                    BufferedReader br1 = new BufferedReader(ipsr1);

                    comp2 = 0.0;
                    while ((ligne1 = br1.readLine()) != null) {
                        ligne1 = ligne1.toLowerCase();
                        String[] Words1 = ligne1.split(" ");

                        for (String word1 : Words1) {
                            comp2++;
                            if (word1.matches(word2)) {
                                comp1++;

                            }
                        }
                    }
                    br1.close();
                }
            }

            br2.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        comp2 = comp2 - 1;
//        System.out.println("*********** Le nombre de keywords trouvés est : " + comp1);
//        System.out.println("*********** Le nombre de mot dans le texte est : " + comp2);
        
        Double comp = (comp1 / comp2) * 100;
        System.out.println("the percentage of keywords is: : " + comp + "%");
        System.out.println("  ");

        return comp;
    }
}
