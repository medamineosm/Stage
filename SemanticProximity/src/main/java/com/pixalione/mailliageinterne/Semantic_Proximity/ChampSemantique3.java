package com.pixalione.mailliageinterne.Semantic_Proximity;




import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;
import com.pixalione.mailliageinterne.PixTagger.PixTaggerFrench;
import com.pixalione.mailliageinterne.PixTagger.TaggedWord;
import com.pixalione.mailliageinterne.fr.pixalione.frenchdictionnary.DictionnaireSyn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/*import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.jet.math.Functions;*/

/*import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;*/

//import fr.pixalione.lemmatisation.MorphologyMervyn;

public class ChampSemantique3 {

	private  Hashtable<TaggedWord,Integer> wTnodes;		// espace des noueds (sommets) du graphe, il correspond au dictionnaire d'unités lexicales
	private  Hashtable<Integer,TaggedWord> wTdico;		// espace des noueds (sommets) du graphe, il correspond au dictionnaire d'unités lexicales
	private  Hashtable<String,ArrayList<TaggedWord>> dicoWordTag;		// espace des noueds (sommets) du graphe, il correspond au dictionnaire d'unités lexicales
	private  ArrayList<TaggedWord> TextWordTag;		// espace des noueds (sommets) du graphe, il correspond au dictionnaire d'unités lexicales
	private  Hashtable<TaggedWord, ArrayList<Integer>> MatriceVoisinage;	//matrice servant a modeliser le graphe dont on doit trouver les cliques
	private  Hashtable<TaggedWord, ArrayList<TaggedWord>> Dico_wTLexUnit_wTsyn;	//matrice servant a modeliser le graphe dont on doit trouver les cliques
	private  Hashtable<Integer,ArrayList<Integer>> Cliques;
	private  Hashtable<Integer,ArrayList<String>> CliquesString;
	private  Hashtable<String,ArrayList<Integer>> dicoEtOccurence;
	private  Hashtable<TaggedWord, Integer> dicoEtOccurenceSimple;
	private ArrayList<String> dicoSynonyme;
	private ArrayList<String> dicoText;
	private PixTaggerFrench Frenchtagger;
	private int NombreMotTotal;	//nombre total de mots important dans le champs étudié
	private String TextSortie="";
	private ArrayList<TaggedWord>Thesaurus;
	private ArrayList<Integer> VG;
	private DictionnaireSyn DicoSyn;
	private  Hashtable<String,TaggedWord> dicoCorresStrWt;                     //dictionnaire de correspondance mots du texte->wordtag correspondant

	
	

	public DictionnaireSyn getDicoSyn() {
		return DicoSyn;
	}
	public void setDicoSyn(DictionnaireSyn dicoSyn) {
		DicoSyn = dicoSyn;
	}
	

	/**
	 * 
	 * @param Text
	 * @param dicoSyn
	 * @param tagger
	 * @param pertinance
	 * @param tag
	 */
	public ChampSemantique3(String Text,DictionnaireSyn dicoSyn,PixTaggerFrench tagger, int pertinance, String tag) {
            
		super();
		long startTime = System.currentTimeMillis();
		if (pertinance<=0){
	//		pertinance=1;
		}
		if(!tag.equals("A")|!tag.equals("N")|!tag.equals("V")){
			tag= "N";
		}
		this.setDicoSyn(dicoSyn);
		this.setFrenchtagger(tagger);
		this.setwTdico(dicoSyn.getwTdico());
		this.setwTnodes(dicoSyn.getwTnodes());
		this.setDicoSynonyme(dicoSyn.getDicoSynonyme());
		this.setDicoWordTag(dicoSyn.getDicoWordTag());
		long endTimeDico = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du dico en millisecondes "+ (endTimeDico-startTime)+"\n";
		RecuperationTextStringDico(Text,tag,1);//RecuperationTextStringDico(Text,1);
		long endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du texte en millisecondes "+ (endTime-endTimeDico)+"\n";

		//MatriceVoisinage = DictionnaireSyn.RemplissageVoisinageNoeudsText(dicoSyn.getDicoPath(),dicoSyn,this.getTextWordTag());
		//RemplissageVoisinageNoeudsText(dicoSyn.getDicoPath());
		MatriceVoisinage = DictionnaireSyn.RemplissageVoisinageNoeudsTextFastPertinence(dicoSyn.getDicoPath(),dicoSyn,this.getTextWordTag());
		//startTime = System.currentTimeMillis();

		ArrayList<Integer> VG= new ArrayList<Integer>();
		Enumeration<TaggedWord> EnumNoeud = MatriceVoisinage.keys();

		this.setCliques(new Hashtable<Integer,ArrayList<Integer>>());
		this.setCliquesString(new Hashtable<Integer,ArrayList<String>>());
		while (EnumNoeud.hasMoreElements())
		{
			TaggedWord noeud =EnumNoeud.nextElement();
			if (!wTnodes.containsKey(noeud)){
				
			}else{
				Integer IDnoeud =wTnodes.get(noeud);
				VG.add(IDnoeud);
			}
		}
		ArrayList<Integer> X=new ArrayList<Integer>();
		ArrayList<Integer> R=new ArrayList<Integer>();

		
		this.TextSortie=this.TextSortie+"liste de cliques trouvées :\n";
		BronKerbosch1_2Pert(VG,X, R,pertinance,tag);
		
		endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"nombre de cliques trouvées : "+Cliques.size()+"\n"+
				"temps de calcul en milli secondes pour bron kerbosch1 sans pivot :"+ (endTime-startTime)+"\n";


	}
	
	

	/**
	 * 
	 * @param Text
	 * @param dicoSyn
	 * @param tagger
	 * @param pertinance
	 */
	public ChampSemantique3(String Text,DictionnaireSyn dicoSyn,PixTaggerFrench tagger, int pertinance) {
		super();
		long startTime = System.currentTimeMillis();
		if (pertinance<=0){
		//	pertinance=1;
		}
		this.setDicoSyn(dicoSyn);
		this.setFrenchtagger(tagger);
		this.setwTdico(dicoSyn.getwTdico());
		this.setwTnodes(dicoSyn.getwTnodes());
		this.setDicoSynonyme(dicoSyn.getDicoSynonyme());
		this.setDicoWordTag(dicoSyn.getDicoWordTag());
		long endTimeDico = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du dico en millisecondes "+ (endTimeDico-startTime)+"\n";
		RecuperationTextStringDico(Text,1);//RecuperationTextStringDico(Text,1);
		long endTime = System.currentTimeMillis();
		
                
		this.TextSortie=this.TextSortie+"temps de traitement du texte en millisecondes "+ (endTime-endTimeDico)+"\n";

		//MatriceVoisinage = DictionnaireSyn.RemplissageVoisinageNoeudsText(dicoSyn.getDicoPath(),dicoSyn,this.getTextWordTag());
		//RemplissageVoisinageNoeudsText(dicoSyn.getDicoPath());
		MatriceVoisinage = DictionnaireSyn.RemplissageVoisinageNoeudsTextFastPertinence(dicoSyn.getDicoPath(),dicoSyn,this.getTextWordTag());
		//startTime = System.currentTimeMillis();

		ArrayList<Integer> VG= new ArrayList<Integer>();
		Enumeration<TaggedWord> EnumNoeud = MatriceVoisinage.keys();

		this.setCliques(new Hashtable<Integer,ArrayList<Integer>>());
		this.setCliquesString(new Hashtable<Integer,ArrayList<String>>());
		while (EnumNoeud.hasMoreElements())
		{
			TaggedWord noeud =EnumNoeud.nextElement();
			if (!wTnodes.containsKey(noeud)){
				
			}else{
				Integer IDnoeud =wTnodes.get(noeud);
				VG.add(IDnoeud);
			}
		}
		ArrayList<Integer> X=new ArrayList<Integer>();
		ArrayList<Integer> R=new ArrayList<Integer>();

		
		this.TextSortie=this.TextSortie+"liste de cliques trouvées :\n";
		BronKerbosch1_2Pert(VG,X, R,pertinance);
		
		endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"nombre de cliques trouvées : "+Cliques.size()+"\n"+
				"temps de calcul en milli secondes pour bron kerbosch1 sans pivot :"+ (endTime-startTime)+"\n";


	}
	
	/**
	 * 
	 * @param Text
	 * @param thesaurus
	 * @param dicoSyn
	 * @param tagger
	 * @param pertinance
	 */
	public ChampSemantique3(String Text,ArrayList<TaggedWord> thesaurus, DictionnaireSyn dicoSyn,PixTaggerFrench tagger, int pertinance) {
		super();
		long startTime = System.currentTimeMillis();
		if (pertinance<=0){
//			pertinance=1;
		}
		this.setDicoSyn(dicoSyn);
		this.setFrenchtagger(tagger);
		this.setwTdico(dicoSyn.getwTdico());
		this.setwTnodes(dicoSyn.getwTnodes());
		this.setDicoSynonyme(dicoSyn.getDicoSynonyme());
		this.setDicoWordTag(dicoSyn.getDicoWordTag());
		this.setThesaurus(thesaurus);
		long endTimeDico = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du dico en millisecondes "+ (endTimeDico-startTime)+"\n";
		ArrayList<String> NamesFilesText = new ArrayList<String>();
		NamesFilesText.add(Text);
		//RecuperationTextDicoWithThesaurus(NamesFilesText,Thesaurus);
		if (pertinance<=0){
			RecuperationTextStringDico(Text,1);
		}else{
			RecuperationTextDicoWithThesaurus(NamesFilesText,Thesaurus);
		}
		long endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du texte en millisecondes "+ (endTime-endTimeDico)+"\n";

		//MatriceVoisinage = DictionnaireSyn.RemplissageVoisinageNoeudsText(dicoSyn.getDicoPath(),dicoSyn,this.getTextWordTag());
		//RemplissageVoisinageNoeudsText(dicoSyn.getDicoPath());
		MatriceVoisinage = DictionnaireSyn.RemplissageVoisinageNoeudsTextFastPertinence(dicoSyn.getDicoPath(),dicoSyn,this.getTextWordTag());
		//startTime = System.currentTimeMillis();

		ArrayList<Integer> VG= new ArrayList<Integer>();
		Enumeration<TaggedWord> EnumNoeud = MatriceVoisinage.keys();

		this.setCliques(new Hashtable<Integer,ArrayList<Integer>>());
		this.setCliquesString(new Hashtable<Integer,ArrayList<String>>());
		while (EnumNoeud.hasMoreElements())
		{
			TaggedWord noeud =EnumNoeud.nextElement();
			if (!wTnodes.containsKey(noeud)){
				
			}else{
				Integer IDnoeud =wTnodes.get(noeud);
				VG.add(IDnoeud);
			}
		}
		ArrayList<Integer> X=new ArrayList<Integer>();
		ArrayList<Integer> R=new ArrayList<Integer>();

		
		this.TextSortie=this.TextSortie+"liste de cliques trouvées :\n";
		BronKerbosch1_2Pert(VG,X, R,pertinance);
		
		endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"nombre de cliques trouvées : "+Cliques.size()+"\n"+
				"temps de calcul en milli secondes pour bron kerbosch1 sans pivot :"+ (endTime-startTime)+"\n";


	}
	
	
	/** Called Constructor
	 * 
	 * @param Text
	 * @param dicoSyn
	 * @param pertinance
	 * @param tag
	 */
	public ChampSemantique3(ArrayList<ArrayList<TaggedWord>> Text,DictionnaireSyn dicoSyn, int pertinance, String tag) {
		super();
		long startTime = System.currentTimeMillis();
		if (pertinance<=0){
			pertinance=1;
		}
		if(!tag.equals("A")|!tag.equals("N")|!tag.equals("V")){
			tag= "N";
		}
		this.setDicoSyn(dicoSyn);
		this.setwTdico(dicoSyn.getwTdico());
		this.setwTnodes(dicoSyn.getwTnodes());
		this.setDicoSynonyme(dicoSyn.getDicoSynonyme());
		this.setDicoWordTag(dicoSyn.getDicoWordTag());
		long endTimeDico = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du dico en millisecondes "+ (endTimeDico-startTime)+"\n";
		RecuperationWtTextStringDico(Text,tag,1);//RecuperationWtTextStringDico(Text,1);
		long endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du texte en millisecondes "+ (endTime-endTimeDico)+"\n";

		//MatriceVoisinage = DictionnaireSyn.RemplissageVoisinageNoeudsText(dicoSyn.getDicoPath(),dicoSyn,this.getTextWordTag());
		//
                RemplissageVoisinageNoeudsText(dicoSyn.getDicoPath());
		//MatriceVoisinage = DictionnaireSyn.RemplissageVoisinageNoeudsTextFastPertinence(dicoSyn.getDicoPath(),dicoSyn,this.getTextWordTag());
		//startTime = System.currentTimeMillis();

		ArrayList<Integer> VG= new ArrayList<Integer>();
		Enumeration<TaggedWord> EnumNoeud = MatriceVoisinage.keys();

		this.setCliques(new Hashtable<Integer,ArrayList<Integer>>());
		this.setCliquesString(new Hashtable<Integer,ArrayList<String>>());
                

		while (EnumNoeud.hasMoreElements())
		{
			TaggedWord noeud =EnumNoeud.nextElement();
			if (!wTnodes.containsKey(noeud)){
				
			}else{
				Integer IDnoeud =wTnodes.get(noeud);
				VG.add(IDnoeud);
			}
		}

		ArrayList<Integer> X=new ArrayList<Integer>();
		ArrayList<Integer> R=new ArrayList<Integer>();

		
		this.TextSortie=this.TextSortie+"liste de cliques trouvées :\n";
		BronKerbosch1_2Pert(VG,X, R,pertinance,tag);
		
		endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"nombre de cliques trouvées : "+Cliques.size()+"\n"+
				"temps de calcul en milli secondes pour bron kerbosch1 sans pivot :"+ (endTime-startTime)+"\n";


	}
	
	/**
	 * 
	 * @param NameFileText
	 * @param NamefileDico
	 * @param tagger
	 */
	public ChampSemantique3(ArrayList<String> NameFileText, String NamefileDico,PixTaggerFrench tagger) {
		super();
		long startTime = System.currentTimeMillis();
		this.setFrenchtagger(tagger);
		RecuperationDico(NamefileDico);
		long endTimeDico = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du dico en millisecondes "+ (endTimeDico-startTime)+"\n";
		RecuperationTextDico(NameFileText);
		long endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du texte en millisecondes "+ (endTime-endTimeDico)+"\n";

		long startTimeRempli= System.currentTimeMillis();
		RemplissageVoisinageNoeudsText(NamefileDico);
		long endTimeRempli = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de remplissage de la matrice de voisinage en millisecondes "+ (endTimeRempli-startTimeRempli)+"\n";

		//startTime = System.currentTimeMillis();


		ArrayList<Integer> VG= new ArrayList<Integer>();
		Enumeration<TaggedWord> EnumNoeud = MatriceVoisinage.keys();

		this.setCliques(new Hashtable<Integer,ArrayList<Integer>>());
		this.setCliquesString(new Hashtable<Integer,ArrayList<String>>());
		while (EnumNoeud.hasMoreElements())
		{
			TaggedWord noeud =EnumNoeud.nextElement();
			if (!wTnodes.containsKey(noeud)){
				
			}else{
				Integer IDnoeud =wTnodes.get(noeud);
				VG.add(IDnoeud);
			}
		}
		ArrayList<Integer> X=new ArrayList<Integer>();
		ArrayList<Integer> R=new ArrayList<Integer>();

		
		this.TextSortie=this.TextSortie+"liste de cliques trouvées :\n";
		BronKerbosch1_2(VG,X, R);
		//BronKerbosch1_1Pivot(VG,X, R);
		
		endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"nombre de cliques trouvées : "+Cliques.size()+"\n"+
				"temps de traitement total en milli secondes :"+ (endTime-startTime)+"\n";


	}

	/**
	 * 
	 * @param NameFileText
	 * @param NamefileDico
	 * @param tagger
	 */
	public ChampSemantique3(String NameFileText, String NamefileDico,PixTaggerFrench tagger) {
		super();
		long startTime = System.currentTimeMillis();
		
		this.setFrenchtagger(tagger);
		RecuperationDico(NamefileDico);
		long endTimeDico = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du dico en millisecondes "+ (endTimeDico-startTime)+"\n";
		ArrayList<String> NamesFilesText = new ArrayList<String>();
		NamesFilesText.add(NameFileText);
		RecuperationTextDico(NamesFilesText);
		long endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du texte en millisecondes "+ (endTime-endTimeDico)+"\n";

		long startTimeRempli= System.currentTimeMillis();
		RemplissageVoisinageNoeudsText(NamefileDico);
		long endTimeRempli = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de remplissage de la matrice de voisinage en millisecondes "+ (endTimeRempli-startTimeRempli)+"\n";

		//startTime = System.currentTimeMillis();


		ArrayList<Integer> VG= new ArrayList<Integer>();
		Enumeration<TaggedWord> EnumNoeud = MatriceVoisinage.keys();

		this.setCliques(new Hashtable<Integer,ArrayList<Integer>>());
		this.setCliquesString(new Hashtable<Integer,ArrayList<String>>());
		while (EnumNoeud.hasMoreElements())
		{
			TaggedWord noeud =EnumNoeud.nextElement();
			if (!wTnodes.containsKey(noeud)){
				
			}else{
				Integer IDnoeud =wTnodes.get(noeud);
				VG.add(IDnoeud);
			}
		}
		ArrayList<Integer> X=new ArrayList<Integer>();
		ArrayList<Integer> R=new ArrayList<Integer>();

		
		this.TextSortie=this.TextSortie+"liste de cliques trouvées :\n";
		//BronKerbosch1_1(VG,X, R);
		//BronKerbosch1_1Autre(VG,X, R);
		BronKerbosch1_2(VG,X, R);
		//BronKerbosch1_1Pivot(VG,X, R);
		
		endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"nombre de cliques trouvées : "+Cliques.size()+"\n"+
				"temps de traitement total en milli secondes :"+ (endTime-startTime)+"\n";


	}

	/**
	 * 
	 * @param NameFileText
	 * @param NamefileDico
	 * @param Thesaurus
	 * @param tagger
	 */
	public ChampSemantique3(String NameFileText, String NamefileDico,ArrayList<TaggedWord>Thesaurus, PixTaggerFrench tagger) {
		super();
		long startTime = System.currentTimeMillis();
		
		this.setFrenchtagger(tagger);
		this.setThesaurus(Thesaurus);
		RecuperationDico(NamefileDico);
		long endTimeDico = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du dico en millisecondes "+ (endTimeDico-startTime)+"\n";
		ArrayList<String> NamesFilesText = new ArrayList<String>();
		NamesFilesText.add(NameFileText);
		RecuperationTextDicoWithThesaurus(NamesFilesText,Thesaurus);
		long endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du texte en millisecondes "+ (endTime-endTimeDico)+"\n";

		long startTimeRempli= System.currentTimeMillis();
		//RemplissageVoisinageNoeudsText();
		//RemplissageVoisinageNoeudsTextFast();
		RemplissageVoisinageNoeudsTextFastPertinence();
		//RemplissageVoisinageNoeudsTextFastPertinenceForte();
		long endTimeRempli = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de remplissage de la matrice de voisinage en millisecondes "+ (endTimeRempli-startTimeRempli)+"\n";

		//startTime = System.currentTimeMillis();

		//calculClique( MatriceVoisinage);


		ArrayList<Integer> VG= new ArrayList<Integer>();
		Enumeration<TaggedWord> EnumNoeud = MatriceVoisinage.keys();

		this.setCliques(new Hashtable<Integer,ArrayList<Integer>>());
		this.setCliquesString(new Hashtable<Integer,ArrayList<String>>());
		while (EnumNoeud.hasMoreElements())
		{
			TaggedWord noeud =EnumNoeud.nextElement();
			if (!wTnodes.containsKey(noeud)){
				
			}else{
				Integer IDnoeud =wTnodes.get(noeud);
				VG.add(IDnoeud);
			}
		}
		this.setVG(VG);
		ArrayList<Integer> X=new ArrayList<Integer>();
		ArrayList<Integer> R=new ArrayList<Integer>();

		
		this.TextSortie=this.TextSortie+"liste de cliques trouvées :\n";
		//BronKerbosch1_1Autre(VG,X, R);
		BronKerbosch1_2(VG,X, R);
		//BronKerbosch1_1Pivot(VG,X, R);
		
		endTime = System.currentTimeMillis();
		this.TextSortie=this.TextSortie+"nombre de cliques trouvées : "+Cliques.size()+"\n"+
				"temps de traitement total en milli secondes :"+ (endTime-startTime)+"\n";


	}

	/**
	 * 
	 * @param NameFileText
	 * @param dico_wTLexUnit_wTsyn
	 * @param wordDico1
	 * @param wordDico2
	 * @param DicoSynonyme
	 * @param DicoWordTag
	 * @param Thesaurus
	 * @param tagger
	 */
	public ChampSemantique3(String NameFileText, Hashtable<TaggedWord, ArrayList<TaggedWord>> dico_wTLexUnit_wTsyn,
			Hashtable<TaggedWord,Integer> wordDico1,
			Hashtable<Integer,TaggedWord> wordDico2,
			ArrayList<String> DicoSynonyme,
			Hashtable<String,ArrayList<TaggedWord>> DicoWordTag,
			ArrayList<TaggedWord>Thesaurus,
			PixTaggerFrench tagger) {
		super();
		long startTime = System.currentTimeMillis();
		
		this.setFrenchtagger(tagger);
		this.setThesaurus(Thesaurus);
		//RecuperationDico(NamefileDico);

		this.setwTdico(wordDico2);
		this.setwTnodes(wordDico1);
		this.setDicoSynonyme(DicoSynonyme);
		this.setDicoWordTag(DicoWordTag);
		this.setDico_wTLexUnit_wTsyn(dico_wTLexUnit_wTsyn);

		long endTimeDico = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du dico en millisecondes "+ (endTimeDico-startTime)+"\n";
		ArrayList<String> NamesFilesText = new ArrayList<String>();
		NamesFilesText.add(NameFileText);
		RecuperationTextDicoWithThesaurus(NamesFilesText,Thesaurus);
		long endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du texte en millisecondes "+ (endTime-endTimeDico)+"\n";

		long startTimeRempli= System.currentTimeMillis();
		RemplissageVoisinageNoeudsTextFastPertinence();
		long endTimeRempli = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de remplissage de la matrice de voisinage en millisecondes "+ (endTimeRempli-startTimeRempli)+"\n";

		//startTime = System.currentTimeMillis();


		ArrayList<Integer> VG= new ArrayList<Integer>();
		Enumeration<TaggedWord> EnumNoeud = MatriceVoisinage.keys();

		this.setCliques(new Hashtable<Integer,ArrayList<Integer>>());
		this.setCliquesString(new Hashtable<Integer,ArrayList<String>>());
		while (EnumNoeud.hasMoreElements())
		{
			TaggedWord noeud =EnumNoeud.nextElement();
			if (!wTnodes.containsKey(noeud)){
				
			}else{
				Integer IDnoeud =wTnodes.get(noeud);
				VG.add(IDnoeud);
			}
		}
		ArrayList<Integer> X=new ArrayList<Integer>();
		ArrayList<Integer> R=new ArrayList<Integer>();

		
		this.TextSortie=this.TextSortie+"liste de cliques trouvées :\n";
		//BronKerbosch1_1Autre(VG,X, R);
		BronKerbosch1_2(VG,X, R);
		//BronKerbosch1_1Pivot(VG,X, R);
		
		endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"nombre de cliques trouvées : "+Cliques.size()+"\n"+
				"temps de traitement total en milli secondes :"+ (endTime-startTime)+"\n";


	}

