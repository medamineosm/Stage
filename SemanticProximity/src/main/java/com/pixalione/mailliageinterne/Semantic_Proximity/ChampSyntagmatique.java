package com.pixalione.mailliageinterne.Semantic_Proximity;


import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;


import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import com.pixalione.mailliageinterne.PixTagger.PixTaggerFrench;
import com.pixalione.mailliageinterne.PixTagger.TaggedWord;
import com.pixalione.mailliageinterne.fr.pixalione.frenchdictionnary.RecupCaracSyn;

public class ChampSyntagmatique {

	private  Hashtable<TaggedWord,Integer> wTnodes = new Hashtable<TaggedWord,Integer>();
	private  Hashtable<Integer,TaggedWord> wTdico = new Hashtable<Integer,TaggedWord>();
	private Hashtable<Integer,Syntagme> DicoSyntagme = new Hashtable<Integer,Syntagme>();
	private Hashtable<Syntagme,Integer> DicoSyntagme2 = new Hashtable<Syntagme,Integer>();
	private  Hashtable<String,TaggedWord> dicoCorresStrWt = new Hashtable<String,TaggedWord> ();		//dictionnaire de correspondance mots du texte->wordtag correspondant
	private  Hashtable<TaggedWord,String> InvdicoCorresStrWt = new Hashtable<TaggedWord,String>();		//dictionnaire de correspondance mots du texte->wordtag correspondant
	private ArrayList<Integer> ListePositionGroupMotClefs = new ArrayList<Integer>();	
	private ArrayList<ArrayList<TaggedWord>> ParagrapheGrpMots = new ArrayList<ArrayList<TaggedWord>>();
	private ArrayList<ArrayList<ArrayList<TaggedWord>>> listeParagrapheGrpMots = new ArrayList<ArrayList<ArrayList<TaggedWord>>>();
	private int PosGrpMotsClefs = -1;
	private ArrayList<Syntagme> ListSyntagme = new ArrayList<Syntagme>();
	private ArrayList<Syntagme> ListSyntagmeTraité = new ArrayList<Syntagme>();
	private  Hashtable<Integer,ArrayList<Integer>> CliquesSynt = new Hashtable<Integer,ArrayList<Integer>>();
	private  Hashtable<Integer,ArrayList<String>> CliquesSyntString = new Hashtable<Integer,ArrayList<String>>();
	private int NombreMotTotal = -1;	//nombre total de mots important dans le champs étudié
	private  Hashtable<Integer,ArrayList<TaggedWord>> TextLemmatised = new Hashtable<Integer,ArrayList<TaggedWord>>();
	private String[] ListeStopWords = { "avoir/V", "être/V", "devoir/V",
			"pouvoir/V", "aller/V", "faire/V", "fa/V", "fai/V", "fais/V",
			"aur/V", "devr/V" };
	private PixTaggerFrench Frenchtagger;
	private SparseDoubleMatrix2D matriceOccurence;
	private Hashtable<Integer,Integer> PosReelPosAutre = new Hashtable<Integer, Integer>();

	public ChampSyntagmatique(ArrayList<String> NameFileText,int Seuil ,PixTaggerFrench tagger){


		this.setFrenchtagger(tagger);
		RecuperationTextDico(NameFileText);
		ExtractionSyntagme();
		ResultatNettoyageListeSyntagme(Seuil);
		
		

	}

	public ChampSyntagmatique(ArrayList<String> NameFileText,PixTaggerFrench tagger){


		this.setFrenchtagger(tagger);
		RecuperationTextDico(NameFileText);
		ExtractionSyntagme();
		ResultatNettoyageListeSyntagme(2);
		
		

	}
	public ChampSyntagmatique(String nameFileText,PixTaggerFrench tagger){


		numPage = 1;
		ArrayList<String> ListNameFileText = new ArrayList<String>();
		ListNameFileText.add(nameFileText);
		this.setFrenchtagger(tagger);
		if(nameFileText.contains("Pixalione")|nameFileText.contains("C:/")){
			RecuperationTextDico(ListNameFileText);
		}else{
			RecuperationTextDico(nameFileText);
		}
		ExtractionSyntagme();

	}
	
	public ChampSyntagmatique(String nameFileText,PixTaggerFrench tagger,String struct){


		numPage = 1;
		ArrayList<String> ListNameFileText = new ArrayList<String>();
		ListNameFileText.add(nameFileText);
		this.setFrenchtagger(tagger);
		//if(nameFileText.contains("Pixalione")|nameFileText.contains("C:/")){
			RecuperationTextDico(ListNameFileText);
		/*}else{
			RecuperationTextDico(nameFileText);
		}*/
		ExtractionSyntagme(struct);
		
		

	}
	
	public ChampSyntagmatique(String nameFileText,int tailleParagraphe,PixTaggerFrench tagger){


		numPage = 1;
		ArrayList<String> ListNameFileText = new ArrayList<String>();
		ListNameFileText.add(nameFileText);
		this.setFrenchtagger(tagger);
		if(nameFileText.contains("Pixalione")|nameFileText.contains("C:/")){
			RecuperationTextDico(ListNameFileText);
		}else{
			RecuperationTextDico(nameFileText);
		}
		ExtractionSyntagme();
		
		

	}
	
	public ChampSyntagmatique(String groupeMotclef,String nameFilepath,PixTaggerFrench tagger){


		numPage = 1;
		ArrayList<String> ListNameFileText = new ArrayList<String>();
		ListNameFileText.add(nameFilepath);
		this.setFrenchtagger(tagger);
		RecuperationTextDico(ListNameFileText,groupeMotclef);	//recuperation de la page a traiter
		//recuperation des mots pleins du groupe de mot
		String sentencePreTraitement = groupeMotclef;
		
		sentencePreTraitement=ModifTextStringForLemmatization(sentencePreTraitement);
		
		String[] listeStrMotClef= sentencePreTraitement.split(" ");
		ArrayList<TaggedWord> MotsPleins=new ArrayList<TaggedWord>();
		//recuperation des mots pleins
		for(int i=0;i<listeStrMotClef.length;i++){
			String strMotClef = listeStrMotClef[i];
			if(this.getDicoCorresStrWt().containsKey(strMotClef)){
				TaggedWord MotPlein = this.getDicoCorresStrWt().get(strMotClef);
				MotsPleins.add(MotPlein);
			}
		}
		ExtractionSyntagme();
		//nettoyage Liste syntagme
		ResultatNettoyageListeSyntagme(MotsPleins);
		
		

	}
        /**
         * Called Constructor
         * @param groupeMotclef
         * @param intervalle
         * @param nameFilepath
         * @param tagger 
         */
	public ChampSyntagmatique(String groupeMotclef,int intervalle, String nameFilepath,PixTaggerFrench tagger){
		groupeMotclef=groupeMotclef.toLowerCase();

		numPage = 1;
		ArrayList<String> ListNameFileText = new ArrayList<String>();
		ListNameFileText.add(nameFilepath);
		this.setFrenchtagger(tagger);
		RecuperationTextDico(ListNameFileText,groupeMotclef);	//recuperation de la page a traiter
		//recuperation des mots pleins du groupe de mot
		String sentencePreTraitement = groupeMotclef;
		
		sentencePreTraitement = Frenchtagger.ModifTextStringForLemmatization(sentencePreTraitement);
                sentencePreTraitement = Frenchtagger.tagsentenceToLemme(sentencePreTraitement);
   
		String[] listeStrMotClef= sentencePreTraitement.split(" ");
		ArrayList<TaggedWord> MotsPleins=new ArrayList<TaggedWord>();
		ArrayList<TaggedWord> MotsClef=new ArrayList<TaggedWord>();
		//recuperation des mots pleins
		for(int i=0;i<listeStrMotClef.length;i++){
			String strMotClef = listeStrMotClef[i];
			if(this.getDicoCorresStrWt().containsKey(strMotClef)){
	
				TaggedWord MotClef = this.getDicoCorresStrWt().get(strMotClef);
				MotsClef.add(MotClef);
                                
				if(MotClef.getTag().equals("NOM")|(MotClef.getTag().equals("ADJ"))|(MotClef.getTag().equals("VER"))){
					TaggedWord MotPlein = this.getDicoCorresStrWt().get(strMotClef);
					MotsPleins.add(MotPlein);
				}
			}
		}
                
                
		if(MotsPleins.size()<1){
			//cas ou il y a pas  de mots interessant à chercher
			MotsClef = new ArrayList<TaggedWord>();
		}else{
			if(MotsPleins.size()==1){
				MotsClef = new ArrayList<TaggedWord>(MotsPleins);
			}
                       
			if(!(MotsClef.get(0).getTag().equals("NOM")|(MotsClef.get(0).getTag().equals("ADJ"))|(MotsClef.get(0).getTag().equals("VER")))){
				MotsClef.remove(0);
			}
			/*if(MotsClef.size()>5){
				for(int i=5;i<MotsClef.size();i++){
					//MotsClef.remove(i);
				}
			}*/
		ExtractionSyntagme(MotsClef);
		//nettoyage Liste syntagme
		ResultatNettoyageListeSyntagme(MotsPleins);
		RecuperationListeParagraphe(this.getListePositionGroupMotClefs(), intervalle);
		RecuperationParagraphe(intervalle);
		//
		
		}  
                
	}

	public void ResultatNettoyageListeSyntagme(int Seuil){
		ArrayList<Syntagme> NewListSyntagme =new ArrayList<Syntagme>( this.getListSyntagme());
		ArrayList<Syntagme> ListSyntagme = new ArrayList<Syntagme>(this.getListSyntagme());
		SparseDoubleMatrix2D matriceOccurence = ExtractionSyntagmeMatriceOccurence();
		
		//on ne garde que les syntagmes qui apparaissent 	u moins une fois dans tous les documents
		for (int row = 0;row<matriceOccurence.rows();row++){
			DoubleMatrix1D rowMat = matriceOccurence.viewRow(row);
			int cardinal = rowMat.cardinality();
			if(cardinal<Seuil){ //si il apparait pas dans au moins Seuil doc
				
				NewListSyntagme.remove(ListSyntagme.get(row));				
			}else{
				
			}
		}
		this.setListSyntagmeTraité(NewListSyntagme);
	}


