/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixalione.mailliageinterne.Semantic_Proximity;

import com.cybozu.labs.langdetect.LangDetectException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import com.pixalione.mailliageinterne.text_extraction.Text_Extraction_;
import org.annolab.tt4j.TreeTaggerException;

/**
 *
 * @author Fabrice
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, URISyntaxException, LangDetectException, TreeTaggerException {
        // TODO code application logic here
        String Url_Backlink = "http://www.led-fr.net/legrand-myhome-led.htm";
        String Url_TargetPage = "http://www.jardiland.com/";
        String AnchorText = "legrand-celiane-electricite-eclairage-quimper";
        String path = "Test\\";

        String name1 = "Backlink";
        String name2 = "TargetPage";

        Text_Extraction_ text_Backlink = new Text_Extraction_(Url_Backlink, name1, path);
        Text_Extraction_ text_TargetPage = new Text_Extraction_(Url_TargetPage, name2, path);

        Language_Detection langue = new Language_Detection();
        if (langue.Language_Detection(name1, name2, path)) {
            StopWords_Elimination a1 = new StopWords_Elimination();
            int compt1 = a1.StopWords_Elimination(name1, path);
            StopWords_Elimination a2 = new StopWords_Elimination();
            int compt2 = a2.StopWords_Elimination(name2, path);
            AnchorText_Type Ty = new AnchorText_Type();
            String AnchorText_Type = Ty.AnchorText_Type(AnchorText);
            if ((compt1 > 150) && (compt2 > 150)) {

                if (AnchorText_Type.matches("Brand") | AnchorText_Type.matches("Neutral Word")| AnchorText_Type.matches("Image without alt")) {
                    System.out.println("     ");
                    System.out.println("********* we apply Keywords method *****************");
                    System.out.println("     ");
                    Lemmatisation lem1 = new Lemmatisation(name1 + "_WSW", path);
                    String nameFile = "KeyWords";
                    KeyWords_treatment key = new KeyWords_treatment();
                    key.KeyWords_treatment(nameFile, path);
                    Lemmatisation lem2 = new Lemmatisation(nameFile + "_final", path);
                    KeyWords_treatment keyw = new KeyWords_treatment();
                    keyw.KeyWords_treatment("Lemm_" + nameFile + "_final", path);
                    KeyWords_Method Result = new KeyWords_Method();
                    Result.KeyWords_Method(("Lemm_" + name1 + "_WSW"), ("Lemm_" + nameFile + "_final_final"), path);

                } else {
                    System.out.println("     ");
                    System.out.println("********* we apply Semantic Proximity method *****************");
                    System.out.println("     ");
                    if (AnchorText_Type.matches("No Brand") | AnchorText_Type.matches("Hybrid")) {
                        SemanticProximity_Method Sem = new SemanticProximity_Method(name1 + "_WSW", name2 + "_WSW", path);
                    }
                }
            } else {
                System.out.println("     ");
                System.out.println("********* we apply Keywords method *****************");
                System.out.println("     ");
                Lemmatisation lem1 = new Lemmatisation(name1 + "_WSW", path);
                String nameFile = "KeyWords";
                KeyWords_treatment key = new KeyWords_treatment();
                key.KeyWords_treatment(nameFile, path);
                Lemmatisation lem2 = new Lemmatisation(nameFile + "_final", path);
                KeyWords_Method Result = new KeyWords_Method();
                Result.KeyWords_Method(("Lemm_" + name1 + "_WSW"), ("Lemm_" + nameFile + "_final"), path);
            }

        } else {
            System.out.println("We need to translate");
        }
        ;

//        }
    }

}
