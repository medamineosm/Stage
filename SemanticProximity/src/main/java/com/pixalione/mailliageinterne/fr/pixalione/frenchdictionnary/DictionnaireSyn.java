package com.pixalione.mailliageinterne.fr.pixalione.frenchdictionnary;


import com.pixalione.mailliageinterne.PixTagger.PixTaggerFrench;
import com.pixalione.mailliageinterne.PixTagger.TaggedWord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

//import edu.stanford.nlp.ling.WordTag;
//import fr.pixalione.lemmatisation.MorphologyMervyn;

public class DictionnaireSyn {
	private  Hashtable<TaggedWord,Integer> wTnodes;		// espace des noueds (sommets) du graphe, il correspond au dictionnaire d'unit�s lexicales
	private  Hashtable<String,String> wTnodes_Mot;		// espace des noueds (sommets) du graphe, il correspond au dictionnaire d'unit�s lexicales
	private  Hashtable<Integer,TaggedWord> wTdico;		// espace des noueds (sommets) du graphe, il correspond au dictionnaire d'unit�s lexicales
	private  Hashtable<String,ArrayList<TaggedWord>> dicoWordTag;		// espace des noueds (sommets) du graphe, il correspond au dictionnaire d'unit�s lexicales
	private  Hashtable<TaggedWord, ArrayList<TaggedWord>> Dico_wTLexUnit_wTsyn;	//matrice servant a modeliser le graphe dont on doit trouver les cliques
	private ArrayList<String> dicoSynonyme;
	private ArrayList<TaggedWord> dicoSimpleWordTag;
	private  Hashtable<TaggedWord, ArrayList<Integer>> MatriceVoisinage;	//matrice servant a modeliser le graphe dont on doit trouver les cliques
	private String DicoPath = "";
        private PixTaggerFrench Tagger;
	
	public DictionnaireSyn(String namefile,PixTaggerFrench tagger){
                this.setTagger(tagger);
		this.setDicoPath(namefile);
		RecuperationDico3(namefile);
		//logging();
	}


	
	


	public DictionnaireSyn(DictionnaireSyn dicoInit) {
		// TODO Auto-generated constructor stub
		this.wTnodes = dicoInit.getwTnodes();
		this.wTnodes_Mot= dicoInit.getwTnodes_Mot();
		this.wTdico= dicoInit.getwTdico();
		this.dicoWordTag= dicoInit.getDicoWordTag();
		this.Dico_wTLexUnit_wTsyn= dicoInit.getDico_wTLexUnit_wTsyn();
		this.dicoSynonyme= dicoInit.getDicoSynonyme();
		this.dicoSimpleWordTag= dicoInit.getDicoSimpleWordTag();
		this.MatriceVoisinage= dicoInit.getMatriceVoisinage();
		this.DicoPath= dicoInit.getDicoPath();
	}
        
        
        
        
        
        public ArrayList<String> ListeTagPossible(String ligne, String UniteLexical){
            
            ArrayList<String> result = new ArrayList<>();
            int indexPar = ligne.indexOf(")");
            String tags = ligne.substring(1,indexPar);
            if (tags.length()>2){
            String[] tab = tags.split(" ");
            for (int i=0;i<tab.length;i++)
                result.add(tab[i]);
            }
            return result;
        }
        
