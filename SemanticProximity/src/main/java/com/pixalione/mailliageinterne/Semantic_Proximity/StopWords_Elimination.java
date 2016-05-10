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
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ahlam
 */
public class StopWords_Elimination {

    /**
     * @param args the command line arguments
     */
    public static final List<String> StopWordsList = new ArrayList(Arrays.asList("en cours", "en cour", "aujour", "hui", "a", "alors", "jusqu'au", "bout", "jusqu'au", "jusqu'a", "jusque", "jusqu", "aucuns", "aussi", "autre", "au", "avant", "avoir", "bon", "bout", "car", "cela", "ces", "ceux", "ce", "chaque", "ci", "comment", "comme", "dans", "dan", "des", "du", "dedans", "dehors", "depuis", "deux", "devrait", "doit", "donc", "dos", "droite", "à", "debut", "de", "elles", "elle", "encore", "en", "essai", "est", "et", "eu", "faites", "fait", "fois", "foi", "font", "force", "haut", "hors", "ici", "ils", "il", "je", "juste", "la", "les", "leur", "le", "maintenant", "mais", "ma", "mes", "mine", "moins", "mon", "mot", "meme", "ni", "nommes", "notre", "nous", "nouveaux", "ou", "parce", "parole", "par", "pas", "personnes", "peut", "peu", "piece", "plupart", "pourquoi", "pour", "quand", "quelles", "quelle", "quels", "quel", "qui", "que", "sans", "sa", "ses", "seulement", "sien", "si", "sont", "son", "sous", "sou", "soyez", "sujet", "sur", "tandis", "ta", "tellement", "tels", "tes", "ton", "tous", "tout", "trop", "tres", "tu", "valeur", "voie", "voient", "vont", "votre", "vous", "vu", "va-t-il", "ca", "etaient", "etat", "etions", "ete", "etre", "aux", "eux", "lui", "me", "moi", "ne", "nos", "on", "qu", "se", "te", "toi", "une", "un", "vos", "c", "d", "j", "l", "a", "n", "t", "y", "x", "etees", "etee", "fussiez", "fussent", "ayant", "eue", "eussiez", "eussent", "ceci", "cette", "cet", "leurs", "soi", "d'une", "d'un", "l'une", "l'un", "qu'est-ce", "qu'une", "qu'un", "pendant", "jusque", "eur", "euro", "flash", "grace", "vers", "ver", "besoin", "merci", "devis", "bonjour", "present", "en cas", "cas", "rendez-vous", "tenez-vous", "montant"));

    public static int StopWords_Elimination(String name, String path) throws IOException {
        int comp1 = 0;
        int comp2 = 0;
        boolean exist;
        String fichier = (path+name+".txt");

        //lecture du fichier texte	
        try {
            InputStream ips = new FileInputStream(fichier);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String ligne;
            File ff = new File(path + name + "_WSW.txt\\"); // définir l'arborescence
            ff.createNewFile();
            FileWriter ffw = new FileWriter(ff);

            while ((ligne = br.readLine()) != null) {
                ligne = ligne.toLowerCase();
                ligne = ligne.replaceAll("\\)", "");
                ligne = ligne.replaceAll("\\(", "");
                ligne = ligne.replaceAll("\\]", "");
                ligne = ligne.replaceAll("\\[", "");
                ligne = ligne.replaceAll("\\:", "");
                ligne = ligne.replaceAll("\\/", "");
                ligne = ligne.replaceAll("/", "");
                ligne = ligne.replaceAll("\\°", "");

                ligne = ligne.replaceAll("»", "");
                ligne = ligne.replaceAll("«", "");
                ligne = ligne.replaceAll("'", " ");
                ligne = ligne.replaceAll("\\.", "");
                ligne = ligne.replaceAll("1", "");
                ligne = ligne.replaceAll("2", "");
                ligne = ligne.replaceAll("3", "");
                ligne = ligne.replaceAll("4", "");
                ligne = ligne.replaceAll("5", "");
                ligne = ligne.replaceAll("6", "");
                ligne = ligne.replaceAll("7", "");
                ligne = ligne.replaceAll("8", "");
                ligne = ligne.replaceAll("9", "");
                ligne = ligne.replaceAll("0", "");
                ligne = ligne.replaceAll("$", "");
                ligne = ligne.replaceAll("£", "");
                ligne = ligne.replaceAll("€", "");
                ligne = ligne.replaceAll("!", "");

                String[] Words = ligne.split(" ");
                for (String word : Words) {
                    comp2++;
                    for (String c1 : StopWordsList) {

                        if (word.matches(c1)) {
                            word = ("");
                            comp1++;
                        }
                        exist = false;
                    }

                    ffw.write(word + " ");  // écrire une ligne dans le fichier resultat.txt
//                     System.out.print(word + " ");
                }

            }

            br.close();
            ffw.close(); // fermer le fichier à la fin des traitements
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        int comp = 0;
        comp = comp2 - comp1;
//        if (comp > 150) {
//
//            System.out.println("Le nombre de mots dans le texte avec les stop words est: " + comp2);
//            System.out.println("Le nombre des stop words dans le texte est: " + comp1);
            System.out.println("The number of words without stop words in "+name+" est : " + comp);
//            System.out.println("   ");
//        } else {
//            System.out.println("    ");
//            System.out.println("Le nombre de mots dans " + name + " est : " + comp + " inférieur ou égale à 150");
//            System.out.println("   ");
//        }
        return comp;
    }

//    public static void main(String[] args) throws IOException, MalformedURLException, URISyntaxException {
//        // TODO code application logic here
//
//        String name = "Backlink";
//        Text_Extraction_ text = new Text_Extraction_("http://www.sixt.fr/plusieurs-renseignements/mentions-legales/", name);
//
//        StopWords_Elimination a = new StopWords_Elimination();
//        int b = a.StopWords_Elimination("C:\\Users\\Fabrice\\Desktop\\Proximité sémantique\\AnchorFindingTT4J V3\\Resultat_Proximite_semantique\\" + name + ".txt", name);
//    }
}
