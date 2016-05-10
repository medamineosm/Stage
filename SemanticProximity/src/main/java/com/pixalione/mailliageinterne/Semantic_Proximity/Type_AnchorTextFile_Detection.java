/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixalione.mailliageinterne.Semantic_Proximity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;

/**
 *
 * @author Ahlam
 */
public class Type_AnchorTextFile_Detection {

    public static final List<String> BrandList = new ArrayList(Arrays.asList("legrand", "le grand"));
    public static final List<String> NeutralWords = new ArrayList(Arrays.asList("cliquez-ici", "voir le site", "lien"));

    public String Type_AnchorTextFile_Detection(String nameFile) throws FileNotFoundException, IOException {
        String fichier = "C:\\Users\\Fabrice\\Desktop\\KeyWords_method\\Test\\" + nameFile + ".txt";
//        String fichier2 = "C:\\Users\\Fabrice\\Desktop\\" + nameFile2 + ".xls";
        String type = "";
        InputStream ips = new FileInputStream(fichier);
        InputStreamReader ipsr = new InputStreamReader(ips);
        BufferedReader br = new BufferedReader(ipsr);
        String ligne;

        while ((ligne = br.readLine()) != null) {
            type = "";
            String word1 = ligne;
            ligne = ligne.toLowerCase();

            ligne = ligne.replaceAll("[\\`\\'\\.\\,\\%\\/\\\\&\\[\\!\\#\\+\\£\\$\\*\\§\\:\\@\\¨\\_\\^\\(\\/\\)\\{\\}\\{\\]\\~\\¤\\°\\=\\?\\;\\<\\>\\»\\«\\€\\|\\/]", "");
            ligne = ligne.trim();

//            Workbook workbook = null;
//            try {
//
//                workbook = Workbook.getWorkbook(new File(fichier2)); /* Récupération du classeur Excel (en lecture) */
//                Sheet sheet = workbook.getSheet(0); /* Un fichier excel est composé de plusieurs feuilles, on y accède de la manière suivante*/
//                Cell a1 = sheet.getCell(0, 0); /* On accède aux cellules avec la méthode getCell(indiceColonne, indiceLigne) */
//                Cell c5 = sheet.getCell("C5"); /* On peut également le faire avec getCell(nomCellule) */
//                String contenuA1 = a1.getContents(); /* On peut récupérer le contenu d'une cellule en utilisant la méthode getContents() */
//                String contenuC5 = c5.getContents();
//                System.out.println(contenuA1);
//                System.out.println(contenuC5);
//            } catch (BiffException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            ligne = ligne.replaceAll("!", "");
//            ligne = ligne.replaceAll("#", "");
//            ligne = ligne.replaceAll("$", "");
//            ligne = ligne.replaceAll("%", "");
//            ligne = ligne.replaceAll("&", "");
//            ligne = ligne.replaceAll("'", "");
//            ligne = ligne.replaceAll("\\*", "");
//            ligne = ligne.replaceAll("\\+", "");
//            ligne = ligne.replaceAll(",", "");
//            ligne = ligne.replaceAll("-", "");
//            ligne = ligne.replaceAll("\\.", "");
//            ligne = ligne.replaceAll("/", "");
//            ligne = ligne.replaceAll(":", "");
//            ligne = ligne.replaceAll(";", "");
//            ligne = ligne.replaceAll("=", "");
//            ligne = ligne.replaceAll("\\?", "");
//            ligne = ligne.replaceAll("@", "");
//            ligne = ligne.replaceAll("^", "");
//            ligne = ligne.replaceAll("_", "");
//            ligne = ligne.replaceAll("`", "");
//            ligne = ligne.replaceAll("\\{", "");
//            ligne = ligne.replaceAll("\\}", "");
//            ligne = ligne.replaceAll("~", "");
//            ligne = ligne.replaceAll("\\)", "");
//            ligne = ligne.replaceAll("\\(", "");
//            ligne = ligne.replaceAll("\\]", "");
//            ligne = ligne.replaceAll("\\[", "");
//            ligne = ligne.replaceAll("\\:", "");
//            ligne = ligne.replaceAll("\\/", "");
//            ligne = ligne.replaceAll("/", "");
//            ligne = ligne.replaceAll("\\°", "");
//            ligne = ligne.replaceAll("<", "");
//            ligne = ligne.replaceAll(">", "");
//            ligne = ligne.replaceAll("»", "");
//            ligne = ligne.replaceAll("«", "");
//            ligne = ligne.replaceAll("'", " ");
//            ligne = ligne.replaceAll("\\.", "");
//            ligne = ligne.replaceAll("$", "");
//            ligne = ligne.replaceAll("£", "");
//            ligne = ligne.replaceAll("€", "");
//            ligne = ligne.replaceAll("!", "");
//            ligne = ligne.replaceAll("xa7", "");
            String word = ligne;

            for (String c1 : BrandList) {

                if (word.matches(c1)) {
                    type = "Brand";
                    System.out.println(word1 + " ===>   " + type);
                    break;
                } else {
                    if (word.contains(c1)) {
                        type = "Hybrid";
                        System.out.println(word1 + " ===>   " + type);
                        break;
                    }
                }
            }
            for (String c2 : NeutralWords) {

                if (word.matches(c2)) {
                    type = "Neutral Word";
                    System.out.println(word1 + " ===>   " + type);
                    break;
                }
            }

            if (type.equalsIgnoreCase("")) {
                type = "Non Brand";
                System.out.println(word1 + " ===>   " + type);
            }
//            workbook.close();
        }
        br.close();
        return type;
    }

//    public static void main(String[] args) throws IOException{
//        // TODO code application logic here
//        String nameFile = "anchortext";
//        String Excel = "test";
//        Type_AnchorText_Detection a = new Type_AnchorText_Detection(nameFile, Excel);
//
//    }
}
