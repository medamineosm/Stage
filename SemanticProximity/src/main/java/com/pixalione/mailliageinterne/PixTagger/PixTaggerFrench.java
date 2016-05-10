
package com.pixalione.mailliageinterne.PixTagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerWrapper;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;

/** The French Tagger
 * This class is an Implementation of the PixTagger Interface
 * It is using TT4J
 *
 * @author Pixalione2
 */
public class PixTaggerFrench {
    
    private TreeTaggerWrapper taggerWrapper = new TreeTaggerWrapper<>();
    private static ArrayList<TaggedWord> TaggedWords;
    private static TaggedWord tempTag = new TaggedWord();
    private String Language ="";
    private int MinLemme = 3;
    private int minText = 1;

    /** The Only constructor of this Class
     * It set the Language and some properties of the Tagger
     * It implemets the Handler of the Tagger Wrapper and defines its operation
     *
     */
    public PixTaggerFrench(){
       taggerWrapper = new TreeTaggerWrapper<>();
       System.setProperty("treetagger.home", "data/TreeTagger");
       TaggedWords = new ArrayList<TaggedWord>();
       this.setLanguage("French");
       taggerWrapper.setHandler(new TokenHandler<String>() {
                                @Override
                                public void token(String token, String pos, String lemma) {
                                        tempTag = new TaggedWord();
                                        tempTag.setToken(token.toLowerCase());
                                        if ((pos.length()>2)&&(pos.substring(0, 3).equals("VER")))
                                            tempTag.setTag("VER");
                                        else
                                            tempTag.setTag(pos);
                                        tempTag.setLemme(lemma.toLowerCase());
                                        TaggedWords.add(tempTag);
   
                                }
                        });
    }
    
 

    public void initialize(){
        TaggedWords.clear();
        TaggedWords = new ArrayList<TaggedWord>();
        TaggedWords.clear();
    }
    

