package com.pixalione.mailliageinterne.Semantic_Proximity;


import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import com.pixalione.mailliageinterne.PixTagger.TaggedWord;
import com.pixalione.mailliageinterne.fr.pixalione.frenchdictionnary.DictionnaireSyn;
import com.pixalione.mailliageinterne.fr.pixalione.frenchdictionnary.RecupCaracSyn;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class ComparaisonSyntagmeGrpMotsClefs {

    private ChampSyntagmatique ChampSyn1;
    private ChampSyntagmatique ChampSyn2;
    private DictionnaireSyn DicoSyn;
    private Syntagme MotsLesPlusProchesSyntagme;
    private ArrayList<Syntagme> ListeMotsProches;
    private String MotsLesPlusProchesFormatString;
    private ArrayList<String> ListeMotsLesPlusProchesFormatString;
    private double DistMotsLesPlusProchesSyntagme;
    private ArrayList<Double> ListeDistMotsLesPlusProchesSyntagme;

    public ComparaisonSyntagmeGrpMotsClefs(ChampSyntagmatique syn1, ChampSyntagmatique syn2, DictionnaireSyn dico) {
        this.setChampSyn1(syn1);
              //  System.out.println("***************syn1**********");
        //  System.out.println(syn1);
        this.setChampSyn2(syn2);
             //   System.out.println("***************syn2**********");
        //   System.out.println(syn2);
        this.setDicoSyn(dico);
        ArrayList<Syntagme> EnsembleSyn1 = syn1.getListSyntagme();
        ArrayList<Syntagme> EnsembleSyn2 = syn2.getListSyntagme();

        double Dista = DistanceMinEntre2EnsembleSyntagmePixa(EnsembleSyn1, EnsembleSyn2, dico.getDico_wTLexUnit_wTsyn());
        double proxiSemantique = 1 - Dista;
        System.out.println("The semantic proximity between the two pages is : " + proxiSemantique);
        this.setDistMotsLesPlusProchesSyntagme(Dista);
    }

    /**
     * Fonction servant � calculer la distance Semantique(synonymie) entre deux
     * mots (plus la distance est faible, plus les mots sont proches) elle ne
     * prend pas en compte les erreurs de tag sur les noms et adjectifs
     *
     * @param Mot1 - WordTag
     * @param Mot2 - WordTag
     * @param DicoVoisinage - Hashtable<WordTag, ArrayList<WordTag>> @return Distan
     * ce - double
     */
    public static double DistanceSemEuclidienneEntreMotsPixa(TaggedWord Mot1, TaggedWord Mot2,
            Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage) {
           // System.out.println("//////////////////////////////// F1 ////////////////////////////////");
        //   System.out.println("************** Mot1 1 ***************");
        //  System.out.println(Mot1);
        //System.out.println("************** Mot2 1 ***************");
        //System.out.println(Mot2);
        double Distance;
        if ((!Mot1.equals(Mot2))) {
            //si mot1 different Mot2
            if ((Mot1.getTag().equals(Mot2.getTag()))) {
                //si mot1 et mot2 ont le meme tag

                if ((DicoVoisinage.containsKey(Mot1)) & (DicoVoisinage.containsKey(Mot2))) {
                    //si les deux mots sont present dans le dictionnaire de voisinage
                    ArrayList<TaggedWord> ListVoisinMot1 = new ArrayList<TaggedWord>(DicoVoisinage.get(Mot1));
                    ArrayList<TaggedWord> ListVoisinMot2 = new ArrayList<TaggedWord>(DicoVoisinage.get(Mot2));
                    if (ListVoisinMot1.contains(Mot2) & ListVoisinMot2.contains(Mot1)) {
                        //si mot2 synonyme de mot1

                        ListVoisinMot1.remove(Mot2); //elimination de mot2 pour pouvoir normaliser
                        ListVoisinMot2.remove(Mot1); //elimination de mot1 pour pouvoir normaliser
                        //    System.out.println("********************Mot1**************");
                        //    System.out.println(Mot1);
                        //    System.out.println("*******************Mot2**************");
                        //    System.out.println(Mot2);
                        //    System.out.println("********************ListVoisinMot1**************");
                        //    System.out.println(ListVoisinMot1);
                        //    System.out.println("********************ListVoisinMot2**************");
                        //    System.out.println(ListVoisinMot2);

                        ArrayList<TaggedWord> ListVoisinTotal = new ArrayList<TaggedWord>(ListVoisinMot1);
                        for (int i = 0; i < ListVoisinMot2.size(); i++) {
                            TaggedWord IdMotSyn = ListVoisinMot2.get(i);
                            if (!ListVoisinTotal.contains(IdMotSyn)) {
                                ListVoisinTotal.add(IdMotSyn);
                            }
                        }
                                            //    System.out.println("**************ListVoisinTotal*****************");
                        //    System.out.println(ListVoisinTotal);

                        ArrayList<TaggedWord> ListVoisinCommun = new ArrayList<TaggedWord>();
                        for (int i = 0; i < ListVoisinTotal.size(); i++) {
                            TaggedWord IdMotSyn = ListVoisinTotal.get(i);
                            if (ListVoisinMot1.contains(IdMotSyn) & ListVoisinMot2.contains(IdMotSyn)) {
                                ListVoisinCommun.add(IdMotSyn);
                            }
                        }
                                             //   System.out.println("**************ListVoisinCommun*****************");
                        //   System.out.println(ListVoisinCommun);

                        if (ListVoisinCommun.size() == 0) {
                            //si les deux mots n'ont rien en commun
                            Distance = 1.0;
                        } else {
                            double dist = ListVoisinTotal.size() - ListVoisinCommun.size();
//                                                        System.out.println(ListVoisinTotal.size());
                            Distance = dist / ListVoisinTotal.size();
//                                                        System.out.println("la Sidtnce = "+Distance);
                        }
                    } else {//si les deux mots n'ont aucun lien de synonymie
                        Distance = 1.0;
                    }
                } else {//si au moins un des deux mots n'est pas present dans le dictionnaire
                    Distance = 1.0;
                }
            } else {//si mot1 et mot2 ont des tags diff�rents
                Distance = 1.0;

            }

        } else {
            Distance = 0.0;	//si mot1 = mot2
        }
        if (1 - Distance > 0.5) {
            ;
        } else if (1 - Distance > 0.0 & 1 - Distance < 0.5) {

        } else {

        }
              //  System.out.println("************ Distance 1 ***************");
        //   System.out.println(Distance);
        return Distance;
    }

//********************* 2 *********************/
    /**
     * Fonction servant � calculer la distance Semantique(synonymie) entre deux
     * mots (plus la distance est faible, plus les mots sont proches)
     *
     * @param //Mot1 - WordTag
     * @param //Mot2 - WordTag
     * @param DicoVoisinage - Hashtable<WordTag, ArrayList<WordTag>> @return Distan
     * ce - double
     */
    public static double DistanceSemEuclidienneEntreMotsPixa2(TaggedWord mot1, TaggedWord mot2,
            Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage) {
// System.out.println("//////////////////////////////// F3 ////////////////////////////////");
//                System.out.println("************** Mot1 3 ***************");
//                System.out.println(mot1);
//                System.out.println("************** Mot2 3 ***************");
//                System.out.println(mot2);
        TaggedWord Mot1 = new TaggedWord(mot1);
        TaggedWord Mot2 = new TaggedWord(mot2);

        //si l'on compare un nom et un adjectif (pour regler les problemes d'erreur de Tag)
        String tags = Mot1.getTagShortFomre() + Mot2.getTagShortFomre();

        if (tags.equals("AN") | tags.equals("NA")) {
            if (DicoVoisinage.containsKey(Mot1)) {
                Mot2.setTag(Mot1.getTag());
            } else if (DicoVoisinage.containsKey(Mot2)) {
                Mot1.setTag(Mot2.getTag());
            } else if (Mot1.getToken().equals(Mot2.getToken())) {
                Mot1.setTag(Mot2.getTag());
            }
        }

        double Distance;
        if ((!Mot1.equals(Mot2))) {
            //si mot1 different Mot2
            if ((Mot1.getTag().equals(Mot2.getTag()))) {
                //si mot1 et mot2 ont le meme tag 

                if ((DicoVoisinage.containsKey(Mot1)) & (DicoVoisinage.containsKey(Mot2))) {
                    //si les deux mots sont present dans le dictionnaire de voisinage
                    ArrayList<TaggedWord> ListVoisinMot1 = new ArrayList<TaggedWord>(DicoVoisinage.get(Mot1));
                    ArrayList<TaggedWord> ListVoisinMot2 = new ArrayList<TaggedWord>(DicoVoisinage.get(Mot2));
                    if (ListVoisinMot1.contains(Mot2) & ListVoisinMot2.contains(Mot1)) {
                        //si mot2 synonyme de mot1

                        ListVoisinMot1.remove(Mot2); //elimination de mot2 pour pouvoir normaliser
                        ListVoisinMot2.remove(Mot1); //elimination de mot1 pour pouvoir normaliser

                        ArrayList<TaggedWord> ListVoisinTotal = new ArrayList<TaggedWord>(ListVoisinMot1);
                        for (int i = 0; i < ListVoisinMot2.size(); i++) {
                            TaggedWord IdMotSyn = ListVoisinMot2.get(i);
                            if (!ListVoisinTotal.contains(IdMotSyn)) {
                                ListVoisinTotal.add(IdMotSyn);
                            }
                        }
                        ArrayList<TaggedWord> ListVoisinCommun = new ArrayList<TaggedWord>();
                        for (int i = 0; i < ListVoisinTotal.size(); i++) {
                            TaggedWord IdMotSyn = ListVoisinTotal.get(i);
                            if (ListVoisinMot1.contains(IdMotSyn) & ListVoisinMot2.contains(IdMotSyn)) {
                                ListVoisinCommun.add(IdMotSyn);
                            }
                        }

                        if (ListVoisinCommun.size() == 0) {
                            //si les deux mots n'ont rien en commun
                            Distance = 1.0;
                        } else {
                            /*double indexSynonym = Math.min(ListVoisinMot1.size(), ListVoisinMot2.size());
                             double dist1 = 1-ListVoisinCommun.size()/indexSynonym;*/

                            double dist = ListVoisinTotal.size() - ListVoisinCommun.size();
                            Distance = dist / ListVoisinTotal.size();	//(car on veut plus on est synonyme plus on est proche de zero)

                            //String test = "";
                        }
                    } else {//si les deux mots n'ont aucun lien de synonymie
                        Distance = 1.0;
                    }
                } else {//si au moins un des deux mots n'est pas present dans le dictionnaire
                    Distance = 1.0;
                }
            } else {//si mot1 et mot2 ont des tags diff�rents
                Distance = 1.0;
            }

        } else {
            Distance = 0.0;	//si mot1 = mot2
        }
//System.out.println("********** Distance 3 *****************");
        //              System.out.println(Distance);
        return Distance; //(car on veut plus on est synonyme plus on est proche de zero)
    }

//********************* 4 *****************************/
    /**
     * Fonction servant � calculer la distance Semantique entre deux groupes de
     * lemmes
     *
     * @param groupe1
     * @param groupe2
     * @param DicoVoisinage
     * @return
     */
    public static Double DistanceMinEntre2groupesLemmePixaAffichage(ArrayList<TaggedWord> groupe1, ArrayList<TaggedWord> groupe2, Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage) {

//                System.out.println("//////////////////////////////// F5 ////////////////////////////////");
//                System.out.println("************** groupe1 5 ***************");
//                System.out.println(groupe1);
//                System.out.println("************** groupe2 5 ***************");
//                System.out.println(groupe2);
        Double Distance = 0.0;
        Double Distancetest = 0.0;
        ArrayList<TaggedWord> groupMax = groupe2;
        ArrayList<TaggedWord> groupMin = groupe1;
        if (groupe1.size() > groupe2.size()) {
            groupMax = groupe1;
            groupMin = groupe2;
        }
        DoubleMatrix2D mat = new SparseDoubleMatrix2D(groupMax.size(), groupMin.size());
        for (int i = 0; i < groupMax.size(); i++) {
            double distMin = Double.MAX_VALUE;
            TaggedWord lemme1 = groupMax.get(i);
            for (int j = 0; j < groupMin.size(); j++) {
                TaggedWord lemme2 = groupMin.get(j);
                double dist = DistanceSemEuclidienneEntreMotsPixa(lemme1, lemme2, DicoVoisinage);
                mat.set(i, j, dist);
                if (dist < distMin) {
                    distMin = dist;
                }
            }
            Distance = Distance + distMin;
        }
        for (int i = 0; i < groupMax.size(); i++) {
            Distancetest = Distancetest + RecuperationDistMinetNettoyageMatrice(mat);
        }
        if (Distance.isNaN()) {

        }
        if (!Distance.equals(Distancetest)) {

        }
//                System.out.println("********** Distance 5 *****************");
//                System.out.println(Distance/groupMax.size());
        return Distance / groupMax.size();
    }

    /**
     * Fonction servant � calculer la distance Semantique entre deux groupes de
     * lemmes
     *
     * @param groupe1
     * @param groupe2
     * @param DicoVoisinage
     * @return
     */
    public static Double DistanceMinEntre2groupesLemmePixa(ArrayList<TaggedWord> groupe1, ArrayList<TaggedWord> groupe2, Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage) {

        /*      System.out.println("//////////////////////////////// F6 ////////////////////////////////");
         System.out.println("************** groupe1 6 ***************");
         System.out.println(groupe1);
         System.out.println("************** groupe2 6 ***************");
         System.out.println(groupe2);*/
        Double Distance = 0.0;
        Double Distancetest = 0.0;
        ArrayList<TaggedWord> groupMax = groupe2;
        ArrayList<TaggedWord> groupMin = groupe1;
        if (groupe1.size() > groupe2.size()) {
            groupMax = groupe1;
            groupMin = groupe2;
        }
        DoubleMatrix2D mat = new SparseDoubleMatrix2D(groupMax.size(), groupMin.size());
        for (int i = 0; i < groupMax.size(); i++) {
            double distMin = Double.MAX_VALUE;
            TaggedWord lemme1 = groupMax.get(i);
            for (int j = 0; j < groupMin.size(); j++) {
                TaggedWord lemme2 = groupMin.get(j);
                //double dist = DistanceSemEuclidienneEntreMots(lemme1,lemme2,DicoVoisinage);
                double dist = DistanceSemEuclidienneEntreMotsPixa2(lemme1, lemme2, DicoVoisinage);
                mat.set(i, j, dist);
                if (dist < distMin) {
                    distMin = dist;
                }
            }
            Distance = Distance + distMin;
            /*           System.out.println("************** DistMin 6 ***************");
             System.out.println(distMin);*/
        }

        for (int i = 0; i < groupMax.size(); i++) {
            Distancetest = Distancetest + RecuperationDistMinetNettoyageMatrice(mat);
        }
        if (Distance.isNaN()) {
        }
        if (!Distance.equals(Distancetest)) {
        }
        /*     System.out.println("********** Distance 6 *****************");
         System.out.println(Distance/groupMax.size());*/
        return Distance / groupMax.size();
    }

	//************************* 7 **************************/
//***************************** 8 **************************/
	//*************************** 9 ***********************/
    /**
     * Fonction servant � calculer la distance Semantique entre deux syntagme
     * configurer pour pixalione
     *
     * @param Syn1
     * @param Syn2
     * @param DicoVoisinage
     * @return
     */
    public static Double DistanceMinEntre2SyntagmePixa(Syntagme Syn1, Syntagme Syn2, Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage) {

        /*         System.out.println("//////////////////////////////// F10 ////////////////////////////////");
         System.out.println("************** Syn1 10 ***************");
         System.out.println(Syn1);
         System.out.println("************** Syn2 10 ***************");
         System.out.println(Syn2);*/
        Double Distance = 1.0;

        double Distancenorme = 0.0;
        String Text = "";
		//recup des caracterisitiques
        // ArrayList<WordTag> MotsDeFonctions1 = Syn1.getMotsDeFonctions();
        ArrayList<TaggedWord> MotsPleins1 = Syn1.getMotsPleins();
		//Integer StructureSyntaxique1= Syn1.getStructureSyntaxique();
        // ArrayList<WordTag> MotsDeFonctions2 = Syn2.getMotsDeFonctions();
        ArrayList<TaggedWord> MotsPleins2 = Syn2.getMotsPleins();
		//Integer StructureSyntaxique2= Syn2.getStructureSyntaxique();

        //on prend syn1 comme groupe de mots clefs et syn2 comme groupe de mot du texte
        ArrayList<TaggedWord> MotsPleinsSyn1;
        ArrayList<TaggedWord> MotsPleinsSyn2;

        MotsPleinsSyn1 = new ArrayList<TaggedWord>(MotsPleins1);
        MotsPleinsSyn2 = new ArrayList<TaggedWord>(MotsPleins2);

        //Distance Synonymique
        Double DistanceSyn = DistanceMinEntre2groupesLemmePixa(MotsPleins1, MotsPleins2, DicoVoisinage);
        Text = Text + "\n meilleure Distance synonymique entre 2 groupes de mots : " + DistanceSyn + "\n";
        boolean EgaliteParfaite = MotsPleins1.equals(MotsPleins2);
        if (!EgaliteParfaite) {
            //	Distance = DistanceSyn+1;
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
        if ((DistanceSyn != 1.0)) {//&!EgaliteParfaite){
            //on prend syn1 comme groupe de mots clefs et syn2 comme groupe de mot du texte
            //avec Syn1 ayant une taille faible par rapport � syn2

			//remplacement des mots du groupe de mots par leur mots clefs synonymes
            //recuperation du synonyme le plus fort des mots clefs et leur distance semantique;
            RecupCaracSyn recup = new RecupCaracSyn(Syn1, Syn2, DicoVoisinage);
            Hashtable<TaggedWord, TaggedWord> DicoSyn = recup.getDicoSyn();	//dictionnaire permettant l'association entre les deux syntagmes

            /*if(DicoSyn.size()==MotsPleinsSyn1.size()){
			
             }*/
            Hashtable<TaggedWord, Double> DicoSynDist = recup.getDicoSynDist();

            //nombre de mots clefs present dans le groupe de mots
            int nbKeyWord = DicoSynDist.size();
            //nombre de mots clefs absents du groupe de mots
            int nbAbsKeyWord = MotsPleinsSyn1.size() - nbKeyWord;

            //tableau de denominateur  pour les score de mots clefs pour le cas d'absences de mots clefs
            ArrayList<Integer> Listdenom = new ArrayList<Integer>();
            nbAbsKeyWord = 0;	//on met a zero pour le test
            if (nbAbsKeyWord == 0) { //ts les mots clefs sont present
                for (int j = 0; j < 2; j++) {
                    Listdenom.add(1);
                }
                for (int j = 2; j < MotsPleinsSyn1.size(); j++) {
                    Listdenom.add(1);
                }
            }
            if (nbAbsKeyWord == 1) { //ts les mots clefs sont present
                Listdenom.add(8);
                Listdenom.add(10);
                for (int j = 2; j < MotsPleinsSyn1.size(); j++) {
                    Listdenom.add(1);
                }
            }
            if (nbAbsKeyWord >= 2) { //ts les mots clefs sont present
                Listdenom.add(16);
                Listdenom.add(20);
                for (int j = 2; j < MotsPleinsSyn1.size(); j++) {
                    Listdenom.add(1);
                }
            }

            //remplacement des mots du groupe de mots par leur mots clefs synonymes
			/**/
            ArrayList<TaggedWord> NewGroupMot = new ArrayList<TaggedWord>();
            for (int i = 0; i < MotsPleinsSyn2.size(); i++) {
                TaggedWord Mot2 = MotsPleinsSyn2.get(i);
                if (DicoSyn.containsKey(Mot2)) {
                    Mot2 = DicoSyn.get(Mot2);
                }
                NewGroupMot.add(Mot2);
            }

            //etablissement des scores pour la presence des mots clefs
            ArrayList<TaggedWord> ListeMotsClefsPresentNonOrdonnee = new ArrayList<TaggedWord>(DicoSyn.values());
            ArrayList<TaggedWord> ListeMotsClefsPresent = new ArrayList<TaggedWord>();
            // rangement en ordre de laiste de mots clefs presents
            for (int l = 0; l < MotsPleinsSyn1.size(); l++) {
                TaggedWord motclef = MotsPleinsSyn1.get(l);
                if (ListeMotsClefsPresentNonOrdonnee.contains(motclef)) {
                    ListeMotsClefsPresent.add(motclef);
                }
            }

            ArrayList<Double> ListeScoreCorrelation = new ArrayList<Double>();	//sert a stocker le score apres correlation
            ArrayList<Double> ListeScoreDist = new ArrayList<Double>();	//sert a stocker le score de distance sans correlation
            //Cas ou il y a plus de deux mots clefs en entree
            int compteur = 0;
            int nbreKeyWord = MotsPleinsSyn1.size();
            for (int k = 0; k < MotsPleinsSyn1.size(); k++) {

                TaggedWord MotClef1 = MotsPleinsSyn1.get(k);
                if (ListeMotsClefsPresent.contains(MotClef1)) {
                    if (NewGroupMot.contains(MotClef1)) {
                        //si le mot clef est present ( si il y aun mot du groupe original qui lui est synonyme
                        int PosDsGrpMotClef1 = MotsPleinsSyn1.indexOf(MotClef1) + 1;	//position du mot clefs dans le groupe de mots clefs
                        int PosDsGrpMot = NewGroupMot.indexOf(MotClef1) + 1;	//position du mot clefs dans le groupe de mots du texte
                        if (compteur + 1 < ListeMotsClefsPresent.size()) {
                            TaggedWord MotClef2 = ListeMotsClefsPresent.get(compteur + 1);
                            int PosDsGrpMotClef2 = MotsPleinsSyn1.indexOf(MotClef2) + 1;	//position du mot clefs dans le groupe de mots clefs

							//calcul du score 
                            //double ScoreMot = (20/3*(3-Math.log(PosDsGrpMot)))/Math.abs(PosDsGrpMotClef1-PosDsGrpMotClef2);
                            int valLN = Math.abs(PosDsGrpMot - (MotsPleinsSyn1.indexOf(MotClef1) + 1)) + 1;
                            //double ScoreMot = (20)*(1-Math.log(valLN)/nbreKeyWord)/Math.abs(PosDsGrpMotClef1-PosDsGrpMotClef2);
                            double ponderationsynonymie = 1 - DicoSynDist.get(MotClef1);
                            double ScoreMotDist = (20) * (1 - Math.log(valLN) / nbreKeyWord) / Math.abs(PosDsGrpMotClef1 - PosDsGrpMotClef2);
                            double ScoreMot = (20) * (ponderationsynonymie) * (1 - Math.log(valLN) / nbreKeyWord) / Math.abs(PosDsGrpMotClef1 - PosDsGrpMotClef2);
                            ScoreMot = ScoreMot / Listdenom.get(k);
                            ListeScoreCorrelation.add(ScoreMot);
                            ScoreMotDist = ScoreMotDist / Listdenom.get(k);
                            ListeScoreDist.add(ScoreMotDist);
                        } else {
                            //double ScoreMot = (20/3*(3-Math.log(PosDsGrpMot)));
                            int valLN = Math.abs(PosDsGrpMot - (MotsPleinsSyn1.indexOf(MotClef1) + 1)) + 1;
							//double test = Math.log(PosDsGrpMot);
                            //double ScoreMot = (20)*(1-Math.log(valLN)/nbreKeyWord);
                            double ponderationsynonymie = 1 - DicoSynDist.get(MotClef1);
                            double ScoreMot = (20) * (ponderationsynonymie) * (1 - Math.log(valLN) / nbreKeyWord);
                            double ScoreMotDist = (20) * (1 - Math.log(valLN) / nbreKeyWord);
                            ScoreMot = ScoreMot / Listdenom.get(k);
                            ListeScoreCorrelation.add(ScoreMot);
                            ScoreMotDist = ScoreMotDist / Listdenom.get(k);
                            ListeScoreDist.add(ScoreMotDist);
                        }

                    } else {
                        ListeScoreCorrelation.add(0.0);
                        ListeScoreDist.add(0.0);
                    }
                    compteur++;
                } else {
                    double valeur = 0.0;
                    if (MotsPleinsSyn2.size() == MotsPleinsSyn1.size()) {
                        valeur = -5.0;//valeur a retrancher dans le score car il y a trop d'elements
                    }
                    ListeScoreCorrelation.add(valeur);
                    ListeScoreDist.add(valeur / Listdenom.get(k));
                }
            }

            //etablissement du facteur de ponderation pour le score de similitude du groupe de mots par rapport au groupe de mots clefs
            ArrayList<Double> ListePond = new ArrayList<Double>();
            double sommepond = 0.0;
            for (int j = 0; j < ListeScoreCorrelation.size(); j++) {
                if (j == 0) {
                    ListePond.add(2.0);
                }
                if (j == 1) {
                    ListePond.add(1.5);
                }
                if (j == 2) {
                    ListePond.add(0.7);
                }
                if (j >= 3) {
                    ListePond.add(0.35);
                }
                if (ListeScoreCorrelation.get(j) != 0.0) {
                    //sommepond = sommepond+ListePond.get(j);	//prise en compte du fait que un mot clefs peut ne pas etre present
                }
                sommepond = sommepond + ListePond.get(j);
            }

            //resultat calcul score groupe de mots
            Double ScoreTotal = 0.0;
            Double ScoreTotalDist = 0.0;
            for (int j = 0; j < MotsPleinsSyn1.size(); j++) {
                ScoreTotal = ScoreTotal + ListeScoreCorrelation.get(j) * ListePond.get(j);
                ScoreTotalDist = ScoreTotalDist + ListeScoreDist.get(j) * ListePond.get(j);
            }
            ScoreTotal = ScoreTotal / sommepond;
            /*      System.out.println("********** Score total 10 ******************");
             System.out.println(ScoreTotal);*/
            ScoreTotalDist = ScoreTotalDist / sommepond;
            double DistanceNonnorme = ScoreTotal;
            /*    System.out.println("********** DistanceNonnorme 10 ******************");
             System.out.println(DistanceNonnorme);*/
            //normalisation pour avoir plus de proxilmit� c'est zero
            double base = 0.0;
            /**/
            for (int k = 0; k < MotsPleinsSyn1.size(); k++) {
				//int PosDsGrpMot = k+1;
                //base = base+ ((20/nbreKeyWord)*(nbreKeyWord-Math.log(PosDsGrpMot))*(ListePond.get(k)));
                base = base + 20 * (ListePond.get(k));
                //base = base+ 1*(ListePond.get(k));
            }
            base = base / sommepond;
            Distancenorme = DistanceNonnorme / base;
            Distance = Distancenorme;//base;
            Distance = 1 - Distancenorme;
            /*     System.out.println("********** Base 10 ******************");
             System.out.println(base);
             System.out.println("********** DistanceNonnorme 10 ******************");
             System.out.println(DistanceNonnorme);*/

        }

        if (Distance.isNaN()) {

        }
        if (Distance != 1.0) {
            DistanceMinEntre2groupesLemmePixaAffichage(MotsPleins1, MotsPleins2, DicoVoisinage);
            Text = "distance entre \"" + Syn1.SynToGrpWords() + "\" et \"" + Syn2.SynToGrpWords() + "\" :" + Text + DistanceMinEntre2SyntagmePixaDistSansSynonymie(Syn1, Syn2, DicoVoisinage)
                    + "\nrésultat distance correlation distance-synonymie pondere = " + Distancenorme * 20 + "\n\n";

        }
        /*  System.out.println("************ Distance 10  ******************");
         System.out.println(Distance);*/
        return Distance;
    }

    /**
     * Fonction servant � calculer la distance Semantique entre deux syntagme
     * configurer pour pixalione
     *
     * @param Syn1
     * @param Syn2
     * @param DicoVoisinage
     * @return
     */
    public static String DistanceMinEntre2SyntagmePixaDistSansSynonymie(Syntagme Syn1, Syntagme Syn2, Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage) {
        /*System.out.println("//////////////////////////////// F11 ////////////////////////////////");
         System.out.println("************** Syn1 11 ***************");
         System.out.println(Syn1);
         System.out.println("************** Syn2 11 ***************");
         System.out.println(Syn2);	*/
        Double Distance = 1.0;
        Double DistanceDist = 1.0;

        double Distancenorme = 0.0;
        String Text = "";
		//recup des caracterisitiques
        // ArrayList<WordTag> MotsDeFonctions1 = Syn1.getMotsDeFonctions();
        ArrayList<TaggedWord> MotsPleins1 = Syn1.getMotsPleins();
		//Integer StructureSyntaxique1= Syn1.getStructureSyntaxique();
        // ArrayList<WordTag> MotsDeFonctions2 = Syn2.getMotsDeFonctions();
        ArrayList<TaggedWord> MotsPleins2 = Syn2.getMotsPleins();
		//Integer StructureSyntaxique2= Syn2.getStructureSyntaxique();

        //on prend syn1 comme groupe de mots clefs et syn2 comme groupe de mot du texte
        ArrayList<TaggedWord> MotsPleinsSyn1;
        ArrayList<TaggedWord> MotsPleinsSyn2;

        MotsPleinsSyn1 = new ArrayList<TaggedWord>(MotsPleins1);
        MotsPleinsSyn2 = new ArrayList<TaggedWord>(MotsPleins2);

        //Distance Synonymique
        Double DistanceSyn = 1 - DistanceMinEntre2groupesLemmePixa(MotsPleins1, MotsPleins2, DicoVoisinage);
        Text = Text + "\n meilleure Distance synonymique entre 2 groupes de mots : " + DistanceSyn + "\n";
        boolean EgaliteParfaite = MotsPleins1.equals(MotsPleins2);
        if (!EgaliteParfaite) {
            Distance = DistanceSyn + 1;
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
        if ((DistanceSyn != 0.0)) {//&!EgaliteParfaite){
            //on prend syn1 comme groupe de mots clefs et syn2 comme groupe de mot du texte
            //avec Syn1 ayant une taille faible par rapport � syn2

			//remplacement des mots du groupe de mots par leur mots clefs synonymes
            //recuperation du synonyme le plus fort des mots clefs et leur distance semantique;
            RecupCaracSyn recup = new RecupCaracSyn(Syn1, Syn2, DicoVoisinage);
            Hashtable<TaggedWord, TaggedWord> DicoSyn = recup.getDicoSyn();	//dictionnaire permettant l'association entre les deux syntagmes

            /*if(DicoSyn.size()==MotsPleinsSyn1.size()){
				
             }*/
            Hashtable<TaggedWord, Double> DicoSynDist = recup.getDicoSynDist();

            //nombre de mots clefs present dans le groupe de mots
            int nbKeyWord = DicoSynDist.size();
            //nombre de mots clefs absents du groupe de mots
            int nbAbsKeyWord = MotsPleinsSyn1.size() - nbKeyWord;

            //tableau de denominateur  pour les score de mots clefs pour le cas d'absences de mots clefs
            ArrayList<Integer> Listdenom = new ArrayList<Integer>();
            nbAbsKeyWord = 0;	//on met a zero pour le test
            if (nbAbsKeyWord == 0) { //ts les mots clefs sont present
                for (int j = 0; j < 2; j++) {
                    Listdenom.add(1);
                }
                for (int j = 2; j < MotsPleinsSyn1.size(); j++) {
                    Listdenom.add(1);
                }
            }
            if (nbAbsKeyWord == 1) { //ts les mots clefs sont present
                Listdenom.add(8);
                Listdenom.add(10);
                for (int j = 2; j < MotsPleinsSyn1.size(); j++) {
                    Listdenom.add(1);
                }
            }
            if (nbAbsKeyWord >= 2) { //ts les mots clefs sont present
                Listdenom.add(16);
                Listdenom.add(20);
                for (int j = 2; j < MotsPleinsSyn1.size(); j++) {
                    Listdenom.add(1);
                }
            }

            //remplacement des mots du groupe de mots par leur mots clefs synonymes
			/**/
            ArrayList<TaggedWord> NewGroupMot = new ArrayList<TaggedWord>();
            for (int i = 0; i < MotsPleinsSyn2.size(); i++) {
                TaggedWord Mot2 = MotsPleinsSyn2.get(i);
                if (DicoSyn.containsKey(Mot2)) {
                    //Mot2 = DicoSyn.get(Mot2);
                }
                NewGroupMot.add(Mot2);
            }

            //etablissement des scores pour la presence des mots clefs
            ArrayList<TaggedWord> ListeMotsClefsPresentNonOrdonnee = new ArrayList<TaggedWord>(DicoSyn.values());
            ArrayList<TaggedWord> ListeMotsClefsPresent = new ArrayList<TaggedWord>();
            // rangement en ordre de laiste de mots clefs presents
            for (int l = 0; l < MotsPleinsSyn1.size(); l++) {
                TaggedWord motclef = MotsPleinsSyn1.get(l);
                if (ListeMotsClefsPresentNonOrdonnee.contains(motclef)) {
                    ListeMotsClefsPresent.add(motclef);
                }
            }

            ArrayList<Double> ListeScoreCorrelation = new ArrayList<Double>();	//sert a stocker le score apres correlation
            ArrayList<Double> ListeScoreDist = new ArrayList<Double>();	//sert a stocker le score de distance sans correlation
            //Cas ou il y a plus de deux mots clefs en entree
            int compteur = 0;
            int nbreKeyWord = MotsPleinsSyn1.size();
            for (int k = 0; k < MotsPleinsSyn1.size(); k++) {

                TaggedWord MotClef1 = MotsPleinsSyn1.get(k);
                if (ListeMotsClefsPresent.contains(MotClef1)) {
                    if (NewGroupMot.contains(MotClef1)) {
                        //si le mot clef est present ( si il y aun mot du groupe original qui lui est synonyme
                        int PosDsGrpMotClef1 = MotsPleinsSyn1.indexOf(MotClef1) + 1;	//position du mot clefs dans le groupe de mots clefs
                        int PosDsGrpMot = NewGroupMot.indexOf(MotClef1) + 1;	//position du mot clefs dans le groupe de mots du texte
                        if (compteur + 1 < ListeMotsClefsPresent.size()) {
                            TaggedWord MotClef2 = ListeMotsClefsPresent.get(compteur + 1);
                            int PosDsGrpMotClef2 = MotsPleinsSyn1.indexOf(MotClef2) + 1;	//position du mot clefs dans le groupe de mots clefs

							//calcul du score 
                            //double ScoreMot = (20/3*(3-Math.log(PosDsGrpMot)))/Math.abs(PosDsGrpMotClef1-PosDsGrpMotClef2);
                            int valLN = Math.abs(PosDsGrpMot - (MotsPleinsSyn1.indexOf(MotClef1) + 1)) + 1;
                            //double ScoreMot = (20)*(1-Math.log(valLN)/nbreKeyWord)/Math.abs(PosDsGrpMotClef1-PosDsGrpMotClef2);
                            double ponderationsynonymie = 1 - DicoSynDist.get(MotClef1);
                            double ScoreMotDist = (20) * (1 - Math.log(valLN) / nbreKeyWord) / Math.abs(PosDsGrpMotClef1 - PosDsGrpMotClef2);
                            double ScoreMot = (20) * (ponderationsynonymie) * (1 - Math.log(valLN) / nbreKeyWord) / Math.abs(PosDsGrpMotClef1 - PosDsGrpMotClef2);
                            ScoreMot = ScoreMot / Listdenom.get(k);
                            ListeScoreCorrelation.add(ScoreMot);
                            ScoreMotDist = ScoreMotDist / Listdenom.get(k);
                            ListeScoreDist.add(ScoreMotDist);
                        } else {
                            //double ScoreMot = (20/3*(3-Math.log(PosDsGrpMot)));
                            int valLN = Math.abs(PosDsGrpMot - (MotsPleinsSyn1.indexOf(MotClef1) + 1)) + 1;
							//double test = Math.log(PosDsGrpMot);
                            //double ScoreMot = (20)*(1-Math.log(valLN)/nbreKeyWord);
                            double ponderationsynonymie = 1 - DicoSynDist.get(MotClef1);
                            double ScoreMot = (20) * (ponderationsynonymie) * (1 - Math.log(valLN) / nbreKeyWord);
                            double ScoreMotDist = (20) * (1 - Math.log(valLN) / nbreKeyWord);
                            ScoreMot = ScoreMot / Listdenom.get(k);
                            ListeScoreCorrelation.add(ScoreMot);
                            ScoreMotDist = ScoreMotDist / Listdenom.get(k);
                            ListeScoreDist.add(ScoreMotDist);
                        }

                    } else {
                        ListeScoreCorrelation.add(0.0);
                        ListeScoreDist.add(0.0);
                    }
                    compteur++;
                } else {
                    ListeScoreCorrelation.add(0.0);
                    ListeScoreDist.add(0.0);
                }
            }

            //etablissement du facteur de ponderation pour le score de similitude du groupe de mots par rapport au groupe de mots clefs
            ArrayList<Double> ListePond = new ArrayList<Double>();
            double sommepond = 0.0;
            for (int j = 0; j < ListeScoreCorrelation.size(); j++) {
                if (j == 0) {
                    ListePond.add(2.0);
                }
                if (j == 1) {
                    ListePond.add(1.5);
                }
                if (j == 2) {
                    ListePond.add(0.7);
                }
                if (j >= 3) {
                    ListePond.add(0.35);
                }
                if (ListeScoreCorrelation.get(j) != 0.0) {
                    //sommepond = sommepond+ListePond.get(j);	//prise en compte du fait que un mot clefs peut ne pas etre present
                }
                sommepond = sommepond + ListePond.get(j);
            }

            //resultat calcul score groupe de mots
            Double ScoreTotal = 0.0;
            Double ScoreTotalDist = 0.0;
            for (int j = 0; j < MotsPleinsSyn1.size(); j++) {
                ScoreTotal = ScoreTotal + ListeScoreCorrelation.get(j) * ListePond.get(j);
                ScoreTotalDist = ScoreTotalDist + ListeScoreDist.get(j) * ListePond.get(j);
            }
            ScoreTotal = ScoreTotal / sommepond;
            ScoreTotalDist = ScoreTotalDist / sommepond;
            DistanceDist = ScoreTotalDist;
            double DistanceNonnorme = ScoreTotal;
            //normalisation pour avoir plus de proxilmit� c'est zero
            double base = 0.0;
            /**/
            for (int k = 0; k < MotsPleinsSyn1.size(); k++) {
				//int PosDsGrpMot = k+1;
                //base = base+ ((20/nbreKeyWord)*(nbreKeyWord-Math.log(PosDsGrpMot))*(ListePond.get(k)));
                base = base + 20 * (ListePond.get(k));
                //base = base+ 1*(ListePond.get(k));
            }
            base = base / sommepond;
            Distancenorme = DistanceNonnorme;//base;
            Distance = 1 - Distancenorme;

        }

        if (Distance.isNaN()) {

        }
        if (Distance != 1.0) {
            //Text= "distance entre \""+Syn1.SynToGrpWords()+"\" et \""+Syn2.SynToGrpWords()+"\" :"+Text+"r�sultat distance synonymie binaire = "+DistanceDist+"\nr�sultat distance correlation distance-synonymie pondere = "+Distancenorm�+"\n\n";
            Text = "r�sultat distance = " + DistanceDist + "\n";
            /*	System.out.println("*************** TEXT 11 ***************");
             System.out.println(Text);*/
        } else {
            Text = "";
        }
        /*   System.out.println("*************** TEXT 11 ***************");
         System.out.println(Text);*/
        return Text;
    }

    /**
     * Fonction servant � calculer la distance Semantique entre deux ensembles
     * de syntagmes
     *
     * @param EnsSyn1
     * @param EnsSyn2
     * @param DicoVoisinage
     * @return
     */
    public static Double DistanceMinEntre2EnsembleSyntagme(ArrayList<Syntagme> EnsSyn1, ArrayList<Syntagme> EnsSyn2, Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage) {

        /*          System.out.println("//////////////////////////////// F12 ////////////////////////////////");
         System.out.println("************** EnsSyn1 12***************");
         System.out.println(EnsSyn1);
         System.out.println("************** EnsSyn2 12***************");
         System.out.println(EnsSyn2);*/
        Double Distance = 0.0;
        ArrayList<Syntagme> newEnsSyn1 = new ArrayList<Syntagme>(EnsSyn1);
        ArrayList<Syntagme> newEnsSyn2 = new ArrayList<Syntagme>(EnsSyn2);
        if (EnsSyn1.size() > EnsSyn2.size()) {
            newEnsSyn1 = new ArrayList<Syntagme>(EnsSyn2);
            newEnsSyn2 = new ArrayList<Syntagme>(EnsSyn1);
        }
        DoubleMatrix2D mat = new SparseDoubleMatrix2D(newEnsSyn1.size(), newEnsSyn2.size());
        for (int i = 0; i < newEnsSyn1.size(); i++) {
            //double distMin=Double.MAX_VALUE;
            Syntagme Syn1 = newEnsSyn1.get(i);
            //Syntagme Syn2min = Syn1;
            for (int j = 0; j < newEnsSyn2.size(); j++) {
                Syntagme Syn2 = newEnsSyn2.get(j);
                //double dist = DistanceMinEntre2Syntagme(Syn1,Syn2,DicoVoisinage);
                double dist = DistanceMinEntre2SyntagmePixa(Syn1, Syn2, DicoVoisinage);
                mat.set(i, j, dist);
                if (dist == 0.0) {
					//Syn2min=Syn2;
                    //distMin=dist;
                }
            }
            /*Distance=Distance+distMin;
             if()
             newEnsSyn2.remove(Syn2min);*/
        }
        for (int i = 0; i < newEnsSyn1.size(); i++) {
            Distance = Distance + RecuperationDistMinetNettoyageMatrice(mat);
        }
        /*   System.out.println("********** Distance 12 *****************");
         System.out.println(Distance/newEnsSyn1.size());*/
        return Distance / newEnsSyn1.size();
    }

    /**
     * * Fonction servant � calculer la distance Semantique entre deux
     * ensembles de syntagmes configurer pour pixalione
     *
     * @param EnsSyn1
     * @param EnsSyn2
     * @param DicoVoisinage
     * @return
     */
    public Double DistanceMinEntre2EnsembleSyntagmePixa(ArrayList<Syntagme> EnsSyn1, ArrayList<Syntagme> EnsSyn2, Hashtable<TaggedWord, ArrayList<TaggedWord>> DicoVoisinage) {

//        System.out.println("//////////////////////////////// F13 ////////////////////////////////");
//        System.out.println("************** EnsSyn1 13 ***************");
//        System.out.println(EnsSyn1);
//        System.out.println("************** EnsSyn2 13 ***************");
//        System.out.println(EnsSyn2);
        Double Distance = 0.0;

        Enumeration<TaggedWord> enu = DicoVoisinage.keys();
        TaggedWord test = enu.nextElement();

        ArrayList<Syntagme> newEnsSyn1 = new ArrayList<Syntagme>(EnsSyn1);
        ArrayList<Syntagme> newEnsSyn2 = new ArrayList<Syntagme>(EnsSyn2);
        ArrayList<Syntagme> ListeSyntagmeProche = new ArrayList<Syntagme>();
        ArrayList<Double> ListeDistanceSyntagmeProche = new ArrayList<Double>();
        ArrayList<String> ListeMotsLesPlusProchesFormatString = new ArrayList<String>();
        if (EnsSyn1.size() > EnsSyn2.size()) {
            newEnsSyn1 = new ArrayList<Syntagme>(EnsSyn2);
            newEnsSyn2 = new ArrayList<Syntagme>(EnsSyn1);
        }
        DoubleMatrix2D mat = new SparseDoubleMatrix2D(newEnsSyn1.size(), newEnsSyn2.size());
        Syntagme Syn2min = new Syntagme();
        double distMin = Double.MAX_VALUE;
        Hashtable<TaggedWord, String> InvDicoCorresp = new Hashtable<TaggedWord, String>(this.getChampSyn2().getInvdicoCorresStrWt());

        for (int i = 0; i < newEnsSyn1.size(); i++) {
            Syntagme Syn1 = new Syntagme(newEnsSyn1.get(i));
            for (int j = 0; j < newEnsSyn2.size(); j++) {
                Syntagme Syn2 = new Syntagme(newEnsSyn2.get(j));

                double dist;
                if (!(Syn1.getStructure().length() == 1)) {//cas du groupe de mot
                    dist = DistanceMinEntre2SyntagmePixa(Syn1, Syn2, DicoVoisinage);
//                    System.out.println("******** Dist SI 13 ****************");
//                    System.out.println(dist);
                } else {
                    TaggedWord MotsPleins1 = Syn1.getMotsPleins().get(0);
                    TaggedWord MotsPleins2 = Syn2.getMotsPleins().get(0);
                    //dist = DistanceSemEuclidienneEntreMots(MotsPleins1,MotsPleins2,DicoVoisinage);
                    dist = DistanceSemEuclidienneEntreMotsPixa2(MotsPleins1, MotsPleins2, DicoVoisinage);
//                    System.out.println("******** Dist SINON 13 ****************");
//                    System.out.println(dist);
                }

                //cas du mot simple
                mat.set(i, j, dist);

                if (dist < 1) {//if(dist<1){

                    Syntagme Syn2new = new Syntagme(Syn2);
                    ArrayList<TaggedWord> MotsPleins = Syn2new.getMotsPleins();
                    for (int k = 0; k < MotsPleins.size(); k++) {
                        TaggedWord mot = new TaggedWord(MotsPleins.get(k));
                        String racine = InvDicoCorresp.get(mot);
                        mot.setToken(racine);
                        MotsPleins.set(k, mot);
                    }
                    if (!ListeSyntagmeProche.contains(Syn2)) {
                        ListeSyntagmeProche.add(Syn2);
                        ListeDistanceSyntagmeProche.add(dist);
                        ListeMotsLesPlusProchesFormatString.add(Syn2.SynToGrpWordsNoTag());
                    }

                }
                if (dist < distMin) {
                    Syn2min = new Syntagme(Syn2);
                    distMin = dist;
                }
            }
        }
        ArrayList<TaggedWord> MotsPleins = Syn2min.getMotsPleins();
        for (int j = 0; j < MotsPleins.size(); j++) {
            TaggedWord mot = MotsPleins.get(j);
            String racine = InvDicoCorresp.get(mot);
            mot.setToken(racine);
        }
        this.setMotsLesPlusProchesSyntagme(Syn2min);
        this.setMotsLesPlusProchesFormatString(Syn2min.SynToGrpWordsNoTag());

        this.setListeDistMotsLesPlusProchesSyntagme(ListeDistanceSyntagmeProche);
        this.setListeMotsProches(ListeSyntagmeProche);
        this.setListeMotsLesPlusProchesFormatString(ListeMotsLesPlusProchesFormatString);

        for (int i = 0; i < newEnsSyn1.size(); i++) {
//                    System.out.println("Distance: "+Distance);
            Distance = Distance + RecuperationDistMinetNettoyageMatrice(mat);
            /*   System.out.println("********** Distance 13 POUR ***********");
             System.out.println(Distance);*/
        }

//        System.out.println("********** Distance 13 *****************");
//         System.out.println("Distance"+ Distance);
//         System.out.println("newEnsSyn1.size()"+newEnsSyn1.size());
         
        return Distance / newEnsSyn1.size();
    }

    /**
     * Fonction permettant de trouver le plus petit element d'un ensemble
     *
     * @param matrice
     * @return
     */
    public static double RecuperationDistMinetNettoyageMatrice(DoubleMatrix2D matrice) {

//           System.out.println("//////////////////////////////// F14 ////////////////////////////////");
//                System.out.println("************** Martice 1 14 ***************");
//                System.out.println(matrice);
        double min = Double.MAX_VALUE;
        int rowPos = -1;
        int colPos = -1;
        for (int row = 0; row < matrice.rows(); row++) {
            for (int col = 0; col < matrice.columns(); col++) {
                double val = matrice.get(row, col);
                if (val <= min && val>=0) {
                    min = val;
                    rowPos = row;
                    colPos = col;
                  //  if (min < 0 ) System.out.println(min);
                } 
            }
//                        System.out.println(matrice);
        }
        //nettoyage
        for (int row = 0; row < matrice.rows(); row++) {
            matrice.set(row, colPos, 1.0);
            if (row == rowPos) {
                for (int col = 0; col < matrice.columns(); col++) {
                    matrice.set(row, col, 1.0);
                }
            }
        }
//        System.out.println("***************************** MIN 14 ************* = " +min);
//        System.out.println("Matrice 14 = ");
//       System.out.println(matrice);

        return min;

    }

    public DictionnaireSyn getDicoSyn() {
        return DicoSyn;
    }

    public void setDicoSyn(DictionnaireSyn dicoSyn) {
        DicoSyn = dicoSyn;
    }

    public ChampSyntagmatique getChampSyn2() {
        return ChampSyn2;
    }

    public void setChampSyn2(ChampSyntagmatique champSyn2) {
        ChampSyn2 = champSyn2;
    }

    public ChampSyntagmatique getChampSyn1() {
        return ChampSyn1;
    }

    public void setChampSyn1(ChampSyntagmatique champSyn1) {
        ChampSyn1 = champSyn1;
    }

    public Syntagme getMotsLesPlusProchesSyntagme() {
        return MotsLesPlusProchesSyntagme;
    }

    public void setMotsLesPlusProchesSyntagme(Syntagme motsLesPlusProchesSyntagme) {
        MotsLesPlusProchesSyntagme = motsLesPlusProchesSyntagme;
    }

    public Double getDistMotsLesPlusProchesSyntagme() {
        return DistMotsLesPlusProchesSyntagme;
    }

    public void setDistMotsLesPlusProchesSyntagme(Double distMotsLesPlusProchesSyntagme) {
        DistMotsLesPlusProchesSyntagme = distMotsLesPlusProchesSyntagme;
    }

    public String getMotsLesPlusProchesFormatString() {
        return MotsLesPlusProchesFormatString;
    }

    public void setMotsLesPlusProchesFormatString(String motsLesPlusProchesFormatString) {
        MotsLesPlusProchesFormatString = motsLesPlusProchesFormatString;
    }

    public ArrayList<Double> getListeDistMotsLesPlusProchesSyntagme() {
        return ListeDistMotsLesPlusProchesSyntagme;
    }

    public void setListeDistMotsLesPlusProchesSyntagme(
            ArrayList<Double> listeDistMotsLesPlusProchesSyntagme) {
        ListeDistMotsLesPlusProchesSyntagme = listeDistMotsLesPlusProchesSyntagme;
    }

    public ArrayList<Syntagme> getListeMotsProches() {
        return ListeMotsProches;
    }

    public void setListeMotsProches(ArrayList<Syntagme> listeMotsProches) {
        ListeMotsProches = listeMotsProches;
    }

    public ArrayList<String> getListeMotsLesPlusProchesFormatString() {
        return ListeMotsLesPlusProchesFormatString;
    }

    public void setListeMotsLesPlusProchesFormatString(
            ArrayList<String> listeMotsLesPlusProchesFormatString) {
        ListeMotsLesPlusProchesFormatString = listeMotsLesPlusProchesFormatString;
    }

}
