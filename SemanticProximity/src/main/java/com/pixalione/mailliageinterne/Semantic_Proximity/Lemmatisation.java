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
import org.annolab.tt4j.TreeTaggerException;
import static java.util.Arrays.asList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerWrapper;

/**
 *
 * @author Ahlam
 */
public class Lemmatisation {

    public Lemmatisation(String FileName, String path) throws IOException, TreeTaggerException {
        String fichier = path + FileName + ".txt";
        System.setProperty("treetagger.home", "C:\\TreeTagger");
        TreeTaggerWrapper tt = new TreeTaggerWrapper<String>();
        File ff = new File(path+"/Lemm_" + FileName + ".txt");
        ff.createNewFile();
        FileWriter ffw = new FileWriter(ff);

        try {
            tt.setModel("data/french.par");
            tt.setHandler(new TokenHandler<String>() {
                public void token(String token, String pos, String lemma) {
                    if (pos.equals("NUM")) {
//                        System.out.println(token);
                        try {
                            ffw.write(token + " ");
                        } catch (IOException ex) {
                            Logger.getLogger(Lemmatisation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
//                        System.out.println(lemma);
                        try {
                            lemma = lemma.replaceAll("à", "a");
                            lemma = lemma.replaceAll("â", "a");
                            lemma = lemma.replaceAll("á", "a");
                            lemma = lemma.replaceAll("â", "a");
                            lemma = lemma.replaceAll("ã", "a");
                            lemma = lemma.replaceAll("ä", "a");
                            lemma = lemma.replaceAll("å", "a");
                            lemma = lemma.replaceAll("â", "a");
                            lemma = lemma.replaceAll("æ", "ae");
                            lemma = lemma.replaceAll("ç", "c");
                            lemma = lemma.replaceAll("é", "e");
                            lemma = lemma.replaceAll("è", "e");
                            lemma = lemma.replaceAll("ê", "e");
                            lemma = lemma.replaceAll("ë", "e");
                            lemma = lemma.replaceAll("ì", "i");
                            lemma = lemma.replaceAll("í", "i");
                            lemma = lemma.replaceAll("î", "i");
                            lemma = lemma.replaceAll("ï", "i");
                            lemma = lemma.replaceAll("ð", "o");
                            lemma = lemma.replaceAll("ò", "o");
                            lemma = lemma.replaceAll("ñ", "n");
                            lemma = lemma.replaceAll("œ", "oe");
                            lemma = lemma.replaceAll("ô", "o");
                            lemma = lemma.replaceAll("ó", "o");
                            lemma = lemma.replaceAll("õ", "o");
                            lemma = lemma.replaceAll("ö", "o");
                            lemma = lemma.replaceAll("ù", "u");
                            lemma = lemma.replaceAll("ú", "u");
                            lemma = lemma.replaceAll("û", "u");
                            lemma = lemma.replaceAll("ü", "u");
                            lemma = lemma.replaceAll("ô", "o");
                            lemma = lemma.replaceAll("ô", "o");
                            
                            
                            
                            ffw.write(lemma + " ");
                        } catch (IOException ex) {
                            Logger.getLogger(Lemmatisation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            InputStream ips = new FileInputStream(fichier);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String ligne;
            while ((ligne = br.readLine()) != null) {
                String[] Words = ligne.split(" ");
                for (String word : Words) {
                    tt.process(asList(new String[]{word}));

                }
            }

//            tt.process(asList(new String[]{"habillé", "mange", "bicyclettes", "belle", "habillée", "."}));
        } finally {
            tt.destroy();
        }
        ffw.close();
    }

//    public static void main(String[] args) throws IOException, TreeTaggerException {
//        Lemmatisation lem = new Lemmatisation("tesst");
//    }
}