    public void setLanguage(String language){
        this.Language=language;
            try {
                taggerWrapper.setModel("data/french.par");
            } catch (IOException ex) {
                Logger.getLogger(PixTaggerFrench.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    

    public ArrayList<TaggedWord> tagsentence(String Sentence){
        this.initialize();
        String[] phrase = Sentence.split(" ");
        //String ch = split2(phrase);
        //phrase = ch.split(" ");
        try {
            taggerWrapper.process(phrase);
        } catch (Exception ex) {
            Logger.getLogger(PixTaggerFrench.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return TaggedWords;
    }
    
    public String tagsentenceToLemme(String Sentence){
        ArrayList<TaggedWord> res = this.tagsentence(Sentence);
        String resultat="";
        for (int i=0;i<res.size();i++){
            resultat = resultat+res.get(i).getLemme()+" ";
        }
        resultat = resultat.trim();
        return resultat;
    }
    


    public TaggedWord tagword(String mot){
        this.initialize();
        
        String[] phrase = mot.split(" ");
        
        try {
            taggerWrapper.process(phrase);
        } catch (Exception ex) {
            Logger.getLogger(PixTaggerFrench.class.getName()).log(Level.SEVERE, null, ex);
        }
        return TaggedWords.get(0);
    }

 

    public void initialisationListStopWords(){
          String ch = this.Language;
          try {

            File ff = new File("data/generated/StopWords_"+ch+".txt");
            File ff2 = new File("data/rsc/StopWords_"+ch+".txt");
            File ff3 = new File("data/generated/log_"+ch+".txt");
            String d2="";
            if (ff3.exists()){
                FileInputStream file = new FileInputStream(new File("data/generated/log_"+ch+".txt"));
                InputStreamReader read = new InputStreamReader(file, Charset.forName("UTF8"));
                BufferedReader buffread = new BufferedReader(read);
                d2 = buffread.readLine();
            }
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
                Date d = new Date(ff2.lastModified());
                String d1 = sdf.format(d);
            if (!ff.exists() || (!d1.equals(d2))){
            ff.createNewFile();
            FileWriter ffw = new FileWriter(ff);
            FileInputStream file = new FileInputStream(new File("data/rsc/StopWords_"+ch+".txt"));

            InputStreamReader read = new InputStreamReader(file, Charset.forName("UTF8"));
            BufferedReader buffread = new BufferedReader(read);
            String ligne;
            // lecture ligne par ligne (sachant qu'une ligne correspond a une
            // phrase)
            ArrayList<String> table = new ArrayList<String>();

            while ((ligne = buffread.readLine()) != null) {
                TaggedWord tg = this.tagword(ligne);
                table.add(tg.getLemmeToString());
            }
            Set set = new HashSet() ;
            set.addAll(table) ;
            table = new ArrayList(set);
            //ffw.write(tg.getLemmeToString()); // écrire une ligne dans le fichier resultat.txt
            for(int i=0;i<table.size();i++){
                ffw.write(table.get(i));
                ffw.write("\n");
            }
            
            // forcer le passage à la ligne
            ffw.close(); // fermer le fichier à la fin des traitements
            ff3.createNewFile();
            FileWriter ffw3 = new FileWriter(ff3);
            d = new Date(ff2.lastModified());
            d1 = sdf.format(d);
            ffw3.write(d1);
            ffw3.close();
            }
        } catch (Exception e) {
        }
      }

    public String getLanguage() {
        return this.Language;
    }


    public String tokenize(String ch) {
                String res="";
        Analyzer a = new StandardAnalyzer(Version.LUCENE_20);

       //StringReader s = new StringReader("Je vais BIEN, ne vous inquiétez pas !!! fine123,236");
        TokenStream t =  a.tokenStream(null, new StringReader(ch));
        try {
            while(t.incrementToken()) {
                res=res+" "+t.getAttribute(TermAttribute.class).term();
            }
        } catch (IOException ex) {
            Logger.getLogger(PixTaggerFrench.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res.trim();
    }


    public String ModifTextStringForLemmatization(String Text) {
        		String sentencePreTraitement=Text;
		
		//Verification que la ligne n'est pas vide
		
		StringBuffer EmptyLigne = new StringBuffer();
		for(int i = 0;i<sentencePreTraitement.length();i++){
			Character ch = new Character(sentencePreTraitement.charAt(i));
			if(Character.isLetterOrDigit(ch)){
				EmptyLigne.append(ch);				
			}
		}	
		if(EmptyLigne.length()==0){//ligne vide
			sentencePreTraitement = "Empty Line";
		}
		else{//ligne avec des éléments
			/*
			 * Avant utilisation du lemmatiseur
			 */
			sentencePreTraitement=sentencePreTraitement.toLowerCase();
			//sentencePreTraitement=sentencePreTraitement.replaceAll("-", " - ");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("<", "");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll(">", "");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("«", "");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("»", "");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("\"", "");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("’", "'");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("/", "");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("|", "");
                        sentencePreTraitement=sentencePreTraitement.replaceAll("\\)", "");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("\\(", "");
                        sentencePreTraitement=sentencePreTraitement.replaceAll("\\.", " \\.");
                        sentencePreTraitement=sentencePreTraitement.replaceAll(",", " ,");
			while(sentencePreTraitement.contains("  ")){
				sentencePreTraitement=sentencePreTraitement.replaceAll("  ", " ");			//gestion des problêmes d'espacement
			}
			if(sentencePreTraitement.startsWith(" ")){
				sentencePreTraitement=sentencePreTraitement.substring(1);		//gestion des problêmes d'espace au debut de ligne
			}
			
			sentencePreTraitement = sentencePreTraitement.replaceAll("`", "'"); // gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("qu'", "que ");			//gestion guillemets 
			sentencePreTraitement=sentencePreTraitement.replaceAll("s'", "se ");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("c'", "ce ");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("n'", "ne ");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("d'", "de ");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("m'", "me ");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("t'", "te ");			//gestion guillemets
			/**/
			sentencePreTraitement=sentencePreTraitement.replaceAll("œ", "oe");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("æ", "ae");			//gestion guillemets
			sentencePreTraitement=sentencePreTraitement.replaceAll("'", "' ");			//gestion guillemets 

		}
		return sentencePreTraitement;
    }

    

    public int getMinLemme() {
        return MinLemme;
    }


    public void setMinLemme(int MinLemme) {
        this.MinLemme = MinLemme;
    }


    public int getMinText() {
        return minText;
    }


    public void setMinText(int minText) {
        this.minText = minText;
    }
    
}