        public ArrayList<TaggedWord> CombinaisonSynonymes(String ligne, ArrayList<String> tags){
            ArrayList<TaggedWord> result = new ArrayList<>();
            int indexPar = ligne.indexOf(")");
            String tab = ligne.substring(indexPar+2,ligne.length());
            
            String[] words = tab.split("\\|");
            for (int i=0;i<words.length;i++){
                for (int j=0;j<tags.size();j++){
                    result.add(new TaggedWord(words[i], tags.get(j), words[i]));
                }
            }
            return result;
        }
        
        
        public ArrayList<TaggedWord> CombinaisonUniteLexicale(String ligne, ArrayList<String> tags){
            ArrayList<TaggedWord> result = new ArrayList<>();
            String[] tabs = ligne.split("\\|");
            for (int i=0;i<tags.size();i++){
                result.add(new TaggedWord(tabs[0], tags.get(i), tabs[0]));
            }
            return result;
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

        private  void RecuperationDico3( String NamefileDico){
            FileInputStream file = null;
            Hashtable<String,String> wordDico_Sring = new Hashtable<>();
            Hashtable<TaggedWord,Integer> wordDico1= new Hashtable<>();
            Hashtable<Integer,TaggedWord> wordDico2= new Hashtable<>();
            ArrayList<String> word1= new ArrayList<>();
            ArrayList<TaggedWord> word3 = new ArrayList<>();	//liste voisins tagger
            Hashtable<TaggedWord,ArrayList<TaggedWord>> dico_wTLexUnit_wTsyn = new Hashtable<>();
            Hashtable<String,ArrayList<TaggedWord>> dicoWord = new Hashtable<>();
            int index =0;
            try {
                file = new FileInputStream("data/DicSyn2.txt");
                InputStreamReader Corpus=new InputStreamReader(file, "UTF-8");
                BufferedReader CorpusReader=new BufferedReader(Corpus);
                String ligne;
                String uniteLexicale;
                String synonymes;
                ArrayList<TaggedWord> synonymsList;
                ArrayList<String> possibleTags;
                String token;
                String tag;
                String lemme;
                String lemmeTag;
                ArrayList<TaggedWord> listeUniLexDifferentTag;
                int l=1;
                while ((ligne = CorpusReader.readLine())!=null){
                        uniteLexicale = ligne.split("\\|")[0];
                        synonymes = CorpusReader.readLine();
                        
                        possibleTags = this.ListeTagPossible(synonymes, uniteLexicale);
                        synonymsList = this.CombinaisonSynonymes(synonymes, possibleTags);
                        listeUniLexDifferentTag = this.CombinaisonUniteLexicale(ligne, possibleTags);
                        for (int i=0;i<listeUniLexDifferentTag.size();i++){
                                wordDico1.put(listeUniLexDifferentTag.get(i), index);
                                wordDico2.put(index, listeUniLexDifferentTag.get(i));
                                word3.add(listeUniLexDifferentTag.get(i));
                                index++;
                        }
                    for (int i=0;i<listeUniLexDifferentTag.size();i++){
                        TaggedWord tg = listeUniLexDifferentTag.get(i);
                    token = tg.getToken();
                    tag = tg.getTag();
                    lemme = tg.getLemme();
                    lemmeTag = tg.getLemmeToString();
                    if (!word1.contains(lemme)){
                        word1.add(lemme);
                    }
                    
                    if (!wordDico_Sring.contains(lemmeTag)){
                        wordDico_Sring.put(token, lemmeTag);
                    }

                    dicoWord.put(token, listeUniLexDifferentTag);

                        
                        dico_wTLexUnit_wTsyn.put(tg,synonymsList);
                        
                    }
                    
                }   
            } catch (Exception ex) {
                Logger.getLogger(DictionnaireSyn.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    file.close();
                } catch (IOException ex) {
                    Logger.getLogger(DictionnaireSyn.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //dico_wTLexUnit_wTsyn=NettoyageDicoSyn(dico_wTLexUnit_wTsyn);
            this.setwTdico(wordDico2);
            this.setwTnodes(wordDico1);
            this.setwTnodes_Mot(wordDico_Sring);
            this.setDicoSynonyme(word1);
            this.setDicoWordTag(dicoWord);
            
            this.setDicoSimpleWordTag(word3);
            this.setDico_wTLexUnit_wTsyn(dico_wTLexUnit_wTsyn);
            logging();
        }

        private ArrayList<TaggedWord> diffrentTagUniteLexicale(String ch, String ch1){

            ArrayList<TaggedWord> resultat = new ArrayList<>();
            
            String par = ch.substring(0, ch.indexOf(")")+1);
            
                String[] listeTag;
                String tgString = ch.substring(ch.indexOf(")")+1);
                String[] divise = tgString.split("\\/");
                String token = divise[0];
                String tag = divise[1];
                String lemme = divise[2];

                if (par.length()==2){
                    listeTag = new String[1];
                    listeTag[0] =tag;
                }

                else{
                    par = par.substring(1, par.length()-1);
                    listeTag = par.split(" ");
                }


                for (int i=0;i<listeTag.length;i++){
                    TaggedWord tg = new TaggedWord(token.toLowerCase(), listeTag[i], lemme.toLowerCase());

                    resultat.add(tg);
                }
            
            
            
            return resultat;
        }
        
        
        
        private ArrayList<TaggedWord> ConvertStringtoListTaggedWord(String ch){
            ArrayList<TaggedWord> resultat = new ArrayList<TaggedWord>();
            String[] diviser = ch.split("\\|");
            for (int i=0;i<diviser.length;i++){
                if(!diviser[i].contains(" ")){
                String[] divide = diviser[i].split("\\/");
                if (divide.length==3){
                String token = divide[0];
                String tag = divide[1];
                String lemme = divide[2];
                TaggedWord tg = new TaggedWord(token.toLowerCase(), tag, lemme.toLowerCase());
                resultat.add(tg);
                }
                }
            }
            return resultat;
        }
        
        
        
        
        private  void RecuperationDico2( String NamefileDico){
                
		Hashtable<String,String> wordDico_Sring = new Hashtable<String,String>();
		Hashtable<TaggedWord,Integer> wordDico1= new Hashtable<TaggedWord,Integer>();
		Hashtable<Integer,TaggedWord> wordDico2= new Hashtable<Integer,TaggedWord>();
		int index =0;
		ArrayList<String> word1= new ArrayList<String>();
		ArrayList<TaggedWord> word2 = new ArrayList<TaggedWord>();
		ArrayList<TaggedWord> word3 = new ArrayList<TaggedWord>();	//liste voisins tagger
		Hashtable<TaggedWord,ArrayList<TaggedWord>> dico_wTLexUnit_wTsyn = new Hashtable<TaggedWord,ArrayList<TaggedWord>>();
		Hashtable<String,ArrayList<TaggedWord>> dicoWord = new Hashtable<String,ArrayList<TaggedWord>>();
		String LexicalUnit=null;
		Boolean isChosenLexicalUnit = true;			//indique si on est sur l'unite lexical choisie

		try {
                    File ff = new File("data/DicSyn.txt"); 
                    ff.createNewFile();
                    FileWriter ffw = new FileWriter(ff);
			FileInputStream file = new FileInputStream(new File(NamefileDico));
			InputStreamReader Corpus=new InputStreamReader(file, "Cp1252");
			BufferedReader CorpusReader=new BufferedReader(Corpus);
			String ligne;
			// lecture ligne par ligne )
                        int cmp=0;
			while ((ligne=CorpusReader.readLine())!=null){
                            cmp = cmp+2;
                          
                            String lexical = ligne;
                            ligne = CorpusReader.readLine();
                            String Synonymes = ligne;
  
                            lexical = lexical.split("\\|")[0];
                            TaggedWord lexicalTW = Tagger.tagword(lexical);
                           
                                int indexPar = Synonymes.indexOf(")");
                                String tagbrute = Synonymes.substring(0, indexPar+1);
                                tagbrute = tagbrute.replaceAll("Adjectif", "ADJ");
                                tagbrute = tagbrute.replaceAll("Adverbe", "ADV");
                                tagbrute = tagbrute.replaceAll("Verbe", "VER");
                                String tag = tagbrute;
                                tag = tag.replace("(", "");
                                tag = tag.replace(")", "");
                                tag = tag.split(" ")[0];
                            if (tag.equals("Adjectif"))
                                tag="ADJ";
                            if (tag.equals("Adverbe"))
                                tag="ADV";
                            if (tag.equals("Verbe"))
                                tag="VER";
                           
                            if ((tag!=null && !tag.equals(""))&&(!tag.equals(lexicalTW.getTag2()))){
                                    lexicalTW.setTag(tag);
                            }
                                
                            lexicalTW.setToken(tagbrute+""+lexicalTW.getToken());
                            ffw.write(lexicalTW.getToken()+"/"+lexicalTW.getTag2()+"/"+lexicalTW.getLemme());
                            ffw.write("\n");
                            Synonymes = formatStringSynonymes(Synonymes);
                            String[] synonymesTab = Synonymes.split("\\|");
                            
                            ArrayList<TaggedWord> synonymesTW = new ArrayList<TaggedWord>();
                            for (int i=0;i<synonymesTab.length;i++){
                                TaggedWord tw;
                                if (synonymesTab[i].contains(" ")){
                                    tw = new TaggedWord(synonymesTab[i].toLowerCase(), tag, synonymesTab[i].toLowerCase());
                                }
                                else
                                tw = Tagger.tagword(synonymesTab[i]);
                                    if (i==synonymesTab.length-1)
                                        ffw.write(tw.getToken()+"/"+tw.getTag2()+"/"+tw.getLemme());
                                    else
                                        ffw.write(tw.getToken()+"/"+tw.getTag2()+"/"+tw.getLemme()+"|");
                                synonymesTW.add(tw);
                            }
                            ffw.write("\n");
                            
                            //synonymesTW = Tagger.tagsentence(Synonymes);
                            
                            dico_wTLexUnit_wTsyn.put(lexicalTW, synonymesTW);
                            
                        }
        
        
        	file.close();
                ffw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
                this.setDico_wTLexUnit_wTsyn(dico_wTLexUnit_wTsyn);
        }
        
        
        
        
        
        
        
        private String formatStringSynonymes(String ch){
            int indexPar = ch.indexOf(")");
            String res = ch.substring(indexPar+2);
            res = res.trim();
            return res;
        }
        




	/*
	 * Fonction servant � recuperer les mots du dictionnaire de synonyme de maniere a pouvoir obtenir une matrice d'occurence (on peut l'utiliser avec le dico meme imparfait)
	 */
	/*private  void RecuperationDico( String NamefileDico){
		Hashtable<String,String> wordDico_Sring = new Hashtable<String,String>();
		Hashtable<TaggedWord,Integer> wordDico1= new Hashtable<TaggedWord,Integer>();
		Hashtable<Integer,TaggedWord> wordDico2= new Hashtable<Integer,TaggedWord>();
		int index =0;
		ArrayList<String> word1= new ArrayList<String>();
		ArrayList<TaggedWord> word2 = new ArrayList<TaggedWord>();
		ArrayList<TaggedWord> word3 = new ArrayList<TaggedWord>();	//liste voisins tagger
		Hashtable<TaggedWord,ArrayList<TaggedWord>> dico_wTLexUnit_wTsyn = new Hashtable<TaggedWord,ArrayList<TaggedWord>>();
		Hashtable<String,ArrayList<TaggedWord>> dicoWord = new Hashtable<String,ArrayList<TaggedWord>>();
		String LexicalUnit=null;
		Boolean isChosenLexicalUnit = true;			//indique si on est sur l'unite lexical choisie

		try {
			FileInputStream file = new FileInputStream(new File(NamefileDico));
			InputStreamReader Corpus=new InputStreamReader(file, "Cp1252");
			BufferedReader CorpusReader=new BufferedReader(Corpus);
			String ligne;
			// lecture ligne par ligne )
			while ((ligne=CorpusReader.readLine())!=null){
				//Pretraitement du corpus
				String sentencePreTraitement=ligne;
				

				//traitement
				sentencePreTraitement=sentencePreTraitement.replaceAll("\\)", "");			//gestion de la parenthese
				sentencePreTraitement=sentencePreTraitement.replaceAll("\\(", "");			//gestion de la parenthese

				String[] fenetre= sentencePreTraitement.split("\\|");				//ici, la fenetre correspond a une phrase

				//test presence sur la ligne indiquant l'unit� lexicale
				isChosenLexicalUnit = (fenetre[1].equals("1")); 


				//traitement de la ligne	
				if (isChosenLexicalUnit==true) 
				{
					LexicalUnit=fenetre[0].toLowerCase();	//on recupere le nom de l'etat qui va servir de clef pour le hashtable
					
				}
				else
				{
					ArrayList<String> ChoixLexicauxPossible = new ArrayList<String>();
					word2 = new ArrayList<TaggedWord>();
					//lemmatisation du mot contenu dans le dico de synonyme

					//r�cuperation du tag
					boolean presenceTag = true;

					String[] listeTag = fenetre[0].split(" ");
					ArrayList<TaggedWord> liste = new ArrayList<TaggedWord>();
					for (int j = 0;j<listeTag.length;j++){
						String tag = listeTag[j];
						if (tag.equals("")){
							presenceTag=false;
							//ajout des mots non tagger du dico avec comme tag NoTag
							liste.add(new TaggedWord(LexicalUnit, "No", LexicalUnit));
							//wordNonTagger.add(LexicalUnit);
							/*liste.add(LexicalUnit+"/N");
							liste.add(LexicalUnit+"/A");
							liste.add(LexicalUnit+"/V");
							liste.add(LexicalUnit+"/ADV"); rien faire
						}
						else if (tag.equals("Nom")){
							liste.add(new TaggedWord(LexicalUnit, "NOM", LexicalUnit));
						}
						else if (tag.equals("Verbe")){
							liste.add(new TaggedWord(LexicalUnit, "VER", LexicalUnit));
						}
				
                                                else if (tag.equals("Adverbe")){
							liste.add(new TaggedWord(LexicalUnit, "ADV", LexicalUnit));
						}
						else if (tag.equals("Adjectif")){
							liste.add(new TaggedWord(LexicalUnit, "ADJ", LexicalUnit));
						}
						else if (tag.equals("Adjectif_cardinal")){
							//liste.add( LexicalUnit+"/A"); rien fqire
						}
					}



					int i = 1;
					if(presenceTag){
						for (int j = 0;j<liste.size();j++){
							TaggedWord mot = liste.get(j);

							//sauvearde des lemmes comme entr�e car il y a des mots differents qui ont le meme lemme et le meme tag (abattu et abattre)
							  //lemmatisation du mot
							//traitement du cas ()
							word2.add(mot);

							//ajout dans le dictionnaire en evitant les doublons
							if(!wordDico2.contains(mot)){
								word3.add(mot);
								wordDico1.put(mot, index);
								wordDico2.put(index, mot );
								wordDico_Sring.put(mot.toString(), LexicalUnit);
							}
							//ajout dans le dictionnaire en evitant les doublons et en prenant les mots pertinents
							if(!wordDico_Sring.contains(mot)&(liste.size()==1)){
								wordDico_Sring.put(mot.toString(), LexicalUnit);
							}
							
							
							index++;
							String Lemme = mot.getLemme();  //lemmatisation du mot
							ChoixLexicauxPossible.add(Lemme);
						}
					}
					else{
						TaggedWord mot =  liste.get(0);
						//traitement du cas ()
						word2.add(mot);

						//ajout dans le dictionnaire en evitant les doublons
						if(!wordDico2.contains(mot)){
							word3.add(mot);
							wordDico1.put(mot, index);
							wordDico2.put(index, mot );
						}
						
						index++;
						ChoixLexicauxPossible.add(LexicalUnit);
					}
					if (!word2.isEmpty()){

						// construction des matrices issus du dico

						//1er choix //en entr�e on prend le mot tel quel du dictionnaire
						dicoWord.put(LexicalUnit, word2);	

						//r�cup�ration des mots possibles
						for (int k = 0;k<ChoixLexicauxPossible.size();k++){
							if (!ChoixLexicauxPossible.get(k).equals("")){
								word1.add(ChoixLexicauxPossible.get(k));
							}
						}

						//2eme choix //en entr�e on prend le WordTag Lemmatis�e tel quel du dictionnaire

						ArrayList<String> ListStringVoisins = new ArrayList<String>();	//Liste de string des voisins

						//r�cup�ration des voisins sous forme de mot
						for (int k=1;k<fenetre.length;k++){
							ListStringVoisins.add(fenetre[k].toLowerCase());
						}
						//r�cup�ration des voisins sous formes de WordTag
						for(int s =0;s<word2.size();s++){
							TaggedWord wTLexicalUnit = word2.get(s);
							ArrayList<TaggedWord> WordTagVoisins = new ArrayList<TaggedWord>();
							if(dico_wTLexUnit_wTsyn.containsKey(wTLexicalUnit)){	//gestion du cas ou le mot est deja present dans le dico
								WordTagVoisins = dico_wTLexUnit_wTsyn.get(wTLexicalUnit);
							}
							if (!wTLexicalUnit.getTag().equals("No")){
								for (int k=0;k<ListStringVoisins.size();k++){
								String mot = ListStringVoisins.get(k);
								TaggedWord motVoisin = new TaggedWord();
									motVoisin.setTag(wTLexicalUnit.getTag());
									motVoisin.setToken(mot);
                                                                        motVoisin.setLemme(mot);

									//lemmatisation si ce nest pas un goupe de mot
									//TaggedWord lemmeVoisin;
									/*if(!mot.contains(" ")){
									 lemmeVoisin = MorphologyMervyn.stemStaticMervyn(motVoisin);
									}*/
									/*else{
										 lemmeVoisin =motVoisin;
									}
									if(!WordTagVoisins.contains(motVoisin)){	//gestion du cas ou le mot est deja present dans le dico
										WordTagVoisins.add(motVoisin);
									}
								}
							}
							dico_wTLexUnit_wTsyn.put(wTLexicalUnit, WordTagVoisins);	//ajout dans la matrice
						}
					}
				}

			}

			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//ajout des mots non tagger du dico avec comme tag NoTag
		/*Iterator<String> IteratorMot = wordNonTagger.iterator();
		while(IteratorMot.hasNext()){
			String Strmot = IteratorMot.next();
			ArrayList<String> ChoixLexicauxPossible = new ArrayList<String>();
			word2 = new ArrayList<WordTag>();
			//test presence dans le dico
			if(!dicoWord.containsKey(Strmot)){
				ArrayList<String> liste = new ArrayList<String>();
				liste.add(Strmot+"/NoTag");
				/*liste.add(Strmot+"/N");
				liste.add(Strmot+"/A");
				liste.add(Strmot+"/V");
				liste.add(Strmot+"/ADV");*/

		/*int i = 1;
				for (int j = 0;j<liste.size();j++){
					WordTag mot = WordTag.valueOf(liste.get(j),"/");
					//sauvearde des lemmes comme entr�e car il y a des mots differents qui ont le meme lemme et le meme tag (abattu et abattre)
					WordTag lemma =  MorphologyMervyn.stemStaticMervyn(mot);  //lemmatisation du mot
					//traitement du cas ()
					word2.add(lemma);

					//ajout dans le dictionnaire en evitant les doublons
					if(!wordDico2.contains(lemma)){
					wordDico1.put(lemma, index);
					wordDico2.put(index, lemma );
					}
					
					index++;
					String Lemme = MorphologyMervyn.stemStaticMervyn(mot).word();  //lemmatisation du mot
					ChoixLexicauxPossible.add(Lemme);
				}
			}
			if (!word2.isEmpty()){}
			dicoWord.put(LexicalUnit, word2);


			//r�cup�ration des mots possibles
			for (int k = 0;k<ChoixLexicauxPossible.size();k++){
				if (!ChoixLexicauxPossible.get(k).equals("")){
					word1.add(ChoixLexicauxPossible.get(k));
				}
			}

		}

		dico_wTLexUnit_wTsyn=NettoyageDicoSyn(dico_wTLexUnit_wTsyn);
		this.setwTdico(wordDico2);
		this.setwTnodes(wordDico1);
		this.setwTnodes_Mot(wordDico_Sring);
		this.setDicoSynonyme(word1);
		this.setDicoWordTag(dicoWord);
		this.setDico_wTLexUnit_wTsyn(dico_wTLexUnit_wTsyn);
		this.setDicoSimpleWordTag(word3);
		
	}
*/
	private Hashtable<TaggedWord,ArrayList<TaggedWord>> NettoyageDicoSyn (Hashtable<TaggedWord,ArrayList<TaggedWord>> DicoImparfait){
		
		Hashtable<TaggedWord,ArrayList<TaggedWord>> DicoBon = new Hashtable<TaggedWord,ArrayList<TaggedWord>>();
		
		//parcours de chaque unit� lexicale du dictionnaire
		Iterator<TaggedWord> IteratorWT = DicoImparfait.keySet().iterator();
		while(IteratorWT.hasNext()){
			
			TaggedWord LexicalUnit = IteratorWT.next();
			//recuperation de la liste de voisins
			ArrayList<TaggedWord> ListeVoisinsLexicalUnit = DicoImparfait.get(LexicalUnit);	//ancienne liste de voisins
			ArrayList<TaggedWord> NewListeVoisinsLexicalUnit = new ArrayList<TaggedWord>();	//nouvelle liste de voisins
			//test sur les voisins
			//pour chaque voisins, on verifie que les liens de synonymie sont bien verifi�
			for(int i =0;i<ListeVoisinsLexicalUnit.size();i++){
				TaggedWord Mot = ListeVoisinsLexicalUnit.get(i);
				if(DicoImparfait.containsKey(Mot)){
					ArrayList<TaggedWord> ListeVoisinsMot = DicoImparfait.get(Mot);
					if(ListeVoisinsMot.contains(LexicalUnit)){
						NewListeVoisinsLexicalUnit.add(Mot);
					}
				}
			}
			DicoBon.put(LexicalUnit, NewListeVoisinsLexicalUnit);
		}

		return DicoBon;
	}
	

	/**
	 * fonction servant � enregistrer les noeuds k voisins � un noeud donn� (les unit�s lexicales synonymes � une unit� lexicale donn�e)
	 * @param namefileDico
	 * @param wTdicoText
	 * @return Hashtable<WordTag, ArrayList<Integer>>
	 */
	public static Hashtable<TaggedWord, ArrayList<Integer>> RemplissageVoisinageNoeudsText(String namefileDico,DictionnaireSyn syn,ArrayList<TaggedWord> wTdicoText){

		Hashtable<TaggedWord,Integer> Dico = syn.getwTnodes();
		Hashtable<String,ArrayList<TaggedWord>> DicoWordTag = syn.getDicoWordTag();
		Hashtable<TaggedWord,ArrayList<Integer>> DictionaireVoisins = new Hashtable<TaggedWord,ArrayList<Integer>>();	//stockage des numeros (Identifiants) des synonymes  d'une unit� lexicales dans un dictionnaire
		ArrayList<Integer> voisins = new ArrayList<Integer>();	//Liste des voisins
		String LexicalUnit=null;
		Boolean isChosenLexicalUnit = true;			//indique si on est sur l'unite lexical choisie
		try {

			FileInputStream file = new FileInputStream(new File(namefileDico));
			InputStreamReader Corpus=new InputStreamReader(file, Charset.forName("UTF8"));
			BufferedReader CorpusReader=new BufferedReader(Corpus);
			String ligne;
			// lecture ligne par ligne )
			while ((ligne=CorpusReader.readLine())!=null){
				//Pretraitement du corpus
				String sentencePreTraitement=ligne;
				sentencePreTraitement=sentencePreTraitement.replaceAll("\\)", "");			//gestion de la parenthese
				sentencePreTraitement=sentencePreTraitement.replaceAll("\\(", "");			//gestion de la parenthese

				String[] fenetre= sentencePreTraitement.split("\\|");				//ici, la fenetre correspond a une phrase

				//test presence sur la ligne indiquant l'unit� lexicale
				isChosenLexicalUnit = (fenetre[1].equals("1")); 

				//traitement de la ligne	
				if (isChosenLexicalUnit==true) 
				{
					LexicalUnit=fenetre[0].toLowerCase();	//on recupere le nom de l'etat qui va servir de clef pour le hashtable

				}
				else
				{
					TaggedWord wTsyn =new TaggedWord();
					ArrayList<String> ListStringVoisins = new ArrayList<String>();	//Liste de string des voisins
					//Recuperation des choix lexicaux possibles
					ArrayList<TaggedWord> ChoixLexicauxPossible = new ArrayList<TaggedWord>();
					if(DicoWordTag.containsKey(LexicalUnit)){
						 ChoixLexicauxPossible = DicoWordTag.get(LexicalUnit);
					}

					//r�cup�ration des voisins sous forme de mot
					for (int i=1;i<fenetre.length;i++){
						ListStringVoisins.add(fenetre[i].toLowerCase());
					}
					//r�cup�ration des voisins sous formes de WordTag
					ArrayList<TaggedWord> WordTagVoisins = new ArrayList<TaggedWord>();
					for (int i=0;i<ListStringVoisins.size();i++){
						String vois = ListStringVoisins.get(i);
						
						if(DicoWordTag.containsKey(vois)){
							WordTagVoisins.addAll(DicoWordTag.get(vois));
						}
					}

					//test pour la pr�sence d'un mot voisins dans le texte
					boolean presenceText = false;
					int k=0;
					while((presenceText==false)&&(k<ChoixLexicauxPossible.size()) ){
						TaggedWord mot = ChoixLexicauxPossible.get(k);
						/*String lemme = MorphologyMervyn.stemStaticMervyn(mot).word();
						 motDico=lemme;
						 presenceText = dicoText.contains(lemme);*/

						presenceText = wTdicoText.contains(mot);
						wTsyn = mot;
						k++;
					}
					k=0;
					while((presenceText==false)&&(k<WordTagVoisins.size()) ){
						TaggedWord mot = WordTagVoisins.get(k);
						/*String lemme = MorphologyMervyn.stemStaticMervyn(mot).word();
						 motDico=lemme;
						 presenceText = dicoText.contains(lemme);*/

						presenceText = wTdicoText.contains(mot);
						wTsyn.setTag(mot.getTag());
						wTsyn.setToken(LexicalUnit);
                                                wTsyn.setLemme(LexicalUnit);
						k++;
					}
					//ajout des voisins dans la matrice de voisinage
					voisins = new ArrayList<Integer>();	//Liste des voisins
					if(presenceText){
						/*for (k = 0;k<WordTagVoisins.size();k++){
							 WordTag mot = WordTagVoisins.get(k);
							 voisins.add(Dico.get(mot));
						 }

						 for (k = 0;k<ChoixLexicauxPossible.size();k++){
							 WordTag mot = ChoixLexicauxPossible.get(k);
							 DictionaireVoisins.put(mot,voisins);
						 }*/
						for (k = 0;k<WordTagVoisins.size();k++){
							TaggedWord mot = WordTagVoisins.get(k);
							if(mot.getTag().equals(wTsyn.getTag())){
								if(Dico.containsKey(mot)){
									
									voisins.add(Dico.get(mot));
								}
							}
						}
						if (voisins.size()==0){
							
						}else{

							DictionaireVoisins.put(wTsyn,voisins);
						}
					}
				}	
			}
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		syn.setMatriceVoisinage(DictionaireVoisins);
		return DictionaireVoisins;
	}
	
	/*
	 * fonction servant � enregistrer les noeuds k voisins � un noeud donn� (les unit�s lexicales synonymes � une unit� lexicale donn�e)
	 * possible car le dico a �t� nettoy�
	 */
	public static Hashtable<TaggedWord, ArrayList<Integer>> RemplissageVoisinageNoeudsTextFastPertinence(String namefileDico,DictionnaireSyn syn,ArrayList<TaggedWord> wTdicoText){

		Hashtable<TaggedWord,Integer> Nodes = syn.getwTnodes();
		Hashtable<TaggedWord,ArrayList<TaggedWord>> dico_wTLexUnit_wTsyn = syn.getDico_wTLexUnit_wTsyn();
		Hashtable<TaggedWord,ArrayList<Integer>> DictionaireVoisins = new Hashtable<TaggedWord,ArrayList<Integer>>();	//stockage des numeros (Identifiants) des synonymes  d'une unit� lexicales dans un dictionnaire
		
		//pour chaque mot du texte
		Iterator<TaggedWord> IteraMotTexte = wTdicoText.iterator();
                
		while(IteraMotTexte.hasNext()){
			TaggedWord wTmotTexte = IteraMotTexte.next();
			/*if(wTmotDico.toString().equals("logiciel/N")){
				
			}*/
			//test de la presence effective du mot du texte dans le dictionnaire de synonyme
			//en tant que unite lexicale
			//if(dico_wTLexUnit_wTsyn.containsKey(wTmotTexte)){		//presenceDico=true;

			//recuperation de la liste des synonymes du mot du texte

			ArrayList<TaggedWord> ListeWtLexVoisins = dico_wTLexUnit_wTsyn.get(wTmotTexte);
			ArrayList<TaggedWord> ListeWtSyn = dico_wTLexUnit_wTsyn.get(wTmotTexte);
			//ajout dans le dictionnaire

			if(!DictionaireVoisins.containsKey(wTmotTexte)){
				//recuperation des id des synonymes
				ArrayList<Integer> ListeIdVoisins = new ArrayList<Integer>();
				//etablissement de la liste des voisins en verifiant leur existence
				for (int i =0;i<ListeWtLexVoisins.size();i++){
					TaggedWord voisin = ListeWtLexVoisins.get(i);
					if(Nodes.containsKey(voisin)){
						Integer IdVoisin = Nodes.get(voisin);
						ListeIdVoisins.add(IdVoisin);
					}
				}
				DictionaireVoisins.put(wTmotTexte, ListeIdVoisins);	//ajout du mot present dans le texte en tant qu'unite lexicale


				for (int i =0;i<ListeWtSyn.size();i++){
					TaggedWord wTSyn = ListeWtSyn.get(i);

					if(!DictionaireVoisins.containsKey(wTSyn)){
						ArrayList<TaggedWord> ListeWtSynVoisins = dico_wTLexUnit_wTsyn.get(wTSyn);
                                                
						//recuperation des id des synonymes
						ListeIdVoisins = new ArrayList<Integer>();

						//ajout du premier element
						ListeIdVoisins.add(Nodes.get(wTmotTexte));

						//etablissement de la liste des voisins en verifiant leur pertinence
                                                
						for (int k =0;k<ListeWtSynVoisins.size();k++){
							TaggedWord voisin = ListeWtSynVoisins.get(k);
							if(ListeWtLexVoisins.contains(voisin)){ //implementation afin de rendre les cliques plus pertinentes
								if(Nodes.containsKey(voisin)){//&
									Integer IdVoisin = Nodes.get(voisin);
									ListeIdVoisins.add(IdVoisin);
								}
							}
						}
						DictionaireVoisins.put(wTSyn, ListeIdVoisins);	//ajout du synonyme du mot present dans le texte en tant qu'unite lexicale
					}
					else{
						ArrayList<TaggedWord> ListeWtSynVoisins = dico_wTLexUnit_wTsyn.get(wTSyn);

						//cas ou le mot est deja present
						//recuperation des id des synonymes
						ListeIdVoisins = DictionaireVoisins.get(wTSyn);

						//ajout du premier element
						if(!ListeIdVoisins.contains(Nodes.get(wTmotTexte))){
							ListeIdVoisins.add(Nodes.get(wTmotTexte));
						}

						//etablissement de la liste des voisins en verifiant leur existence
						for (int k =0;k<ListeWtSynVoisins.size();k++){
							TaggedWord voisin = ListeWtSynVoisins.get(k);
							if(ListeWtLexVoisins.contains(voisin)){// implementation afin de rendre les cliques plus pertinentes
								if(Nodes.containsKey(voisin)){
									Integer IdVoisin = Nodes.get(voisin);
									if(!ListeIdVoisins.contains(IdVoisin)){
										ListeIdVoisins.add(IdVoisin);
									}
								}
							}
						}
						DictionaireVoisins.put(wTSyn, ListeIdVoisins);	//ajout du synonyme du mot present dans le texte en tant qu'unite lexicale
					}/**/
				}
			}
		}
		syn.setMatriceVoisinage(DictionaireVoisins);
		return DictionaireVoisins;
	}


	
	public ArrayList<String> getDicoSynonyme() {
		return dicoSynonyme;
	}
	public void setDicoSynonyme(ArrayList<String> dicoSynonyme) {
		this.dicoSynonyme = dicoSynonyme;
	}

	public Hashtable<String,ArrayList<TaggedWord>> getDicoWordTag() {
		return dicoWordTag;
	}

	public void setDicoWordTag(Hashtable<String,ArrayList<TaggedWord>> dicoWordTag) {
		this.dicoWordTag = dicoWordTag;
	}

	public Hashtable<TaggedWord,Integer> getwTnodes() {
		return wTnodes;
	}

	public void setwTnodes(Hashtable<TaggedWord,Integer> wTnodes) {
		this.wTnodes = wTnodes;
	}

	public Hashtable<Integer,TaggedWord> getwTdico() {
		return wTdico;
	}

	public void setwTdico(Hashtable<Integer,TaggedWord> wTdico) {
		this.wTdico = wTdico;
	}


	public Hashtable<TaggedWord, ArrayList<TaggedWord>> getDico_wTLexUnit_wTsyn() {
		return Dico_wTLexUnit_wTsyn;
	}

	public void setDico_wTLexUnit_wTsyn(Hashtable<TaggedWord, ArrayList<TaggedWord>> dico_wTLexUnit_wTsyn) {
		Dico_wTLexUnit_wTsyn = dico_wTLexUnit_wTsyn;
	}
	public Hashtable<String, String> getwTnodes_Mot() {
		return wTnodes_Mot;
	}


	public void setwTnodes_Mot(Hashtable<String, String> wTnodes_Mot) {
		this.wTnodes_Mot = wTnodes_Mot;
	}





	public ArrayList<TaggedWord> getDicoSimpleWordTag() {
		return dicoSimpleWordTag;
	}





	public void setDicoSimpleWordTag(ArrayList<TaggedWord> dicoSimpleWordTag) {
		this.dicoSimpleWordTag = dicoSimpleWordTag;
	}





	public Hashtable<TaggedWord, ArrayList<Integer>> getMatriceVoisinage() {
		return MatriceVoisinage;
	}





	public void setMatriceVoisinage(Hashtable<TaggedWord, ArrayList<Integer>> matriceVoisinage) {
		MatriceVoisinage = matriceVoisinage;
	}






	public String getDicoPath() {
		return DicoPath;
	}






	public void setDicoPath(String dicoPath) {
		DicoPath = dicoPath;
	}

    public PixTaggerFrench getTagger() {
        return Tagger;
    }

    public void setTagger(PixTaggerFrench Tagger) {
        this.Tagger = Tagger;
    }
    
    public void logging(){
         // dico_wTLexUnit_wTsyn
            Enumeration<TaggedWord> enum5 = Dico_wTLexUnit_wTsyn.keys();
            
            try {
            File ff = new File("res/dic/1.txt"); 
            ff.createNewFile();
            FileWriter ffw = new FileWriter(ff);
                while (enum5.hasMoreElements()){
                    TaggedWord wt = enum5.nextElement();
                   
                    ffw.write("1* : "+wt.getTokenToString()); // écrire une ligne dans le fichier resultat.txt
                    ffw.write("\n"); // forcer le passage à la ligne
                    ArrayList<TaggedWord> wtAL = Dico_wTLexUnit_wTsyn.get(wt);
                    ffw.write("2* : "+wtAL.size());
                    for (int i=0;i<wtAL.size();i++)
                        ffw.write("3*\t"+wtAL.get(i).getTokenToString()); // écrire une ligne dans le fichier resultat.txt
                        ffw.write("\n"); // forcer le passage à la ligne
                    
                }
                ffw.close();
            } catch (Exception e) {
            }
            
    }
        
        

}
