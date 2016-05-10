/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixalione.mailliageinterne.PixTagger;




import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;

//import edu.stanford.nlp.ling.WordTag;
//import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.util.Collections;

/**
 *
 * @author Fabrice
 */
public class NewMain2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        PixTaggerFrench pixTag = new PixTaggerFrench ();
        String GrpMotClef = "achter";
        String TextPathGrpMotsClef ="vendre";
        int TailleParag = 3;
        ArrayList <String> ListTextCorpus = new ArrayList <String> ();
        String Mot1 = "interupteur";
        String Mot2 = "Site";
        String Mot3 = "lampe";
        ListTextCorpus.add(Mot1);
        ListTextCorpus.add(Mot2);
        ListTextCorpus.add(Mot3);
   
    }
    
}
