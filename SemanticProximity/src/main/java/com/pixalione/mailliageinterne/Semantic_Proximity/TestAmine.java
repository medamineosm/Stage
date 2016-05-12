package com.pixalione.mailliageinterne.Semantic_Proximity;

import com.cybozu.labs.langdetect.LangDetectException;
import com.pixalione.mailliageinterne.text_extraction.Text_Extraction_;
import com.sun.org.apache.regexp.internal.RE;
import org.annolab.tt4j.TreeTaggerException;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by OUASMINE Mohammed Amine on 11/05/2016.
 */
public class TestAmine {
    public static void main(String[] args) throws IOException, URISyntaxException, LangDetectException, TreeTaggerException {
        String page_1 = "http://www.jardiland.com/";
        String page_2 = "http://www.novasep.com/";
        String page_3 = "http://www.kooora.com/";

        String page_1_name = "jardiland";
        String page_2_name = "novasep";
        String page_3_name = "kooora";

        String Repository_Path = "Output-dir\\";

        Text_Extraction_ text_jardiland = new Text_Extraction_(page_1, page_1_name, Repository_Path);
        Text_Extraction_ text_novasep = new Text_Extraction_(page_2, page_2_name, Repository_Path);
        Text_Extraction_ text_koora = new Text_Extraction_(page_3, page_3_name, Repository_Path);

        Language_Detection langue = new Language_Detection();
        if (langue.Language_Detection(page_1_name, page_2_name, Repository_Path)) {
            StopWords_Elimination a1 = new StopWords_Elimination();
            int compt1 = a1.StopWords_Elimination(page_1_name, Repository_Path);
            StopWords_Elimination a2 = new StopWords_Elimination();
            int compt2 = a2.StopWords_Elimination(page_2_name, Repository_Path);
            System.out.println("     ");
            System.out.println("********* we apply Keywords method *****************");
            System.out.println("     ");
            Lemmatisation lem1 = new Lemmatisation(page_1_name + "_WSW", Repository_Path);
            String nameFile = "KeyWords";
            KeyWords_treatment key = new KeyWords_treatment();
            key.KeyWords_treatment(nameFile, Repository_Path);
            System.out.println("     ");
            System.out.println("********* we apply Keywords method *****************");
            System.out.println("     ");
        }else{
            System.out.println("Pas La meme langue");
        }
    }
}