	/**
	 * 
	 * @param NameFile
	 * @param dicoSyn
	 * @param tagger
	 * @param pertinance
	 */
	public ChampSemantique3(File NameFile,DictionnaireSyn dicoSyn,PixTaggerFrench tagger, int pertinance) {
		super();
		long startTime = System.currentTimeMillis();
		if (pertinance<=0){
		//	pertinance=1;
		}
		this.setDicoSyn(dicoSyn);
		this.setFrenchtagger(tagger);
		this.setwTdico(dicoSyn.getwTdico());
		this.setwTnodes(dicoSyn.getwTnodes());
		this.setDicoSynonyme(dicoSyn.getDicoSynonyme());
		this.setDicoWordTag(dicoSyn.getDicoWordTag());
		long endTimeDico = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du dico en millisecondes "+ (endTimeDico-startTime)+"\n";
		RecuperationFromFile_Dico(NameFile,1);//RecuperationTextStringDico(Text,1);
		long endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"temps de traitement du texte en millisecondes "+ (endTime-endTimeDico)+"\n";

		//MatriceVoisinage = DictionnaireSyn.RemplissageVoisinageNoeudsText(dicoSyn.getDicoPath(),dicoSyn,this.getTextWordTag());
		//RemplissageVoisinageNoeudsText(dicoSyn.getDicoPath());
		MatriceVoisinage = DictionnaireSyn.RemplissageVoisinageNoeudsTextFastPertinence(dicoSyn.getDicoPath(),dicoSyn,this.getTextWordTag());
		//startTime = System.currentTimeMillis();

		ArrayList<Integer> VG= new ArrayList<Integer>();
		Enumeration<TaggedWord> EnumNoeud = MatriceVoisinage.keys();

		this.setCliques(new Hashtable<Integer,ArrayList<Integer>>());
		this.setCliquesString(new Hashtable<Integer,ArrayList<String>>());
		while (EnumNoeud.hasMoreElements())
		{
			TaggedWord noeud =EnumNoeud.nextElement();
			if (!wTnodes.containsKey(noeud)){
				
			}else{
				Integer IDnoeud =wTnodes.get(noeud);
				VG.add(IDnoeud);
			}
		}
		ArrayList<Integer> X=new ArrayList<Integer>();
		ArrayList<Integer> R=new ArrayList<Integer>();

		
		this.TextSortie=this.TextSortie+"liste de cliques trouvées :\n";
		BronKerbosch1_2Pert(VG,X, R,pertinance);
		
		endTime = System.currentTimeMillis();
		
		this.TextSortie=this.TextSortie+"nombre de cliques trouvées : "+Cliques.size()+"\n"+
				"temps de calcul en milli secondes pour bron kerbosch1 sans pivot :"+ (endTime-startTime)+"\n";


	}
	
	
	