	/** Used
	 * Fonction servant a ne recuperer que les syntagmes correspondants aux mots clefs
	 * @param ListeMotsPleins - ArrayList<WordTag>
	 */
	public void ResultatNettoyageListeSyntagme(ArrayList<TaggedWord> ListeMotsPleins){
		ArrayList<Syntagme> ListSyntagmePrec = this.getListSyntagme();
		ArrayList<Syntagme> ListSyntagmePost = new ArrayList<Syntagme>();
		for (int i = 0;i<ListSyntagmePrec.size();i++){
                    
			Syntagme Syn = ListSyntagmePrec.get(i);
			ArrayList<TaggedWord> MotsPleinsSyn = Syn.getMotsPleins();
			if(MotsPleinsSyn.contains(ListeMotsPleins.get(0))){
				//ListSyntagmePost.add(Syn);
				
			}
                        
			if(MotsPleinsSyn.equals(ListeMotsPleins)){
				ListSyntagmePost.add(Syn);
			}
		}

		this.setListSyntagme(ListSyntagmePost);
	}

	/**
	 * Fonction servant à recuperer la matrice d'occurence;
	 * @return matriceOccurence - SparseDoubleMatrix2D
	 */
	private  SparseDoubleMatrix2D ExtractionSyntagmeMatriceOccurence(){
		Hashtable<Syntagme,Integer> ListeFin2 = new Hashtable<Syntagme,Integer>(this.getDicoSyntagme2());
		SparseDoubleMatrix2D matriceOccurence = new SparseDoubleMatrix2D(ListeFin2.size(),this.getNumPage());
		int numPage=-1;
		//ArrayList<Syntagme> listeSyntagme =new ArrayList<Syntagme>();
		Hashtable<Integer,ArrayList<TaggedWord>> Text = this.getTextLemmatised();
		//matrice Occurence Par Page

		for(int i =0;i<Text.size();i++){
			ArrayList<TaggedWord> phrase = Text.get(i);
			if(phrase.isEmpty()){
				//saut de page
				numPage++;
			}else{
				//recuperation des syntagmes de la phrase
				RecuperationListeSyntagme Liste = new RecuperationListeSyntagme(phrase);
				ArrayList<Syntagme> List = Liste.getListeSyntagme();

				//ajout du syntagme dans le futur dictionnaire de syntagme
				for (int j =0;j<List.size();j++){
					Syntagme syn = List.get(j);
					if(ListeFin2.containsKey(syn)){
						int idLigne= ListeFin2.get(syn);
						int idCil = numPage;
						double occurence = matriceOccurence.get(idLigne, idCil);
						occurence++;
						matriceOccurence.set(idLigne, idCil,occurence);
						
					}
				}
			}
		}
		this.setMatriceOccurence(matriceOccurence);
		return matriceOccurence;
	}


	
	/**
	 * fonction servant a recuperer le dictionnaire  grace à un ou pllusieurs fichiers textes
	 * @param nameFileTexts - ArrayList<String>
	 */
	private  void RecuperationTextDico( ArrayList<String> nameFileTexts){
                Hashtable<Integer,Integer> PosR = new Hashtable<Integer, Integer>();
		numPage = nameFileTexts.size();
		PixTaggerFrench tagger = this.getFrenchtagger();
		Hashtable<Integer,ArrayList<TaggedWord>> CorpusSaved= new Hashtable<Integer,ArrayList<TaggedWord>>();
		int indexLigne=0;
		int indexMot=0;
                int indexLigneReel=0;
		Hashtable<TaggedWord,Integer> word1= new Hashtable<TaggedWord,Integer>();
		Hashtable<Integer,TaggedWord> word2= new Hashtable<Integer,TaggedWord>();
		Hashtable<String,TaggedWord> word3= new Hashtable<String,TaggedWord>();
		Hashtable<TaggedWord,String> word4= new Hashtable<TaggedWord,String>();
		
		Iterator<String> SentenceIterator = nameFileTexts.iterator();
		while (SentenceIterator.hasNext())
		{
			//WordTag sautdePage =WordTag.valueOf("SautDePage/noTag","/");
			ArrayList<TaggedWord> sautdePage = new ArrayList<TaggedWord>();
			//page.add(sautdePage);
			CorpusSaved.put(CorpusSaved.size(), sautdePage);

			try{
				String nameFileText=SentenceIterator.next();
                                
				FileInputStream file = new FileInputStream(new File(nameFileText));				
				//si l'on veut les occurences par pages
				//int NumeroFile = nameFileTexts.indexOf(nameFileText);


				InputStreamReader Corpus=new InputStreamReader(file, Charset.forName("UTF8"));
				BufferedReader CorpusReader=new BufferedReader(Corpus);
				String ligne;
				// lecture ligne par ligne (sachant qu'une ligne correspond a une phrase)
				while ((ligne=CorpusReader.readLine())!=null){
                                    indexLigneReel++;
					if(ligne.length()<3)
					{
						ligne="";
					}
					if(!ligne.equals("")&&!ligne.isEmpty()&&!ligne.contains("\n")&&!ligne.contains("\t")&&(!ligne.equals("Â ")))
					{
						//Pretraitement du corpus
						String sentencePreTraitement=ligne;
						sentencePreTraitement=Frenchtagger.ModifTextStringForLemmatization(sentencePreTraitement);
                                                //sentencePreTraitement=Frenchtagger.tokenize(sentencePreTraitement);
						ArrayList<TaggedWord> taggedWords = Frenchtagger.tagsentence(sentencePreTraitement);  
                                                ArrayList<TaggedWord> WordsTagged = new ArrayList<TaggedWord>();
                                                
						for (int i =0;i<taggedWords.size();i++)
						{
							TaggedWord mot = taggedWords.get(i);
							if ((mot.getTagShortFomre().equals("N"))|(mot.getTagShortFomre().equals("A"))|(mot.getTagShortFomre().equals("V"))|(mot.getTag().equals("PUN"))|(mot.getTag().equals("SENT"))) //on ne garde que les adjectifs, noms, verbes et adverbes
							{
								WordsTagged.add(mot);
								String strMot = mot.getLemme();
								word4.put(mot,strMot);
                                                                
								if(!word3.containsKey(strMot)){
									word3.put(strMot,mot);
									
								}
							}else{
								WordsTagged.add(mot);
							}

						}
						if (WordsTagged.size()>=1){	//routine verification taille
                                                        PosR.put(indexLigne, indexLigneReel);
                                                        //System.out.println(indexLigne+" "+indexLigneReel);
							indexLigne = CorpusSaved.size();
							CorpusSaved.put(indexLigne,WordsTagged);
							for (int j =0;j<WordsTagged.size();j++){
								TaggedWord mot = WordsTagged.get(j);
								if(!word1.containsKey(mot))
								{
									word1.put(mot,indexMot);	//remplissage du dictionnaire de mots
									word2.put(indexMot,mot);
									indexMot++;
								}
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
		}
		this.setInvdicoCorresStrWt(word4);
		this.setDicoCorresStrWt(word3);
		this.setTextLemmatised(CorpusSaved);
		this.setwTdico(word2);
		this.setwTnodes(word1);
                this.setPosReelPosAutre(PosR);

                

                

	}

	
	/**
	 * fonction servant a recuperer le dictionnaire  grace à un ou pllusieurs fichiers textes
	 * @param nameFileTexts - ArrayList<String>
	 * @param GroupeMotClef - String
	 */
	private  void RecuperationTextDico( ArrayList<String> nameFileTexts, String GroupeMotClef){
                Hashtable<Integer,Integer> PosR = new Hashtable<Integer, Integer>();
		numPage = nameFileTexts.size();
		GroupeMotClef=GroupeMotClef.toLowerCase();
		//ArrayList<WordTag> listWtGroupMotClefs = new ArrayList<WordTag>();
		ArrayList<Integer> ListePositionGroupMotClefs = new ArrayList<Integer>();
		PixTaggerFrench tagger = this.getFrenchtagger();
		Hashtable<Integer,ArrayList<TaggedWord>> CorpusSaved= new Hashtable<Integer,ArrayList<TaggedWord>>();
		int indexLigne=0;
                int indexLigneReel=0;
		int indexMot=0;
		boolean presenceMotsclef = false;

		Hashtable<TaggedWord,Integer> word1= new Hashtable<TaggedWord,Integer>();
		Hashtable<Integer,TaggedWord> word2= new Hashtable<Integer,TaggedWord>();
		Hashtable<String,TaggedWord> word3= new Hashtable<String,TaggedWord>();
		Hashtable<TaggedWord,String> word4= new Hashtable<TaggedWord,String>();
		
		Iterator<String> SentenceIterator = nameFileTexts.iterator();
		while (SentenceIterator.hasNext()&!presenceMotsclef)
		{
			//WordTag sautdePage =WordTag.valueOf("SautDePage/noTag","/");
			//ArrayList<WordTag> sautdePage = new ArrayList<WordTag>();
			//page.add(sautdePage);
			//CorpusSaved.put(CorpusSaved.size(), sautdePage);

			try{
				String nameFileText=SentenceIterator.next();
				FileInputStream file = new FileInputStream(new File(nameFileText));				
				//si l'on veut les occurences par pages
				//int NumeroFile = nameFileTexts.indexOf(nameFileText);


				InputStreamReader Corpus=new InputStreamReader(file, Charset.forName("UTF8"));
				BufferedReader CorpusReader=new BufferedReader(Corpus);
				String ligne;
				// lecture ligne par ligne (sachant qu'une ligne correspond a une phrase)
				while ((ligne=CorpusReader.readLine())!=null&!presenceMotsclef){
                                    indexLigneReel++;
					if(ligne.length()<3)
					{
						ligne="";
					}
					if(!ligne.equals("")&&!ligne.isEmpty()&&!ligne.contains("\n")&&!ligne.contains("\t")&&(!ligne.equals("Â ")))
					{
						//Pretraitement du corpus
						String sentencePreTraitement=ligne;
						
						//String sentencePostTraitement="";
						//sentencePreTraitement=sentencePreTraitement.toLowerCase();
						boolean presenceMotClefs = sentencePreTraitement.toLowerCase().contains(GroupeMotClef);
						if (presenceMotClefs){
							this.setPosGrpMotsClefs(CorpusSaved.size());
							ListePositionGroupMotClefs.add(CorpusSaved.size());
						}
						
						
						
						sentencePreTraitement=Frenchtagger.ModifTextStringForLemmatization(sentencePreTraitement);
                                                //sentencePreTraitement=Frenchtagger.tokenize(sentencePreTraitement);
                                                
						ArrayList<TaggedWord> taggedWords = Frenchtagger.tagsentence(sentencePreTraitement); 

						ArrayList<TaggedWord> WordsTagged = new ArrayList<TaggedWord>();
						for (int i =0;i<taggedWords.size();i++)
						{
							TaggedWord mot = taggedWords.get(i);
							if ((mot.getTagShortFomre().equals("N"))|(mot.getTagShortFomre().equals("A"))|(mot.getTagShortFomre().equals("V"))|(mot.getTag().equals("PUN"))|(mot.getTag().equals("SENT"))) //on ne garde que les adjectifs, noms, verbes et adverbes
							{
                                                                
								WordsTagged.add(mot);
								String strMot = mot.getLemme().toLowerCase();
								word4.put(mot,strMot);
								if(!word3.containsKey(strMot)&presenceMotClefs){
									word3.put(strMot, mot);
									}
							}else{
								WordsTagged.add(mot);
								String strMot = mot.getLemme().toLowerCase();
								word4.put(mot,strMot);
								if(!word3.containsKey(strMot)&presenceMotClefs){
									word3.put(strMot, mot);
									}
							}				

						}
						if (WordsTagged.size()>=1){	//routine verification taille

							indexLigne = CorpusSaved.size();
							CorpusSaved.put(indexLigne,WordsTagged);
                                                        //System.out.println(indexLigne+" "+indexLigneReel);
                                                        PosR.put(indexLigne, indexLigneReel);
							for (int j =0;j<WordsTagged.size();j++){
								TaggedWord mot = WordsTagged.get(j);
								if(!word1.containsKey(mot))
								{
									word1.put(mot,indexMot);	//remplissage du dictionnaire de mots
									word2.put(indexMot,mot);
									indexMot++;
								}
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
		}
		this.setInvdicoCorresStrWt(word4);
		this.setDicoCorresStrWt(word3);
		this.setTextLemmatised(CorpusSaved);
		this.setwTdico(word2);
		this.setwTnodes(word1);
		this.setListePositionGroupMotClefs(ListePositionGroupMotClefs);
                this.setPosReelPosAutre(PosR);

	}
	/**
	 * 
	 * 
	 */
	public ArrayList<ArrayList<TaggedWord>> RecuperationParagraphe(int intervalle){
		intervalle++;
		int posGrpMot = this.getPosGrpMotsClefs();
		int tailleCorpus = this.getTextLemmatised().size();
		//verif si on est pas proche du debut ou de la fin du texte
		if(posGrpMot+intervalle>tailleCorpus){
			int translation = posGrpMot+intervalle-tailleCorpus;
			posGrpMot=posGrpMot-translation;
		}
		if(posGrpMot-intervalle<0){
			int translation = intervalle-posGrpMot;
			posGrpMot=posGrpMot+translation;
		}
		//recup paragraphe
		ArrayList<ArrayList<TaggedWord>> paragraphe= new ArrayList<ArrayList<TaggedWord>>();
		for (int i =posGrpMot-intervalle;i<posGrpMot+intervalle;i++){
			paragraphe.add(this.getTextLemmatised().get(i));
		}
		this.setParagrapheGrpMots(paragraphe);
		return paragraphe;
		
	}
	/**
	 * 
	 * @param posGrpMot
	 * @param intervalle
	 * @return
	 */
	public ArrayList<ArrayList<TaggedWord>> RecuperationParagraphe(int posGrpMot,int intervalle){
		intervalle++;
		int tailleCorpus = this.getTextLemmatised().size();
		//verif si on est pas proche du debut ou de la fin du texte
		if(posGrpMot+intervalle>tailleCorpus){
			int translation = posGrpMot+intervalle-tailleCorpus;
			posGrpMot=posGrpMot-translation;
		}
		if(posGrpMot-intervalle<0){
			int translation = intervalle-posGrpMot;
			posGrpMot=posGrpMot+translation;
		}
		//recup paragraphe
		ArrayList<ArrayList<TaggedWord>> paragraphe= new ArrayList<ArrayList<TaggedWord>>();
		for (int i =posGrpMot-intervalle;i<posGrpMot+intervalle;i++){
			paragraphe.add(this.getTextLemmatised().get(i));
		}
		this.setParagrapheGrpMots(paragraphe);
		return paragraphe;
		
	}
	/**
	 * 
	 * @param listPosGrpMots
	 * @param intervalle
	 * @return
	 */
	public ArrayList<ArrayList<ArrayList<TaggedWord>>> RecuperationListeParagraphe(ArrayList<Integer> listPosGrpMots,int intervalle){
		
		ArrayList<ArrayList<ArrayList<TaggedWord>>> listparagraphe= new ArrayList<ArrayList<ArrayList<TaggedWord>>>();
		
		for (int i=0; i<listPosGrpMots.size();i++){
			int posGrpMot = listPosGrpMots.get(i);
			ArrayList<ArrayList<TaggedWord>> paragraphe= new ArrayList<ArrayList<TaggedWord>>(RecuperationParagraphe(posGrpMot,intervalle));
			listparagraphe.add(paragraphe);
		}
		if(listparagraphe.size()>1){
			
		}
		return listparagraphe;
	}

	
	/**
	 * fonction servant a recuperer le dictionnaire  grace à un texte sous format string
	 * @param Text - String
	 */
	private  void RecuperationTextDico(String Text){

		PixTaggerFrench tagger = this.getFrenchtagger();
		Hashtable<Integer,ArrayList<TaggedWord>> CorpusSaved= new Hashtable<Integer,ArrayList<TaggedWord>>();
		int indexLigne=0;
		int indexMot=0;

		Hashtable<TaggedWord,Integer> word1= new Hashtable<TaggedWord,Integer>();
		Hashtable<Integer,TaggedWord> word2= new Hashtable<Integer,TaggedWord>();
		Hashtable<String,TaggedWord> word3= new Hashtable<String,TaggedWord>();
		Hashtable<TaggedWord,String> word4= new Hashtable<TaggedWord,String>();
		
		String[] ligneText = {Text};
		/*if(Text.contains(". ")){
		ligneText = Text.split("\\. ");
		}*/
		for (int l =0;l<ligneText.length;l++)
		{


			String ligne= ligneText[l];
			if(ligne.length()<3)
			{
				ligne="";
			}
			if(!ligne.equals("")&&!ligne.isEmpty()&&!ligne.contains("\n")&&!ligne.contains("\t")&&(!ligne.equals("Â ")))
			{
				//Pretraitement du corpus
				String sentencePreTraitement=ligne;
				sentencePreTraitement=Frenchtagger.ModifTextStringForLemmatization(sentencePreTraitement);
                                sentencePreTraitement=Frenchtagger.tokenize(sentencePreTraitement);
				ArrayList<TaggedWord> taggedWords = Frenchtagger.tagsentence(sentencePreTraitement);
				ArrayList<TaggedWord> WordsTagged = new ArrayList<TaggedWord>();

				for (int i =0;i<taggedWords.size();i++)
				{
					TaggedWord mot = taggedWords.get(i);
					if ((mot.getTag().equals("N"))|(mot.getTag().equals("A"))|(mot.getTag().equals("V"))) //on ne garde que les adjectifs, noms, verbes et adverbes
					{
						WordsTagged.add(mot);
						String strMot = mot.getLemme().toLowerCase();
						word4.put(mot,strMot);
						if(!word3.containsKey(strMot)){
							word3.put(strMot, mot);
							}
					}else{
						WordsTagged.add(mot);
					}


				}
				if (WordsTagged.size()>=1){	//routine verification taille

					CorpusSaved.put(indexLigne,WordsTagged);
					indexLigne++;
					for (int j =0;j<WordsTagged.size();j++){
						TaggedWord mot = WordsTagged.get(j);
						if(!word1.containsKey(mot))
						{
							word1.put(mot,indexMot);	//remplissage du dictionnaire de mots
							word2.put(indexMot,mot);
							indexMot++;
						}
					}
				}
			}
		}
		this.setInvdicoCorresStrWt(word4);
		this.setDicoCorresStrWt(word3);
		this.setTextLemmatised(CorpusSaved);
		this.setwTdico(word2);
		this.setwTnodes(word1);

	}


	
	/**
	 * Fonction servant à extraire les syntagmes d'un texte
	 */
	private void ExtractionSyntagme(){
		Hashtable<Integer,Syntagme> ListeFin = new Hashtable<Integer,Syntagme>();
		Hashtable<Syntagme,Integer> ListeFin2 = new Hashtable<Syntagme,Integer>();
		ArrayList<Syntagme> listeSyntagme =new ArrayList<Syntagme>();
		Hashtable<Integer,ArrayList<TaggedWord>> Text = this.getTextLemmatised();
		for(int i =0;i<Text.size();i++){
			ArrayList<TaggedWord> phrase = Text.get(i);
			if(phrase.isEmpty()){
				//saut de page
				
			}else{
				//recuperation des syntagmes de la phrase
				RecuperationListeSyntagme Liste = new RecuperationListeSyntagme(phrase);
				ArrayList<Syntagme> List = Liste.getListeSyntagme();

				//ajout du syntagme dans le futur dictionnaire de syntagme
				for (int j =0;j<List.size();j++){
					Syntagme syn = List.get(j);
					if(listeSyntagme.contains(syn)){					
						
					}
					if(!listeSyntagme.contains(syn)){
						Integer index = ListeFin.size();
						ListeFin.put(index, syn);
						ListeFin2.put(syn, index);
						listeSyntagme.add(syn);
						

					}
				}
			}
		}
		this.setDicoSyntagme2(ListeFin2);
		this.setDicoSyntagme(ListeFin);
		this.setListSyntagme(listeSyntagme);
	}

	/**
	 * Fonction servant à extraire les syntagmes d'un texte
	 */
	private void ExtractionSyntagme(ArrayList<TaggedWord> GrpMotsClefs){
		Hashtable<Integer,Syntagme> ListeFin = new Hashtable<Integer,Syntagme>();
		Hashtable<Syntagme,Integer> ListeFin2 = new Hashtable<Syntagme,Integer>();
		ArrayList<Syntagme> listeSyntagme =new ArrayList<Syntagme>();
		Hashtable<Integer,ArrayList<TaggedWord>> Text = this.getTextLemmatised();
                
		for(int i =0;i<Text.size();i++){
			ArrayList<TaggedWord> phrase = Text.get(i);
                      
                        if(phrase.isEmpty()){
				//saut de page
				
			}else{
				//recuperation des syntagmes de la phrase
				RecuperationListeSyntagme Liste = new RecuperationListeSyntagme(phrase,GrpMotsClefs);
				ArrayList<Syntagme> List = Liste.getListeSyntagme();
                               
				//ajout du syntagme dans le futur dictionnaire de syntagme
				for (int j =0;j<List.size();j++){
					Syntagme syn = List.get(j);
                                        
					if(listeSyntagme.contains(syn)){					
						
					}
					if(!listeSyntagme.contains(syn)){
						Integer index = ListeFin.size();
						ListeFin.put(index, syn);
						ListeFin2.put(syn, index);
						listeSyntagme.add(syn);
						

					}
				}
			}
		}
		this.setDicoSyntagme2(ListeFin2);
		this.setDicoSyntagme(ListeFin);
		this.setListSyntagme(listeSyntagme);
                
	}
	
	/**
	 * Fonction servant à extraire les syntagmes d'un texte
	 */
	private void ExtractionSyntagme(String StructGrpMotsClefs){
		Hashtable<Integer,Syntagme> ListeFin = new Hashtable<Integer,Syntagme>();
		Hashtable<Syntagme,Integer> ListeFin2 = new Hashtable<Syntagme,Integer>();
		ArrayList<Syntagme> listeSyntagme =new ArrayList<Syntagme>();
		Hashtable<Integer,ArrayList<TaggedWord>> Text = this.getTextLemmatised();
		for(int i =0;i<Text.size();i++){
			ArrayList<TaggedWord> phrase = Text.get(i);
			if(phrase.isEmpty()){
				//saut de page
				
			}else{
				//recuperation des syntagmes de la phrase
				RecuperationListeSyntagme Liste = new RecuperationListeSyntagme(phrase,StructGrpMotsClefs);
				ArrayList<Syntagme> List = Liste.getListeSyntagme();

				//ajout du syntagme dans le futur dictionnaire de syntagme
				for (int j =0;j<List.size();j++){
					Syntagme syn = List.get(j);
					if(listeSyntagme.contains(syn)){					
						
					}
					if(!listeSyntagme.contains(syn)){
						Integer index = ListeFin.size();
						ListeFin.put(index, syn);
						ListeFin2.put(syn, index);
						listeSyntagme.add(syn);
						

					}
				}
			}
		}
		this.setDicoSyntagme2(ListeFin2);
		this.setDicoSyntagme(ListeFin);
		this.setListSyntagme(listeSyntagme);
	}

	
	/**
	 * Fonction servant à calculer la distance Semantique(synonymie) entre deux mots (plus la distance est faible, plus les mots sont proches)
	 * @param Mot1 - WordTag
	 * @param Mot2 - WordTag
	 * @param DicoVoisinage - Hashtable<TaggedWord, ArrayList<TaggedWord>>
	 * @return Distance - double
	 */
	public static  double DistanceSemEuclidienneEntreMotsPixa(TaggedWord Mot1, TaggedWord Mot2,
			Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage)
	{

		double Distance;
		if((!Mot1.equals(Mot2))){
			//si mot1 different Mot2
			if((Mot1.getTag().equals(Mot2.getTag()))){
				//si mot1 et mot2 ont le meme tag

				if((DicoVoisinage.containsKey(Mot1))&(DicoVoisinage.containsKey(Mot2))){
					//si les deux mots sont present dans le dictionnaire de voisinage
					ArrayList<TaggedWord> ListVoisinMot1 = new ArrayList<TaggedWord>(DicoVoisinage.get(Mot1));
					ArrayList<TaggedWord> ListVoisinMot2 = new ArrayList<TaggedWord>(DicoVoisinage.get(Mot2));
					if(ListVoisinMot1.contains(Mot2)&ListVoisinMot2.contains(Mot1)){
						//si mot2 synonyme de mot1

						ListVoisinMot1.remove(Mot2); //elimination de mot2 pour pouvoir normaliser
						ListVoisinMot2.remove(Mot1); //elimination de mot1 pour pouvoir normaliser

						ArrayList<TaggedWord> ListVoisinTotal = new ArrayList<TaggedWord>(ListVoisinMot1);
						for (int i = 0;i<ListVoisinMot2.size();i++){
							TaggedWord IdMotSyn = ListVoisinMot2.get(i);
							if(!ListVoisinTotal.contains(IdMotSyn)){
								ListVoisinTotal.add(IdMotSyn);
							}
						}
						ArrayList<TaggedWord> ListVoisinCommun=new ArrayList<TaggedWord>();
						for (int i = 0;i<ListVoisinTotal.size();i++){
							TaggedWord IdMotSyn = ListVoisinTotal.get(i);
							if(ListVoisinMot1.contains(IdMotSyn)&ListVoisinMot2.contains(IdMotSyn)){
								ListVoisinCommun.add(IdMotSyn);
							}
						}

						if (ListVoisinCommun.size()==0){
							//si les deux mots n'ont rien en commun
							Distance=1.0;
						}else{
							double dist = ListVoisinTotal.size()-ListVoisinCommun.size();
							Distance = dist/ListVoisinTotal.size();
						}
					}
					else{//si les deux mots n'ont aucun lien de synonymie
						Distance = 1.0;
					}
				}else{//si au moins un des deux mots n'est pas present dans le dictionnaire
					Distance = 1.0;
				}
			}
			else{//si mot1 et mot2 ont des tags différents
				Distance=1.0;
				
			}

		}else{
			Distance = 0.0;	//si mot1 = mot2
		}
		if(1-Distance>0.5){
			
		}
		else if(1-Distance>0.0&1-Distance<0.5){
			
		}
		else{
			
		}
		return Distance;
	}

	/**
	 * Fonction servant à calculer la distance Semantique(synonymie) entre deux mots (plus la distance est faible, plus les mots sont proches)
	 * @param //Mot1 - TaggedWord
	 * @param //Mot2 - TaggedWord
	 * @param DicoVoisinage - Hashtable<WordTag, ArrayList<WordTag>>
	 * @return Distance - double
	 */
	public static  double DistanceSemEuclidienneEntreMots(TaggedWord mot1, TaggedWord mot2,
			Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage)
	{
		TaggedWord Mot1 = new TaggedWord(mot1);
		TaggedWord Mot2 = new TaggedWord(mot2);
		
		//si l'on compare un nom et un adjectif (pour regler les problemes d'erreur de Tag)
				String tags = Mot1.getTag()+Mot2.getTag();
				if(tags.equals("AN")|tags.equals("NA")){
					if(DicoVoisinage.containsKey(Mot1)){
						Mot2.setTag(Mot1.getTag());
					}else if(DicoVoisinage.containsKey(Mot2)){
						Mot1.setTag(Mot2.getTag());
					}else if(Mot1.getLemme().equals(Mot2.getLemme())){
						Mot1.setTag(Mot2.getTag());
					}
				}

		double Distance;
		if((!Mot1.equals(Mot2))){
			//si mot1 different Mot2
			if((Mot1.getTag().equals(Mot2.getTag()))){
				//si mot1 et mot2 ont le meme tag

				if((DicoVoisinage.containsKey(Mot1))&(DicoVoisinage.containsKey(Mot2))){
					//si les deux mots sont present dans le dictionnaire de voisinage
					ArrayList<TaggedWord> ListVoisinMot1 = new ArrayList<TaggedWord>(DicoVoisinage.get(Mot1));
					ArrayList<TaggedWord> ListVoisinMot2 = new ArrayList<TaggedWord>(DicoVoisinage.get(Mot2));
					if(ListVoisinMot1.contains(Mot2)&ListVoisinMot2.contains(Mot1)){
						//si mot2 synonyme de mot1

						ListVoisinMot1.remove(Mot2); //elimination de mot2 pour pouvoir normaliser
						ListVoisinMot2.remove(Mot1); //elimination de mot1 pour pouvoir normaliser

						ArrayList<TaggedWord> ListVoisinTotal = new ArrayList<TaggedWord>(ListVoisinMot1);
						for (int i = 0;i<ListVoisinMot2.size();i++){
							TaggedWord IdMotSyn = ListVoisinMot2.get(i);
							if(!ListVoisinTotal.contains(IdMotSyn)){
								ListVoisinTotal.add(IdMotSyn);
							}
						}
						ArrayList<TaggedWord> ListVoisinCommun=new ArrayList<TaggedWord>();
						for (int i = 0;i<ListVoisinTotal.size();i++){
							TaggedWord IdMotSyn = ListVoisinTotal.get(i);
							if(ListVoisinMot1.contains(IdMotSyn)&ListVoisinMot2.contains(IdMotSyn)){
								ListVoisinCommun.add(IdMotSyn);
							}
						}

						if (ListVoisinCommun.size()==0){
							//si les deux mots n'ont rien en commun
							Distance=1.0;
						}else{
							double dist = ListVoisinTotal.size()-ListVoisinCommun.size();
							Distance = dist/ListVoisinTotal.size();
						}
					}
					else{//si les deux mots n'ont aucun lien de synonymie
						Distance = 1.0;
					}
				}else{//si au moins un des deux mots n'est pas present dans le dictionnaire
					Distance = 1.0;
				}
			}
			else{//si mot1 et mot2 ont des tags différents
				Distance=1.0;
			}

		}else{
			Distance = 0.0;	//si mot1 = mot2
		}
		return Distance;
	}

	/**
	 * Fonction servant à calculer la distance Semantique entre deux groupes de lemmes
	 * @param groupe1 - ArrayList<TaggedWord> groupe1
	 * @param groupe2 - ArrayList<TaggedWord> groupe1
	 * @param DicoVoisinage - Hashtable<TaggedWord, ArrayList<TaggedWord>>
	 * @return Distance - double
	 */
	public static Double DistanceMinEntre2groupesLemme (ArrayList<TaggedWord> groupe1, ArrayList<TaggedWord> groupe2,Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage){
		Double Distance=0.0;
		ArrayList<TaggedWord> groupMax=groupe2;
		ArrayList<TaggedWord> groupMin=groupe1;
		if (groupe1.size()>groupe2.size()){
			groupMax=groupe1;
			groupMin=groupe2;
		}
		for (int i = 0;i<groupMin.size();i++){
			double distMin = Double.MAX_VALUE;
			TaggedWord lemme1 = groupMin.get(i);
			for (int j=0;j<groupMax.size();j++){
				TaggedWord lemme2 = groupMax.get(j);
				double dist = DistanceSemEuclidienneEntreMots(lemme1,lemme2,DicoVoisinage);
				if(dist<distMin){
					distMin=dist;
				}
			}
			Distance=Distance+distMin;
		}
		if(Distance.isNaN())
		{
			
		}
		return Distance/groupMin.size();
	}
	
	/**
	 * Fonction servant à calculer la distance Semantique entre deux groupes de lemmes
	 * @param groupe1
	 * @param groupe2
	 * @param DicoVoisinage
	 * @return
	 */
	public static Double DistanceMinEntre2groupesLemmePixaAffichage (ArrayList<TaggedWord> groupe1, ArrayList<TaggedWord> groupe2,Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage){
		Double Distance=0.0;
		Double Distancetest=0.0;
		ArrayList<TaggedWord> groupMax=groupe2;
		ArrayList<TaggedWord> groupMin=groupe1;
		if (groupe1.size()>groupe2.size()){
			groupMax=groupe1;
			groupMin=groupe2;
		}
		DoubleMatrix2D mat = new SparseDoubleMatrix2D(groupMax.size(),groupMin.size());
		for (int i = 0;i<groupMax.size();i++){
			double distMin = Double.MAX_VALUE;
			TaggedWord lemme1 = groupMax.get(i);
			for (int j=0;j<groupMin.size();j++){
				TaggedWord lemme2 = groupMin.get(j);
				double dist = DistanceSemEuclidienneEntreMotsPixa(lemme1,lemme2,DicoVoisinage);
				mat.set(i, j, dist);
				if(dist<distMin){
					distMin=dist;
				}
			}
			Distance=Distance+distMin;
		}
		for (int i=0;i<groupMax.size();i++){
			Distancetest = Distancetest + RecuperationDistMinetNettoyageMatrice(mat);
		}
		if(Distance.isNaN())
		{
			
		}
		if(!Distance.equals(Distancetest)){
			
		}
		return Distance/groupMax.size();
	}

	/**
	 * Fonction servant à calculer la distance Semantique entre deux groupes de lemmes
	 * @param groupe1
	 * @param groupe2
	 * @param DicoVoisinage
	 * @return
	 */
	public static Double DistanceMinEntre2groupesLemmePixa (ArrayList<TaggedWord> groupe1, ArrayList<TaggedWord> groupe2,Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage){
		Double Distance=0.0;
		Double Distancetest=0.0;
		ArrayList<TaggedWord> groupMax=groupe2;
		ArrayList<TaggedWord> groupMin=groupe1;
		if (groupe1.size()>groupe2.size()){
			groupMax=groupe1;
			groupMin=groupe2;
		}
		DoubleMatrix2D mat = new SparseDoubleMatrix2D(groupMax.size(),groupMin.size());
		for (int i = 0;i<groupMax.size();i++){
			double distMin = Double.MAX_VALUE;
			TaggedWord lemme1 = groupMax.get(i);
			for (int j=0;j<groupMin.size();j++){
				TaggedWord lemme2 = groupMin.get(j);
				double dist = DistanceSemEuclidienneEntreMots(lemme1,lemme2,DicoVoisinage);
				mat.set(i, j, dist);
				if(dist<distMin){
					distMin=dist;
				}
			}
			Distance=Distance+distMin;
		}
		for (int i=0;i<groupMax.size();i++){
			Distancetest = Distancetest + RecuperationDistMinetNettoyageMatrice(mat);
		}
		if(Distance.isNaN())
		{
			
		}
		if(!Distance.equals(Distancetest)){
			
		}
		return Distance/groupMax.size();
	}

	
	/**
	 * Fonction servant à calculer la distance Semantique entre deux groupes de mots de liaisosns
	 * @param groupe1
	 * @param groupe2
	 * @return
	 */
	public static Double DistanceMinEntre2groupesMotsLiaisons (ArrayList<TaggedWord> groupe1, ArrayList<TaggedWord> groupe2){
		Double Distance=0.0;
		ArrayList<TaggedWord> groupMax=groupe2;
		ArrayList<TaggedWord> groupMin=groupe1;
		if (groupe1.size()>groupe2.size()){
			groupMax=groupe1;
			groupMin=groupe2;
		}
		//faut mette une pondeation
		if (groupe1.size()*groupe2.size()!=0){
			for (int i = 0;i<groupMin.size();i++){
				double distMin = 1.0;
				TaggedWord lemme1 = groupMin.get(i);
				for (int j=0;j<groupMax.size();j++){
					TaggedWord lemme2 = groupMax.get(j);
					if(lemme2.getTag().equals(lemme1.getTag())){
						distMin=0.0;
					}
				}
				Distance=Distance+distMin;
			}

			if(Distance.isNaN())
			{
				
			}
			Distance=Distance/groupMin.size();
		}
		return Distance;
	}

	
	public static void AffichageIntersectionEnsembleSyntagme(ChampSyntagmatique EnsSyn1,ChampSyntagmatique EnsSyn2){
		
		ArrayList<Syntagme> newEnsSyn1 = new ArrayList<Syntagme>(EnsSyn1.getListSyntagme());
		ArrayList<Syntagme> newEnsSyn2 = new ArrayList<Syntagme>(EnsSyn2.getListSyntagme());
		
		ArrayList<Syntagme> intersection =  new ArrayList<Syntagme>();
		for (int i = 0;i<newEnsSyn1.size();i++){
			Syntagme Syn1 = newEnsSyn1.get(i);
			ArrayList<TaggedWord> MotsPleinsSyn1 = Syn1.getMotsPleins();
			for(int j=0;j<newEnsSyn2.size();j++){
				Syntagme Syn2 = newEnsSyn2.get(j);
				ArrayList<TaggedWord> MotsPleinsSyn2 = Syn2.getMotsPleins();
				
				if(MotsPleinsSyn2.equals(MotsPleinsSyn1)){
					intersection.add(Syn1);
					intersection.add(Syn2);
					
				}
			}
			
		}
		
	}
	
	/**
	 * Fonction servant à calculer la distance Semantique entre deux syntagme
	 * @param Syn1
	 * @param Syn2
	 * @param DicoVoisinage
	 * @return
	 */
	public static Double DistanceMinEntre2Syntagme (Syntagme Syn1,Syntagme Syn2,Hashtable<TaggedWord,ArrayList<TaggedWord>> DicoVoisinage){
		Double Distance=0.0;
		//recup des caracterisitiques
		ArrayList<TaggedWord> MotsDeFonctions1 = Syn1.getMotsDeFonctions();
		ArrayList<TaggedWord> MotsPleins1= Syn1.getMotsPleins();
		Integer StructureSyntaxique1= Syn1.getStructureSyntaxique();
		ArrayList<TaggedWord> MotsDeFonctions2 = Syn2.getMotsDeFonctions();
		ArrayList<TaggedWord> MotsPleins2 = Syn2.getMotsPleins();
		Integer StructureSyntaxique2= Syn2.getStructureSyntaxique();

		if (!StructureSyntaxique2.equals(StructureSyntaxique1)){
			Distance=1.0;
		}
		else{
			Double Distance1 = 1.0* DistanceMinEntre2groupesLemme(MotsPleins1,MotsPleins2,DicoVoisinage);
			Double Distance2 = 0.0*DistanceMinEntre2groupesMotsLiaisons(MotsDeFonctions1,MotsDeFonctions2);
			Distance = Distance+ Distance1+Distance2;
			if(Distance.isNaN())
			{
				
			}
		}

		if(Distance!=1.0){
			
		}
		return Distance;
	}
	
	/**
	 * Fonction servant à calculer la distance Semantique entre deux syntagme configurer pour pixalione
	 * @param Syn1
	 * @param Syn2
	 * @param DicoVoisinage
	 * @return
	 */
	public static Double DistanceMinEntre2SyntagmePixa (Syntagme Syn1,Syntagme Syn2,Hashtable<TaggedWord,ArrayList<TaggedWord>> DicoVoisinage){
		Double Distance=1.0;
		
		double Distancenormé = 0.0;
		String Text ="";
		//recup des caracterisitiques
		// ArrayList<WordTag> MotsDeFonctions1 = Syn1.getMotsDeFonctions();
		ArrayList<TaggedWord> MotsPleins1= Syn1.getMotsPleins();
		//Integer StructureSyntaxique1= Syn1.getStructureSyntaxique();
		// ArrayList<WordTag> MotsDeFonctions2 = Syn2.getMotsDeFonctions();
		ArrayList<TaggedWord> MotsPleins2 = Syn2.getMotsPleins();
		//Integer StructureSyntaxique2= Syn2.getStructureSyntaxique();


		//on prend syn1 comme groupe de mots clefs et syn2 comme groupe de mot du texte
		ArrayList<TaggedWord> MotsPleinsSyn1;
		ArrayList<TaggedWord> MotsPleinsSyn2;

		MotsPleinsSyn1= new  ArrayList<TaggedWord>(MotsPleins1);
		MotsPleinsSyn2=new  ArrayList<TaggedWord>(MotsPleins2);

		//Distance Synonymique
		Double DistanceSyn = 1- DistanceMinEntre2groupesLemmePixa(MotsPleins1,MotsPleins2,DicoVoisinage);
		Text = Text+"\n meilleure Distance synonymique entre 2 groupes de mots : "+DistanceSyn +"\n";
		boolean EgaliteParfaite=MotsPleins1.equals(MotsPleins2);
		if(!EgaliteParfaite){
			Distance = DistanceSyn+1;
		}
		//Distance Structurelle
		/*Double DistanceStruct;
		 	if (StructureSyntaxique2.equals(StructureSyntaxique1)){
		 		DistanceStruct=0.0;
			}else{
				DistanceStruct=1.0;
			}*/

		//Distance Position des mots
		//marche si il ya au moins une synonymie
		if((DistanceSyn!=0.0)){//&!EgaliteParfaite){
			//on prend syn1 comme groupe de mots clefs et syn2 comme groupe de mot du texte
			//avec Syn1 ayant une taille faible par rapport à syn2

			//remplacement des mots du groupe de mots par leur mots clefs synonymes
			//recuperation du synonyme le plus fort des mots clefs et leur distance semantique;

			RecupCaracSyn recup = new RecupCaracSyn(Syn1,Syn2 , DicoVoisinage);
			Hashtable<TaggedWord,TaggedWord> DicoSyn = recup.getDicoSyn();	//dictionnaire permettant l'association entre les deux syntagmes

			/*if(DicoSyn.size()==MotsPleinsSyn1.size()){
				
			}*/

			Hashtable<TaggedWord,Double> DicoSynDist = recup.getDicoSynDist();

			//nombre de mots clefs present dans le groupe de mots
			int nbKeyWord = DicoSynDist.size();
			//nombre de mots clefs absents du groupe de mots
			int nbAbsKeyWord = MotsPleinsSyn1.size() - nbKeyWord;

			//tableau de denominateur  pour les score de mots clefs pour le cas d'absences de mots clefs
			ArrayList<Integer> Listdenom = new ArrayList<Integer>();
			nbAbsKeyWord=0;	//on met a zero pour le test
			if(nbAbsKeyWord==0){ //ts les mots clefs sont present
				for(int j=0;j<2;j++){
					Listdenom.add(1);
				}
				for(int j=2;j<MotsPleinsSyn1.size();j++){
					Listdenom.add(1);
				}
			}
			if(nbAbsKeyWord==1){ //ts les mots clefs sont present
				Listdenom.add(8);
				Listdenom.add(10);
				for(int j=2;j<MotsPleinsSyn1.size();j++){
					Listdenom.add(1);
				}
			}
			if(nbAbsKeyWord>=2){ //ts les mots clefs sont present
				Listdenom.add(16);
				Listdenom.add(20);
				for(int j=2;j<MotsPleinsSyn1.size();j++){
					Listdenom.add(1);
				}
			}

			//remplacement des mots du groupe de mots par leur mots clefs synonymes
			/**/
			ArrayList<TaggedWord> NewGroupMot = new ArrayList<TaggedWord>();
			for (int i =0;i<MotsPleinsSyn2.size();i++){
				TaggedWord Mot2 = MotsPleinsSyn2.get(i);
				if(DicoSyn.containsKey(Mot2)){
					Mot2 = DicoSyn.get(Mot2);
				}
				NewGroupMot.add(Mot2);
			}


			//etablissement des scores pour la presence des mots clefs
			ArrayList<TaggedWord> ListeMotsClefsPresentNonOrdonnée = new ArrayList<TaggedWord>(DicoSyn.values());
			ArrayList<TaggedWord> ListeMotsClefsPresent = new ArrayList<TaggedWord>();
			// rangement en ordre de laiste de mots clefs presents
			for(int l=0;l<MotsPleinsSyn1.size();l++){
				TaggedWord motclef = MotsPleinsSyn1.get(l);
				if(ListeMotsClefsPresentNonOrdonnée.contains(motclef)){
					ListeMotsClefsPresent.add(motclef);
				}
			}

			
			
			ArrayList<Double> ListeScoreCorrelation = new ArrayList<Double>();	//sert a stocker le score apres correlation
			ArrayList<Double> ListeScoreDist = new ArrayList<Double>();	//sert a stocker le score de distance sans correlation
			//Cas ou il y a plus de deux mots clefs en entree
			int compteur=0;
			int nbreKeyWord = MotsPleinsSyn1.size();
			for(int k=0;k<MotsPleinsSyn1.size();k++){

				TaggedWord MotClef1 = MotsPleinsSyn1.get(k);
				if(ListeMotsClefsPresent.contains(MotClef1)){
					if (NewGroupMot.contains(MotClef1)){
						//si le mot clef est present ( si il y aun mot du groupe original qui lui est synonyme
						int PosDsGrpMotClef1 = MotsPleinsSyn1.indexOf(MotClef1)+1;	//position du mot clefs dans le groupe de mots clefs
						int PosDsGrpMot = NewGroupMot.indexOf(MotClef1)+1;	//position du mot clefs dans le groupe de mots du texte
						if(compteur+1<ListeMotsClefsPresent.size()){
							TaggedWord MotClef2 = ListeMotsClefsPresent.get(compteur+1);
							int PosDsGrpMotClef2 = MotsPleinsSyn1.indexOf(MotClef2)+1;	//position du mot clefs dans le groupe de mots clefs

							//calcul du score 
							//double ScoreMot = (20/3*(3-Math.log(PosDsGrpMot)))/Math.abs(PosDsGrpMotClef1-PosDsGrpMotClef2);
							int valLN = Math.abs(PosDsGrpMot- (MotsPleinsSyn1.indexOf(MotClef1)+1))+1;
							//double ScoreMot = (20)*(1-Math.log(valLN)/nbreKeyWord)/Math.abs(PosDsGrpMotClef1-PosDsGrpMotClef2);
							double ponderationsynonymie =1-DicoSynDist.get(MotClef1); 
							double ScoreMotDist = (20)*(1-Math.log(valLN)/nbreKeyWord)/Math.abs(PosDsGrpMotClef1-PosDsGrpMotClef2);
							double ScoreMot = (20)*(ponderationsynonymie)*(1-Math.log(valLN)/nbreKeyWord)/Math.abs(PosDsGrpMotClef1-PosDsGrpMotClef2);
							ScoreMot = ScoreMot/Listdenom.get(k);
							ListeScoreCorrelation.add(ScoreMot);
							ScoreMotDist = ScoreMotDist/Listdenom.get(k);
							ListeScoreDist.add(ScoreMotDist);
						}
						else{
							//double ScoreMot = (20/3*(3-Math.log(PosDsGrpMot)));
							int valLN = Math.abs(PosDsGrpMot- (MotsPleinsSyn1.indexOf(MotClef1)+1))+1;
							//double test = Math.log(PosDsGrpMot);
							//double ScoreMot = (20)*(1-Math.log(valLN)/nbreKeyWord);
							double ponderationsynonymie =1-DicoSynDist.get(MotClef1); 
							double ScoreMot = (20)*(ponderationsynonymie)*(1-Math.log(valLN)/nbreKeyWord);
							double ScoreMotDist = (20)*(1-Math.log(valLN)/nbreKeyWord);
							ScoreMot = ScoreMot/Listdenom.get(k);
							ListeScoreCorrelation.add(ScoreMot);
							ScoreMotDist = ScoreMotDist/Listdenom.get(k);
							ListeScoreDist.add(ScoreMotDist);
						}
						
					}
					else{
						ListeScoreCorrelation.add(0.0);
						ListeScoreDist.add(0.0);
					}
					compteur++;
				}
				else{
					ListeScoreCorrelation.add(0.0);
					ListeScoreDist.add(0.0);
				}
			}

			//etablissement du facteur de ponderation pour le score de similitude du groupe de mots par rapport au groupe de mots clefs
			ArrayList<Double> ListePond = new ArrayList<Double>();
			double sommepond = 0.0;
			for(int j=0;j<ListeScoreCorrelation.size();j++){
				if(j==0){
					ListePond.add(2.0);
				}
				if(j==1){
					ListePond.add(1.5);
				}
				if(j==2){
					ListePond.add(0.7);
				}
				if(j>=3){
					ListePond.add(0.35);
				}
				if(ListeScoreCorrelation.get(j)!=0.0){
					//sommepond = sommepond+ListePond.get(j);	//prise en compte du fait que un mot clefs peut ne pas etre present
				}
				sommepond = sommepond+ListePond.get(j);
			}

			//resultat calcul score groupe de mots
			Double ScoreTotal = 0.0;
			Double ScoreTotalDist = 0.0;
			for(int j=0;j<MotsPleinsSyn1.size();j++){
				ScoreTotal=ScoreTotal+ListeScoreCorrelation.get(j)*ListePond.get(j);
				ScoreTotalDist=ScoreTotalDist+ListeScoreDist.get(j)*ListePond.get(j);
			}
			ScoreTotal=ScoreTotal/sommepond;
			ScoreTotalDist=ScoreTotalDist/sommepond;
			double DistanceNonnormé = ScoreTotal;
			//normalisation pour avoir plus de proxilmité c'est zero
			double base=0.0;
			/**/for(int k=0;k<MotsPleinsSyn1.size();k++){
				//int PosDsGrpMot = k+1;
				//base = base+ ((20/nbreKeyWord)*(nbreKeyWord-Math.log(PosDsGrpMot))*(ListePond.get(k)));
				base = base+ 20*(ListePond.get(k));
				//base = base+ 1*(ListePond.get(k));
			}
			base = base/sommepond;
			Distancenormé= DistanceNonnormé;///base;
			Distance= Distancenormé;//base;
			//Distance = 1-Distancenormé;
			
		}


		if(Distance.isNaN())
		{
			
		}
		if(Distance!=1.0){
			DistanceMinEntre2groupesLemmePixaAffichage(MotsPleins1,MotsPleins2,DicoVoisinage);
			Text= "distance entre \""+Syn1.SynToGrpWords()+"\" et \""+Syn2.SynToGrpWords()+"\" :"+Text+DistanceMinEntre2SyntagmePixaDistSansSynonymie( Syn1, Syn2, DicoVoisinage)
					+"\nrésultat distance correlation distance-synonymie pondere = "+Distancenormé+"\n\n";
			
		}
		return Distance;
	}

	/**
	 * Fonction servant à calculer la distance Semantique entre deux syntagme configurer pour pixalione
	 * @param Syn1
	 * @param Syn2
	 * @param DicoVoisinage
	 * @return
	 */
	public static String DistanceMinEntre2SyntagmePixaDistSansSynonymie (Syntagme Syn1,Syntagme Syn2,Hashtable<TaggedWord,ArrayList<TaggedWord>> DicoVoisinage){
		Double Distance=1.0;
		Double DistanceDist=1.0;
		
		double Distancenormé = 0.0;
		String Text ="";
		//recup des caracterisitiques
		// ArrayList<WordTag> MotsDeFonctions1 = Syn1.getMotsDeFonctions();
		ArrayList<TaggedWord> MotsPleins1= Syn1.getMotsPleins();
		//Integer StructureSyntaxique1= Syn1.getStructureSyntaxique();
		// ArrayList<WordTag> MotsDeFonctions2 = Syn2.getMotsDeFonctions();
		ArrayList<TaggedWord> MotsPleins2 = Syn2.getMotsPleins();
		//Integer StructureSyntaxique2= Syn2.getStructureSyntaxique();


		//on prend syn1 comme groupe de mots clefs et syn2 comme groupe de mot du texte
		ArrayList<TaggedWord> MotsPleinsSyn1;
		ArrayList<TaggedWord> MotsPleinsSyn2;

		MotsPleinsSyn1= new  ArrayList<TaggedWord>(MotsPleins1);
		MotsPleinsSyn2=new  ArrayList<TaggedWord>(MotsPleins2);

		//Distance Synonymique
		Double DistanceSyn = 1- DistanceMinEntre2groupesLemmePixa(MotsPleins1,MotsPleins2,DicoVoisinage);
		Text = Text+"\n meilleure Distance synonymique entre 2 groupes de mots : "+DistanceSyn +"\n";
		boolean EgaliteParfaite=MotsPleins1.equals(MotsPleins2);
		if(!EgaliteParfaite){
			Distance = DistanceSyn+1;
		}
		//Distance Structurelle
		/*Double DistanceStruct;
		 	if (StructureSyntaxique2.equals(StructureSyntaxique1)){
		 		DistanceStruct=0.0;
			}else{
				DistanceStruct=1.0;
			}*/

		//Distance Position des mots
		//marche si il ya au moins une synonymie
		if((DistanceSyn!=0.0)){//&!EgaliteParfaite){
			//on prend syn1 comme groupe de mots clefs et syn2 comme groupe de mot du texte
			//avec Syn1 ayant une taille faible par rapport à syn2

			//remplacement des mots du groupe de mots par leur mots clefs synonymes
			//recuperation du synonyme le plus fort des mots clefs et leur distance semantique;

			RecupCaracSyn recup = new RecupCaracSyn(Syn1,Syn2 , DicoVoisinage);
			Hashtable<TaggedWord,TaggedWord> DicoSyn = recup.getDicoSyn();	//dictionnaire permettant l'association entre les deux syntagmes

			/*if(DicoSyn.size()==MotsPleinsSyn1.size()){
				
			}*/

			Hashtable<TaggedWord,Double> DicoSynDist = recup.getDicoSynDist();

			//nombre de mots clefs present dans le groupe de mots
			int nbKeyWord = DicoSynDist.size();
			//nombre de mots clefs absents du groupe de mots
			int nbAbsKeyWord = MotsPleinsSyn1.size() - nbKeyWord;

			//tableau de denominateur  pour les score de mots clefs pour le cas d'absences de mots clefs
			ArrayList<Integer> Listdenom = new ArrayList<Integer>();
			nbAbsKeyWord=0;	//on met a zero pour le test
			if(nbAbsKeyWord==0){ //ts les mots clefs sont present
				for(int j=0;j<2;j++){
					Listdenom.add(1);
				}
				for(int j=2;j<MotsPleinsSyn1.size();j++){
					Listdenom.add(1);
				}
			}
			if(nbAbsKeyWord==1){ //ts les mots clefs sont present
				Listdenom.add(8);
				Listdenom.add(10);
				for(int j=2;j<MotsPleinsSyn1.size();j++){
					Listdenom.add(1);
				}
			}
			if(nbAbsKeyWord>=2){ //ts les mots clefs sont present
				Listdenom.add(16);
				Listdenom.add(20);
				for(int j=2;j<MotsPleinsSyn1.size();j++){
					Listdenom.add(1);
				}
			}

			//remplacement des mots du groupe de mots par leur mots clefs synonymes
			/**/
			ArrayList<TaggedWord> NewGroupMot = new ArrayList<TaggedWord>();
			for (int i =0;i<MotsPleinsSyn2.size();i++){
				TaggedWord Mot2 = MotsPleinsSyn2.get(i);
				if(DicoSyn.containsKey(Mot2)){
					//Mot2 = DicoSyn.get(Mot2);
				}
				NewGroupMot.add(Mot2);
			}


			//etablissement des scores pour la presence des mots clefs
			ArrayList<TaggedWord> ListeMotsClefsPresentNonOrdonnée = new ArrayList<TaggedWord>(DicoSyn.values());
			ArrayList<TaggedWord> ListeMotsClefsPresent = new ArrayList<TaggedWord>();
			// rangement en ordre de laiste de mots clefs presents
			for(int l=0;l<MotsPleinsSyn1.size();l++){
				TaggedWord motclef = MotsPleinsSyn1.get(l);
				if(ListeMotsClefsPresentNonOrdonnée.contains(motclef)){
					ListeMotsClefsPresent.add(motclef);
				}
			}

			
			
			ArrayList<Double> ListeScoreCorrelation = new ArrayList<Double>();	//sert a stocker le score apres correlation
			ArrayList<Double> ListeScoreDist = new ArrayList<Double>();	//sert a stocker le score de distance sans correlation
			//Cas ou il y a plus de deux mots clefs en entree
			int compteur=0;
			int nbreKeyWord = MotsPleinsSyn1.size();
			for(int k=0;k<MotsPleinsSyn1.size();k++){

				TaggedWord MotClef1 = MotsPleinsSyn1.get(k);
				if(ListeMotsClefsPresent.contains(MotClef1)){
					if (NewGroupMot.contains(MotClef1)){
						//si le mot clef est present ( si il y aun mot du groupe original qui lui est synonyme
						int PosDsGrpMotClef1 = MotsPleinsSyn1.indexOf(MotClef1)+1;	//position du mot clefs dans le groupe de mots clefs
						int PosDsGrpMot = NewGroupMot.indexOf(MotClef1)+1;	//position du mot clefs dans le groupe de mots du texte
						if(compteur+1<ListeMotsClefsPresent.size()){
							TaggedWord MotClef2 = ListeMotsClefsPresent.get(compteur+1);
							int PosDsGrpMotClef2 = MotsPleinsSyn1.indexOf(MotClef2)+1;	//position du mot clefs dans le groupe de mots clefs

							//calcul du score 
							//double ScoreMot = (20/3*(3-Math.log(PosDsGrpMot)))/Math.abs(PosDsGrpMotClef1-PosDsGrpMotClef2);
							int valLN = Math.abs(PosDsGrpMot- (MotsPleinsSyn1.indexOf(MotClef1)+1))+1;
							//double ScoreMot = (20)*(1-Math.log(valLN)/nbreKeyWord)/Math.abs(PosDsGrpMotClef1-PosDsGrpMotClef2);
							double ponderationsynonymie =1-DicoSynDist.get(MotClef1); 
							double ScoreMotDist = (20)*(1-Math.log(valLN)/nbreKeyWord)/Math.abs(PosDsGrpMotClef1-PosDsGrpMotClef2);
							double ScoreMot = (20)*(ponderationsynonymie)*(1-Math.log(valLN)/nbreKeyWord)/Math.abs(PosDsGrpMotClef1-PosDsGrpMotClef2);
							ScoreMot = ScoreMot/Listdenom.get(k);
							ListeScoreCorrelation.add(ScoreMot);
							ScoreMotDist = ScoreMotDist/Listdenom.get(k);
							ListeScoreDist.add(ScoreMotDist);
						}
						else{
							//double ScoreMot = (20/3*(3-Math.log(PosDsGrpMot)));
							int valLN = Math.abs(PosDsGrpMot- (MotsPleinsSyn1.indexOf(MotClef1)+1))+1;
							//double test = Math.log(PosDsGrpMot);
							//double ScoreMot = (20)*(1-Math.log(valLN)/nbreKeyWord);
							double ponderationsynonymie =1-DicoSynDist.get(MotClef1); 
							double ScoreMot = (20)*(ponderationsynonymie)*(1-Math.log(valLN)/nbreKeyWord);
							double ScoreMotDist = (20)*(1-Math.log(valLN)/nbreKeyWord);
							ScoreMot = ScoreMot/Listdenom.get(k);
							ListeScoreCorrelation.add(ScoreMot);
							ScoreMotDist = ScoreMotDist/Listdenom.get(k);
							ListeScoreDist.add(ScoreMotDist);
						}
						
					}
					else{
						ListeScoreCorrelation.add(0.0);
						ListeScoreDist.add(0.0);
					}
					compteur++;
				}
				else{
					ListeScoreCorrelation.add(0.0);
					ListeScoreDist.add(0.0);
				}
			}

			//etablissement du facteur de ponderation pour le score de similitude du groupe de mots par rapport au groupe de mots clefs
			ArrayList<Double> ListePond = new ArrayList<Double>();
			double sommepond = 0.0;
			for(int j=0;j<ListeScoreCorrelation.size();j++){
				if(j==0){
					ListePond.add(2.0);
				}
				if(j==1){
					ListePond.add(1.5);
				}
				if(j==2){
					ListePond.add(0.7);
				}
				if(j>=3){
					ListePond.add(0.35);
				}
				if(ListeScoreCorrelation.get(j)!=0.0){
					//sommepond = sommepond+ListePond.get(j);	//prise en compte du fait que un mot clefs peut ne pas etre present
				}
				sommepond = sommepond+ListePond.get(j);
			}

			//resultat calcul score groupe de mots
			Double ScoreTotal = 0.0;
			Double ScoreTotalDist = 0.0;
			for(int j=0;j<MotsPleinsSyn1.size();j++){
				ScoreTotal=ScoreTotal+ListeScoreCorrelation.get(j)*ListePond.get(j);
				ScoreTotalDist=ScoreTotalDist+ListeScoreDist.get(j)*ListePond.get(j);
			}
			ScoreTotal=ScoreTotal/sommepond;
			ScoreTotalDist=ScoreTotalDist/sommepond;
			DistanceDist =ScoreTotalDist; 
			double DistanceNonnormé = ScoreTotal;
			//normalisation pour avoir plus de proxilmité c'est zero
			double base=0.0;
			/**/for(int k=0;k<MotsPleinsSyn1.size();k++){
				//int PosDsGrpMot = k+1;
				//base = base+ ((20/nbreKeyWord)*(nbreKeyWord-Math.log(PosDsGrpMot))*(ListePond.get(k)));
				base = base+ 20*(ListePond.get(k));
				//base = base+ 1*(ListePond.get(k));
			}
			base = base/sommepond;
			Distancenormé= DistanceNonnormé;//base;
			Distance = 1-Distancenormé;
			
		}


		if(Distance.isNaN())
		{
			
		}
		if(Distance!=1.0){
			//Text= "distance entre \""+Syn1.SynToGrpWords()+"\" et \""+Syn2.SynToGrpWords()+"\" :"+Text+"résultat distance synonymie binaire = "+DistanceDist+"\nrésultat distance correlation distance-synonymie pondere = "+Distancenormé+"\n\n";
			Text= "résultat distance = "+DistanceDist+"\n";
			
		}else{
			Text="";
		}
		return Text;
	}

	/**
	 * Fonction servant à calculer la distance Semantique entre deux ensembles de syntagmes
	 * @param EnsSyn1
	 * @param EnsSyn2
	 * @param DicoVoisinage
	 * @return
	 */
	public static Double DistanceMinEntre2EnsembleSyntagme (ArrayList<Syntagme> EnsSyn1,ArrayList<Syntagme> EnsSyn2,Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage){
		Double Distance=0.0;
		ArrayList<Syntagme> newEnsSyn1 = new ArrayList<Syntagme>(EnsSyn1);
		ArrayList<Syntagme> newEnsSyn2 = new ArrayList<Syntagme>(EnsSyn2);
		if(EnsSyn1.size()>EnsSyn2.size()){
			newEnsSyn1 = new ArrayList<Syntagme>(EnsSyn2);
			newEnsSyn2 = new ArrayList<Syntagme>(EnsSyn1);
		}
		DoubleMatrix2D mat = new SparseDoubleMatrix2D(newEnsSyn1.size(),newEnsSyn2.size());
		for (int i=0;i<newEnsSyn1.size();i++){
			//double distMin=Double.MAX_VALUE;
			Syntagme Syn1 = newEnsSyn1.get(i);
			//Syntagme Syn2min = Syn1;
			for(int j=0;j<newEnsSyn2.size();j++){
				Syntagme Syn2 = newEnsSyn2.get(j);
				//double dist = DistanceMinEntre2Syntagme(Syn1,Syn2,DicoVoisinage);
				double dist = DistanceMinEntre2SyntagmePixa(Syn1,Syn2,DicoVoisinage);
				mat.set(i, j, dist);
				if(dist==0.0){
					//Syn2min=Syn2;
					//distMin=dist;
				}
			}
			/*Distance=Distance+distMin;
			if()
			newEnsSyn2.remove(Syn2min);*/
		}
		for (int i=0;i<newEnsSyn1.size();i++){
			Distance = Distance + RecuperationDistMinetNettoyageMatrice(mat);
		}		
		return Distance/newEnsSyn1.size();
	}
	
	/**
	 * * Fonction servant à calculer la distance Semantique entre deux ensembles de syntagmes configurer pour pixalione
	 * @param EnsSyn1
	 * @param EnsSyn2
	 * @param DicoVoisinage
	 * @return
	 */
	public static Double DistanceMinEntre2EnsembleSyntagmePixa (ArrayList<Syntagme> EnsSyn1,ArrayList<Syntagme> EnsSyn2,Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage){
		Double Distance=0.0;
		ArrayList<Syntagme> newEnsSyn1 = new ArrayList<Syntagme>(EnsSyn1);
		ArrayList<Syntagme> newEnsSyn2 = new ArrayList<Syntagme>(EnsSyn2);
		if(EnsSyn1.size()>EnsSyn2.size()){
			newEnsSyn1 = new ArrayList<Syntagme>(EnsSyn2);
			newEnsSyn2 = new ArrayList<Syntagme>(EnsSyn1);
		}
		DoubleMatrix2D mat = new SparseDoubleMatrix2D(newEnsSyn1.size(),newEnsSyn2.size());
		for (int i=0;i<newEnsSyn1.size();i++){
			//double distMin=Double.MAX_VALUE;
			Syntagme Syn1 = newEnsSyn1.get(i);
			//Syntagme Syn2min = Syn1;
			for(int j=0;j<newEnsSyn2.size();j++){
				Syntagme Syn2 = newEnsSyn2.get(j);
				//double dist = DistanceMinEntre2Syntagme(Syn1,Syn2,DicoVoisinage);
				double dist = DistanceMinEntre2SyntagmePixa(Syn1,Syn2,DicoVoisinage);
				mat.set(i, j, dist);
				if(dist==0.0){
					//Syn2min=Syn2;
					//distMin=dist;
				}
			}
			/*Distance=Distance+distMin;
			if()
			newEnsSyn2.remove(Syn2min);*/
		}
		for (int i=0;i<newEnsSyn1.size();i++){
			Distance = Distance + RecuperationDistMinetNettoyageMatrice(mat);
		}		
		return Distance/newEnsSyn1.size();
	}
	
	/**
	 * Fonction permettant de trouver le plus petit element d'un ensemble
	 * @param matrice
	 * @return
	 */
	public static double RecuperationDistMinetNettoyageMatrice (DoubleMatrix2D matrice){
		double min = Double.MAX_VALUE;
		int rowPos = -1;
		int colPos = -1;
		for(int row = 0;row<matrice.rows();row++){
			for(int col = 0;col<matrice.columns();col++){
				double val = matrice.get(row, col);
				if(val<=min){
					min = val;
					rowPos = row;
					colPos = col;
				}
			}
		}
		//nettoyage
		for(int row = 0;row<matrice.rows();row++){
			matrice.set(row, colPos, 1.0);
			if( row == rowPos){
				for(int col = 0;col<matrice.columns();col++){
					matrice.set(row, col, 1.0);
				}
			}
		}		
		return min;

	}
	
	/**
	 * function for modification of sentences for a good lemmatization
	 * @param Text
	 * @return
	 */
	public String ModifTextStringForLemmatization(String Text){
		
		String sentencePreTraitement=Text;
		/*
		 * Avant utilisation du lemmatiseur
		 */
		sentencePreTraitement=sentencePreTraitement.replaceAll("-", " - ");			//gestion guillemets
		
		sentencePreTraitement=sentencePreTraitement.replaceAll("<", "");			//gestion guillemets
		sentencePreTraitement=sentencePreTraitement.replaceAll("<", "");			//gestion guillemets
		sentencePreTraitement=sentencePreTraitement.replaceAll(">", "");			//gestion guillemets
		sentencePreTraitement=sentencePreTraitement.replaceAll("«", "");			//gestion guillemets
		sentencePreTraitement=sentencePreTraitement.replaceAll("»", "");			//gestion guillemets
		sentencePreTraitement=sentencePreTraitement.replaceAll("\"", "");			//gestion guillemets
		sentencePreTraitement=sentencePreTraitement.replaceAll("’", "'");			//gestion guillemets
		sentencePreTraitement=sentencePreTraitement.replaceAll("qu'", "que ");			//gestion guillemets 
		sentencePreTraitement=sentencePreTraitement.replaceAll("s'", "se ");			//gestion guillemets
		sentencePreTraitement=sentencePreTraitement.replaceAll("c'", "ce ");			//gestion guillemets
		sentencePreTraitement=sentencePreTraitement.replaceAll("n'", "ne ");			//gestion guillemets
		sentencePreTraitement=sentencePreTraitement.replaceAll("d'", "de ");			//gestion guillemets
		sentencePreTraitement=sentencePreTraitement.replaceAll("'", "' ");			//gestion guillemets
		//sentencePreTraitement=sentencePreTraitement.replaceAll("’", "'");			//gestion guillemets 
		
		
		return sentencePreTraitement;
	}



	public Hashtable<Integer, ArrayList<TaggedWord>> getTextLemmatised() {
		return TextLemmatised;
	}


	public void setTextLemmatised(Hashtable<Integer, ArrayList<TaggedWord>> textLemmatised) {
		TextLemmatised = textLemmatised;
	}


	public int getNombreMotTotal() {
		return NombreMotTotal;
	}


	public void setNombreMotTotal(int nombreMotTotal) {
		NombreMotTotal = nombreMotTotal;
	}


	public Hashtable<Integer,ArrayList<Integer>> getCliquesSynt() {
		return CliquesSynt;
	}


	public void setCliquesSynt(Hashtable<Integer,ArrayList<Integer>> cliquesSynt) {
		CliquesSynt = cliquesSynt;
	}


	public Hashtable<Integer,ArrayList<String>> getCliquesSyntString() {
		return CliquesSyntString;
	}


	public void setCliquesSyntString(Hashtable<Integer,ArrayList<String>> cliquesSyntString) {
		CliquesSyntString = cliquesSyntString;
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


	public PixTaggerFrench getFrenchtagger() {
		return Frenchtagger;
	}


	public void setFrenchtagger(PixTaggerFrench frenchtagger) {
		Frenchtagger = frenchtagger;
	}


	public Hashtable<Integer,Syntagme> getDicoSyntagme() {
		return DicoSyntagme;
	}


	public void setDicoSyntagme(Hashtable<Integer,Syntagme> dicoSyntagme) {
		DicoSyntagme = dicoSyntagme;
	}
	public ArrayList<Syntagme> getListSyntagme() {
		return ListSyntagme;
	}
	public void setListSyntagme(ArrayList<Syntagme> listSyntagme) {
		ListSyntagme = listSyntagme;
	}

	public Hashtable<Syntagme, Integer> getDicoSyntagme2() {
		return DicoSyntagme2;
	}
	public void setDicoSyntagme2(Hashtable<Syntagme, Integer> dicoSyntagme2) {
		DicoSyntagme2 = dicoSyntagme2;
	}public SparseDoubleMatrix2D getMatriceOccurence() {
		return matriceOccurence;
	}
	public void setMatriceOccurence(SparseDoubleMatrix2D matriceOccurence) {
		this.matriceOccurence = matriceOccurence;
	}
	private int numPage;

	public int getNumPage() {
		return numPage;
	}
	public void setNumPage(int numPage) {
		this.numPage = numPage;
	}
	public ArrayList<Syntagme> getListSyntagmeTraité() {
		return ListSyntagmeTraité;
	}
	public void setListSyntagmeTraité(ArrayList<Syntagme> listSyntagmeTraité) {
		ListSyntagmeTraité = listSyntagmeTraité;
	}

	public String[] getListeStopWords() {
		return ListeStopWords;
	}

	public void setListeStopWords(String[] listeStopWords) {
		ListeStopWords = listeStopWords;
	}

	public Hashtable<String,TaggedWord> getDicoCorresStrWt() {
		return dicoCorresStrWt;
	}

	public void setDicoCorresStrWt(Hashtable<String,TaggedWord> dicoCorresStrWt) {
		this.dicoCorresStrWt = dicoCorresStrWt;
	}

	public int getPosGrpMotsClefs() {
		return PosGrpMotsClefs;
	}

	public void setPosGrpMotsClefs(int posGrpMotsClefs) {
		PosGrpMotsClefs = posGrpMotsClefs;
	}

	public ArrayList<ArrayList<TaggedWord>> getParagrapheGrpMots() {
		return ParagrapheGrpMots;
	}

	public void setParagrapheGrpMots(ArrayList<ArrayList<TaggedWord>> paragrapheGrpMots) {
		ParagrapheGrpMots = paragrapheGrpMots;
	}

	public ArrayList<Integer> getListePositionGroupMotClefs() {
		return ListePositionGroupMotClefs;
	}

	public void setListePositionGroupMotClefs(
			ArrayList<Integer> listePositionGroupMotClefs) {
		ListePositionGroupMotClefs = listePositionGroupMotClefs;
	}

	public Hashtable<TaggedWord,String> getInvdicoCorresStrWt() {
		return InvdicoCorresStrWt;
	}

	public void setInvdicoCorresStrWt(Hashtable<TaggedWord,String> invdicoCorresStrWt) {
		InvdicoCorresStrWt = invdicoCorresStrWt;
	}

	public ArrayList<ArrayList<ArrayList<TaggedWord>>> getListeParagrapheGrpMots() {
		return listeParagrapheGrpMots;
	}

	public void setListeParagrapheGrpMots(ArrayList<ArrayList<ArrayList<TaggedWord>>> listeParagrapheGrpMots) {
		this.listeParagrapheGrpMots = listeParagrapheGrpMots;
	}

    public Hashtable<Integer, Integer> getPosReelPosAutre() {
        return PosReelPosAutre;
    }

    public void setPosReelPosAutre(Hashtable<Integer, Integer> PosReelPosAutre) {
        this.PosReelPosAutre = PosReelPosAutre;
    }

        

}
