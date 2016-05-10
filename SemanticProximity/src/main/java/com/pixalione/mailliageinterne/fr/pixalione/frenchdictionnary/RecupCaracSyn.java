package com.pixalione.mailliageinterne.fr.pixalione.frenchdictionnary;



import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import com.pixalione.mailliageinterne.PixTagger.TaggedWord;
import com.pixalione.mailliageinterne.Semantic_Proximity.ChampSyntagmatique;
import com.pixalione.mailliageinterne.Semantic_Proximity.Syntagme;

import java.util.ArrayList;
import java.util.Hashtable;

/*
 * classe permettant de recuperer a partir de deux liste de mots, les meilleures synonymes et leur resultat de distance
 */
public class RecupCaracSyn {

	private ArrayList<TaggedWord> ListeSyn1;	//listsyn1 plus petit que listsyn2
	private ArrayList<TaggedWord> ListeSyn2;
	private Hashtable<TaggedWord,TaggedWord> DicoSyn = new Hashtable<TaggedWord,TaggedWord>();
	private Hashtable<TaggedWord,Double> DicoSynDist = new Hashtable<TaggedWord,Double>();
	
	public RecupCaracSyn(Syntagme Syn1, Syntagme Syn2, Hashtable<TaggedWord,ArrayList<TaggedWord>> DicoVoisinage){
		 
		 ArrayList<TaggedWord> MotsPleins1= Syn1.getMotsPleins();
		 ArrayList<TaggedWord> MotsPleins2 = Syn2.getMotsPleins();
		 
			//Distance Position des mots
		 	//on prend syn1 comme groupe de mots clefs et syn2 comme groupe de mot du texte
		 	//avec Syn1 ayant une taille faible par rapport � syn2
		 	ArrayList<TaggedWord> MotsPleinsSyn1;
		 	ArrayList<TaggedWord> MotsPleinsSyn2;
		 	 	 MotsPleinsSyn1= new  ArrayList<TaggedWord>(MotsPleins1);
			 	 MotsPleinsSyn2=new  ArrayList<TaggedWord>(MotsPleins2);
		 	
		 	this.setListeSyn1(MotsPleinsSyn1);
		 	this.setListeSyn2(MotsPleinsSyn2);
		 	//remplacement des mots du groupe de mots par leur mots clefs synonymes
		 	//recuperation du synonyme le plus fort des mots clefs et leur distance semantique;
		 	DoubleMatrix2D mat = new SparseDoubleMatrix2D(MotsPleinsSyn1.size(),MotsPleinsSyn2.size());
		 	
		 	for(int i =0;i<MotsPleinsSyn1.size();i++){
		 		TaggedWord Mot1 = MotsPleinsSyn1.get(i);
		 		for(int j=0;j<MotsPleinsSyn2.size();j++){
			 		TaggedWord Mot2 = MotsPleinsSyn2.get(j);
			 		
			 		
			 		double dist = ChampSyntagmatique.DistanceSemEuclidienneEntreMots(Mot1,Mot2,DicoVoisinage);
			 		mat.set(i, j, dist);
			 		//reuperation du meilleure syn
			 	}//reuperation du meilleure syn
		 	}
		 	RecuperationDistMinetSynetNettoyageMatrice ( mat);
		
	}

	public ArrayList<TaggedWord> getListeSyn1() {
		return ListeSyn1;
	}

	public void setListeSyn1(ArrayList<TaggedWord> listeSyn1) {
		ListeSyn1 = listeSyn1;
	}

	public ArrayList<TaggedWord> getListeSyn2() {
		return ListeSyn2;
	}

	public void setListeSyn2(ArrayList<TaggedWord> listeSyn2) {
		ListeSyn2 = listeSyn2;
	}
	
	public void RecuperationDistMinetSynetNettoyageMatrice (DoubleMatrix2D matrice){
		Hashtable<TaggedWord,TaggedWord> dicoSyn = new Hashtable<TaggedWord,TaggedWord>();
		Hashtable<TaggedWord,Double> dicoSynDist = new Hashtable<TaggedWord,Double>();
		ArrayList<TaggedWord> MotsPleins1 = this.getListeSyn1();
	 	ArrayList<TaggedWord> MotsPleins2 = this.getListeSyn2();
		for(int i =0;i<Math.min(matrice.rows(), matrice.columns());i++){
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
			//nettoyage et remplissage de valeur
					
			if(min!=1.0){
				TaggedWord Mot1 = MotsPleins1.get(rowPos);
				TaggedWord Mot2SynMot1 = MotsPleins2.get(colPos);
				dicoSynDist.put(Mot1, min);
				dicoSyn.put(Mot2SynMot1, Mot1);					
			}
			for(int row = 0;row<matrice.rows();row++){
				matrice.set(row, colPos, 1.0);
				if( row == rowPos){
					for(int col = 0;col<matrice.columns();col++){
						matrice.set(row, col, 1.0);
					}
				}
			}
		}
		this.setDicoSyn(dicoSyn);
		this.setDicoSynDist(dicoSynDist);
	}

	public Hashtable<TaggedWord,TaggedWord> getDicoSyn() {
		return DicoSyn;
	}

	public void setDicoSyn(Hashtable<TaggedWord,TaggedWord> dicoSyn) {
		DicoSyn = dicoSyn;
	}

	public Hashtable<TaggedWord,Double> getDicoSynDist() {
		return DicoSynDist;
	}

	public void setDicoSynDist(Hashtable<TaggedWord,Double> dicoSynDist) {
		DicoSynDist = dicoSynDist;
	}

}