	/**
	 * fonction servant a recuperer le dictionnaire  grace à un texte sous format string
	 * @param Text - String
	 */
	private  void RecuperationTextStringDico(String Text,int occur){

		PixTaggerFrench tagger = this.getFrenchtagger();
		ArrayList<String> TableauDico = new ArrayList<String>();
		ArrayList<TaggedWord> TableauwTDico = new ArrayList<TaggedWord>();
		int NbMots = 0;

		Hashtable<String,TaggedWord> word3= new Hashtable<String,TaggedWord>();
		Hashtable<TaggedWord,Integer> word4= new Hashtable<TaggedWord,Integer>();

		Text = Text.toLowerCase();
		String[] ligneText = {Text};
		if(Text.contains(". ")){
		ligneText = Text.split("\\. ");
		}/**/
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
				sentencePreTraitement = Frenchtagger.ModifTextStringForLemmatization(sentencePreTraitement);
				sentencePreTraitement = Frenchtagger.tokenize(sentencePreTraitement);

				ArrayList<TaggedWord> taggedWords = Frenchtagger.tagsentence(sentencePreTraitement);
				ArrayList<TaggedWord> WordsTagged = new ArrayList<TaggedWord>();


				for (int i =0;i<taggedWords.size();i++)
				{
					TaggedWord mot = taggedWords.get(i);
					//récupération des Good-Word "grossier"
					if ((mot.getTag().equals("N"))|(mot.getTag().equals("A"))|(mot.getTag().equals("V"))) //|(mot.tag().equals("ADV")) on ne garde que les adjectifs, noms, verbes
					{
                                                
						if(mot.getToken().length()>=3){

                                                
						//sentencePostTraitement= sentencePostTraitement + Lemme+ " ";
						WordsTagged.add(mot);
						String strMot = mot.getLemme();
						if(!word3.containsKey(strMot)){
							word3.put(strMot, mot);
							}
						}
					}else{
						WordsTagged.add(mot);
						String strMot = mot.getLemme();
						if(!word3.containsKey(strMot)){
							word3.put(strMot, mot);
						}
					}
					
				}
				

				/*fin utilisation lemmatiseur*/

				if (WordsTagged.size()>=3){
					Iterator<TaggedWord> wTiterator =WordsTagged.iterator();
					while (wTiterator.hasNext()){
						TaggedWord wTLemme = wTiterator.next();
						NbMots++;
						if(this.wTnodes.containsKey(wTLemme)){
							//WordTag wTLemme =MorphologyMervyn.stemStaticMervyn(mot);
							if(!wTLemme.getToken().equals("")&&!TableauwTDico.contains(wTLemme))
							{
								TableauwTDico.add(wTLemme);	//remplissage non rangé du dictionnaire
								TableauDico.add(wTLemme.getLemme());
								word4.put(wTLemme,1);
							}
							else if (!wTLemme.getToken().equals("")&&TableauwTDico.contains(wTLemme))
							{
								/* cas où l'on veut les occurences par pages
						ArrayList<Integer> VectOccurenceWord = word3.get(mot);
						Integer occurence = VectOccurenceWord.get(NumeroFile);
						occurence++;	//itération des occurences
						VectOccurenceWord.set(NumeroFile, occurence);
						word3.put(mot,VectOccurenceWord);*/
								Integer occurence = word4.get(wTLemme);
								occurence++;	//itération des occurences
								word4.put(wTLemme,occurence);
							}
						}
					}

				}
			}
		}




		//Classification en ordre croissant des elts du dictionnaire
		Collections.sort(TableauDico);

		//introduction d une notion d'occurence
		
		
		Set<TaggedWord> set1 = word4.keySet();
		Iterator<TaggedWord> wTiterator = set1.iterator();
		while (wTiterator.hasNext()){
			TaggedWord mot = wTiterator.next();
			Integer nbOccurence = word4.get(mot);
			if (nbOccurence<occur){
				TableauwTDico.remove(mot);
			}
		}
		
		this.setDicoCorresStrWt(word3);
		this.setDicoText(TableauDico);
		this.setNombreMotTotal(NbMots);
		this.setDicoEtOccurenceSimple(word4);
		this.setTextWordTag(TableauwTDico);
		//this.setDicoEtOccurence(word3);

	}

	
	/**
	 * fonction servant a recuperer le dictionnaire  grace à un texte sous format string
	 * @param NameFileText
	 * @param occur
	 * @return Hashtable<String,WordTag>
	 */
	private  Hashtable<String,TaggedWord> RecuperationFromFile_Dico(File NameFileText,int occur){

		PixTaggerFrench tagger = this.getFrenchtagger();
		ArrayList<String> TableauDico = new ArrayList<String>();
		ArrayList<TaggedWord> TableauwTDico = new ArrayList<TaggedWord>();
		//ArrayList<WordTag> WordTagDico = new ArrayList<WordTag>();
		int NbMots = 0;

		Hashtable<String,TaggedWord> word3= new Hashtable<String,TaggedWord>();
		Hashtable<TaggedWord,Integer> word4= new Hashtable<TaggedWord,Integer>();

		FileInputStream file;
		try {
			file = new FileInputStream(NameFileText);
				//si l'on veut les occurences par pages
				//int NumeroFile = nameFileTexts.indexOf(nameFileText);


				InputStreamReader Corpus=new InputStreamReader(file, Charset.forName("UTF8"));
				BufferedReader CorpusReader=new BufferedReader(Corpus);
				String ligne;
				// lecture ligne par ligne (sachant qu'une ligne correspond a une phrase)
				while ((ligne=CorpusReader.readLine())!=null){
					if(ligne.length()<3)
					{
						ligne="";
					}
					if(!ligne.equals("")&&!ligne.isEmpty()&&!ligne.contains("\n")&&!ligne.contains("\t")&&(!ligne.equals("Â ")))
					{
						//Pretraitement du corpus
						String sentencePreTraitement=ligne;
						sentencePreTraitement = Frenchtagger.ModifTextStringForLemmatization(sentencePreTraitement);
						sentencePreTraitement = Frenchtagger.tokenize(sentencePreTraitement);
						ArrayList<TaggedWord> taggedWords = Frenchtagger.tagsentence(sentencePreTraitement);
						ArrayList<TaggedWord> WordsTagged = new ArrayList<TaggedWord>();


						for (int i =0;i<taggedWords.size();i++)
						{
							TaggedWord mot = taggedWords.get(i);
							//récupération des Good-Word "grossier"
							if ((mot.getTag().equals("N"))|(mot.getTag().equals("A"))|(mot.getTag().equals("V"))) //|(mot.tag().equals("ADV")) on ne garde que les adjectifs, noms, verbes
							{
                                                                
                                                                
								if(mot.getToken().length()>=3){
								WordsTagged.add(mot);
								String strMot = mot.getLemme();
								if(!word3.containsKey(strMot)){
									word3.put(strMot, mot);
									}
								}
							}else{
								WordsTagged.add(mot);
								String strMot = mot.getLemme();
								if(!word3.containsKey(strMot)){
									word3.put(strMot, mot);
								}
							}
							
						}
						

						/*fin utilisation lemmatiseur*/

						if (WordsTagged.size()>=3){
							Iterator<TaggedWord> wTiterator =WordsTagged.iterator();
							while (wTiterator.hasNext()){
								TaggedWord wTLemme = wTiterator.next();
								//WordTag wTLemme =MorphologyMervyn.stemStaticMervyn(mot);
								if(this.wTnodes.containsKey(wTLemme)){


									if(!wTLemme.getToken().equals("")&&!TableauwTDico.contains(wTLemme))
									{
										TableauwTDico.add(wTLemme);	//remplissage non rangé du dictionnaire
										TableauDico.add(wTLemme.getLemme());
										word4.put(wTLemme,1);
									}
									else if (!wTLemme.getToken().equals("")&&TableauwTDico.contains(wTLemme))
									{
										/* cas où l'on veut les occurences par pages
										ArrayList<Integer> VectOccurenceWord = word3.get(mot);
										Integer occurence = VectOccurenceWord.get(NumeroFile);
										occurence++;	//itération des occurences
										VectOccurenceWord.set(NumeroFile, occurence);
										word3.put(mot,VectOccurenceWord);*/
										Integer occurence = word4.get(wTLemme);
										occurence++;	//itération des occurences
										word4.put(wTLemme,occurence);
									}
								}
							}
						}
					}
				}


				file.close();
			
		//Classification en ordre croissant des elts du dictionnaire
		Collections.sort(TableauDico);

		//introduction d une notion d'occurence
		
		Set<TaggedWord> set1 = word4.keySet();
		Iterator<TaggedWord> wTiterator = set1.iterator();
		while (wTiterator.hasNext()){
			TaggedWord mot = wTiterator.next();
			Integer nbOccurence = word4.get(mot);
			if (nbOccurence<occur){
				TableauwTDico.remove(mot);
			}
		}
		
			this.setDicoCorresStrWt(word3);
			this.setDicoText(TableauDico);
			this.setNombreMotTotal(NbMots);
			this.setDicoEtOccurenceSimple(word4);
			this.setTextWordTag(TableauwTDico);
			//this.setDicoEtOccurence(word3);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return word3;
	
	}
 	/**
	 * fonction servant a recuperer les correspondances string- wortag d'un texte sous format file
	 * @param NameFileText
	 * @param dicoSyn
	 * @param tagger
	 * @param occur
	 * @return
	 */
	public static Hashtable<String,TaggedWord> RecuperationFromFile_Dico(File NameFileText,DictionnaireSyn dicoSyn,PixTaggerFrench tagger,int occur){

		
		Hashtable<TaggedWord, Integer> wTnodes=dicoSyn.getwTnodes();
		ArrayList<String> TableauDico = new ArrayList<String>();
		ArrayList<TaggedWord> TableauwTDico = new ArrayList<TaggedWord>();
		//ArrayList<WordTag> WordTagDico = new ArrayList<WordTag>();
		
		Hashtable<String,TaggedWord> word3= new Hashtable<String,TaggedWord>();
		Hashtable<TaggedWord,Integer> word4= new Hashtable<TaggedWord,Integer>();

		FileInputStream file;
		try {
			file = new FileInputStream(NameFileText);
				//si l'on veut les occurences par pages
				//int NumeroFile = nameFileTexts.indexOf(nameFileText);


				InputStreamReader Corpus=new InputStreamReader(file, Charset.forName("UTF8"));
				BufferedReader CorpusReader=new BufferedReader(Corpus);
				String ligne;
				// lecture ligne par ligne (sachant qu'une ligne correspond a une phrase)
				while ((ligne=CorpusReader.readLine())!=null){
					if(ligne.length()<3)
					{
						ligne="";
					}
					if(!ligne.equals("")&&!ligne.isEmpty()&&!ligne.contains("\n")&&!ligne.contains("\t")&&(!ligne.equals("Â ")))
					{
						//Pretraitement du corpus
						String sentencePreTraitement=ligne;
						sentencePreTraitement = tagger.ModifTextStringForLemmatization(sentencePreTraitement);
						sentencePreTraitement = tagger.tokenize(sentencePreTraitement);
						ArrayList<TaggedWord> taggedWords = tagger.tagsentence(sentencePreTraitement);
						ArrayList<TaggedWord> WordsTagged = new ArrayList<TaggedWord>();


						for (int i =0;i<taggedWords.size();i++)
						{
							TaggedWord mot = taggedWords.get(i);
							//récupération des Good-Word "grossier"
							if ((mot.getTag().equals("N"))|(mot.getTag().equals("A"))|(mot.getTag().equals("V"))) //|(mot.tag().equals("ADV")) on ne garde que les adjectifs, noms, verbes
							{
                                                                
								if(mot.getToken().length()>=3){
								//sentencePostTraitement= sentencePostTraitement + Lemme+ " ";
								WordsTagged.add(mot);
								String strMot = mot.getLemme();
								if(!word3.containsKey(strMot)){
									word3.put(strMot, mot);
									}
								}
							}else{
								WordsTagged.add(mot);
								String strMot = mot.getLemme();
								if(!word3.containsKey(strMot)){
									word3.put(strMot, mot);
								}
							}
							
						}
						

						/*fin utilisation lemmatiseur*/

						if (WordsTagged.size()>=3){
							Iterator<TaggedWord> wTiterator =WordsTagged.iterator();
							while (wTiterator.hasNext()){
								TaggedWord wTLemme = wTiterator.next();
								//WordTag wTLemme =MorphologyMervyn.stemStaticMervyn(mot);
								if(wTnodes.containsKey(wTLemme)){


									if(!wTLemme.getToken().equals("")&&!TableauwTDico.contains(wTLemme))
									{
										TableauwTDico.add(wTLemme);	//remplissage non rangé du dictionnaire
										TableauDico.add(wTLemme.getLemme());
										word4.put(wTLemme,1);
									}
									else if (!wTLemme.getToken().equals("")&&TableauwTDico.contains(wTLemme))
									{
										/* cas où l'on veut les occurences par pages
										ArrayList<Integer> VectOccurenceWord = word3.get(mot);
										Integer occurence = VectOccurenceWord.get(NumeroFile);
										occurence++;	//itération des occurences
										VectOccurenceWord.set(NumeroFile, occurence);
										word3.put(mot,VectOccurenceWord);*/
										Integer occurence = word4.get(wTLemme);
										occurence++;	//itération des occurences
										word4.put(wTLemme,occurence);
									}
								}
							}
						}
					}
				}


				file.close();
			
		//Classification en ordre croissant des elts du dictionnaire
		Collections.sort(TableauDico);

		//introduction d une notion d'occurence
		
		Set<TaggedWord> set1 = word4.keySet();
		Iterator<TaggedWord> wTiterator = set1.iterator();
		while (wTiterator.hasNext()){
			TaggedWord mot = wTiterator.next();
			Integer nbOccurence = word4.get(mot);
			if (nbOccurence<occur){
				TableauwTDico.remove(mot);
			}
		}
		
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return word3;
	
	}

	
	/**
	 * fonction servant a recuperer le dictionnaire  grace à un texte sous format string
	 * @param Text - String
	 * @param Tag - String
	 */
	private  void RecuperationTextStringDico(String Text,String Tag,int occur){

		PixTaggerFrench tagger = this.getFrenchtagger();
		ArrayList<String> TableauDico = new ArrayList<String>();
		ArrayList<TaggedWord> TableauwTDico = new ArrayList<TaggedWord>();
		//ArrayList<WordTag> WordTagDico = new ArrayList<WordTag>();
		int NbMots = 0;

		Hashtable<TaggedWord,Integer> word4= new Hashtable<TaggedWord,Integer>();

		String[] ligneText = {Text};
		if(Text.contains(". ")){
		ligneText = Text.split("\\. ");
		}/**/
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
				ArrayList<TaggedWord> taggedWords = tagger.tagsentence(sentencePreTraitement);
				ArrayList<TaggedWord> WordsTagged = new ArrayList<TaggedWord>();


				for (int i =0;i<taggedWords.size();i++)
				{
					TaggedWord mot = taggedWords.get(i);

					//récupération des Good-Word "grossier"
					if ((mot.getTag().equals("N"))|(mot.getTag().equals("A"))|(mot.getTag().equals("V"))) //|(mot.tag().equals("ADV")) on ne garde que les adjectifs, noms, verbes
					{
                                                
						if(mot.getToken().length()>=3){
						//sentencePostTraitement= sentencePostTraitement + Lemme+ " ";
						WordsTagged.add(mot);
						}
					}
					else
					{
						//rien faire
						//sentencePostTraitement= sentencePostTraitement+mot.word()+" ";
					}
					
				}
				

				/*fin utilisation lemmatiseur*/

				if (WordsTagged.size()>=3){
					Iterator<TaggedWord> wTiterator =WordsTagged.iterator();
					while (wTiterator.hasNext()){
						TaggedWord wTLemme = wTiterator.next();
						NbMots++;
						if(this.wTnodes.containsKey(wTLemme)){
							//WordTag wTLemme =MorphologyMervyn.stemStaticMervyn(mot);
							if(!wTLemme.getToken().equals("")&wTLemme.getTag().equals(Tag)&&!TableauwTDico.contains(wTLemme))
							{
								TableauwTDico.add(wTLemme);	//remplissage non rangé du dictionnaire
								TableauDico.add(wTLemme.getLemme());
								word4.put(wTLemme,1);
							}
							else if (!wTLemme.getToken().equals("")&&wTLemme.getTag().equals(Tag)&TableauwTDico.contains(wTLemme))
							{
								/* cas où l'on veut les occurences par pages
						ArrayList<Integer> VectOccurenceWord = word3.get(mot);
						Integer occurence = VectOccurenceWord.get(NumeroFile);
						occurence++;	//itération des occurences
						VectOccurenceWord.set(NumeroFile, occurence);
						word3.put(mot,VectOccurenceWord);*/
								Integer occurence = word4.get(wTLemme);
								occurence++;	//itération des occurences
								word4.put(wTLemme,occurence);
							}
						}
					}

				}
			}
		}




		//Classification en ordre croissant des elts du dictionnaire
		Collections.sort(TableauDico);

		//introduction d une notion d'occurence
		
		
		Set<TaggedWord> set1 = word4.keySet();
		Iterator<TaggedWord> wTiterator = set1.iterator();
		while (wTiterator.hasNext()){
			TaggedWord mot = wTiterator.next();
			Integer nbOccurence = word4.get(mot);
			if (nbOccurence<occur){
				TableauwTDico.remove(mot);
			}
		}
		

		this.setDicoText(TableauDico);
		this.setNombreMotTotal(NbMots);
		this.setDicoEtOccurenceSimple(word4);
		this.setTextWordTag(TableauwTDico);
		//this.setDicoEtOccurence(word3);

	}

	
	/**
	 * fonction servant a recuperer le dictionnaire  grace à un texte sous format wordtag
	 * @param Text - ArrayList<ArrayList<TaggedWord>>
	 */
	private  void RecuperationWtTextStringDico(ArrayList<ArrayList<TaggedWord>> Text,int occur){

		ArrayList<String> TableauDico = new ArrayList<String>();
		ArrayList<TaggedWord> TableauwTDico = new ArrayList<TaggedWord>();
		//ArrayList<WordTag> WordTagDico = new ArrayList<WordTag>();
		int NbMots = 0;

		Hashtable<TaggedWord,Integer> word4= new Hashtable<TaggedWord,Integer>();


		for (int l =0;l<Text.size();l++)
		{


			ArrayList<TaggedWord> WordsTagged = new ArrayList<TaggedWord>(Text.get(l));


			if (WordsTagged.size()>=3){
				Iterator<TaggedWord> wTiterator =WordsTagged.iterator();
				while (wTiterator.hasNext()){
					TaggedWord wTLemme = wTiterator.next();
					NbMots++;
					if(this.wTnodes.containsKey(wTLemme)){
						//WordTag wTLemme =MorphologyMervyn.stemStaticMervyn(mot);
						if(!wTLemme.getToken().equals("")&&!TableauwTDico.contains(wTLemme))
						{
							TableauwTDico.add(wTLemme);	//remplissage non rangé du dictionnaire
							TableauDico.add(wTLemme.getLemme());
							word4.put(wTLemme,1);
						}
						else if (!wTLemme.getToken().equals("")&&TableauwTDico.contains(wTLemme))
						{
							/* cas où l'on veut les occurences par pages
						ArrayList<Integer> VectOccurenceWord = word3.get(mot);
						Integer occurence = VectOccurenceWord.get(NumeroFile);
						occurence++;	//itération des occurences
						VectOccurenceWord.set(NumeroFile, occurence);
						word3.put(mot,VectOccurenceWord);*/
							Integer occurence = word4.get(wTLemme);
							occurence++;	//itération des occurences
							word4.put(wTLemme,occurence);
						}
					}
				}
			}
		}

		//Classification en ordre croissant des elts du dictionnaire
		Collections.sort(TableauDico);

		//introduction d une notion d'occurence

		
		Set<TaggedWord> set1 = word4.keySet();
		Iterator<TaggedWord> wTiterator = set1.iterator();
		while (wTiterator.hasNext()){
			TaggedWord mot = wTiterator.next();
			Integer nbOccurence = word4.get(mot);
			if (nbOccurence<occur){
				TableauwTDico.remove(mot);
			}
		}
		

		this.setDicoText(TableauDico);
		this.setNombreMotTotal(NbMots);
		this.setDicoEtOccurenceSimple(word4);
		this.setTextWordTag(TableauwTDico);
		//this.setDicoEtOccurence(word3);

	}

	/**
	 * fonction servant a recuperer le dictionnaire  grace à un texte sous format wordtag
	 * @param Text - ArrayList<ArrayList<WordTag>>
	 * @param Tag - String
	 */
	private  void RecuperationWtTextStringDico(ArrayList<ArrayList<TaggedWord>> Text,String Tag, int occur){

		ArrayList<String> TableauDico = new ArrayList<String>();
		ArrayList<TaggedWord> TableauwTDico = new ArrayList<TaggedWord>();
		//ArrayList<WordTag> WordTagDico = new ArrayList<WordTag>();
		int NbMots = 0;

		Hashtable<TaggedWord,Integer> word4= new Hashtable<TaggedWord,Integer>();

		if(Text.isEmpty()){	//test d'erreur
			int NbMot=NbMots;
		}
		for (int l =0;l<Text.size();l++)
		{	
			ArrayList<TaggedWord> WordsTagged = new ArrayList<TaggedWord>(Text.get(l));


			if (WordsTagged.size()>=3){
				Iterator<TaggedWord> wTiterator =WordsTagged.iterator();
				while (wTiterator.hasNext()){
					TaggedWord wTLemme = wTiterator.next();
					NbMots++;
					if(this.wTnodes.containsKey(wTLemme)){
						//WordTag wTLemme =MorphologyMervyn.stemStaticMervyn(mot);

						if(!wTLemme.getToken().equals("")&wTLemme.getTagShortFomre().equals(Tag)&&!TableauwTDico.contains(wTLemme))
						{
							TableauwTDico.add(wTLemme);	//remplissage non rangé du dictionnaire
							TableauDico.add(wTLemme.getLemme());
							word4.put(wTLemme,1);
						}
						else if (!wTLemme.getToken().equals("")&wTLemme.getTag().equals(Tag)&&TableauwTDico.contains(wTLemme))
						{
							/* cas où l'on veut les occurences par pages
						ArrayList<Integer> VectOccurenceWord = word3.get(mot);
						Integer occurence = VectOccurenceWord.get(NumeroFile);
						occurence++;	//itération des occurences
						VectOccurenceWord.set(NumeroFile, occurence);
						word3.put(mot,VectOccurenceWord);*/
							Integer occurence = word4.get(wTLemme);
							occurence++;	//itération des occurences
							word4.put(wTLemme,occurence);
						}
					}
				}
			}
		}

		//Classification en ordre croissant des elts du dictionnaire
		Collections.sort(TableauDico);

		//introduction d une notion d'occurence

		
		Set<TaggedWord> set1 = word4.keySet();
		Iterator<TaggedWord> wTiterator = set1.iterator();
		while (wTiterator.hasNext()){
			TaggedWord mot = wTiterator.next();
			Integer nbOccurence = word4.get(mot);
			if (nbOccurence<occur){
				TableauwTDico.remove(mot);
			}
		}
		

		this.setDicoText(TableauDico);
		this.setNombreMotTotal(NbMots);
		this.setDicoEtOccurenceSimple(word4);
		this.setTextWordTag(TableauwTDico);
		//this.setDicoEtOccurence(word3);

	}

	
	/**
	 * Fonction servant à recuperer les mots du dictionnaire de synonyme
	 */
	private  void RecuperationDico2( String NamefileDico){

		Hashtable<TaggedWord,Integer> wordDico1= new Hashtable<TaggedWord,Integer>();
		Hashtable<Integer,TaggedWord> wordDico2= new Hashtable<Integer,TaggedWord>();
		int index =0;
		ArrayList<String> word1= new ArrayList<String>();
		ArrayList<String> wordNonTagger= new ArrayList<String>();
		ArrayList<TaggedWord> word2 = new ArrayList<TaggedWord>();
		Hashtable<String,ArrayList<TaggedWord>> dicoWord = new Hashtable<String,ArrayList<TaggedWord>>();
		String LexicalUnit=null;
		Boolean isChosenLexicalUnit = true;			//indique si on est sur l'unite lexical choisie

		try {
			FileInputStream file = new FileInputStream(new File(NamefileDico));
			InputStreamReader Corpus=new InputStreamReader(file, Charset.forName("UTF8"));
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

				//test presence sur la ligne indiquant l'unité lexicale
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

					//récuperation du tag
					boolean presenceTag = true;
					String[] listeTag = fenetre[0].split(" ");
					ArrayList<TaggedWord> liste = new ArrayList<TaggedWord>();
					for (int j = 0;j<listeTag.length;j++){
						String tag = listeTag[j];
						if (tag.equals("")){
							presenceTag=false;
							wordNonTagger.add(LexicalUnit);
							/*liste.add(LexicalUnit+"/N");
							liste.add(LexicalUnit+"/A");
							liste.add(LexicalUnit+"/V");
							liste.add(LexicalUnit+"/ADV"); rien faire*/
						}
						else if (tag.equals("Nom")){
							liste.add(new TaggedWord(LexicalUnit, "N", LexicalUnit));
						}
						else if (tag.equals("Verbe")){
							liste.add(new TaggedWord(LexicalUnit, "V", LexicalUnit));
						}
						else if (tag.equals("Adverbe")){
							liste.add(new TaggedWord(LexicalUnit, "ADV", LexicalUnit));
						}
						else if (tag.equals("Adjectif")){
							liste.add(new TaggedWord(LexicalUnit, "A", LexicalUnit));
						}
						else if (tag.equals("Adjectif_cardinal")){
							//liste.add( LexicalUnit+"/A"); rien fqire
						}
					}



					int i = 1;
					if(presenceTag){
						for (int j = 0;j<liste.size();j++){
							TaggedWord mot = liste.get(j);


							//traitement du cas ()
							word2.add(mot);

							//ajout dans le dictionnaire en evitant les doublons
							if(!wordDico2.contains(mot)){
								wordDico1.put(mot, index);
								wordDico2.put(index, mot );
							}
							
							if(wordDico1.size()+i==(wordDico2.size())){


								i++;
							}
							index++;
							String Lemme = mot.getLemme();  //lemmatisation du mot
							ChoixLexicauxPossible.add(Lemme);
						}
					}
					if (!word2.isEmpty()){}
					dicoWord.put(LexicalUnit, word2);


					//récupération des mots possibles
					for (int k = 0;k<ChoixLexicauxPossible.size();k++){
						if (!ChoixLexicauxPossible.get(k).equals("")){
							word1.add(ChoixLexicauxPossible.get(k));
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
		/**/Iterator<String> IteratorMot = wordNonTagger.iterator();
		while(IteratorMot.hasNext()){
			String Strmot = IteratorMot.next();
			ArrayList<String> ChoixLexicauxPossible = new ArrayList<String>();
			word2 = new ArrayList<TaggedWord>();
			//test presence dans le dico
			if(!dicoWord.containsKey(Strmot)){
				ArrayList<TaggedWord> liste = new ArrayList<TaggedWord>();
				liste.add( new TaggedWord(Strmot, "", Strmot));
				/*liste.add(Strmot+"/N");
				liste.add(Strmot+"/A");
				liste.add(Strmot+"/V");
				liste.add(Strmot+"/ADV");*/

				int i = 1;
				for (int j = 0;j<liste.size();j++){
					TaggedWord mot = liste.get(j);

					//traitement du cas ()
					word2.add(mot);

					//ajout dans le dictionnaire en evitant les doublons
					if(!wordDico2.contains(mot)){
						wordDico1.put(mot, index);
						wordDico2.put(index, mot );
					}
					if(wordDico1.size()+i==(wordDico2.size())){


						i++;
					}
					index++;
					String Lemme = mot.getLemme();  //lemmatisation du mot
					ChoixLexicauxPossible.add(Lemme);
				}
			}
			if (!word2.isEmpty()){}
			dicoWord.put(LexicalUnit, word2);


			//récupération des mots possibles
			for (int k = 0;k<ChoixLexicauxPossible.size();k++){
				if (!ChoixLexicauxPossible.get(k).equals("")){
					word1.add(ChoixLexicauxPossible.get(k));
				}
			}

		}

		this.setwTdico(wordDico2);
		this.setwTnodes(wordDico1);
		this.setDicoSynonyme(word1);
		this.setDicoWordTag(dicoWord);
	}



	/**
	 * Fonction servant à recuperer les mots du dictionnaire de synonyme de maniere a pouvoir obtenir une matrice d'occurence (on peut l'utiliser avec le dico meme imparfait)
	 */
	private  void RecuperationDico( String NamefileDico){

		Hashtable<TaggedWord,Integer> wordDico1= new Hashtable<TaggedWord,Integer>();
		Hashtable<Integer,TaggedWord> wordDico2= new Hashtable<Integer,TaggedWord>();
		int index =0;
		ArrayList<String> word1= new ArrayList<String>();
		ArrayList<TaggedWord> word2 = new ArrayList<TaggedWord>();
		//ArrayList<WordTag> word3 = new ArrayList<WordTag>();	//liste voisins tagger
		Hashtable<TaggedWord,ArrayList<TaggedWord>> dico_wTLexUnit_wTsyn = new Hashtable<TaggedWord,ArrayList<TaggedWord>>();
		Hashtable<String,ArrayList<TaggedWord>> dicoWord = new Hashtable<String,ArrayList<TaggedWord>>();
		String LexicalUnit=null;
		Boolean isChosenLexicalUnit = true;			//indique si on est sur l'unite lexical choisie

		try {
			FileInputStream file = new FileInputStream(new File(NamefileDico));
			InputStreamReader Corpus=new InputStreamReader(file, Charset.forName("UTF8"));
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

				//test presence sur la ligne indiquant l'unité lexicale
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

					//récuperation du tag
					boolean presenceTag = true;

					String[] listeTag = fenetre[0].split(" ");
					ArrayList<TaggedWord> liste = new ArrayList<TaggedWord>();
					for (int j = 0;j<listeTag.length;j++){
						String tag = listeTag[j];
						if (tag.equals("")){
							presenceTag=false;
							//ajout des mots non tagger du dico avec comme tag NoTag
							liste.add(new TaggedWord(LexicalUnit, "", LexicalUnit));
							//wordNonTagger.add(LexicalUnit);
							/*liste.add(LexicalUnit+"/N");
							liste.add(LexicalUnit+"/A");
							liste.add(LexicalUnit+"/V");
							liste.add(LexicalUnit+"/ADV"); rien faire*/
						}
						else if (tag.equals("Nom")){
							liste.add(new TaggedWord(LexicalUnit, "N", LexicalUnit));
						}
						else if (tag.equals("Verbe")){
							liste.add(new TaggedWord(LexicalUnit, "V", LexicalUnit));
						}
						else if (tag.equals("Adverbe")){
							liste.add(new TaggedWord(LexicalUnit, "ADV", LexicalUnit));
						}
						else if (tag.equals("Adjectif")){
							liste.add(new TaggedWord(LexicalUnit, "A", LexicalUnit));
						}
						else if (tag.equals("Adjectif_cardinal")){
							//liste.add( LexicalUnit+"/A"); rien fqire
						}
					}



					int i = 1;
					if(presenceTag){
						for (int j = 0;j<liste.size();j++){
							TaggedWord mot = liste.get(j);

							//sauvearde des lemmes comme entrée car il y a des mots differents qui ont le meme lemme et le meme tag (abattu et abattre)

							//traitement du cas ()
							word2.add(mot);

							//ajout dans le dictionnaire en evitant les doublons
							if(!wordDico2.contains(mot)){
								wordDico1.put(mot, index);
								wordDico2.put(index, mot );
							}

							if(wordDico1.size()+i==(wordDico2.size())){


								i++;
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
							wordDico1.put(mot, index);
							wordDico2.put(index, mot );
						}

						if(wordDico1.size()+i==(wordDico2.size())){

							i++;
						}
						index++;
						ChoixLexicauxPossible.add(LexicalUnit);
					}
					if (!word2.isEmpty()){

						// construction des matrices issus du dico

						//1er choix //en entrée on prend le mot tel quel du dictionnaire
						dicoWord.put(LexicalUnit, word2);

						//récupération des mots possibles
						for (int k = 0;k<ChoixLexicauxPossible.size();k++){
							if (!ChoixLexicauxPossible.get(k).equals("")){
								word1.add(ChoixLexicauxPossible.get(k));
							}
						}

						//2eme choix //en entrée on prend le WordTag Lemmatisée tel quel du dictionnaire

						ArrayList<String> ListStringVoisins = new ArrayList<String>();	//Liste de string des voisins

						//récupération des voisins sous forme de mot
						for (int k=1;k<fenetre.length;k++){
							ListStringVoisins.add(fenetre[k].toLowerCase());
						}
						//récupération des voisins sous formes de WordTag
						for(int s =0;s<word2.size();s++){
							TaggedWord wTLexicalUnit = word2.get(s);
							ArrayList<TaggedWord> WordTagVoisins = new ArrayList<TaggedWord>();
							if(dico_wTLexUnit_wTsyn.containsKey(wTLexicalUnit)){	//gestion du cas ou le mot est deja present dans le dico
								WordTagVoisins = dico_wTLexUnit_wTsyn.get(wTLexicalUnit);
							}
							if (!wTLexicalUnit.getTag().equals("")){
								for (int k=0;k<ListStringVoisins.size();k++){
									String mot = ListStringVoisins.get(k);
									TaggedWord motVoisin = new TaggedWord();
									motVoisin.setTag(wTLexicalUnit.getTag());
									motVoisin.setToken(mot);
                                                                        motVoisin.setLemme(mot);

									//lemmatisation
									//WordTag lemmeVoisin = MorphologyMervyn.stemStaticMervyn(motVoisin);

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
					//sauvearde des lemmes comme entrée car il y a des mots differents qui ont le meme lemme et le meme tag (abattu et abattre)
					WordTag lemma =  MorphologyMervyn.stemStaticMervyn(mot);  //lemmatisation du mot
					//traitement du cas ()
					word2.add(lemma);

					//ajout dans le dictionnaire en evitant les doublons
					if(!wordDico2.contains(lemma)){
					wordDico1.put(lemma, index);
					wordDico2.put(index, lemma );
					}
					if(wordDico1.size()+i==(wordDico2.size())){


						i++;
					}
					index++;
					String Lemme = MorphologyMervyn.stemStaticMervyn(mot).word();  //lemmatisation du mot
					ChoixLexicauxPossible.add(Lemme);
				}
			}
			if (!word2.isEmpty()){}
			dicoWord.put(LexicalUnit, word2);


			//récupération des mots possibles
			for (int k = 0;k<ChoixLexicauxPossible.size();k++){
				if (!ChoixLexicauxPossible.get(k).equals("")){
					word1.add(ChoixLexicauxPossible.get(k));
				}
			}

		}*/

		dico_wTLexUnit_wTsyn=NettoyageDicoSyn(dico_wTLexUnit_wTsyn);
		this.setwTdico(wordDico2);
		this.setwTnodes(wordDico1);
		this.setDicoSynonyme(word1);
		this.setDicoWordTag(dicoWord);
		this.setDico_wTLexUnit_wTsyn(dico_wTLexUnit_wTsyn);
	}


	

	/**
	 * 
	 * @param DicoImparfait
	 * @return
	 */
	private Hashtable<TaggedWord,ArrayList<TaggedWord>> NettoyageDicoSyn (Hashtable<TaggedWord,ArrayList<TaggedWord>> DicoImparfait){

		Hashtable<TaggedWord,ArrayList<TaggedWord>> DicoBon = new Hashtable<TaggedWord,ArrayList<TaggedWord>>();

		//parcours de chaque unité lexicale du dictionnaire
		Iterator<TaggedWord> IteratorWT = DicoImparfait.keySet().iterator();
		while(IteratorWT.hasNext()){
			TaggedWord LexicalUnit = IteratorWT.next();
			//recuperation de la liste de voisins
			ArrayList<TaggedWord> ListeVoisinsLexicalUnit = DicoImparfait.get(LexicalUnit);	//ancienne liste de voisins
			ArrayList<TaggedWord> NewListeVoisinsLexicalUnit = new ArrayList<TaggedWord>();	//nouvelle liste de voisins
			//test sur les voisins
			//pour chaque voisins, on verifie que les liens de synonymie sont bien verifié
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
	 *  fonction servant a recuperer le dictionnaire  grace à un ou pllusieurs fichiers textes
	 */
	private  void RecuperationTextDico( ArrayList<String> nameFileTexts){

		PixTaggerFrench tagger = this.getFrenchtagger();
		ArrayList<String> TableauDico = new ArrayList<String>();
		ArrayList<TaggedWord> TableauwTDico = new ArrayList<TaggedWord>();
		//ArrayList<WordTag> WordTagDico = new ArrayList<WordTag>();
		int NbMots = 0;

		//initialisation à 0 du vecteur d'occurence //si l'on veut les occurences par pages
		/*int NbPages = nameFileTexts.size();
		ArrayList<Integer> VectOccurenceInit = new ArrayList<Integer>();
		for (int i=0;i<NbPages;i++)
		{
			VectOccurenceInit.add(0);
		}
		Hashtable<String,ArrayList<Integer>> word3= new Hashtable<String,ArrayList<Integer>>();
		 */

		Hashtable<TaggedWord,Integer> word4= new Hashtable<TaggedWord,Integer>();
		//Integer index = 0;	//permet de stocker l'index d'unmot dans le dictionnaire
		Iterator<String> SentenceIterator = nameFileTexts.iterator();
		while (SentenceIterator.hasNext())
		{

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
						ArrayList<TaggedWord> taggedWords = tagger.tagsentence(sentencePreTraitement);
						ArrayList<TaggedWord> WordsTagged = new ArrayList<TaggedWord>();


						for (int i =0;i<taggedWords.size();i++)
						{
							TaggedWord mot = taggedWords.get(i);
							//récupération des Good-Word "grossier"
							if ((mot.getTag().equals("N"))|(mot.getTag().equals("A"))|(mot.getTag().equals("V"))) //|(mot.tag().equals("ADV")) on ne garde que les adjectifs, noms, verbes
							{

								//sentencePostTraitement= sentencePostTraitement + Lemme+ " ";
								WordsTagged.add(mot);
							}
							else
							{
								//rien faire
								//sentencePostTraitement= sentencePostTraitement+mot.word()+" ";
							}

						}
						

						/*fin utilisation lemmatiseur*/

						if (WordsTagged.size()>=3){
							Iterator<TaggedWord> wTiterator =WordsTagged.iterator();
							while (wTiterator.hasNext()){
								TaggedWord wTLemme = wTiterator.next();

								if(this.wTnodes.containsKey(wTLemme)){
									//WordTag wTLemme =MorphologyMervyn.stemStaticMervyn(mot);
									if(!wTLemme.getToken().equals("")&&!TableauwTDico.contains(wTLemme))
									{
										TableauwTDico.add(wTLemme);	//remplissage non rangé du dictionnaire
										TableauDico.add(wTLemme.getLemme());
										word4.put(wTLemme,1);
									}
									else if (!wTLemme.getToken().equals("")&&TableauwTDico.contains(wTLemme))
									{
										/* cas où l'on veut les occurences par pages
								ArrayList<Integer> VectOccurenceWord = word3.get(mot);
								Integer occurence = VectOccurenceWord.get(NumeroFile);
								occurence++;	//itération des occurences
								VectOccurenceWord.set(NumeroFile, occurence);
								word3.put(mot,VectOccurenceWord);*/
										Integer occurence = word4.get(wTLemme);
										occurence++;	//itération des occurences
										word4.put(wTLemme,occurence);
									}
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
		//Classification en ordre croissant des elts du dictionnaire
		Collections.sort(TableauDico);

		//introduction d une notion d'occurence
		int occur = 5;

		Set<TaggedWord> set1 = word4.keySet();
		Iterator<TaggedWord> wTiterator = set1.iterator();
		while (wTiterator.hasNext()){
			TaggedWord mot = wTiterator.next();
			Integer nbOccurence = word4.get(mot);
			if (nbOccurence<occur){
				TableauwTDico.remove(mot);
			}
		}


		this.setDicoText(TableauDico);
		this.setNombreMotTotal(NbMots);
		this.setDicoEtOccurenceSimple(word4);
		this.setTextWordTag(TableauwTDico);
		//this.setDicoEtOccurence(word3);
	}

	/**
	 * fonction servant a recuperer le dictionnaire  grace à un ou pllusieurs fichiers textes
	 * @param nameFileTexts
	 * @param Thesaurus
	 * @return TextWordtag - 
	 */
	private  ArrayList<TaggedWord> RecuperationTextDicoWithThesaurus( ArrayList<String> nameFileTexts, ArrayList<TaggedWord> Thesaurus){

		PixTaggerFrench tagger = this.getFrenchtagger();
		ArrayList<String> TableauDico = new ArrayList<String>();
		ArrayList<TaggedWord> TableauwTDico = new ArrayList<TaggedWord>();
		//ArrayList<WordTag> WordTagDico = new ArrayList<WordTag>();
		int NbMots = 0;

		//initialisation à 0 du vecteur d'occurence //si l'on veut les occurences par pages
		/*int NbPages = nameFileTexts.size();
		ArrayList<Integer> VectOccurenceInit = new ArrayList<Integer>();
		for (int i=0;i<NbPages;i++)
		{
			VectOccurenceInit.add(0);
		}
		Hashtable<String,ArrayList<Integer>> word3= new Hashtable<String,ArrayList<Integer>>();
		 */

		Hashtable<TaggedWord,Integer> word4= new Hashtable<TaggedWord,Integer>();
		//Integer index = 0;	//permet de stocker l'index d'unmot dans le dictionnaire
		Iterator<String> SentenceIterator = nameFileTexts.iterator();
		while (SentenceIterator.hasNext())
		{

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

						ArrayList<TaggedWord> taggedWords = tagger.tagsentence(sentencePreTraitement);
						ArrayList<TaggedWord> WordsTagged = new ArrayList<TaggedWord>();


						for (int i =0;i<taggedWords.size();i++)
						{

							TaggedWord mot = taggedWords.get(i);
							//récupération des Good-Word "grossier"
							if ((mot.getTag().equals("N"))|(mot.getTag().equals("A"))|(mot.getTag().equals("V"))) //|(mot.tag().equals("ADV")) on ne garde que les adjectifs, noms, verbes
							{
								//sentencePostTraitement= sentencePostTraitement + Lemme+ " ";
								WordsTagged.add(mot);
							}
							else
							{
								//rien faire
								//sentencePostTraitement= sentencePostTraitement+mot.word()+" ";
							}

						}


						/*fin utilisation lemmatiseur*/

						if (WordsTagged.size()>=3){
							Iterator<TaggedWord> wTiterator =WordsTagged.iterator();
							while (wTiterator.hasNext()){
								TaggedWord wTLemme = wTiterator.next();
								//WordTag wTLemme =MorphologyMervyn.stemStaticMervyn(mot);
								if(this.wTnodes.containsKey(wTLemme)){


									if(!wTLemme.getToken().equals("")&&!TableauwTDico.contains(wTLemme)&&Thesaurus.contains(wTLemme))
									{
										TableauwTDico.add(wTLemme);	//remplissage non rangé du dictionnaire
										TableauDico.add(wTLemme.getLemme());
										word4.put(wTLemme,1);
									}
									else if (!wTLemme.getToken().equals("")&&TableauwTDico.contains(wTLemme)&&Thesaurus.contains(wTLemme))
									{
										/* cas où l'on veut les occurences par pages
										ArrayList<Integer> VectOccurenceWord = word3.get(mot);
										Integer occurence = VectOccurenceWord.get(NumeroFile);
										occurence++;	//itération des occurences
										VectOccurenceWord.set(NumeroFile, occurence);
										word3.put(mot,VectOccurenceWord);*/
										Integer occurence = word4.get(wTLemme);
										occurence++;	//itération des occurences
										word4.put(wTLemme,occurence);
									}
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
		//Classification en ordre croissant des elts du dictionnaire
		Collections.sort(TableauDico);

		//introduction d une notion d'occurence
		int occur = 1;

		Set<TaggedWord> set1 = word4.keySet();
		Iterator<TaggedWord> wTiterator = set1.iterator();
		while (wTiterator.hasNext()){
			TaggedWord mot = wTiterator.next();
			Integer nbOccurence = word4.get(mot);
			if (nbOccurence<occur){
				TableauwTDico.remove(mot);
			}
		}


		this.setDicoText(TableauDico);
		this.setNombreMotTotal(NbMots);
		this.setDicoEtOccurenceSimple(word4);
		this.setTextWordTag(TableauwTDico);
		//this.setDicoEtOccurence(word3);
		return TableauwTDico;
	}


	/**
	 * fonction servant à enregistrer les noeuds k voisins à un noeud donné (les unités lexicales synonymes à une unité lexicale donnée)
	 */
	private void RemplissageVoisinageNoeudsText(String namefileDico){

		Hashtable<TaggedWord,Integer> Dico = this.getwTnodes();
		//ArrayList<String> dicoText = this.getDicoText();
		ArrayList<TaggedWord> wTdicoText = this.getTextWordTag();
		Hashtable<String,ArrayList<TaggedWord>> DicoWordTag = this.getDicoWordTag();
		Hashtable<TaggedWord,ArrayList<Integer>> DictionaireVoisins = new Hashtable<TaggedWord,ArrayList<Integer>>();	//stockage des numeros (Identifiants) des synonymes  d'une unité lexicales dans un dictionnaire
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

				//test presence sur la ligne indiquant l'unité lexicale
				isChosenLexicalUnit = (fenetre[1].equals("1"));

				//traitement de la ligne
				if (isChosenLexicalUnit==true)
				{
					LexicalUnit=fenetre[0].toLowerCase();	//on recupere le nom de l'etat qui va servir de clef pour le hashtable

				}
				else
				{

					//String motDico ="";
					TaggedWord wTsyn =new TaggedWord();	//sert a stocker le mot present dans le texte et sur la liste des synonymes
					ArrayList<String> ListStringVoisins = new ArrayList<String>();	//Liste de string des voisins

					//Recuperation des choix lexicaux possibles
					ArrayList<TaggedWord> ChoixLexicauxPossible = new ArrayList<TaggedWord>();
					if(DicoWordTag.containsKey(LexicalUnit)){
						 ChoixLexicauxPossible = DicoWordTag.get(LexicalUnit);
					}

					//récupération des voisins sous forme de mot
					for (int i=1;i<fenetre.length;i++){
						ListStringVoisins.add(fenetre[i].toLowerCase());
					}
					//récupération des voisins sous formes de WordTag
					ArrayList<TaggedWord> WordTagVoisins = new ArrayList<TaggedWord>();
					for (int i=0;i<ListStringVoisins.size();i++){
						String vois = ListStringVoisins.get(i);
						
						if(DicoWordTag.containsKey(vois)){
							WordTagVoisins.addAll(DicoWordTag.get(vois));
						}
					}

					//test pour la présence d'un mot voisins dans le texte

					boolean presenceText = false;
					//cas ou l'on l'unite lexicale et ses différents tags est présents dans le texte
					int k=0;
					while((presenceText==false)&&(k<ChoixLexicauxPossible.size()) ){
						/*WordTag mot = ChoixLexicauxPossible.get(k);
						String lemme = MorphologyMervyn.stemStaticMervyn(mot).word();
						 motDico=lemme;
						 presenceText = dicoText.contains(lemme);
						WordTag lemme = MorphologyMervyn.stemStaticMervyn(mot);*/
						TaggedWord lemme = ChoixLexicauxPossible.get(k);
						presenceText = wTdicoText.contains(lemme);
						wTsyn = lemme;
						k++;
					}

					//cas ou  les synonymes et ses différents tags de l'unite lexicale sont présent dans le texte
					k=0;
					while((presenceText==false)&&(k<WordTagVoisins.size()) ){
						//WordTag mot = WordTagVoisins.get(k);
						/*String lemme = MorphologyMervyn.stemStaticMervyn(mot).word();
						 motDico=lemme;
						 presenceText = dicoText.contains(lemme);
						WordTag lemme = MorphologyMervyn.stemStaticMervyn(mot);*/
						TaggedWord lemme = WordTagVoisins.get(k);
						presenceText = wTdicoText.contains(lemme);
						wTsyn.setTag(lemme.getTag());
						wTsyn.setToken(LexicalUnit);
                                                wTsyn.setLemme(LexicalUnit);

						//lemmatisation avant sauvegarde
						//wTsyn = MorphologyMervyn.stemStaticMervyn(wTsyn);
						k++;
					}

					//test

					//ajout des voisins dans la matrice de voisinage
					voisins = new ArrayList<Integer>();	//Liste des voisins
					if(presenceText){
						/*if(wTsyn.toString().equals("abrit/V")){
							
						}
						for (k = 0;k<WordTagVoisins.size();k++){
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
							//gestion du fait que il y a des anciens voisins
							if(DictionaireVoisins.containsKey(wTsyn)){
								
								ArrayList<Integer> voisinsprec = DictionaireVoisins.get(wTsyn);
								for(int z=0;z<voisinsprec.size();z++){
									if(!voisins.contains(voisinsprec.get(z))){
										voisins.add(voisinsprec.get(z));
									}
								}

							}
							//rangement des id de mots en ordre croissant
							//Collections.sort(voisins);

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
		this.setMatriceVoisinage(DictionaireVoisins);
	}


	/**
	 * function for modification of sentences for a good lemmatization
	 * @param Text
	 * @return
	 */
	public static String ModifTextStringForLemmatization(String Text){
		
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



	/*
	 * fonction servant à enregistrer les noeuds k voisins à un noeud donné (les unités lexicales synonymes à une unité lexicale donnée)
	 */
	private void RemplissageVoisinageNoeudsText(){

		Hashtable<TaggedWord,Integer> Nodes = this.getwTnodes();
		Hashtable<Integer,TaggedWord> Dico = this.getwTdico();
		Hashtable<TaggedWord,ArrayList<TaggedWord>> dico_wTLexUnit_wTsyn = this.getDico_wTLexUnit_wTsyn();
		ArrayList<TaggedWord> wTdicoText = this.getTextWordTag();
		Hashtable<TaggedWord,ArrayList<Integer>> DictionaireVoisins = new Hashtable<TaggedWord,ArrayList<Integer>>();	//stockage des numeros (Identifiants) des synonymes  d'une unité lexicales dans un dictionnaire
		int indexMotTexte =0;
		//pour chaque mot du dictionnaire de synonyme
		Iterator<Integer> IteraMotDico = Dico.keySet().iterator();
		while(IteraMotDico.hasNext()&&(indexMotTexte<wTdicoText.size())){
			TaggedWord wTmotDico = Dico.get(IteraMotDico.next());
			Boolean presenceTexte = false;
			/*if(wTmotDico.toString().equals("logiciel/N")){
				
			}*/


			//test de la presence effective du mot du dictionnaire dans le texte analysé
			//en tant que unite lexicale ou synonyme de l'unite lexicale
			if(dico_wTLexUnit_wTsyn.containsKey(wTmotDico)){
				ArrayList<TaggedWord> ListeWtVoisins = dico_wTLexUnit_wTsyn.get(wTmotDico);
				presenceTexte=(wTdicoText.contains(wTmotDico));
				int k =0;
				while(presenceTexte.equals(false)&&(k<ListeWtVoisins.size())){
					TaggedWord voisin = ListeWtVoisins.get(k);
					presenceTexte=wTdicoText.contains(voisin);
					k++;
				}
				if (presenceTexte){
					//indexMotTexte++;
					ArrayList<Integer> ListeIdVoisins = new ArrayList<Integer>();
					//etablissement de la liste des voisins en verifiant leur existence
					for (int i =0;i<ListeWtVoisins.size();i++){
						TaggedWord voisin = ListeWtVoisins.get(i);
						if(Dico.contains(voisin)){
							Integer IdVoisin = Nodes.get(voisin);
							ListeIdVoisins.add(IdVoisin);
						}
					}
					DictionaireVoisins.put(wTmotDico, ListeIdVoisins);
				}
			}
		}
		this.setMatriceVoisinage(DictionaireVoisins);
	}




	/**
	 *fonction servant à enregistrer les noeuds k voisins à un noeud donné (les unités lexicales synonymes à une unité lexicale donnée)
	 * possible car le dico a été nettoyé 
	 */
	private void RemplissageVoisinageNoeudsTextFastPertinence(){

		Hashtable<TaggedWord,Integer> Nodes = this.getwTnodes();
		Hashtable<TaggedWord,ArrayList<TaggedWord>> dico_wTLexUnit_wTsyn = this.getDico_wTLexUnit_wTsyn();
		ArrayList<TaggedWord> wTdicoText = this.getTextWordTag();
		Hashtable<TaggedWord,ArrayList<Integer>> DictionaireVoisins = new Hashtable<TaggedWord,ArrayList<Integer>>();	//stockage des numeros (Identifiants) des synonymes  d'une unité lexicales dans un dictionnaire

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
		this.setMatriceVoisinage(DictionaireVoisins);
	}

	/*
	 * fonction servant à enregistrer les noeuds k voisins à un noeud donné (les unités lexicales synonymes à une unité lexicale donnée)
	 * possible car le dico a été nettoyé
	 */
	private void RemplissageVoisinageNoeudsTextFastPertinenceForte(){

		Hashtable<TaggedWord,Integer> Nodes = this.getwTnodes();
		Hashtable<TaggedWord,ArrayList<TaggedWord>> dico_wTLexUnit_wTsyn = this.getDico_wTLexUnit_wTsyn();
		ArrayList<TaggedWord> wTdicoText = this.getTextWordTag();
		Hashtable<TaggedWord,ArrayList<Integer>> DictionaireVoisins = new Hashtable<TaggedWord,ArrayList<Integer>>();	//stockage des numeros (Identifiants) des synonymes  d'une unité lexicales dans un dictionnaire

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
						//recuperation des id des synonymes
						ListeIdVoisins = new ArrayList<Integer>();

						//ajout du premier element
						ListeIdVoisins.add(Nodes.get(wTmotTexte));
						DictionaireVoisins.put(wTSyn, ListeIdVoisins);	//ajout du synonyme du mot present dans le texte en tant qu'unite lexicale
					}
					else{
						//cas ou le mot est deja present
						//recuperation des id des synonymes
						ListeIdVoisins = DictionaireVoisins.get(wTSyn);

						//etablissement de la liste des voisins en verifiant leur existence
						if(!ListeIdVoisins.contains(Nodes.get(wTmotTexte))){
							ListeIdVoisins.add(Nodes.get(wTmotTexte));
						}
						DictionaireVoisins.put(wTSyn, ListeIdVoisins);	//ajout du synonyme du mot present dans le texte en tant qu'unite lexicale
					}/**/
				}
			}
		}
		this.setMatriceVoisinage(DictionaireVoisins);
	}

	private void RemplissageVoisinageNoeudsTextFast(){

		Hashtable<TaggedWord,Integer> Nodes = this.getwTnodes();
		Hashtable<TaggedWord,ArrayList<TaggedWord>> dico_wTLexUnit_wTsyn = this.getDico_wTLexUnit_wTsyn();
		ArrayList<TaggedWord> wTdicoText = this.getTextWordTag();
		Hashtable<TaggedWord,ArrayList<Integer>> DictionaireVoisins = new Hashtable<TaggedWord,ArrayList<Integer>>();	//stockage des numeros (Identifiants) des synonymes  d'une unité lexicales dans un dictionnaire

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

						//etablissement de la liste des voisins en verifiant leur existence
						for (int k =0;k<ListeWtSynVoisins.size();k++){
							TaggedWord voisin = ListeWtSynVoisins.get(k);
							if(Nodes.containsKey(voisin)){
								Integer IdVoisin = Nodes.get(voisin);
								ListeIdVoisins.add(IdVoisin);

							}
						}
						DictionaireVoisins.put(wTSyn, ListeIdVoisins);	//ajout du synonyme du mot present dans le texte en tant qu'unite lexicale
					}

				}
			}
		}
		this.setMatriceVoisinage(DictionaireVoisins);
	}

	/**
	 * Fonction permettant de retrouver l'intersection de deux champs semantique
	 * @param ch1 - ChampSemantique3
	 * @param ch2 - ChampSemantique3
	 * @return EnsCliquesCommunes - ArrayList<ArrayList<String>>
	 */
	public static ArrayList<ArrayList<String>> IntersectionEnsemblesCliques(ChampSemantique3 ch1,ChampSemantique3 ch2){
		Hashtable<Integer,ArrayList<String>> EnsCliquesStringChmin =ch1.getCliquesString();
		Hashtable<Integer,ArrayList<String>> EnsCliquesStringChmax =ch2.getCliquesString();
		String Text ="";
		//verification que Chmin a le nombre de cliques minimale 
		if(EnsCliquesStringChmin.size()>EnsCliquesStringChmax.size()){
			EnsCliquesStringChmin =ch2.getCliquesString();
			EnsCliquesStringChmax =ch1.getCliquesString();
		}
		
		//récuperation des cliques (sens) communes au deux espaces semantiques
		ArrayList<ArrayList<String>> EnsCliquesCommunes = new ArrayList<ArrayList<String>>();
		
		for (int i=0;i<EnsCliquesStringChmin.size();i++){
			ArrayList<String> Clique = EnsCliquesStringChmin.get(i);
			if(EnsCliquesStringChmax.containsValue(Clique)){
				EnsCliquesCommunes.add(Clique);
				Text = Text+Clique.toString()+"\n";
			}
		}
		
		return EnsCliquesCommunes;
	}
	/**
	 * fonction permettant la récuperation du dictionnaires d'un paragraphe sous format string
	 * @param ch1
	 * @param Tag
	 * @return
	 */
	public static ArrayList<String> RecupDicoparagraphe(ChampSemantique3 ch1,String Tag){
		//récuperation des dictionnaires du paragraphe
				ArrayList<TaggedWord> Dico1 = new ArrayList<TaggedWord>(ch1.getTextWordTag());
				
				//récuperation des dictionnaires des deux paragraphes sous format string
				ArrayList<String> Dico1word = new ArrayList<String>();
				for(int i=0;i<Dico1.size();i++){
					if(Dico1.get(i).getTagShortFomre().equals(Tag)){
					Dico1word.add(Dico1.get(i).getLemme());
					}
				}
				
				return Dico1word;
	}
	public static ArrayList<String> RéalisationDicoMotsCliques(ArrayList<ArrayList<String>> EnsCliquesCommunes){
		//réalisation du dictionnaire de mots dans les cliques communes
				ArrayList<String> DicoCliques = new ArrayList<String>();
				for (int i=0;i<EnsCliquesCommunes.size();i++){
					ArrayList<String> CliqueCommune = new ArrayList<String>(EnsCliquesCommunes.get(i));
					DicoCliques.addAll(CliqueCommune);
				}
				Collections.sort(DicoCliques);
				ArrayList<String> newDicoCliques = new ArrayList<String>();
				for(int s=0;s<DicoCliques.size();s++){
					String mot=DicoCliques.get(s);

					if(!newDicoCliques.contains(mot)&!mot.equals("")){
						newDicoCliques.add(mot);
					}
				}
				DicoCliques = newDicoCliques;
				return DicoCliques;
	}
	
	
	/**
	 * 
	 * @param ch1 - ChampSemantique3
	 * @param ch2 - ChampSemantique3
	 * @param ListeMotsClefsCommun - ArrayList<String>
	 * @return ArrayList<ArrayList<String>>
	 */
	public static ArrayList<ArrayList<String>> RecupCliquesInter(ChampSemantique3 ch1,ChampSemantique3 ch2, ArrayList<String> ListeMotsClefsCommun){
		Hashtable<Integer,ArrayList<String>> EnsCliquesStringChmin =ch1.getCliquesString();
		Hashtable<Integer,ArrayList<String>> EnsCliquesStringChmax =ch2.getCliquesString();
               
                
		//String Text ="Mots clefs Communs, donc pas pertinent pour la suite : "+ListeMotsClefsCommun.toString()+"\nOn a : "+EnsCliquesStringChmin.size() +" et "+EnsCliquesStringChmax.size()+"\n";
		//verification que Chmin a le nombre de cliques minimale 
		if(EnsCliquesStringChmin.size()>EnsCliquesStringChmax.size()){
			EnsCliquesStringChmin =ch2.getCliquesString();
			EnsCliquesStringChmax =ch1.getCliquesString();
		}
		
		//récuperation des cliques (sens) communes au deux espaces semantiques
		ArrayList<ArrayList<String>> EnsCliquesCommunes = new ArrayList<ArrayList<String>>();
		
		for (int i=0;i<EnsCliquesStringChmin.size();i++){
			ArrayList<String> Clique = EnsCliquesStringChmin.get(i);
			
			//test de la presence d'un mots clefs communs
			boolean presenceMotClef = false;
			
			Iterator<String> iteratorMotClef = ListeMotsClefsCommun.iterator();
			while(iteratorMotClef.hasNext()&!presenceMotClef){
				String MotClef = iteratorMotClef.next();
				presenceMotClef = Clique.contains(MotClef);
			}
			//presenceMotClef = false; //ici, on n'a pas besoin de differencier
                        //if(!presenceMotClef){
				if(EnsCliquesStringChmax.containsValue(Clique)){
					EnsCliquesCommunes.add(Clique);
				//	Text = Text+Clique.toString()+"\n";
				}
			//}
		}
               
		return EnsCliquesCommunes;
	}
	
	
	/**
	 * 
	 * @param ch1 - ChampSemantique3
	 * @param ch2 - ChampSemantique3
	 * @param ListeMotsClefsCommun - ArrayList<String>
	 * @return ArrayList<ArrayList<String>>
	 */
	public static ArrayList<ArrayList<String>> RecupCliquesUnion(ChampSemantique3 ch1,ChampSemantique3 ch2, ArrayList<String> ListeMotsClefsCommun){
		Hashtable<Integer,ArrayList<String>> EnsCliquesStringChmin =ch1.getCliquesString();
		Hashtable<Integer,ArrayList<String>> EnsCliquesStringChmax =ch2.getCliquesString();
                
               
                
                
                
                
                
                
                
                
                
		//String Text ="Mots clefs Communs, donc pas pertinent pour la suite : "+ListeMotsClefsCommun.toString()+"\nOn a : "+EnsCliquesStringChmin.size() +" et "+EnsCliquesStringChmax.size()+"\n";
		//verification que Chmin a le nombre de cliques minimale 
		if(EnsCliquesStringChmin.size()>EnsCliquesStringChmax.size()){
			EnsCliquesStringChmin =ch2.getCliquesString();
			EnsCliquesStringChmax =ch1.getCliquesString();
		}
		
		//récuperation des cliques (sens) unions au deux espaces semantiques
		ArrayList<ArrayList<String>> EnsCliquesunions = new ArrayList<ArrayList<String>>();
		//int cmp=0;
		for (int i=0;i<EnsCliquesStringChmin.size();i++){
			ArrayList<String> Clique = EnsCliquesStringChmin.get(i);
			//test de la presence d'un mots clefs communs
			boolean presenceMotClef = false;
			Iterator<String> iteratorMotClef = ListeMotsClefsCommun.iterator();
			while(iteratorMotClef.hasNext()&!presenceMotClef){
				String MotClef = iteratorMotClef.next();
				presenceMotClef = Clique.contains(MotClef);
			}
			//if(!presenceMotClef){
					EnsCliquesunions.add(Clique);
//					Text = Text+Clique.toString()+"\n";
			//}
                       // else{
                            //cmp++;
                         
                        //}
		}
		
		for (int i=0;i<EnsCliquesStringChmax.size();i++){
			ArrayList<String> Clique = EnsCliquesStringChmax.get(i);
			//test de la presence d'un mots clefs communs
			boolean presenceMotClef = false;
			Iterator<String> iteratorMotClef = ListeMotsClefsCommun.iterator();
			while(iteratorMotClef.hasNext()&!presenceMotClef){
				String MotClef = iteratorMotClef.next();
				presenceMotClef = Clique.contains(MotClef);
			}
			if(!presenceMotClef&!EnsCliquesunions.contains(Clique)){
				
					EnsCliquesunions.add(Clique);
//					Text = Text+Clique.toString()+"\n";
			}
		}
		return EnsCliquesunions;
	}
	
	/**
	 * Fonction permettant de retrouver l'intersection de deux champs semantique
	 * @param ch1 - ChampSemantique3
	 * @param ch2 - ChampSemantique3
	 * @param ListeMotsClefsCommun - ArrayList<String>
	 * @return EnsCliquesCommunes - ArrayList<ArrayList<String>>
	 */
	public static ArrayList<ArrayList<String>> IntersectionEnsemblesCliquesReduit(ChampSemantique3 ch1,ChampSemantique3 ch2, ArrayList<String> ListeMotsClefsCommun,String Tag){
		Hashtable<Integer,ArrayList<String>> EnsCliquesStringChmin =ch1.getCliquesString();
		Hashtable<Integer,ArrayList<String>> EnsCliquesStringChmax =ch2.getCliquesString();
		String Text ="Mots clefs Communs, donc pas pertinent pour la suite : "+ListeMotsClefsCommun.toString()+"\nOn a : "+EnsCliquesStringChmin.size() +" et "+EnsCliquesStringChmax.size()+"\n";
		//verification que Chmin a le nombre de cliques minimale 
		if(EnsCliquesStringChmin.size()>EnsCliquesStringChmax.size()){
			EnsCliquesStringChmin =ch2.getCliquesString();
			EnsCliquesStringChmax =ch1.getCliquesString();
		}
		
		//récuperation des cliques (sens) communes au deux espaces semantiques
		ArrayList<ArrayList<String>> EnsCliquesCommunes = new ArrayList<ArrayList<String>>();
		
		for (int i=0;i<EnsCliquesStringChmin.size();i++){
			ArrayList<String> Clique = EnsCliquesStringChmin.get(i);
			
			//test de la presence d'un mots clefs communs
			boolean presenceMotClef = false;
			Iterator<String> iteratorMotClef = ListeMotsClefsCommun.iterator();
			while(iteratorMotClef.hasNext()&!presenceMotClef){
				String MotClef = iteratorMotClef.next();
				presenceMotClef = Clique.contains(MotClef);
			}
			if(!presenceMotClef){
				if(EnsCliquesStringChmax.containsValue(Clique)){
					EnsCliquesCommunes.add(Clique);
					Text = Text+Clique.toString()+"\n";
				}
			}
		}
		
		Text ="notions semantiques commun aux deux textes \n";
		
		//récuperation des dictionnaires des deux paragraphes sous format string
				
		//réalisation du dictionnaire de mots dans les cliques communes
		ArrayList<String> DicoCliquescommunes = new ArrayList<String>();
		DicoCliquescommunes = RéalisationDicoMotsCliques(EnsCliquesCommunes);
		ArrayList<String> newDicoCliquescommunes = new ArrayList<String>(DicoCliquescommunes);
		
		//nettoyage des cliques semantiques
		ArrayList<ArrayList<String>> newEnsCliquesCommunes = new ArrayList<ArrayList<String>>();
		
		for (int i=0;i<EnsCliquesCommunes.size();i++){
			ArrayList<String> PrecCliqueCommune = new ArrayList<String>(EnsCliquesCommunes.get(i));
			ArrayList<String> newCliqueCommune = new ArrayList<String>();
			for(int j=0;j<PrecCliqueCommune.size();j++){
				String mot = PrecCliqueCommune.get(j);
				boolean presenceText= newDicoCliquescommunes.contains(mot);
				if(presenceText){
					//newDicoCliquescommunes.remove(mot);
					newCliqueCommune.add(mot);
				}
			}
			if(!newCliqueCommune.isEmpty()&!newEnsCliquesCommunes.contains(newCliqueCommune)){
				newEnsCliquesCommunes.add(newCliqueCommune);
				Text = Text+newCliqueCommune.toString()+"\n";
			}
		}
		EnsCliquesCommunes = newEnsCliquesCommunes;
		
		
		return EnsCliquesCommunes;
	}
	
public static ArrayList<ArrayList<String>> NettoyageCliqSem( ArrayList<ArrayList<String>> EnsCliquesCommunes ,ArrayList<String> newDicoCliquescommunes){
//nettoyage des cliques semantiques
		ArrayList<ArrayList<String>> newEnsCliquesCommunes = new ArrayList<ArrayList<String>>();

		for (int i=0;i<EnsCliquesCommunes.size();i++){
			ArrayList<String> PrecCliqueCommune = new ArrayList<String>(EnsCliquesCommunes.get(i));
			ArrayList<String> newCliqueCommune = new ArrayList<String>();
			for(int j=0;j<PrecCliqueCommune.size();j++){
				String mot = PrecCliqueCommune.get(j);
				boolean presenceText= newDicoCliquescommunes.contains(mot);
				if(presenceText){
					//newDicoCliquescommunes.remove(mot);
					newCliqueCommune.add(mot);
				}
			}
			if(!newCliqueCommune.isEmpty()&!newEnsCliquesCommunes.contains(newCliqueCommune)){
				newEnsCliquesCommunes.add(newCliqueCommune);
			}
		}
		
		return newEnsCliquesCommunes;
}

public static ArrayList<String> NettoyageDicoEnsCliques(ArrayList<String> DicoCliquescommunes,ArrayList<String> Dico1word,ArrayList<String> Dico2word){
	ArrayList<String> newDicoCliquescommunes = new ArrayList<String>(DicoCliquescommunes);
	//nettoyage du dictionnaire afin de ne garder que les mots present dans le texte;
		for(int i=0;i<DicoCliquescommunes.size();i++){
			String motClique = DicoCliquescommunes.get(i);
			boolean presenceText =  Dico1word.contains(motClique)|Dico2word.contains(motClique);
			if(!presenceText){
				newDicoCliquescommunes.remove(motClique);
			}

		}
		return newDicoCliquescommunes;
}
	/**
	 * Fonction permettant de retrouver l'intersection de deux champs semantique
	 * @param ch1 - ChampSemantique3
	 * @param ch2 - ChampSemantique3
	 * @param ListeMotsClefsCommun - ArrayList<String>
	 * @param Tag - String
	 * @return EnsCliquesCommunes - ArrayList<ArrayList<String>>
	 */
	public static ArrayList<ArrayList<String>> IntersectionEtUnionEnsemblesCliquesReduit(ChampSemantique3 ch1,ChampSemantique3 ch2, ArrayList<String> ListeMotsClefsCommun, String Tag){
		
		//récuperation des cliques (sens) communes au deux espaces semantiques
		ArrayList<ArrayList<String>> EnsCliquesCommunes = RecupCliquesInter( ch1, ch2,  ListeMotsClefsCommun);
		
		
		
		//String Text ="Mots clefs Communs, donc pas pertinent pour la suite : "+ListeMotsClefsCommun.toString()+"\n notions semantiques present dans l'union des 2 textes \n";
		//récuperation des dictionnaires des deux paragraphes sous format string
				ArrayList<String> Dico1word = new ArrayList<String>(RecupDicoparagraphe(ch1,Tag));
				ArrayList<String> Dico2word = new ArrayList<String>(RecupDicoparagraphe(ch2,Tag));
				
		
				//réalisation du dictionnaire de mots dans les cliques communes
				ArrayList<String> DicoCliquescommunes = new ArrayList<String>();
				DicoCliquescommunes = RéalisationDicoMotsCliques(EnsCliquesCommunes);
				
				//nettoyage du dictionnaire afin de ne garder que les mots present dans le texte;
				ArrayList<String> newDicoCliquescommunes = NettoyageDicoEnsCliques( DicoCliquescommunes,Dico1word, Dico2word);
		
		DicoCliquescommunes = newDicoCliquescommunes;
		newDicoCliquescommunes = new ArrayList<String>(DicoCliquescommunes);
		
		//nettoyage des cliques semantiques
		ArrayList<ArrayList<String>> newEnsCliquesCommunes = NettoyageCliqSem(  EnsCliquesCommunes , newDicoCliquescommunes);
		EnsCliquesCommunes = newEnsCliquesCommunes;
		
		
		
		//récuperation des cliques (sens) union au deux espaces semantiques
				ArrayList<ArrayList<String>> EnsCliquesunion = RecupCliquesUnion( ch1, ch2,  ListeMotsClefsCommun);
				
				
				
				//String Text ="Mots clefs Communs, donc pas pertinent pour la suite : "+ListeMotsClefsCommun.toString()+"\n notions semantiques present dans l'union des 2 textes \n";
				//récuperation des dictionnaires des deux paragraphes sous format string
						 Dico1word = new ArrayList<String>(RecupDicoparagraphe(ch1,Tag));
						Dico2word = new ArrayList<String>(RecupDicoparagraphe(ch2,Tag));
						
				
						//réalisation du dictionnaire de mots dans les cliques union
						ArrayList<String> DicoCliquesunion = new ArrayList<String>();
						DicoCliquesunion = RéalisationDicoMotsCliques(EnsCliquesunion);
						
						//nettoyage du dictionnaire afin de ne garder que les mots present dans le texte;
						ArrayList<String> newDicoCliquesunion = NettoyageDicoEnsCliques( DicoCliquesunion,Dico1word, Dico2word);
				
				DicoCliquesunion = newDicoCliquesunion;
				newDicoCliquesunion = new ArrayList<String>(DicoCliquesunion);
				
				//nettoyage des cliques semantiques
				ArrayList<ArrayList<String>> newEnsCliquesunion = NettoyageCliqSem(  EnsCliquesunion , newDicoCliquesunion);
				EnsCliquesunion = newEnsCliquesunion;
				
		return EnsCliquesCommunes;
	}

	/**
	 * Fonction permettant de retrouver l'intersection de deux champs semantique
	 * @param ch1 - ChampSemantique3
	 * @param ch2 - ChampSemantique3
	 * @param //ListeMotsClefsCommun - ArrayList<WordTag>
	 * @param Tag - String
	 * @return rapport de contexte - Double[]
	 */
	public static Double[] IntersectionEtUnionEnsemblesCliquesReduitWt(ChampSemantique3 ch1,ChampSemantique3 ch2, ArrayList<TaggedWord> WtListeMotsClefsCommun, String Tag){
		
		//recup sous format string
		 ArrayList<String> ListeMotsClefsCommun = new ArrayList<String>();
		 for (int i=0;i<WtListeMotsClefsCommun.size();i++){
			 String mot = WtListeMotsClefsCommun.get(i).toString();
			 ListeMotsClefsCommun.add(mot);
		 }
		
		//récuperation des cliques (sens) communes au deux espaces semantiques
		ArrayList<ArrayList<String>> EnsCliquesCommunes = RecupCliquesInter( ch1, ch2,  ListeMotsClefsCommun);

		

		//String Text ="Mots clefs Communs, donc pas pertinent pour la suite : "+ListeMotsClefsCommun.toString()+"\n notions semantiques present dans l'union des 2 textes \n";
		//récuperation des dictionnaires des deux paragraphes sous format string
				ArrayList<String> Dico1word = new ArrayList<String>(RecupDicoparagraphe(ch1,Tag));
				ArrayList<String> Dico2word = new ArrayList<String>(RecupDicoparagraphe(ch2,Tag));

		
				//réalisation du dictionnaire de mots dans les cliques communes
				ArrayList<String> DicoCliquescommunes = new ArrayList<String>();
				DicoCliquescommunes = RéalisationDicoMotsCliques(EnsCliquesCommunes);
				        
				//nettoyage du dictionnaire afin de ne garder que les mots present dans le texte;
				ArrayList<String> newDicoCliquescommunes = NettoyageDicoEnsCliques( DicoCliquescommunes,Dico1word, Dico2word);
                                
                                
		DicoCliquescommunes = newDicoCliquescommunes;
		newDicoCliquescommunes = new ArrayList<String>(DicoCliquescommunes);
		
		//nettoyage des cliques semantiques
		ArrayList<ArrayList<String>> newEnsCliquesCommunes = NettoyageCliqSem(  EnsCliquesCommunes , newDicoCliquescommunes);
		EnsCliquesCommunes = newEnsCliquesCommunes;
                
		
		
		//récuperation des cliques (sens) union au deux espaces semantiques
				ArrayList<ArrayList<String>> EnsCliquesunion = RecupCliquesUnion( ch1, ch2,  ListeMotsClefsCommun);

				
				
				//String Text ="Mots clefs Communs, donc pas pertinent pour la suite : "+ListeMotsClefsCommun.toString()+"\n notions semantiques present dans l'union des 2 textes \n";
				//récuperation des dictionnaires des deux paragraphes sous format string
						 Dico1word = new ArrayList<String>(RecupDicoparagraphe(ch1,Tag));
						Dico2word = new ArrayList<String>(RecupDicoparagraphe(ch2,Tag));
						
				
						//réalisation du dictionnaire de mots dans les cliques union
						ArrayList<String> DicoCliquesunion = new ArrayList<String>();
						DicoCliquesunion = RéalisationDicoMotsCliques(EnsCliquesunion);
						
						//nettoyage du dictionnaire afin de ne garder que les mots present dans le texte;
						ArrayList<String> newDicoCliquesunion = NettoyageDicoEnsCliques( DicoCliquesunion,Dico1word, Dico2word);
				
				DicoCliquesunion = newDicoCliquesunion;
				newDicoCliquesunion = new ArrayList<String>(DicoCliquesunion);
				
				//nettoyage des cliques semantiques
				ArrayList<ArrayList<String>> newEnsCliquesunion = NettoyageCliqSem(  EnsCliquesunion , newDicoCliquesunion);
				EnsCliquesunion = newEnsCliquesunion;
				Double[] rapport = {(double)EnsCliquesCommunes.size(),(double)EnsCliquesunion.size()};
		return rapport;
	}

	
	/**
	 * Fonction permettant de retrouver l'intersection de deux champs semantique
	 * @param ch1 - ChampSemantique3
	 * @param ch2 - ChampSemantique3
	 * @param ListeMotsClefsCommun - ArrayList<String>
	 * @return EnsCliquesUnion - ArrayList<ArrayList<String>>
	 */
	public static ArrayList<ArrayList<String>> IntersectionEnsemblesCliquesReduitAutre(ChampSemantique3 ch1,ChampSemantique3 ch2, ArrayList<String> ListeMotsClefsCommun){
		Hashtable<Integer,ArrayList<String>> EnsCliquesStringChmin =ch1.getCliquesString();
		Hashtable<Integer,ArrayList<String>> EnsCliquesStringChmax =ch2.getCliquesString();
		String Text ="Mots clefs Communs, donc pas pertinent pour la suite : "+ListeMotsClefsCommun.toString()+"\nOn a : "+EnsCliquesStringChmin.size() +" et "+EnsCliquesStringChmax.size()+"\n";
		//verification que Chmin a le nombre de cliques minimale 
		if(EnsCliquesStringChmin.size()>EnsCliquesStringChmax.size()){
			EnsCliquesStringChmin =ch2.getCliquesString();
			EnsCliquesStringChmax =ch1.getCliquesString();
		}
		
		//récuperation des cliques (sens) Union au deux espaces semantiques
		ArrayList<ArrayList<String>> EnsCliquesUnion = new ArrayList<ArrayList<String>>();
		
		for (int i=0;i<EnsCliquesStringChmin.size();i++){
			ArrayList<String> Clique = EnsCliquesStringChmin.get(i);
			//test de la presence d'un mots clefs communs
			boolean presenceMotClef = false;
			Iterator<String> iteratorMotClef = ListeMotsClefsCommun.iterator();
			while(iteratorMotClef.hasNext()&!presenceMotClef){
				String MotClef = iteratorMotClef.next();
				presenceMotClef = Clique.contains(MotClef);
			}
			if(!presenceMotClef){
					EnsCliquesUnion.add(Clique);
					Text = Text+Clique.toString()+"\n";
			}
		}
		
		for (int i=0;i<EnsCliquesStringChmax.size();i++){
			ArrayList<String> Clique = EnsCliquesStringChmax.get(i);
			//test de la presence d'un mots clefs communs
			boolean presenceMotClef = false;
			Iterator<String> iteratorMotClef = ListeMotsClefsCommun.iterator();
			while(iteratorMotClef.hasNext()&!presenceMotClef){
				String MotClef = iteratorMotClef.next();
				presenceMotClef = Clique.contains(MotClef);
			}
			if(!presenceMotClef&!EnsCliquesUnion.contains(Clique)){
				
					EnsCliquesUnion.add(Clique);
					Text = Text+Clique.toString()+"\n";
			}
		}

		Text ="Mots clefs Communs, donc pas pertinent pour la suite : "+ListeMotsClefsCommun.toString()+"\n notions semantiques present dans l'union des 2 textes \n";
		//récuperation des dictionnaires des deux paragraphes
		ArrayList<TaggedWord> Dico1 = new ArrayList<TaggedWord>(ch1.getTextWordTag());
		ArrayList<TaggedWord> Dico2 = new ArrayList<TaggedWord>(ch2.getTextWordTag());
		
		//récuperation des dictionnaires des deux paragraphes sous format string
		ArrayList<String> Dico1word = new ArrayList<String>();
                
		for(int i=0;i<Dico1.size();i++){
			if(Dico1.get(i).getTag().equals("N")){
			Dico1word.add(Dico1.get(i).getToken());
			}
		}
               
		ArrayList<String> Dico2word = new ArrayList<String>();
		for(int i=0;i<Dico2.size();i++){
			if(Dico2.get(i).getTag().equals("N")){
				Dico2word.add(Dico2.get(i).getToken());
			}
		}
		
		//réalisation du dictionnaire de mots dans les cliques Union
		ArrayList<String> DicoCliquesUnion = new ArrayList<String>();
		for (int i=0;i<EnsCliquesUnion.size();i++){
			ArrayList<String> CliqueCommune = new ArrayList<String>(EnsCliquesUnion.get(i));
			DicoCliquesUnion.addAll(CliqueCommune);
		}
		Collections.sort(DicoCliquesUnion);
		ArrayList<String> newDicoCliquesUnion = new ArrayList<String>();
		for(int s=0;s<DicoCliquesUnion.size();s++){
			String mot=DicoCliquesUnion.get(s);

			if(!newDicoCliquesUnion.contains(mot)&!mot.equals("")){
				newDicoCliquesUnion.add(mot);
			}
		}
		DicoCliquesUnion = newDicoCliquesUnion;
		newDicoCliquesUnion = new ArrayList<String>(DicoCliquesUnion);
		//nettoyage du dictionnaire afin de ne garder que les mots present dans le texte;
		for(int i=0;i<DicoCliquesUnion.size();i++){
			String motClique = DicoCliquesUnion.get(i);
			boolean presenceText =  Dico1word.contains(motClique)|Dico2word.contains(motClique);
			if(!presenceText){
				newDicoCliquesUnion.remove(motClique);
			}
			else{
				
			}
		}
		
		DicoCliquesUnion = newDicoCliquesUnion;
		newDicoCliquesUnion = new ArrayList<String>(DicoCliquesUnion);
		
		//nettoyage des cliques semantiques
		ArrayList<ArrayList<String>> newEnsCliquesUnion = new ArrayList<ArrayList<String>>();
		
		for (int i=0;i<EnsCliquesUnion.size();i++){
			ArrayList<String> PrecCliqueCommune = new ArrayList<String>(EnsCliquesUnion.get(i));
			ArrayList<String> newCliqueCommune = new ArrayList<String>();
			for(int j=0;j<PrecCliqueCommune.size();j++){
				String mot = PrecCliqueCommune.get(j);
				boolean presenceText= newDicoCliquesUnion.contains(mot);
				if(presenceText){
					//newDicoCliquesUnion.remove(mot);
					newCliqueCommune.add(mot);
				}
			}
			if(!newCliqueCommune.isEmpty()&!newEnsCliquesUnion.contains(newCliqueCommune)){
				newEnsCliquesUnion.add(newCliqueCommune);
				Text = Text+newCliqueCommune.toString()+"\n";
			}
		}
		EnsCliquesUnion = newEnsCliquesUnion;
	
		
		return EnsCliquesUnion;
	}






	/*
	 * Fonction (remanié) servant à trouver les cliques maximales (algo de Bron–Kerbosch)
	 */
	private  void BronKerbosch1_1(ArrayList<Integer> Pnode,ArrayList<Integer> Xnode,ArrayList<Integer> Rclique)	//Hashtable<Integer,ArrayList<Integer>> BronKerbosch1(ArrayList<Integer> ,ArrayList<Integer> Xnode,ArrayList<Integer> Rclique)
	{
		//ArrayList<Integer> RcliqueNew = new ArrayList<Integer>();	//Clique etudié
		//ArrayList<Integer> XnodeNew = new ArrayList<Integer>();	//sommet deja contenu dans une clique maximale
		//ArrayList<Integer> PnodeNew = new ArrayList<Integer>();	//sommet non contenu dans une clique maximale et susceptible d'y appartenir
		//int tailleDico = nodes.get("size")-1;
		int numClique=this.getCliques().size();
		if ((Pnode.isEmpty())&&(Xnode.isEmpty())&&(1<Rclique.size()))
		{
			//Classification en ordre croissant des elts de la clique
			List<Integer> unsortList = new ArrayList<Integer>(Rclique);
			Collections.sort(unsortList);
			ArrayList<String> RcliqueString = new ArrayList<String>();
			Rclique = (ArrayList<Integer>) unsortList;
			Hashtable<Integer,ArrayList<Integer>> CLiquePrec =this.getCliques();
			if(CLiquePrec.contains(Rclique)){
				
			}

			boolean presenceText = false;
			TaggedWord mot = new TaggedWord();
			//test de la presence d'au moins un mot de la clique dans le texte
			Iterator<Integer> IteratorIdWord = Rclique.iterator();
			while(IteratorIdWord.hasNext()&&!presenceText){
				Integer IdWord = IteratorIdWord.next();
				mot = this.getwTdico().get(IdWord);
				presenceText = this.TextWordTag.contains(mot);
			}
			if(presenceText){/**/
				//if(true){
				CLiquePrec.put(numClique, Rclique);
				this.setCliques(CLiquePrec);

				for (int i=0;i<Rclique.size();i++)
				{
					this.TextSortie= this.TextSortie+"\""+this.getwTdico().get(Rclique.get(i)) + "\" ";
					
					RcliqueString.add(this.getwTdico().get(Rclique.get(i)).getToken());
				}
				
				if(RcliqueString.contains("ateur")){
					
				}
				this.TextSortie=this.TextSortie+"\n";
				
				Hashtable<Integer,ArrayList<String>> CLiqueStringPrec =this.getCliquesString();
				CLiqueStringPrec.put(numClique, RcliqueString);
				this.setCliquesString(CLiqueStringPrec);
				numClique++;
			}

		}
		else
		{
			//while(!(Pnode.size()==0))		//for (int i=0;i<Pnode.size();i++)
			Collections.sort(Pnode);
			for (int i=0;i<Pnode.size();i++)
			{
				//int i = 0;
				Integer numSommet = Pnode.get(i);

				TaggedWord sommet=this.getwTdico().get(numSommet);


				//soustraction
				Pnode.remove(Pnode.indexOf(numSommet));	//soustraction d'un sommet

				//Union ds la clique
				ArrayList<Integer> RcliqueNew=new ArrayList<Integer>();
				for (int m=0;m<Rclique.size();m++)
				{
					RcliqueNew.add(Rclique.get(m));
				}

				if (!Rclique.contains(numSommet))
				{
					RcliqueNew.add(numSommet);		//ajout dans la clique
				}

				//recuperation des voisins du sommet courant
				ArrayList<Integer> voisins = new ArrayList<Integer>(this.getMatriceVoisinage().get(sommet));
				Collections.sort(voisins);

				//test sur le voisinage (intersections)
				/*if (!(voisins==null))
				{*/
				//voisinage sur P
				ArrayList<Integer> PnodeNew = new ArrayList<Integer>();	//sommet non traité pour une clique maximale et susceptible d'y appartenir

				boolean intersectionP = false;
				//voisinage de P
				for(Integer l=0;l<Pnode.size();l++)
				{
					Integer numsommet = Pnode.get(l);
					intersectionP = false;
					for (int k=0;k<voisins.size();k++)
					{
						Integer numVoisins= voisins.get(k); 	//numéro d'indice du voisin
						if(numsommet.equals(numVoisins))
						{
							intersectionP = true;
							if (!PnodeNew.contains(numsommet))
							{
								PnodeNew.add(numsommet);		//ajout dans la clique
							}
							/*PnodeNew.add(numsommet);*/
						}
					}
					if (intersectionP==false)
					{
						//PnodeNew.add(numsommet);
					}
				}
				//voisinage sur X
				if (Xnode.size()!=0){
					
				}
				ArrayList<Integer> XnodeNew = new ArrayList<Integer>();	//sommet deja traité pour une clique maximale
				boolean intersectionX = false;
				//voisinage de X
				for(Integer l=0;l<Xnode.size();l++)
				{
					Integer numsommet = Xnode.get(l);
					intersectionX = false;
					for (int k=0;k<voisins.size();k++)
					{
						Integer numVoisins= voisins.get(k); 	//numéro d'indice du voisin
						if(numsommet.equals(numVoisins))
						{
							intersectionX = true;
							//XnodeNew.add(numsommet);
							if (!XnodeNew.contains(numsommet))
							{
								XnodeNew.add(numsommet);		//ajout dans la clique
							}
						}
					}
					if (intersectionX==false)
					{
						//XnodeNew.remove(numsommet);
					}
				}
				BronKerbosch1_1(PnodeNew, XnodeNew, RcliqueNew);
				/*if(Pnode.size()==0){
						Xnode.addAll(XnodeNew);
					}*/
				Xnode.add(numSommet);		//ajout dans la clique
				/*}*/
			}
		}
	}

	/*
	 * Fonction (remanié) servant à trouver les cliques maximales (algo de Bron–Kerbosch)
	 */
	private  void BronKerbosch1_1Autre(ArrayList<Integer> Pnode,ArrayList<Integer> Xnode,ArrayList<Integer> Rclique)	//Hashtable<Integer,ArrayList<Integer>> BronKerbosch1(ArrayList<Integer> ,ArrayList<Integer> Xnode,ArrayList<Integer> Rclique)
	{
		//ArrayList<Integer> RcliqueNew = new ArrayList<Integer>();	//Clique etudié
		//ArrayList<Integer> XnodeNew = new ArrayList<Integer>();	//sommet deja contenu dans une clique maximale
		//ArrayList<Integer> PnodeNew = new ArrayList<Integer>();	//sommet non contenu dans une clique maximale et susceptible d'y appartenir
		//int tailleDico = nodes.get("size")-1;
		int numClique=this.getCliques().size();
		if ((Pnode.isEmpty())&&(Xnode.isEmpty()))//&&(1<Rclique.size()))
		{
			//Classification en ordre croissant des elts de la clique
			List<Integer> unsortList = new ArrayList<Integer>(Rclique);
			Collections.sort(unsortList);
			ArrayList<String> RcliqueString = new ArrayList<String>();
			Rclique = (ArrayList<Integer>) unsortList;
			Hashtable<Integer,ArrayList<Integer>> CLiquePrec =this.getCliques();

			boolean presenceText = false;
			TaggedWord mot = new TaggedWord();
			//test de la presence d'au moins un mot de la clique dans le texte
			Iterator<Integer> IteratorIdWord = Rclique.iterator();
			while(IteratorIdWord.hasNext()&&!presenceText){
				Integer IdWord = IteratorIdWord.next();
				mot = this.getwTdico().get(IdWord);
				presenceText = this.TextWordTag.contains(mot);
			}
			if(presenceText){
				/*if(true){*/
				CLiquePrec.put(numClique, Rclique);
				this.setCliques(CLiquePrec);

				for (int i=0;i<Rclique.size();i++)
				{
					this.TextSortie= this.TextSortie+"\""+this.getwTdico().get(Rclique.get(i)) + "\" ";
					
					RcliqueString.add(this.getwTdico().get(Rclique.get(i)).getToken());
				}
				
				if(RcliqueString.contains("ateur")){
					
				}
				this.TextSortie=this.TextSortie+"\n";
				
				Hashtable<Integer,ArrayList<String>> CLiqueStringPrec =this.getCliquesString();
				CLiqueStringPrec.put(numClique, RcliqueString);
				this.setCliquesString(CLiqueStringPrec);
				numClique++;
			}

		}
		else
		{
			//while(!(Pnode.size()==0))		//for (int i=0;i<Pnode.size();i++)
			Collections.sort(Pnode);
			for (int i=0;i<Pnode.size();i++)
			{
				//int i = 0;
				Integer numSommet = Pnode.get(i);

				TaggedWord sommet=this.getwTdico().get(numSommet);

				//soustraction
				Pnode.remove(Pnode.indexOf(numSommet));	//soustraction d'un sommet


				//Union ds la clique
				ArrayList<Integer> RcliqueNew=new ArrayList<Integer>();
				for (int m=0;m<Rclique.size();m++)
				{
					RcliqueNew.add(Rclique.get(m));
				}

				if (!Rclique.contains(numSommet))
				{
					RcliqueNew.add(numSommet);		//ajout dans la clique
				}

				//recuperation des voisins du sommet courant
				ArrayList<Integer> voisins = new ArrayList<Integer>(this.getMatriceVoisinage().get(sommet));
				//voisins.removeAll(Rclique);
				Collections.sort(voisins);

				//test sur le voisinage (intersections)
				/*if (!(voisins==null))
				{*/
				//voisinage sur P
				ArrayList<Integer> PnodeNew = new ArrayList<Integer>();	//sommet non traité pour une clique maximale et susceptible d'y appartenir


				//voisinage de P
				for(Integer l=0;l<Pnode.size();l++)
				{
					Integer numsommet = Pnode.get(l);

					for (int k=0;k<voisins.size();k++)
					{
						Integer numVoisins= voisins.get(k); 	//numéro d'indice du voisin
						if(numsommet.equals(numVoisins))
						{

							if (!PnodeNew.contains(numsommet))
							{
								PnodeNew.add(numsommet);		//ajout dans la clique
							}
						}
					}
				}
				//voisinage sur X
				if (Xnode.size()!=0){
					
				}
				ArrayList<Integer> XnodeNew = new ArrayList<Integer>();	//sommet deja traité pour une clique maximale
				//voisinage de X
				for(Integer l=0;l<Xnode.size();l++)
				{
					Integer numsommet = Xnode.get(l);

					for (int k=0;k<voisins.size();k++)
					{
						Integer numVoisins= voisins.get(k); 	//numéro d'indice du voisin
						if(numsommet.equals(numVoisins))
						{

							//XnodeNew.add(numsommet);
							if (!XnodeNew.contains(numsommet))
							{
								XnodeNew.add(numsommet);		//ajout dans la clique
							}
						}
					}
				}
				BronKerbosch1_1Autre(PnodeNew, XnodeNew, RcliqueNew);
				Xnode.add(numSommet);		//ajout dans la clique

			}
		}
	}

	/*
	 * Fonction (remanié) servant à trouver les cliques maximales (algo de Bron–Kerbosch)
	 */
	private  void BronKerbosch1_2(ArrayList<Integer> Pnode,ArrayList<Integer> Xnode,ArrayList<Integer> Rclique)	//Hashtable<Integer,ArrayList<Integer>> BronKerbosch1(ArrayList<Integer> ,ArrayList<Integer> Xnode,ArrayList<Integer> Rclique)
	{
		int numClique=this.getCliques().size();
		if ((Pnode.isEmpty())&&(Xnode.isEmpty()))//&&(1<Rclique.size()))
		{
			//Classification en ordre croissant des elts de la clique
			List<Integer> unsortList = new ArrayList<Integer>(Rclique);
			Collections.sort(unsortList);
			ArrayList<String> RcliqueString = new ArrayList<String>();
			Rclique = (ArrayList<Integer>) unsortList;
			Hashtable<Integer,ArrayList<Integer>> CLiquePrec =this.getCliques();

			boolean presenceThes = false;
			boolean presenceText = false;

			TaggedWord mot = new TaggedWord();

			ArrayList<TaggedWord> WtMots = new ArrayList<TaggedWord>();
			Iterator<Integer> IteratorIdWord = Rclique.iterator();


			//test de la presence d'au moins un mot de la clique dans le texte
			while(IteratorIdWord.hasNext()&&WtMots.size()<2){
				Integer IdWord = IteratorIdWord.next();
				mot = this.getwTdico().get(IdWord);
				presenceText = this.getTextWordTag().contains(mot);
				if(presenceText&&WtMots.size()<1){
					WtMots.add(mot);
				}
				presenceThes = this.getThesaurus().contains(mot);
				if(presenceThes&WtMots.size()==1){
					WtMots.add(mot);
				}
			}/**/
			//test de la presence d'au moins 2 mot de la clique dans le thesaurus
			if(!(WtMots.size()==2)){
				presenceThes = false;
				mot = new TaggedWord();

				WtMots = new ArrayList<TaggedWord>();
				IteratorIdWord = Rclique.iterator();
				while(IteratorIdWord.hasNext()&&WtMots.size()<2){
					Integer IdWord = IteratorIdWord.next();
					mot = this.getwTdico().get(IdWord);
					presenceThes = this.getThesaurus().contains(mot);
					if(presenceThes){
						WtMots.add(mot);
					}
				}
			}
			if(WtMots.size()==2){
				/*if(true){*/
				CLiquePrec.put(numClique, Rclique);
				this.setCliques(CLiquePrec);

				this.TextSortie= this.TextSortie+"Mots presents: "+WtMots.toString()+" ";
				
				for (int i=0;i<Rclique.size();i++)
				{
					this.TextSortie= this.TextSortie+"\""+this.getwTdico().get(Rclique.get(i)) + "\" ";
					
					RcliqueString.add(this.getwTdico().get(Rclique.get(i)).getToken());
				}
				
				this.TextSortie=this.TextSortie+"\n";
				
				Hashtable<Integer,ArrayList<String>> CLiqueStringPrec =this.getCliquesString();
				CLiqueStringPrec.put(numClique, RcliqueString);
				this.setCliquesString(CLiqueStringPrec);
				numClique++;
			}

		}
		else
		{
			Collections.sort(Pnode);
			for (int i=0;i<Pnode.size();i++)
			{
				//int i = 0;
				Integer numSommet = Pnode.get(i);
				TaggedWord sommet=this.getwTdico().get(numSommet);

				//soustraction
				Pnode.remove(Pnode.indexOf(numSommet));	//soustraction d'un sommet

				//Union ds la clique
				ArrayList<Integer> RcliqueNew=new ArrayList<Integer>();
				for (int m=0;m<Rclique.size();m++)
				{
					RcliqueNew.add(Rclique.get(m));
				}

				if (!Rclique.contains(numSommet))
				{
					RcliqueNew.add(numSommet);		//ajout dans la clique
				}

				//recuperation des voisins du sommet courant
				ArrayList<Integer> voisins = new ArrayList<Integer>(this.getMatriceVoisinage().get(sommet));
				//voisins.removeAll(Rclique);
				Collections.sort(voisins);

				//voisinage sur P
				ArrayList<Integer> PnodeNew = new ArrayList<Integer>();	//sommet non traité pour une clique maximale et susceptible d'y appartenir

				//voisinage de P
				for(Integer l=0;l<Pnode.size();l++)
				{
					Integer numsommet = Pnode.get(l);

					for (int k=0;k<voisins.size();k++)
					{
						Integer numVoisins= voisins.get(k); 	//numéro d'indice du voisin
						if(numsommet.equals(numVoisins))
						{
							if (!PnodeNew.contains(numsommet))
							{
								PnodeNew.add(numsommet);		//ajout dans la clique
							}
						}
					}
				}
				//voisinage sur X
				ArrayList<Integer> XnodeNew = new ArrayList<Integer>();	//sommet deja traité pour une clique maximale

				//voisinage de X
				for(Integer l=0;l<Xnode.size();l++)
				{
					Integer numsommet = Xnode.get(l);

					for (int k=0;k<voisins.size();k++)
					{
						Integer numVoisins= voisins.get(k); 	//numéro d'indice du voisin
						if(numsommet.equals(numVoisins))
						{
							if (!XnodeNew.contains(numsommet))
							{
								XnodeNew.add(numsommet);		//ajout dans la clique
							}
						}
					}
				}
				BronKerbosch1_2(PnodeNew, XnodeNew, RcliqueNew);
				Xnode.add(numSommet);		//ajout dans la clique
			}
		}
	}


	/**
	 * Fonction (remanié) servant à trouver les cliques maximales (algo de Bron–Kerbosch)
	 * @param Pnode
	 * @param Xnode
	 * @param Rclique
	 * @param //Pertinence
	 */
	private  void BronKerbosch1_2Pert(ArrayList<Integer> Pnode,ArrayList<Integer> Xnode,ArrayList<Integer> Rclique,int nb)	//Hashtable<Integer,ArrayList<Integer>> BronKerbosch1(ArrayList<Integer> ,ArrayList<Integer> Xnode,ArrayList<Integer> Rclique)
	{
		int numClique=this.getCliques().size();
		if ((Pnode.isEmpty())&&(Xnode.isEmpty()))//&&(1<Rclique.size()))
		{
			//Classification en ordre croissant des elts de la clique
			List<Integer> unsortList = new ArrayList<Integer>(Rclique);
			Collections.sort(unsortList);
			ArrayList<String> RcliqueString = new ArrayList<String>();
			Rclique = (ArrayList<Integer>) unsortList;
			Hashtable<Integer,ArrayList<Integer>> CLiquePrec =this.getCliques();

			boolean presenceText = false;

			TaggedWord mot = new TaggedWord();

			ArrayList<TaggedWord> WtMots = new ArrayList<TaggedWord>();
			Iterator<Integer> IteratorIdWord = Rclique.iterator();

			if(nb<=0){//recuperation de la clique avec que des mots contenus dans le texte ou le thesaurus
				ArrayList<Integer> newRclique = new ArrayList<Integer>();
				IteratorIdWord = Rclique.iterator();
				while(IteratorIdWord.hasNext()){
					Integer IdWord = IteratorIdWord.next();
					mot = this.getwTdico().get(IdWord);
					TaggedWord motThesaurus = this.getThesaurus().get(0);
					presenceText = this.getTextWordTag().contains(mot);//|this.getThesaurus().contains(mot);
					boolean presenceThesaurus = Rclique.contains(this.getwTnodes().get(motThesaurus));
					if(presenceText&presenceThesaurus){
						newRclique.add(IdWord);
					}
				}
				Rclique=newRclique;
				nb=1;
			}
			
			//test de la presence d'au moins nb mots de la clique dans le texte
			IteratorIdWord = Rclique.iterator();
			while(IteratorIdWord.hasNext()&&WtMots.size()<nb){
				Integer IdWord = IteratorIdWord.next();
				mot = this.getwTdico().get(IdWord);
				presenceText = this.getTextWordTag().contains(mot);
				if(presenceText){
					WtMots.add(mot);
				}

			}
			
			if(WtMots.size()==nb){
				/*if(true){*/
				CLiquePrec.put(numClique, Rclique);
				this.setCliques(CLiquePrec);

				this.TextSortie= this.TextSortie+"Mots presents: "+WtMots.toString()+" ";
				
				for (int i=0;i<Rclique.size();i++)
				{
					this.TextSortie= this.TextSortie+"\""+this.getwTdico().get(Rclique.get(i)) + "\" ";
					
					RcliqueString.add(this.getwTdico().get(Rclique.get(i)).getToken());
				}
				this.TextSortie=this.TextSortie+"\n";
				Hashtable<Integer,ArrayList<String>> CLiqueStringPrec =this.getCliquesString();
				CLiqueStringPrec.put(numClique, RcliqueString);
				this.setCliquesString(CLiqueStringPrec);
				numClique++;
			}
			

		}
		else
		{
			Collections.sort(Pnode);
			for (int i=0;i<Pnode.size();i++)
			{
				//int i = 0;
				Integer numSommet = Pnode.get(i);
				TaggedWord sommet=this.getwTdico().get(numSommet);

				//soustraction
				Pnode.remove(Pnode.indexOf(numSommet));	//soustraction d'un sommet

				//Union ds la clique
				ArrayList<Integer> RcliqueNew=new ArrayList<Integer>();
				for (int m=0;m<Rclique.size();m++)
				{
					RcliqueNew.add(Rclique.get(m));
				}

				if (!Rclique.contains(numSommet))
				{
					RcliqueNew.add(numSommet);		//ajout dans la clique
				}

				//recuperation des voisins du sommet courant
				ArrayList<Integer> voisins = new ArrayList<Integer>(this.getMatriceVoisinage().get(sommet));
				//voisins.removeAll(Rclique);
				Collections.sort(voisins);

				//voisinage sur P
				ArrayList<Integer> PnodeNew = new ArrayList<Integer>();	//sommet non traité pour une clique maximale et susceptible d'y appartenir

				//voisinage de P
				for(Integer l=0;l<Pnode.size();l++)
				{
					Integer numsommet = Pnode.get(l);

					for (int k=0;k<voisins.size();k++)
					{
						Integer numVoisins= voisins.get(k); 	//numéro d'indice du voisin
						if(numsommet.equals(numVoisins))
						{
							if (!PnodeNew.contains(numsommet))
							{
								PnodeNew.add(numsommet);		//ajout dans la clique
							}
						}
					}
				}
				//voisinage sur X
				ArrayList<Integer> XnodeNew = new ArrayList<Integer>();	//sommet deja traité pour une clique maximale

				//voisinage de X
				for(Integer l=0;l<Xnode.size();l++)
				{
					Integer numsommet = Xnode.get(l);

					for (int k=0;k<voisins.size();k++)
					{
						Integer numVoisins= voisins.get(k); 	//numéro d'indice du voisin
						if(numsommet.equals(numVoisins))
						{
							if (!XnodeNew.contains(numsommet))
							{
								XnodeNew.add(numsommet);		//ajout dans la clique
							}
						}
					}
				}
				BronKerbosch1_2Pert(PnodeNew, XnodeNew, RcliqueNew,nb);
				Xnode.add(numSommet);		//ajout dans la clique
			}
		}
	}

	/**
	 * Fonction (remanié) servant à trouver les cliques maximales (algo de Bron–Kerbosch)
	 * @param Pnode
	 * @param Xnode
	 * @param Rclique
	 * @param //Pertinence
	 * @param Tag
	 */
	private  void BronKerbosch1_2Pert(ArrayList<Integer> Pnode,ArrayList<Integer> Xnode,ArrayList<Integer> Rclique,int nb,String Tag)	//Hashtable<Integer,ArrayList<Integer>> BronKerbosch1(ArrayList<Integer> ,ArrayList<Integer> Xnode,ArrayList<Integer> Rclique)
	{
		int numClique=this.getCliques().size();

		if ((Pnode.isEmpty())&&(Xnode.isEmpty()))//&&(1<Rclique.size()))
		{

			//Classification en ordre croissant des elts de la clique
			List<Integer> unsortList = new ArrayList<Integer>(Rclique);
			Collections.sort(unsortList);
			ArrayList<String> RcliqueString = new ArrayList<String>();
			Rclique = (ArrayList<Integer>) unsortList;
			Hashtable<Integer,ArrayList<Integer>> CLiquePrec =this.getCliques();
			Hashtable<Integer,ArrayList<String>> CLiqueStringPrec =this.getCliquesString();
				
			boolean presenceText = false;

			TaggedWord mot = new TaggedWord();

			ArrayList<TaggedWord> WtMots = new ArrayList<TaggedWord>();
			Iterator<Integer> IteratorIdWord = Rclique.iterator();


			//test de la presence d'au moins nb mots de la clique dans le texte
			while(IteratorIdWord.hasNext()&&WtMots.size()<nb){
				Integer IdWord = IteratorIdWord.next();
				mot = this.getwTdico().get(IdWord);
				presenceText = this.getTextWordTag().contains(mot);
				if(presenceText){
					WtMots.add(mot);
				}

			}
			if(WtMots.size()==nb){
				/*if(true){*/
				
				this.TextSortie= this.TextSortie+"Mots presents: "+WtMots.toString()+" ";
				
				for (int i=0;i<Rclique.size();i++)
				{
					this.TextSortie= this.TextSortie+"\""+this.getwTdico().get(Rclique.get(i)) + "\" ";
					
					RcliqueString.add(this.getwTdico().get(Rclique.get(i)).getToken());
				}
				this.TextSortie=this.TextSortie+"\n";
				String tagClique = this.getwTdico().get(Rclique.get(0)).getTagShortFomre();
                               
				if(tagClique.equals(Tag)){
					CLiquePrec.put(numClique, Rclique);
					this.setCliques(CLiquePrec);
					CLiqueStringPrec.put(numClique, RcliqueString);
					this.setCliquesString(CLiqueStringPrec);
					numClique++;
				}
			}

		}
		else
		{
			Collections.sort(Pnode);
			for (int i=0;i<Pnode.size();i++)
			{
				//int i = 0;
				Integer numSommet = Pnode.get(i);
				TaggedWord sommet=this.getwTdico().get(numSommet);

				//soustraction
				Pnode.remove(Pnode.indexOf(numSommet));	//soustraction d'un sommet

				//Union ds la clique
				ArrayList<Integer> RcliqueNew=new ArrayList<Integer>();
				for (int m=0;m<Rclique.size();m++)
				{
					RcliqueNew.add(Rclique.get(m));
				}

				if (!Rclique.contains(numSommet))
				{
					RcliqueNew.add(numSommet);		//ajout dans la clique
				}

				//recuperation des voisins du sommet courant
 
				ArrayList<Integer> voisins = new ArrayList<Integer>(this.getMatriceVoisinage().get(sommet));
				//voisins.removeAll(Rclique);
				Collections.sort(voisins);

				//voisinage sur P
				ArrayList<Integer> PnodeNew = new ArrayList<Integer>();	//sommet non traité pour une clique maximale et susceptible d'y appartenir

				//voisinage de P
				for(Integer l=0;l<Pnode.size();l++)
				{
					Integer numsommet = Pnode.get(l);

					for (int k=0;k<voisins.size();k++)
					{
						Integer numVoisins= voisins.get(k); 	//numéro d'indice du voisin
						if(numsommet.equals(numVoisins))
						{
							if (!PnodeNew.contains(numsommet))
							{
								PnodeNew.add(numsommet);		//ajout dans la clique
							}
						}
					}
				}
				//voisinage sur X
				ArrayList<Integer> XnodeNew = new ArrayList<Integer>();	//sommet deja traité pour une clique maximale

				//voisinage de X
				for(Integer l=0;l<Xnode.size();l++)
				{
					Integer numsommet = Xnode.get(l);

					for (int k=0;k<voisins.size();k++)
					{
						Integer numVoisins= voisins.get(k); 	//numéro d'indice du voisin
						if(numsommet.equals(numVoisins))
						{
							if (!XnodeNew.contains(numsommet))
							{
								XnodeNew.add(numsommet);		//ajout dans la clique
							}
						}
					}
				}
				BronKerbosch1_2Pert(PnodeNew, XnodeNew, RcliqueNew,nb, Tag);
				Xnode.add(numSommet);		//ajout dans la clique
			}
		}
	}

	/*
	 * Fonction (remanié) servant à trouver les cliques maximales (algo de Bron–Kerbosch)
	 */
	private  void BronKerbosch1_1Pivot(ArrayList<Integer> Pnode,ArrayList<Integer> Xnode,ArrayList<Integer> Rclique)	//Hashtable<Integer,ArrayList<Integer>> BronKerbosch1(ArrayList<Integer> ,ArrayList<Integer> Xnode,ArrayList<Integer> Rclique)
	{
		int numClique=this.getCliques().size();
		if ((Pnode.isEmpty())&&(Xnode.isEmpty()))
		{
			//Classification en ordre croissant des elts de la clique
			List<Integer> unsortList = new ArrayList<Integer>(Rclique);
			Collections.sort(unsortList);
			ArrayList<String> RcliqueString = new ArrayList<String>();
			Rclique = (ArrayList<Integer>) unsortList;
			Hashtable<Integer,ArrayList<Integer>> CLiquePrec =this.getCliques();
			CLiquePrec.put(numClique, Rclique);
			this.setCliques(CLiquePrec);

			for (int i=0;i<Rclique.size();i++)
			{
				
				RcliqueString.add(this.getwTdico().get(Rclique.get(i)).getToken());
			}

			Hashtable<Integer,ArrayList<String>> CLiqueStringPrec =this.getCliquesString();
			CLiqueStringPrec.put(numClique, RcliqueString);
			this.setCliquesString(CLiqueStringPrec);
			numClique++;

		}
		else
		{
			Collections.sort(Pnode);
			//choix du pivot

			//Union de P et X
			ArrayList<Integer> PnodeunionX=new ArrayList<Integer>();
			for (int m=0;m<Pnode.size();m++)
			{
				PnodeunionX.add(Pnode.get(m));
			}
			for (int m=0;m<Xnode.size();m++)
			{
				Integer node = Xnode.get(m);
				if (!PnodeunionX.contains(node))
				{
					PnodeunionX.add(node);		//ajout dans la clique
				}
			}
			//Integer Intpivot=Integer.MIN_VALUE;
			TaggedWord Strpivot=new TaggedWord();
			Integer nbVoisinsPivot =Integer.MIN_VALUE;
			//Test sur le pivot
			for (int m=0;m<PnodeunionX.size();m++)
			{
				Integer node =PnodeunionX.get(m);
				TaggedWord sommet=this.getwTdico().get(node);
				//recuperation du nombre de degree
				ArrayList<Integer> voisins = new ArrayList<Integer>(this.getMatriceVoisinage().get(sommet));
				Integer nbVoisins = voisins.size();
				if(nbVoisinsPivot<nbVoisins){
					//Intpivot=node;
					Strpivot=sommet;
				}

			}

			for (int i=0;i<Pnode.size();i++)
			{
				//int i = 0;
				//test sur si le sommet n est pas un voisin du pivot
				//recuperation des voisins du pivot
				ArrayList<Integer> voisinsPivot = new ArrayList<Integer>(this.getMatriceVoisinage().get(Strpivot));
				Collections.sort(voisinsPivot);

				Integer numSommet = Pnode.get(i);

				TaggedWord sommet=this.getwTdico().get(numSommet);

				if(!voisinsPivot.contains(numSommet))
				{
					//soustraction
					Pnode.remove(Pnode.indexOf(numSommet));	//soustraction d'un sommet

					//Union ds la clique
					ArrayList<Integer> RcliqueNew=new ArrayList<Integer>();
					for (int m=0;m<Rclique.size();m++)
					{
						RcliqueNew.add(Rclique.get(m));
					}

					if (!Rclique.contains(numSommet))
					{
						RcliqueNew.add(numSommet);		//ajout dans la clique
					}


					//recuperation des voisins du sommet courant
					ArrayList<Integer> voisins = new ArrayList<Integer>(this.getMatriceVoisinage().get(sommet));
					//voisins.removeAll(Rclique);
					Collections.sort(voisins);

					//voisinage sur P
					ArrayList<Integer> PnodeNew = new ArrayList<Integer>();	//sommet non traité pour une clique maximale et susceptible d'y appartenir

					//voisinage de P
					for(Integer l=0;l<Pnode.size();l++)
					{
						Integer numsommet = Pnode.get(l);

						for (int k=0;k<voisins.size();k++)
						{
							Integer numVoisins= voisins.get(k); 	//numéro d'indice du voisin
							if(numsommet.equals(numVoisins))
							{
								if (!PnodeNew.contains(numsommet))
								{
									PnodeNew.add(numsommet);		//ajout dans la clique
								}
							}
						}
					}
					//voisinage sur X
					ArrayList<Integer> XnodeNew = new ArrayList<Integer>();	//sommet deja traité pour une clique maximale

					//voisinage de X
					for(Integer l=0;l<Xnode.size();l++)
					{
						Integer numsommet = Xnode.get(l);

						for (int k=0;k<voisins.size();k++)
						{
							Integer numVoisins= voisins.get(k); 	//numéro d'indice du voisin
							if(numsommet.equals(numVoisins))
							{
								if (!XnodeNew.contains(numsommet))
								{
									XnodeNew.add(numsommet);		//ajout dans la clique
								}
							}
						}
					}
					BronKerbosch1_1Pivot(PnodeNew, XnodeNew, RcliqueNew);
					Xnode.add(numSommet);		//ajout dans la clique
				}
			}
		}
	}


	/*
	 * Fonction servant à indiquer la présence du Ieme contextonyme dans la Jieme Clique
	 */
	private static Double xIJ (String Contextonyme, ArrayList<String> CliquesString )
	{
		if (CliquesString.contains(Contextonyme))
		{
			return (1.0);
		}else
		{
			return (0.0);
		}
	}

	/*
	 * Fonction servant à calculer xKpoint qui correspond au nombre de contextonyme présente dans la clique K
	 */
	private static Double xKpoint (Hashtable<Integer,String>DicoContextonyme, ArrayList<String> CliquesStringK )
	{
		Double somme= 0.0;
		for (int i=0;i<DicoContextonyme.size();i++){
			String Contextonyme = DicoContextonyme.get(i);
			somme = somme + xIJ(Contextonyme,CliquesStringK);
		}
		return somme;

	}

	/*
	 * Fonction servant à calculer xPointI qui correspond au nombre de clique total contenant le Ieme contextonyme dans la clique K
	 */
	private static Double xPointI (String Contextonyme, Hashtable<Integer,ArrayList<String>> EnsembleCliquesString1 ,Hashtable<Integer,ArrayList<String>> EnsembleCliquesString2 )
	{
		Double somme= 0.0;
		//Calcul dans l'ensemble 1
		for (int i=0;i<EnsembleCliquesString1.size();i++){
			ArrayList<String> CliquesStringK = EnsembleCliquesString1.get(i);
			somme = somme + xIJ(Contextonyme,CliquesStringK);
		}
		if (!EnsembleCliquesString1.equals(EnsembleCliquesString2)){
			//Calcul dans l'ensemble 2 si on a 2 espaces disjoints

			for (int i=0;i<EnsembleCliquesString2.size();i++){
				ArrayList<String> CliquesStringL = EnsembleCliquesString2.get(i);
				somme = somme + xIJ(Contextonyme,CliquesStringL);
			}
		}
		return somme;

	}

	/*
	 * Fonction servant à calculer xPointPoint qui correspond au nombre total de contextonymes contenues dans l'ensemble
	 */
	private static Double xPointPoint (Hashtable<Integer,String>DicoContextonyme, Hashtable<Integer,ArrayList<String>> EnsembleCliquesString1 ,Hashtable<Integer,ArrayList<String>> EnsembleCliquesString2 )
	{
		Double somme= 0.0;
		//Calcul dans l'ensemble 1
		for (int i=0;i<DicoContextonyme.size();i++){
			String Contextonyme = DicoContextonyme.get(i);
			somme = somme + xPointI(Contextonyme,EnsembleCliquesString1,EnsembleCliquesString2);
		}

		return somme;

	}

	/*
	 * Fonction servant à calculer la distance du Khi2 entre 2 cliques
	 */
	public double DistanceKhi2EnsembleCliques (Hashtable<Integer,ArrayList<String>> EnsembleCliquesString1 , Hashtable<Integer,ArrayList<String>> EnsembleCliquesString2 )
	{
		Double SommeDistance = 0.0;
		Set<Integer> set1 = EnsembleCliquesString1.keySet();
		Iterator<Integer> iteratorCliques1 = set1.iterator();
		while (iteratorCliques1.hasNext())
		{
			ArrayList<String> CliquesString1 =EnsembleCliquesString1.get(iteratorCliques1.next());
			Set<Integer> set2 = EnsembleCliquesString2.keySet();
			Iterator<Integer> iteratorCliques2 = set2.iterator();
			while (iteratorCliques2.hasNext())
			{
				ArrayList<String> CliquesString2 =EnsembleCliquesString2.get(iteratorCliques2.next());
				SommeDistance = SommeDistance+ DistanceKhi2Cliques(CliquesString1,EnsembleCliquesString1,CliquesString2,EnsembleCliquesString2);

			}

		}
		return SommeDistance;
	}


	/*
	 * Fonction servant à calculer la distance du Khi2 entre 2 cliques
	 */
	public static double DistanceKhi2Cliques (ArrayList<String> CliquesString1, Hashtable<Integer,ArrayList<String>> EnsembleCliquesString1 ,ArrayList<String> CliquesString2, Hashtable<Integer,ArrayList<String>> EnsembleCliquesString2 )
	{
		Hashtable<Integer,String> DicoContextonyme = new Hashtable<Integer,String>();

		//création du dico de contextonyme

		//creation de l'espace total
		Hashtable<Integer,ArrayList<String>> EnsembleCliquesString = new Hashtable<Integer,ArrayList<String>>(EnsembleCliquesString1);
		for (int j=0 ;j<EnsembleCliquesString2.size();j++){
			ArrayList<String> CliquesString = EnsembleCliquesString2.get(j);
			if (!EnsembleCliquesString.contains(CliquesString)){
				int n =EnsembleCliquesString.size() ;
				EnsembleCliquesString.put(n,CliquesString);
			}
		}
		//recuperation dico
		int index =0;

		Set<Integer> set = EnsembleCliquesString.keySet();
		Iterator<Integer> iteratorCliques = set.iterator();
		while (iteratorCliques.hasNext())
		{
			ArrayList<String> clique =EnsembleCliquesString.get(iteratorCliques.next());
			Iterator<String> cliqueIterator = clique.iterator();
			while (cliqueIterator.hasNext())
			{
				String mot = cliqueIterator.next();
				if (!DicoContextonyme.contains(mot)){
					DicoContextonyme.put(index, mot);
					index++;
				}
			}
		}

		/*for (int i= 0 ;i<EnsembleCliquesString.size();i++){
			DicoContextonyme.put(index, CliquesString1.get(i));
			index ++;
		}
		for (int i= 0 ;index<CliquesString1.size();i++){
			if (DicoContextonyme.containsValue(EnsembleCliquesString2.get(i))){
				DicoContextonyme.put(index, CliquesString1.get(i));
				index ++;
			}

		}*/

		double XPointPoint = xPointPoint(DicoContextonyme,  EnsembleCliquesString1 , EnsembleCliquesString2);
		double X1Point = xKpoint (DicoContextonyme, CliquesString1 );
		double X2Point = xKpoint (DicoContextonyme, CliquesString2 );
		Double somme= 0.0;
		//Calcul Distance métrique Khi2
		for (int i=0;i<DicoContextonyme.size();i++){
			String Contextonyme = DicoContextonyme.get(i);
			double X1i = xIJ (Contextonyme,  CliquesString1 );
			double X2i = xIJ (Contextonyme,  CliquesString2 );
			double XPointI = xPointI ( Contextonyme, EnsembleCliquesString1 ,EnsembleCliquesString2 );
			double A = XPointPoint/XPointI;
			double Aprime = X1i/X1Point;
			double Bprime = X2i/X2Point;
			double B = (Aprime-Bprime)*(Aprime-Bprime);
			somme = somme + A*B;
		}

		//return Math.sqrt(somme);
		return (somme);

	}

	/*
	 * Fonction servant à calculer la distance Normalisée du Khi2 entre 2 cliques
	 */
	public static double DistanceKhi2CliquesNorm (ArrayList<String> CliquesString1, Hashtable<Integer,ArrayList<String>> EnsembleCliquesString1 ,ArrayList<String> CliquesString2, Hashtable<Integer,ArrayList<String>> EnsembleCliquesString2 )
	{
		//amelioration vitesse
		Iterator<String> itera = CliquesString1.iterator();
		boolean NonDisjoint=true;
		while(itera.hasNext()&&NonDisjoint)
		{
			String mot = itera.next();
			NonDisjoint=CliquesString2.contains(mot);
		}
		Double somme=1.0;
		Double denominator= 1.0;
		if(NonDisjoint){	//cas ou il ya un mot commun aux deux cliques
			Hashtable<Integer,String> DicoContextonyme = new Hashtable<Integer,String>();

			//création du dico de contextonyme

			//creation de l'espace total
			Hashtable<Integer,ArrayList<String>> EnsembleCliquesString = new Hashtable<Integer,ArrayList<String>>(EnsembleCliquesString2); //on choisit 2 car c'est celui avec le cardinal le plus grand
			for (int j=0 ;j<EnsembleCliquesString1.size();j++){
				ArrayList<String> CliquesString = EnsembleCliquesString1.get(j);
				if (!EnsembleCliquesString.contains(CliquesString)){
					int n =EnsembleCliquesString.size() ;
					EnsembleCliquesString.put(n,CliquesString);
				}
			}
			//recuperation dico
			int index =0;

			Set<Integer> set = EnsembleCliquesString.keySet();
			Iterator<Integer> iteratorCliques = set.iterator();
			while (iteratorCliques.hasNext())
			{
				ArrayList<String> clique =EnsembleCliquesString.get(iteratorCliques.next());
				Iterator<String> cliqueIterator = clique.iterator();
				while (cliqueIterator.hasNext())
				{
					String mot = cliqueIterator.next();
					if (!DicoContextonyme.contains(mot)){
						DicoContextonyme.put(index, mot);
						index++;
					}
				}
			}
			double XPointPoint = xPointPoint(DicoContextonyme,  EnsembleCliquesString1 , EnsembleCliquesString2);
			double X1Point = xKpoint (DicoContextonyme, CliquesString1 );
			double X2Point = xKpoint (DicoContextonyme, CliquesString2 );
			somme= 0.0;
			denominator= 0.0;

			//Calcul Distance métrique Khi2
			for (int i=0;i<DicoContextonyme.size();i++){
				String Contextonyme = DicoContextonyme.get(i);
				double X1i = xIJ (Contextonyme,  CliquesString1 );
				double X2i = xIJ (Contextonyme,  CliquesString2 );
				double XPointI = xPointI ( Contextonyme, EnsembleCliquesString1 ,EnsembleCliquesString2 );
				double A = XPointPoint/XPointI;
				double Aprime = X1i/X1Point;
				double Bprime = X2i/X2Point;
				double B =  Math.pow((Aprime-Bprime),2);
				double Bdenom = Math.pow(Aprime,2)+Math.pow(Bprime,2);
				somme = somme + A*B;
				//Calcul denominator for normalisation
				denominator = denominator + A*Bdenom;

			}
		}
		return (somme/denominator);

	}


	/*
	 * recuperation Distance entre Ensembles de Cliques
	 */
	public double DistanceKhi2EnsembleCliquesAutre (Hashtable<Integer,ArrayList<String>> EnsembleCliquesString1 , Hashtable<Integer,ArrayList<String>> EnsembleCliquesString2 )
	{
		Double SommeDistance = 0.0;
		Hashtable<Integer,Integer> Table = new Hashtable<Integer,Integer>();

		Hashtable<Integer,ArrayList<String>> EnsembleCliquesString1prime = new Hashtable<Integer,ArrayList<String>>( EnsembleCliquesString1);
		Hashtable<Integer,ArrayList<String>> EnsembleCliquesString2prime = new Hashtable<Integer,ArrayList<String>>( EnsembleCliquesString2);


		for (int index = 0;index<Math.min(EnsembleCliquesString1.size(),EnsembleCliquesString2.size());index++)
		{
			Integer IDClique1=-1;
			Integer IDClique2 =-12;
			double distmini=Integer.MAX_VALUE;
			//Calcul de la distance minimale entre ttes les cliques des 2 pages
			Set<Integer> set1 = EnsembleCliquesString1prime.keySet();
			Iterator<Integer> iteratorCliques1 = set1.iterator();
			while (iteratorCliques1.hasNext())
			{
				Integer IdCliques1 = iteratorCliques1.next();
				ArrayList<String> CliquesString1 =EnsembleCliquesString1prime.get(IdCliques1);
				Set<Integer> set2 = EnsembleCliquesString2prime.keySet();
				Iterator<Integer> iteratorCliques2 = set2.iterator();

				while (iteratorCliques2.hasNext())
				{

					Integer IdCliques2 = iteratorCliques2.next();
					ArrayList<String> CliquesString2 =EnsembleCliquesString2prime.get(IdCliques2);
					double DistanceOrientéCli1_Cli2 = ChampSemantique3.DistanceKhi2Cliques(CliquesString1,EnsembleCliquesString1,CliquesString2,EnsembleCliquesString2);
					if (DistanceOrientéCli1_Cli2<distmini){
						distmini = DistanceOrientéCli1_Cli2;
						IDClique1 =IdCliques1;
						IDClique2 =IdCliques2;
					}
				}

			}
			Table.put(IDClique1, IDClique2);
			//suppression des cliques retenues
			EnsembleCliquesString1prime.remove(IDClique1);
			EnsembleCliquesString2prime.remove(IDClique2);
		}

		Set<Integer> setSomme = Table.keySet();
		Iterator<Integer> iteratorDist = setSomme.iterator();
		while (iteratorDist.hasNext())
		{
			Integer IdCliques1 = iteratorDist.next();
			double DistanceOrientéCli1_Cli2 = ChampSemantique3.DistanceKhi2Cliques(EnsembleCliquesString1.get(IdCliques1), EnsembleCliquesString1, EnsembleCliquesString2.get(Table.get(IdCliques1)) ,EnsembleCliquesString2);
			SommeDistance = SommeDistance+DistanceOrientéCli1_Cli2;
		}

		return SommeDistance;
	}


	public static double DistanceKhi2EnsembleCliquesAutre2 (Hashtable<Integer,ArrayList<String>> EnsembleCliquesString1 , Hashtable<Integer,ArrayList<String>> EnsembleCliquesString2 )
	{
		Double SommeDistance = 0.0;
		Hashtable<String,Double> Table = new Hashtable<String,Double>();

		//tqbleau des distances
		Hashtable<String,Double> TableDist = new Hashtable<String,Double>();

		//Calcul de la distance  entre ttes les cliques des 2 pages
		Set<Integer> set1 = EnsembleCliquesString1.keySet();
		Iterator<Integer> iteratorCliques1 = set1.iterator();
		while (iteratorCliques1.hasNext())
		{
			Integer IdCliques1 = iteratorCliques1.next();
			ArrayList<String> CliquesString1 =EnsembleCliquesString1.get(IdCliques1);
			Set<Integer> set2 = EnsembleCliquesString2.keySet();
			Iterator<Integer> iteratorCliques2 = set2.iterator();

			while (iteratorCliques2.hasNext())
			{

				Integer IdCliques2 = iteratorCliques2.next();
				ArrayList<String> CliquesString2 =EnsembleCliquesString2.get(IdCliques2);
				double DistanceOrientéCli1_Cli2 = ChampSemantique3.DistanceKhi2Cliques(CliquesString1,EnsembleCliquesString1,CliquesString2,EnsembleCliquesString2);
				String identifiant = IdCliques1+"-"+IdCliques2;
				TableDist.put(identifiant, DistanceOrientéCli1_Cli2);
			}

		}

		for (int index = 0;index<Math.min(EnsembleCliquesString1.size(),EnsembleCliquesString2.size());index++)
		{
			Integer IDClique1=-1;
			Integer IDClique2 =-12;
			double distmini=Integer.MAX_VALUE;
			//Calcul de la distance minimale entre ttes les cliques des 2 pages
			Set<String> setTable = TableDist.keySet();
			Iterator<String> iteratorTable = setTable.iterator();
			while (iteratorTable.hasNext())
			{
				String identifiant = iteratorTable.next();
				double DistanceOrientéCli1_Cli2 = TableDist.get(identifiant);

				if (DistanceOrientéCli1_Cli2<distmini){
					distmini = DistanceOrientéCli1_Cli2;
					String[] id = identifiant.split("-");
					IDClique1 =Integer.parseInt(id[0]);
					IDClique2 =Integer.parseInt(id[1]);
				}
			}


			String Identifiant = IDClique1+"-"+IDClique2;
			Table.put(Identifiant, distmini);
			//suppression des cliques retenues
			for(int i=0;i<EnsembleCliquesString2.size();i++){
				Identifiant = IDClique1+"-"+i;
				TableDist.remove(Identifiant);
			}
			for(int i=0;i<EnsembleCliquesString1.size();i++){
				Identifiant = i+"-"+IDClique2;
				TableDist.remove(Identifiant);
			}
		}

		Set<String> setSomme = Table.keySet();
		Iterator<String> iteratorDist = setSomme.iterator();
		while (iteratorDist.hasNext())
		{
			String identifiant = iteratorDist.next();
			double DistanceOrientéCli1_Cli2 = Table.get(identifiant);
			SommeDistance = SommeDistance+DistanceOrientéCli1_Cli2;
		}

		return SommeDistance;
	}

	public static double DistanceKhi2EnsembleCliquesNorm (Hashtable<Integer,ArrayList<String>> EnsembleCliquesString1 , Hashtable<Integer,ArrayList<String>> EnsembleCliquesString2 )
	{
		double result = 0.0;
		//amelioration vitesse
		if(!EnsembleCliquesString1.equals(EnsembleCliquesString2))
		{


			Double SommeDistance = 0.0;
			Hashtable<String,Double> Table = new Hashtable<String,Double>();

			Hashtable<Integer,ArrayList<String>> EnsembleCliquesStringSizemin;
			Hashtable<Integer,ArrayList<String>> EnsembleCliquesStringSizemax;

			//tqbleau des distances
			Hashtable<String,Double> TableDist = new Hashtable<String,Double>();

			//amelioration temps de calcul
			if(EnsembleCliquesString1.size()<=EnsembleCliquesString2.size()){
				EnsembleCliquesStringSizemin =  EnsembleCliquesString1;
				EnsembleCliquesStringSizemax = EnsembleCliquesString2;
			}else{
				EnsembleCliquesStringSizemin =  EnsembleCliquesString2;
				EnsembleCliquesStringSizemax = EnsembleCliquesString1;
			}

			//Calcul de la distance  entre ttes les cliques des 2 pages
			Set<Integer> set1 = EnsembleCliquesStringSizemin.keySet();
			Iterator<Integer> iteratorCliques1 = set1.iterator();
			while (iteratorCliques1.hasNext())
			{
				Integer IdCliques1 = iteratorCliques1.next();
				ArrayList<String> CliquesString1 =EnsembleCliquesStringSizemin.get(IdCliques1);
				Set<Integer> set2 = EnsembleCliquesStringSizemax.keySet();
				Iterator<Integer> iteratorCliques2 = set2.iterator();

				while (iteratorCliques2.hasNext())
				{

					Integer IdCliques2 = iteratorCliques2.next();
					ArrayList<String> CliquesString2 =EnsembleCliquesStringSizemax.get(IdCliques2);
					double DistanceOrientéCli1_Cli2 = ChampSemantique3.DistanceKhi2CliquesNorm(CliquesString1,EnsembleCliquesStringSizemin,CliquesString2,EnsembleCliquesStringSizemax);
					String identifiant = IdCliques1+"-"+IdCliques2;
					TableDist.put(identifiant, DistanceOrientéCli1_Cli2);
				}

			}

			for (int index = 0;index<Math.min(EnsembleCliquesStringSizemin.size(),EnsembleCliquesStringSizemax.size());index++)
			{
				Integer IDClique1=-1;
				Integer IDClique2 =-12;
				double distmini=Integer.MAX_VALUE;
				//Calcul de la distance minimale entre ttes les cliques des 2 pages
				Set<String> setTable = TableDist.keySet();
				Iterator<String> iteratorTable = setTable.iterator();
				while (iteratorTable.hasNext())
				{
					String identifiant = iteratorTable.next();
					double DistanceOrientéCli1_Cli2 = TableDist.get(identifiant);

					if (DistanceOrientéCli1_Cli2<distmini){
						distmini = DistanceOrientéCli1_Cli2;
						String[] id = identifiant.split("-");
						IDClique1 =Integer.parseInt(id[0]);
						IDClique2 =Integer.parseInt(id[1]);
					}
				}


				String Identifiant = IDClique1+"-"+IDClique2;
				Table.put(Identifiant, distmini);
				//suppression des cliques retenues
				for(int i=0;i<EnsembleCliquesStringSizemax.size();i++){
					Identifiant = IDClique1+"-"+i;
					TableDist.remove(Identifiant);
				}
				for(int i=0;i<EnsembleCliquesStringSizemin.size();i++){
					Identifiant = i+"-"+IDClique2;
					TableDist.remove(Identifiant);
				}
			}

			Set<String> setSomme = Table.keySet();
			Iterator<String> iteratorDist = setSomme.iterator();
			while (iteratorDist.hasNext())
			{
				String identifiant = iteratorDist.next();
				double DistanceOrientéCli1_Cli2 = Table.get(identifiant);
				SommeDistance = SommeDistance+DistanceOrientéCli1_Cli2;
			}

			
			result = SommeDistance/Table.size();
		}
		return result;
	}

	public static double DistanceKhi2EnsembleCliquesNormFast (Hashtable<Integer,ArrayList<String>> EnsembleCliquesString1 , Hashtable<Integer,ArrayList<String>> EnsembleCliquesString2 )
	{
		Double SommeDistance = 0.0;
		if(!EnsembleCliquesString1.equals(EnsembleCliquesString2)){
			Hashtable<String,Double> Table = new Hashtable<String,Double>();

			//tableau des distances
			Hashtable<String,Double> TableDist = new Hashtable<String,Double>();

			Hashtable<Integer,String> DicoContextonyme = new Hashtable<Integer,String>();

			//création du dico de contextonyme

			//creation de l'espace total
			//pour la matrice
			//Hashtable<Integer,ArrayList<String>> EnsembleCliquesMat = new Hashtable<Integer,ArrayList<String>>(EnsembleCliquesString1);
			//pour le dico
			Hashtable<Integer,ArrayList<String>> EnsembleCliquesString = new Hashtable<Integer,ArrayList<String>>(EnsembleCliquesString1);

			int TailleEns1 = EnsembleCliquesString1.size();

			for (int j=0 ;j<EnsembleCliquesString2.size();j++){
				ArrayList<String> CliquesString = EnsembleCliquesString2.get(j);
				//int n =EnsembleCliquesMat.size() ;
				//EnsembleCliquesMat.put(n,CliquesString);//pour la matrice
				//EnsembleCliquesMat.put(TailleEns1+j,CliquesString);//pour la matrice
				if (!EnsembleCliquesString.contains(CliquesString)){
					int n =EnsembleCliquesString.size() ;
					EnsembleCliquesString.put(n,CliquesString);
					//EnsembleCliquesMat.put(n,CliquesString);//pour la matrice
					

				}

			}

			//recuperation dico
			int index =0;

			Set<Integer> set = EnsembleCliquesString.keySet();
			Iterator<Integer> iteratorCliques = set.iterator();
			while (iteratorCliques.hasNext())
			{
				ArrayList<String> clique =EnsembleCliquesString.get(iteratorCliques.next());
				Iterator<String> cliqueIterator = clique.iterator();
				while (cliqueIterator.hasNext())
				{
					String mot = cliqueIterator.next();
					if (!DicoContextonyme.contains(mot)){
						DicoContextonyme.put(index, mot);
						index++;
					}
				}
			}

			//construction de la matrice de calcul
			//DoubleMatrix2D matrix1 = new SparseDoubleMatrix2D(DicoContextonyme.size(),EnsembleCliquesString1.size());
			//DoubleMatrix2D matrix2 = new DenseDoubleMatrix2D(DicoContextonyme.size(),EnsembleCliquesString2.size());

			//fast ?
			DoubleMatrix2D matrix = new DenseDoubleMatrix2D(EnsembleCliquesString.size(),DicoContextonyme.size());

			//remplissage matrice de calcul
			//Calcul Distance métrique Khi2

			for (int i=0;i<EnsembleCliquesString.size();i++){
				for(int c=0;c<DicoContextonyme.size();c++){
					String Contextonyme = DicoContextonyme.get(c);
					ArrayList<String> cliqueContextonyme = EnsembleCliquesString.get(i);
					double Xij = xIJ (Contextonyme,  cliqueContextonyme );
					matrix.setQuick(i, c, Xij);

				}
			}

			//Calcul de la distance  entre ttes les cliques des 2 pages
			double denominator = 0.0;
			double somme = 0.0;
			int XPointPoint = matrix.cardinality();
			for (int i = 0; i<TailleEns1;i++){
				String IdCliques1 =""+i;
				String IdCliques2 ="";
				//DoubleMatrix1D tes = matrix.viewRow(i);
				int XIPoint = matrix.viewRow(i).cardinality();
				for (int j = TailleEns1; j<EnsembleCliquesString.size();j++){
					IdCliques2 =""+j;
					int XJPoint = matrix.viewColumn(j).cardinality();
					for(int k = 0;k<DicoContextonyme.size();k++){
						int  XPointK= matrix.viewColumn(k).cardinality();

						double Xik = matrix.get(i, k);
						double Xjk = matrix.get(j, k);
						double A = XPointPoint/XPointK;
						double Aprime = Xik/XIPoint;
						double Bprime = Xjk/XJPoint;
						double B = Math.pow(Aprime,2)+Math.pow(Bprime,2);
						denominator = denominator + A*B;
						double C = Math.pow((Aprime-Bprime),2);
						somme = somme + A*C;
					}
				}
				double DistanceOrientéCli1_Cli2 = somme/denominator;
				String identifiant = IdCliques1+"-"+IdCliques2;
				TableDist.put(identifiant, DistanceOrientéCli1_Cli2);
			}






			for (int index1 = 0;index1<Math.min(EnsembleCliquesString1.size(),EnsembleCliquesString2.size());index1++)
			{
				Integer IDClique1=-1;
				Integer IDClique2 =-12;
				double distmini=Integer.MAX_VALUE;
				//Calcul de la distance minimale entre ttes les cliques des 2 pages
				Set<String> setTable = TableDist.keySet();
				Iterator<String> iteratorTable = setTable.iterator();
				while (iteratorTable.hasNext())
				{
					String identifiant = iteratorTable.next();
					double DistanceOrientéCli1_Cli2 = TableDist.get(identifiant);

					if (DistanceOrientéCli1_Cli2<distmini){
						distmini = DistanceOrientéCli1_Cli2;
						String[] id = identifiant.split("-");
						IDClique1 =Integer.parseInt(id[0]);
						IDClique2 =Integer.parseInt(id[1]);
					}
				}


				String Identifiant = IDClique1+"-"+IDClique2;
				Table.put(Identifiant, distmini);
				//suppression des cliques retenues
				for(int i=0;i<EnsembleCliquesString2.size();i++){
					Identifiant = IDClique1+"-"+i;
					TableDist.remove(Identifiant);
				}
				for(int i=0;i<EnsembleCliquesString1.size();i++){
					Identifiant = i+"-"+IDClique2;
					TableDist.remove(Identifiant);
				}
			}

			Set<String> setSomme = Table.keySet();
			Iterator<String> iteratorDist = setSomme.iterator();
			while (iteratorDist.hasNext())
			{
				String identifiant = iteratorDist.next();
				double DistanceOrientéCli1_Cli2 = Table.get(identifiant);
				SommeDistance = SommeDistance+DistanceOrientéCli1_Cli2;
			}
			SommeDistance=SommeDistance/Table.size();
		}
		return SommeDistance;
	}

	public static double DistanceKhi2EnsembleCliquesNormFast2 (Hashtable<Integer,ArrayList<String>> EnsembleCliquesString1 , Hashtable<Integer,ArrayList<String>> EnsembleCliquesString2 )
	{
		
		Double SommeDistance = 0.0;
		Double SommeDistancetest = 0.0;
		Hashtable<Integer,String> DicoContextonyme = new Hashtable<Integer,String>();

		//création du dico de contextonyme

		//creation de l'espace total
		ArrayList<String> thesaurusContextonyme = new ArrayList<String>();
		/**/for(int s=0;s<EnsembleCliquesString1.size();s++){
			thesaurusContextonyme.addAll(EnsembleCliquesString1.get(s));
		}
		for(int s=0;s<EnsembleCliquesString2.size();s++){
			thesaurusContextonyme.addAll(EnsembleCliquesString2.get(s));
		}
		Collections.sort(thesaurusContextonyme);
		int index =0;
		for(int s=0;s<thesaurusContextonyme.size();s++){
			String mot=thesaurusContextonyme.get(s);

			if(!DicoContextonyme.contains(mot)&!mot.equals("")){
				DicoContextonyme.put(index, mot);
				index++;
			}
		}

		Hashtable<Integer,ArrayList<String>> EnsembleCliquesString = new Hashtable<Integer,ArrayList<String>>(EnsembleCliquesString1);
		int TailleEns1 = EnsembleCliquesString1.size();
		for (int j=0 ;j<EnsembleCliquesString2.size();j++){
			ArrayList<String> CliquesString = EnsembleCliquesString2.get(j);
			if (!EnsembleCliquesString.contains(CliquesString)){
				int n =EnsembleCliquesString.size() ;
				EnsembleCliquesString.put(n,CliquesString);
			}
		}
		thesaurusContextonyme=new ArrayList<String>();
		for(int s=0;s<EnsembleCliquesString.size();s++){
			thesaurusContextonyme.addAll(EnsembleCliquesString.get(s));
			Collections.sort(thesaurusContextonyme);
		}

		//pour les matrices


		DoubleMatrix2D matrixI_J = new SparseDoubleMatrix2D(EnsembleCliquesString.size(),DicoContextonyme.size());	//construction de la matrice de l'ensemble total
		DoubleMatrix2D matrixEnsI = new SparseDoubleMatrix2D(EnsembleCliquesString1.size(),DicoContextonyme.size());
		DoubleMatrix2D matrixEnsJ = new SparseDoubleMatrix2D(EnsembleCliquesString2.size(),DicoContextonyme.size());

		//remplissage matrice
		for(int rowI=0;rowI<matrixEnsI.rows();rowI++){
			ArrayList<String> cliqueContextonyme = EnsembleCliquesString1.get(rowI);
			for(int colK=0;colK<matrixEnsI.columns();colK++){
				String Contextonyme = DicoContextonyme.get(colK);
				double Xik = xIJ (Contextonyme,  cliqueContextonyme );
				if(Xik!=0.0){
					matrixEnsI.setQuick(rowI,colK, Xik);
					matrixI_J.setQuick(rowI,colK, Xik);
				}
			}
		}
		int pointeurMatI_J=TailleEns1;
		boolean egaliteClique=false;
		for(int rowJ=0;rowJ<matrixEnsJ.rows();rowJ++){
			ArrayList<String> cliqueContextonyme = EnsembleCliquesString2.get(rowJ);
			egaliteClique=EnsembleCliquesString1.contains(cliqueContextonyme);
			for(int colK=0;colK<matrixEnsJ.columns();colK++){
				String Contextonyme = DicoContextonyme.get(colK);
				double Xjk = xIJ (Contextonyme,  cliqueContextonyme );
				matrixEnsJ.setQuick(rowJ,colK, Xjk);
				if(!egaliteClique){
					matrixI_J.setQuick(pointeurMatI_J,colK, Xjk);
				}else{
                                    
				}
			}
			if(!egaliteClique){
				pointeurMatI_J++;
			}
		}

		////Calcul de la distance  entre ttes les cliques des 2 pages
		//int XPointPoint =matrixI_J.cardinality();

		//Calcul des matrice Xik/Xipoint_racineXpointk
		DoubleMatrix2D matrix_XikSurXipoint_racineXpointK=new SparseDoubleMatrix2D(matrixEnsI.rows(),matrixEnsI.columns());
		for(int rowI=0;rowI<matrixEnsI.rows();rowI++){
			double XIPoint = matrixEnsI.viewRow(rowI).cardinality();
			XIPoint = matrixEnsI.viewRow(rowI).zSum();
			for(int colK=0;colK<matrixEnsI.columns();colK++){
				double XpointK = matrixI_J.viewColumn(colK).cardinality();

				//XpointK = matrixEnsJ.viewColumn(colK).cardinality()+matrixEnsI.viewColumn(colK).cardinality();
				double racineXpointK = Math.sqrt(XpointK);
				double racineXpointpoint = Math.sqrt(matrixI_J.cardinality());
				double Xik =matrixEnsI.get(rowI,colK);
				double XikSurXipoint_racineXpointK=Xik*racineXpointpoint/(XIPoint*racineXpointK);
				matrix_XikSurXipoint_racineXpointK.setQuick(rowI,colK, XikSurXipoint_racineXpointK);
			}
		}

		DoubleMatrix2D matrix_XjkSurXjpoint_racineXpointK=new SparseDoubleMatrix2D(matrixEnsJ.rows(),matrixEnsJ.columns());
		for(int rowJ=0;rowJ<matrixEnsJ.rows();rowJ++){
			double XJPoint = matrixEnsJ.viewRow(rowJ).cardinality();
			for(int colK=0;colK<matrixEnsJ.columns();colK++){
				double XpointK = matrixI_J.viewColumn(colK).cardinality();
				//double XpointK = matrixEnsJ.viewColumn(colK).cardinality()+matrixEnsI.viewColumn(colK).cardinality();
				double racineXpointK = Math.sqrt(XpointK);
				double racineXpointpoint = Math.sqrt(matrixI_J.cardinality());
				double Xjk =matrixEnsJ.get(rowJ,colK);
				double XjkSurXjpoint_racineXpointK=Xjk*racineXpointpoint/(XJPoint*racineXpointK);
				matrix_XjkSurXjpoint_racineXpointK.setQuick(rowJ,colK, XjkSurXjpoint_racineXpointK);
			}
		}

		//Calcul des matrice Xik/Xipoint_racineXpointKAuCarreAuCarre
		DoubleMatrix2D matrix_XikSurXipoint_racineXpointKAuCarre = matrix_XikSurXipoint_racineXpointK.copy().assign(DoubleFunctions.pow(2));
		DoubleMatrix2D matrix_XjkSurXjpoint_racineXpointKAuCarre = matrix_XjkSurXjpoint_racineXpointK.copy().assign(DoubleFunctions.pow(2));

		//Calcul de la matrice XikXjk/XipointXjpoint

		/*DoubleMatrix2D transp_matrix_XjkSurXjpoint_racineXpointK = matrix_XjkSurXjpoint_racineXpointK.viewDice().copy();
			DoubleMatrix2D matrice_XikXjkSurXipointXjpoint = matrix_XikSurXipoint_racineXpointK.zMult(transp_matrix_XjkSurXjpoint_racineXpointK, null);
			matrice_XikXjkSurXipointXjpoint=matrice_XikXjkSurXipointXjpoint.copy().assign(Functions.mult(2));
		 */
		//calcul de la matrice des distance entre cliques
		DoubleMatrix2D matriceIJ = new DenseDoubleMatrix2D(matrixEnsI.rows(),matrixEnsJ.rows());
		for (int row =0;row<matrixEnsI.rows();row++){
			for(int col = 0;col<matrixEnsJ.rows();col++){

				//calcul Cij
				double Cij =0.0;
				DoubleMatrix1D Cik = matrix_XikSurXipoint_racineXpointK.viewColumn(col);
				DoubleMatrix1D Cjk = matrix_XjkSurXjpoint_racineXpointK.viewColumn(col);
				Cij = (Cik.zDotProduct(Cjk));
				/*for(int colK=0;colK<matrixEnsJ.columns();colK++){
						Cij=Cij+matrix_XikSurXipoint_racineXpointK.get(col, colK)*matrix_XjkSurXjpoint_racineXpointK.get(col, colK);
					}*/
				DoubleMatrix1D Aik = matrix_XikSurXipoint_racineXpointKAuCarre.viewColumn(col).copy();
				DoubleMatrix1D Bjk = matrix_XjkSurXjpoint_racineXpointKAuCarre.viewColumn(col).copy();
				double denominateur = Aik.zSum()+Bjk.zSum();
				double distNorm = 1 - (2*Cij/denominateur);
				if((distNorm<0)||(distNorm>1)){
                                    
				}
				matriceIJ.setQuick(row, col, distNorm);
			}
		}


		//recuperation des dist
		DoubleMatrix2D matrice = matriceIJ;
		for (int index1 = 0;index1<Math.min(matriceIJ.columns(),matriceIJ.rows());index1++)
		{
			SommeDistance=SommeDistance+RecuperationDistMinetNettoyageMatrice (matrice);
		}
		SommeDistance=SommeDistance/Math.min(EnsembleCliquesString1.size(),EnsembleCliquesString2.size());

		for (int index1 = 0;index1<Math.min(matriceIJ.columns(),matriceIJ.rows());index1++)
		{
			SommeDistancetest=SommeDistancetest+RecuperationDistMinetNettoyageMatrice (matrice);
		}
		//}
		return SommeDistance;
	}



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

	public Hashtable<TaggedWord, ArrayList<Integer>> getMatriceVoisinage() {
		return MatriceVoisinage;
	}
	public void setMatriceVoisinage(
			Hashtable<TaggedWord, ArrayList<Integer>> matriceVoisinage) {
		MatriceVoisinage = matriceVoisinage;
	}
	public Hashtable<Integer, ArrayList<Integer>> getCliques() {
		return Cliques;
	}
	public void setCliques(Hashtable<Integer, ArrayList<Integer>> cliques) {
		Cliques = cliques;
	}
	public Hashtable<Integer,ArrayList<String>> getCliquesString() {
		return CliquesString;
	}
	public void setCliquesString(Hashtable<Integer,ArrayList<String>> cliquesString) {
		CliquesString = cliquesString;
	}
	public Hashtable<String,ArrayList<Integer>> getDicoEtOccurence() {
		return dicoEtOccurence;
	}
	public void setDicoEtOccurence(Hashtable<String,ArrayList<Integer>> dicoEtOccurence) {
		this.dicoEtOccurence = dicoEtOccurence;
	}
	public Hashtable<TaggedWord,Integer> getDicoEtOccurenceSimple() {
		return dicoEtOccurenceSimple;
	}
	public void setDicoEtOccurenceSimple(Hashtable<TaggedWord, Integer> word4) {
		this.dicoEtOccurenceSimple = word4;
	}
	public int getNombreMotTotal() {
		return NombreMotTotal;
	}
	public void setNombreMotTotal(int nombreMotTotal) {
		NombreMotTotal = nombreMotTotal;
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

	public ArrayList<TaggedWord> getTextWordTag() {
		return TextWordTag;
	}

	public void setTextWordTag(ArrayList<TaggedWord> textWordTag) {
		TextWordTag = textWordTag;
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

	public ArrayList<String> getDicoText() {
		return dicoText;
	}

	public void setDicoText(ArrayList<String> dicoText) {
		this.dicoText = dicoText;
	}

	public PixTaggerFrench getFrenchtagger() {
		return Frenchtagger;
	}

	public void setFrenchtagger(PixTaggerFrench frenchtagger) {
		Frenchtagger = frenchtagger;
	}

	public String getTextSortie() {
		return TextSortie;
	}

	public void setTextSortie(String textSortie) {
		TextSortie = textSortie;
	}

	public Hashtable<TaggedWord, ArrayList<TaggedWord>> getDico_wTLexUnit_wTsyn() {
		return Dico_wTLexUnit_wTsyn;
	}

	public void setDico_wTLexUnit_wTsyn(Hashtable<TaggedWord, ArrayList<TaggedWord>> dico_wTLexUnit_wTsyn) {
		Dico_wTLexUnit_wTsyn = dico_wTLexUnit_wTsyn;
	}

	public ArrayList<Integer> getVG() {
		return VG;
	}

	public void setVG(ArrayList<Integer> vG) {
		VG = vG;
	}

	public ArrayList<TaggedWord> getThesaurus() {
		return Thesaurus;
	}

	public void setThesaurus(ArrayList<TaggedWord> thesaurus) {
		Thesaurus = thesaurus;
	}
	public Hashtable<String,TaggedWord> getDicoCorresStrWt() {
		return dicoCorresStrWt;
	}
	public void setDicoCorresStrWt(Hashtable<String,TaggedWord> dicoCorresStrWt) {
		this.dicoCorresStrWt = dicoCorresStrWt;
	}

}