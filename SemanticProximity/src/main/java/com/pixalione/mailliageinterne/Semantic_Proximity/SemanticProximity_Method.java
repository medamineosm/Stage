/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixalione.mailliageinterne.Semantic_Proximity;

import com.pixalione.mailliageinterne.PixTagger.PixTaggerFrench;
import com.pixalione.mailliageinterne.fr.pixalione.frenchdictionnary.DictionnaireSyn;

import java.util.ArrayList;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Fabrice
 */
public class SemanticProximity_Method{

    /**
     * @param args the command line arguments
     */
    public static final List<String> category = new ArrayList(Arrays.asList("en cours", "en cour", "aujour", "hui", "a", "alors", "jusqu'au", "bout", "jusqu'au", "jusqu'a", "jusque", "jusqu", "aucuns", "aussi", "autre", "au", "avant", "avoir", "bon", "bout", "car", "cela", "ces", "ceux", "ce", "chaque", "ci", "comment", "comme", "dans", "dan", "des", "du", "dedans", "dehors", "depuis", "deux", "devrait", "doit", "donc", "dos", "droite", "à", "debut", "de", "elles", "elle", "encore", "en", "essai", "est", "et", "eu", "faites", "fait", "fois", "foi", "font", "force", "haut", "hors", "ici", "ils", "il", "je", "juste", "la", "les", "leur", "le", "maintenant", "mais", "ma", "mes", "mine", "moins", "mon", "mot", "meme", "ni", "nommes", "notre", "nous", "nouveaux", "ou", "parce", "parole", "par", "pas", "personnes", "peut", "peu", "piece", "plupart", "pourquoi", "pour", "quand", "quelles", "quelle", "quels", "quel", "qui", "que", "sans", "sa", "ses", "seulement", "sien", "si", "sont", "son", "sous", "sou", "soyez", "sujet", "sur", "tandis", "ta", "tellement", "tels", "tes", "ton", "tous", "tout", "trop", "tres", "tu", "valeur", "voie", "voient", "vont", "votre", "vous", "vu", "va-t-il", "ca", "etaient", "etat", "etions", "ete", "etre", "aux", "eux", "lui", "me", "moi", "ne", "nos", "on", "qu", "se", "te", "toi", "une", "un", "vos", "c", "d", "j", "l", "a", "n", "t", "y", "x", "etees", "etee", "fussiez", "fussent", "ayant", "eue", "eussiez", "eussent", "ceci", "cette", "cet", "leurs", "soi", "d'une", "d'un", "l'une", "l'un", "qu'est-ce", "qu'une", "qu'un", "pendant", "jusque", "eur", "euro", "flash", "grace", "vers", "ver", "besoin", "merci", "devis", "bonjour", "present", "en cas", "cas", "rendez-vous", "tenez-vous", "montant"));

     public SemanticProximity_Method (String namefile1, String namefile2 , String path) throws IOException, MalformedURLException, URISyntaxException {
            ArrayList<String> NameFile1 = new ArrayList<String>();
            String mot1 = path + namefile1 + ".txt\\";
            NameFile1.add(mot1);
            PixTaggerFrench tagger1 = new PixTaggerFrench();
            ChampSyntagmatique syn1 = new ChampSyntagmatique(NameFile1, tagger1);

            ArrayList<String> NameFile2 = new ArrayList<String>();
            String mot2 =  path + namefile2 + ".txt\\";
            NameFile2.add(mot2);

            PixTaggerFrench tagger2 = new PixTaggerFrench();
            ChampSyntagmatique syn2 = new ChampSyntagmatique(NameFile2, tagger2);

            String nameFile = "dic/Dico_wTLexUnit_wTsyn.txt";
            PixTaggerFrench Tagger = new PixTaggerFrench();

            DictionnaireSyn dico = new DictionnaireSyn(nameFile, Tagger);

            ComparaisonSyntagmeGrpMotsClefs a = new ComparaisonSyntagmeGrpMotsClefs(syn1, syn2, dico);

        }
    
    
//    public static int readReplace(String fileName, String name) throws IOException {
//        int comp = 0;
//        int comp1 = 0;
//        int comp2 = 0;
//        boolean exist;
//        String fichier = fileName;
//
//        //lecture du fichier texte	
//        try {
//            InputStream ips = new FileInputStream(fichier);
//
//            InputStreamReader ipsr = new InputStreamReader(ips);
//
//            BufferedReader br = new BufferedReader(ipsr);
//            String ligne;
//            String newName = "C:\\Users\\Fabrice\\Desktop\\Proximité sémantique\\AnchorFindingTT4J V3\\Resultat_Proximite_semantique\\" + name + "_WSW.txt\\";
//            System.out.println(newName);
//            File ff = new File(newName); // définir l'arborescence
//            ff.createNewFile();
//            FileWriter ffw = new FileWriter(ff);
//
//            while ((ligne = br.readLine()) != null) {
//                ligne = ligne.toLowerCase();
//                ligne = ligne.replaceAll("\\)", "");
//                ligne = ligne.replaceAll("\\(", "");
//                ligne = ligne.replaceAll("\\]", "");
//                ligne = ligne.replaceAll("\\[", "");
//                ligne = ligne.replaceAll("\\:", "");
//                ligne = ligne.replaceAll("\\/", "");
//                ligne = ligne.replaceAll("/", "");
//                ligne = ligne.replaceAll("»", "");
//                ligne = ligne.replaceAll("«", "");
//                ligne = ligne.replaceAll("'", " ");
//                ligne = ligne.replaceAll("\\.", "");
//                ligne = ligne.replaceAll("\\°", "");
//                ligne = ligne.replaceAll("1", "");
//                ligne = ligne.replaceAll("2", "");
//                ligne = ligne.replaceAll("3", "");
//                ligne = ligne.replaceAll("4", "");
//                ligne = ligne.replaceAll("5", "");
//                ligne = ligne.replaceAll("6", "");
//                ligne = ligne.replaceAll("7", "");
//                ligne = ligne.replaceAll("8", "");
//                ligne = ligne.replaceAll("9", "");
//                ligne = ligne.replaceAll("0", "");
//                ligne = ligne.replaceAll("$", "");
//                ligne = ligne.replaceAll("£", "");
//                ligne = ligne.replaceAll("€", "");
//                ligne = ligne.replaceAll("!", "");
//                ligne = ligne.replaceAll("/", "");
//
//                String[] Words = ligne.split(" ");
//                for (String word : Words) {
//                    comp2++;
//                    for (String c1 : category) {
//
//                        if (!word.matches(c1)) {
//                            ffw.write(word + " ");  // écrire une ligne dans le fichier resultat.txt
//                            // System.out.print(word + " ");
//                        } else {
//                            comp1++;
//                        }
//
//                    }
//
//                }
//            }
//            br.close();
//            ffw.close(); // fermer le fichier à la fin des traitements
//
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }
//        comp = comp2 - comp1;
//        if (comp > 150) {
//
//            System.out.println("Le nombre de mots dans le texte avec les stop words est: " + comp2);
//            System.out.println("Le nombre des stop words dans le texte est: " + comp1);
//            System.out.println("Le nombre de mots dans le texte sans les stop words est: " + comp);
//            System.out.println("   ");
//        } else {
//            System.out.println("    ");
//            System.out.println("Le nombre de mots dans "+name+" est : "+comp+" inférieur ou égale à 150");
//            System.out.println("    ");
//        }
//
//        return comp;
//    }


   
  //To change body of generated methods, choose Tools | Templates.
}
