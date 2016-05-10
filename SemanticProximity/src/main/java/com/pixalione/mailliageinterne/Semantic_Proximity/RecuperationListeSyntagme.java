package com.pixalione.mailliageinterne.Semantic_Proximity;

import com.pixalione.mailliageinterne.PixTagger.TaggedWord;

import java.util.ArrayList;

//import edu.stanford.nlp.ling.WordTag;

public class RecuperationListeSyntagme {
    
	private ArrayList<String> StructSyn1 = new ArrayList<String>(Structure.INSTANCE.StructSyn1);
	private ArrayList<String> StructSyn2 = new ArrayList<String>(Structure.INSTANCE.StructSyn2);
	private ArrayList<String> StructSyn3 = new ArrayList<String>(Structure.INSTANCE.StructSyn3);
	private ArrayList<String> StructSyn4 = new ArrayList<String>(Structure.INSTANCE.StructSyn4);
	private ArrayList<Syntagme> ListeSyntagme = new ArrayList<Syntagme>();
	private String StructureKeyWord = "";
	public RecuperationListeSyntagme(ArrayList<TaggedWord> phrase) {
            
		super();
 
		//ajout bizarrz
				/*this.StructSyn1.add("V");
				this.StructSyn1.add("N");
				this.StructSyn1.add("A");
				this.StructSyn1.add("D N");*/
				/*this.StructSyn1.add("N V");
				this.StructSyn1.add("N N");		//structure mauvaise, mise car le tagging n'est pas parfait
				this.StructSyn3.add("V A");
				this.StructSyn1.add("V N");
				this.StructSyn3.add("N A");
				this.StructSyn3.add("A N");*/
				
				//this.StructSyn4.add("P D N");
				
				/*this.StructSyn4.add("V P N");
				this.StructSyn1.add("V D N");
				this.StructSyn2.add("V N A");
				this.StructSyn2.add("A P N");
				this.StructSyn1.add("N N N");		//structure mauvaise, mise car le tagging n'est pas parfait
				this.StructSyn1.add("N N A");		//structure mauvaise, mise car le tagging n'est pas parfait
				this.StructSyn4.add("N A N");
				this.StructSyn4.add("N P N");*/
				
				/*this.StructSyn4.add("N N P N");
				this.StructSyn2.add("N A P N");
				this.StructSyn3.add("N D N A");
				this.StructSyn4.add("N P N A");
				this.StructSyn2.add("V D N A");
				this.StructSyn4.add("V P D N");*/
				//this.StructSyn4.add("P N");
				//this.StructSyn4.add("D N P N");
				//this.StructSyn4.add("V D N P N");
		
		RecuperationSyntagme(phrase);
	}

	public RecuperationListeSyntagme(ArrayList<TaggedWord> phrase,ArrayList<TaggedWord> GroupeMotClef) {

		super();

		//r�cup�ration Structure des mots clefs cherch�s
		String struct = "";
		for(int i =0;i<GroupeMotClef.size();i++){
			struct = struct+GroupeMotClef.get(i).getTagShortFomre();
			if(i<GroupeMotClef.size()-1){
				struct = struct+" ";
			}
		}
 
		
		//ajout bizarrz
				/*this.StructSyn1.add("V");
				this.StructSyn1.add("N");
				this.StructSyn1.add("A");
				this.StructSyn1.add("D N");*/
				/*this.StructSyn1.add("N V");
				this.StructSyn1.add("N N");		//structure mauvaise, mise car le tagging n'est pas parfait
				this.StructSyn3.add("V A");
				this.StructSyn1.add("V N");
				this.StructSyn3.add("N A");
				this.StructSyn3.add("A N");*/
				
				//this.StructSyn4.add("P D N");
				
				/*this.StructSyn4.add("V P N");
				this.StructSyn1.add("V D N");
				this.StructSyn2.add("V N A");
				this.StructSyn2.add("A P N");
				this.StructSyn1.add("N N N");		//structure mauvaise, mise car le tagging n'est pas parfait
				this.StructSyn1.add("N N A");		//structure mauvaise, mise car le tagging n'est pas parfait
				this.StructSyn4.add("N A N");
				this.StructSyn4.add("N P N");*/
				
				/*this.StructSyn4.add("N N P N");
				this.StructSyn2.add("N A P N");
				this.StructSyn3.add("N D N A");
				this.StructSyn4.add("N P N A");
				this.StructSyn2.add("V D N A");
				this.StructSyn4.add("V P D N");*/
				//this.StructSyn4.add("P N");
				//this.StructSyn4.add("D N P N");
				//this.StructSyn4.add("V D N P N");
		
		ArrayList<String> ensembleStruct = new ArrayList<String>();
		ensembleStruct.addAll(StructSyn1);
		ensembleStruct.addAll(StructSyn2);
		ensembleStruct.addAll(StructSyn3);
		ensembleStruct.addAll(StructSyn4);

		if(!ensembleStruct.contains(struct)){
                 
			if(struct.length()<=3)
                            this.StructSyn1.add(struct);
                        if(struct.length()==5)
                            this.StructSyn2.add(struct);
                        if(struct.length()==7)
                            this.StructSyn3.add(struct);
                        if(struct.length()>7)    
                            this.StructSyn4.add(struct);
			this.setStructureKeyWord(struct);
		}
                if (struct.length()>7)
                    this.setStructureKeyWord(struct);
             
		if(struct.length()==1){
			RecuperationSyntagme(phrase,struct);
		}else{
			RecuperationSyntagme(phrase);
		}
	}

	public RecuperationListeSyntagme(ArrayList<TaggedWord> phrase,String Struct) {

		super();
             
		//r�cup�ration Structure des mots clefs cherch�s
		String struct = Struct;
		
		
		
		//ajout bizarrz
		/*this.StructSyn1.add("V");
		this.StructSyn1.add("N");
		this.StructSyn1.add("A");
		this.StructSyn1.add("D N");*/
		/*this.StructSyn1.add("N V");
		this.StructSyn1.add("N N");		//structure mauvaise, mise car le tagging n'est pas parfait
		this.StructSyn3.add("V A");
		this.StructSyn1.add("V N");
		this.StructSyn3.add("N A");
		this.StructSyn3.add("A N");*/
		
		//this.StructSyn4.add("P D N");
		
		/*this.StructSyn4.add("V P N");
		this.StructSyn1.add("V D N");
		this.StructSyn2.add("V N A");
		this.StructSyn2.add("A P N");
		this.StructSyn1.add("N N N");		//structure mauvaise, mise car le tagging n'est pas parfait
		this.StructSyn1.add("N N A");		//structure mauvaise, mise car le tagging n'est pas parfait
		this.StructSyn4.add("N A N");
		this.StructSyn4.add("N P N");*/
		
		/*this.StructSyn4.add("N N P N");
                this.StructSyn4.add("N P N N");
		this.StructSyn2.add("N A P N");
		this.StructSyn3.add("N D N A");
		this.StructSyn4.add("N P N A");
		this.StructSyn2.add("V D N A");
		this.StructSyn4.add("V P D N");
		//this.StructSyn4.add("P N");*/
		//this.StructSyn4.add("D N P N");
		//this.StructSyn4.add("V D N P N");
		
		ArrayList<String> ensembleStruct = new ArrayList<String>();
		ensembleStruct.addAll(StructSyn1);
		ensembleStruct.addAll(StructSyn2);
		ensembleStruct.addAll(StructSyn3);
		ensembleStruct.addAll(StructSyn4);
		
		if(!ensembleStruct.contains(struct)){
                    
                    if(struct.length()<=3)
			this.StructSyn1.add(struct);
                    if(struct.length()==5)
                        this.StructSyn2.add(struct);
                    if(struct.length()==7)
                        this.StructSyn3.add(struct);
                    if(struct.length()>7)    
                        this.StructSyn4.add(struct);
		}
                if(struct.length()>7)
                    this.setStructureKeyWord(struct);
                
		if(struct.length()==1){
			RecuperationSyntagme(phrase,struct);
		}else{
			RecuperationSyntagme(phrase);
		}
	}

	
	public void RecuperationSyntagme(ArrayList<TaggedWord> phrase) {

		ArrayList<Syntagme> ListeSyn = new ArrayList<Syntagme>();
		// Taille 2
		int Taille = 2;
		for (int i = 0; i + Taille - 1 < phrase.size(); i++) {
			ArrayList<TaggedWord> MotPleins = new ArrayList<TaggedWord>();
			ArrayList<TaggedWord> MotsDeFonctions = new ArrayList<TaggedWord>();
			TaggedWord wTmot1 = phrase.get(i);
			TaggedWord wTmot2 = phrase.get(i + 1);
			MotPleins.add(wTmot1);
			MotPleins.add(wTmot2);
			String Struct = wTmot1.getTagShortFomre()+ " " + wTmot2.getTagShortFomre();
			if (StructSyn1.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 1);
				Syt.setStructure(Struct);
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}
			/*if (StructSyn2.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 2);
				Syt.setStructure(Struct);
                                
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}
			if (StructSyn3.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 3);
				Syt.setStructure(Struct);
                                
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}
			if (StructSyn4.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 4);
				Syt.setStructure(Struct);
                                
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}*/
		}
		// Taille 3
		Taille = 3;
		for (int i = 0; i + Taille - 1 < phrase.size(); i++) {

			ArrayList<TaggedWord> MotPleins = new ArrayList<TaggedWord>();
			ArrayList<TaggedWord> MotsDeFonctions = new ArrayList<TaggedWord>();
			TaggedWord wTmot1 = phrase.get(i);
			TaggedWord wTmot2 = phrase.get(i + 1);
			TaggedWord wTmot3 = phrase.get(i + 2);
			MotPleins.add(wTmot1);
			MotPleins.add(wTmot2);
			MotPleins.add(wTmot3);
			for (int k = 0; k < MotPleins.size(); k++) {
				TaggedWord mot = MotPleins.get(k);
				if ((!mot.getTagShortFomre().equals("N") & !mot.getTagShortFomre().equals("A") & !mot
						.getTagShortFomre().equals("V"))) {
					MotsDeFonctions.add(mot);
				}
			}
			for (int k = 0; k < MotsDeFonctions.size(); k++) {
				TaggedWord mot = MotsDeFonctions.get(k);
				MotPleins.remove(mot);
			}

			String Struct = wTmot1.getTagShortFomre()+ " " + wTmot2.getTagShortFomre()+ " "
					+ wTmot3.getTagShortFomre();
                        
			/*if (StructSyn1.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 1);
				Syt.setStructure(Struct);
                                
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}*/
			if (StructSyn2.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 2);
				Syt.setStructure(Struct);
                               
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}
			/*if (StructSyn3.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 3);
				Syt.setStructure(Struct);
                          
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}
			if (StructSyn4.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 4);
				Syt.setStructure(Struct);
                                
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}*/
		}
		// Taille 4
		Taille = 4;
		for (int i = 0; i + Taille - 1 < phrase.size(); i++) {

			ArrayList<TaggedWord> MotPleins = new ArrayList<TaggedWord>();
			ArrayList<TaggedWord> MotsDeFonctions = new ArrayList<TaggedWord>();
			TaggedWord wTmot1 = phrase.get(i);
			TaggedWord wTmot2 = phrase.get(i + 1);
			TaggedWord wTmot3 = phrase.get(i + 2);
			TaggedWord wTmot4 = phrase.get(i + 3);
			MotPleins.add(wTmot1);
			MotPleins.add(wTmot2);
			MotPleins.add(wTmot3);
			MotPleins.add(wTmot4);
			for (int k = 0; k < MotPleins.size(); k++) {
				TaggedWord mot = MotPleins.get(k);
				if ((!mot.getTagShortFomre().equals("N") & !mot.getTagShortFomre().equals("A") & !mot
						.getTagShortFomre().equals("V"))) {
					MotsDeFonctions.add(mot);
				}
			}
			for (int k = 0; k < MotsDeFonctions.size(); k++) {
				TaggedWord mot = MotsDeFonctions.get(k);
				MotPleins.remove(mot);
			}

			String Struct = wTmot1.getTagShortFomre()+ " " + wTmot2.getTagShortFomre()+ " "
					+ wTmot3.getTagShortFomre()+ " " + wTmot4.getTagShortFomre();
                        
			/*if (StructSyn1.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 1);
				Syt.setStructure(Struct);
                               
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}if (StructSyn2.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 2);
				Syt.setStructure(Struct);
                               
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}*/
			if (StructSyn3.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 3);
				Syt.setStructure(Struct);
                                
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}
			/*if (StructSyn4.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 4);
				Syt.setStructure(Struct);
                             
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}*/
		}
		// Taille 5
		/*Taille = 5;
		for (int i = 0; i + Taille - 1 < phrase.size(); i++) {

			ArrayList<TaggedWord> MotPleins = new ArrayList<TaggedWord>();
			ArrayList<TaggedWord> MotsDeFonctions = new ArrayList<TaggedWord>();
			TaggedWord wTmot1 = phrase.get(i);
			TaggedWord wTmot2 = phrase.get(i + 1);
			TaggedWord wTmot3 = phrase.get(i + 2);
			TaggedWord wTmot4 = phrase.get(i + 3);
			TaggedWord wTmot5 = phrase.get(i + 4);
			MotPleins.add(wTmot1);
			MotPleins.add(wTmot2);
			MotPleins.add(wTmot3);
			MotPleins.add(wTmot4);
			MotPleins.add(wTmot5);
			for (int k = 0; k < MotPleins.size(); k++) {
				TaggedWord mot = MotPleins.get(k);
				if ((!mot.getTagShortFomre().equals("N") & !mot.getTagShortFomre().equals("A") & !mot
						.getTagShortFomre().equals("V"))) {
					MotsDeFonctions.add(mot);
				}
			}
			for (int k = 0; k < MotsDeFonctions.size(); k++) {
				TaggedWord mot = MotsDeFonctions.get(k);
				MotPleins.remove(mot);
			}

			String Struct = wTmot1.getTagShortFomre()+ " " + wTmot2.getTagShortFomre()+ " "
					+ wTmot3.getTagShortFomre()+ " " + wTmot4.getTagShortFomre()+ " " + wTmot5.getTagShortFomre();
                       /
			if (StructSyn1.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 1);
				Syt.setStructure(Struct);
                               
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}
			if (StructSyn2.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 2);
				Syt.setStructure(Struct);
                               
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}
			if (StructSyn3.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 3);
				Syt.setStructure(Struct);
                                
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}
			if (StructSyn4.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 4);
				Syt.setStructure(Struct);
                                
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}
		}*/
		//taille>5
                if (this.getStructureKeyWord().length()>0)
                   
		if(this.getStructureKeyWord().length()>7){
			String[] struct = this.getStructureKeyWord().split(" ");
			Taille = struct.length;
			for (int i = 0; i + Taille - 1 < phrase.size(); i++) {

				String Struct ="";
				ArrayList<TaggedWord> MotPleins = new ArrayList<TaggedWord>();
				ArrayList<TaggedWord> MotsDeFonctions = new ArrayList<TaggedWord>();
				for(int j=0;j<Taille;j++){
					TaggedWord wTmot = phrase.get(i+j);
					MotPleins.add(wTmot);
					Struct = Struct +wTmot.getTagShortFomre();
					if(j+1<Taille){
						Struct = Struct + " ";
					}
				}
                                
				for (int k = 0; k < MotPleins.size(); k++) {
					TaggedWord mot = MotPleins.get(k);
					if ((!mot.getTagShortFomre().equals("N") & !mot.getTagShortFomre().equals("A") & !mot
							.getTagShortFomre().equals("V"))) {
						MotsDeFonctions.add(mot);
					}
				}
				for (int k = 0; k < MotsDeFonctions.size(); k++) {
					TaggedWord mot = MotsDeFonctions.get(k);
					MotPleins.remove(mot);
				}

				 
				if (StructSyn4.contains(Struct)) {
					Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 4);
					Syt.setStructure(Struct);
                                        
					if (!ListeSyn.contains(Syt)) {
						ListeSyn.add(Syt);
					}
				}
			}
		}

                
		this.setListeSyntagme(ListeSyn);

	}

	public void RecuperationSyntagme(ArrayList<TaggedWord> phrase, String Tag) {
		ArrayList<Syntagme> ListeSyn = new ArrayList<Syntagme>();
		// Taille 1
		int Taille = 1;
		for (int i = 0; i + Taille - 1 < phrase.size(); i++) {
			ArrayList<TaggedWord> MotPleins = new ArrayList<TaggedWord>();
			ArrayList<TaggedWord> MotsDeFonctions = new ArrayList<TaggedWord>();
			TaggedWord wTmot1 = phrase.get(i);
			MotPleins.add(wTmot1);
			String Struct = wTmot1.getTagShortFomre();
			if (StructSyn1.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 1);
				Syt.setStructure(Struct);
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}
			if (StructSyn2.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 2);
				Syt.setStructure(Struct);
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}
			if (StructSyn3.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 3);
				Syt.setStructure(Struct);
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}
			if (StructSyn4.contains(Struct)) {
				Syntagme Syt = new Syntagme(MotPleins, MotsDeFonctions, 4);
				Syt.setStructure(Struct);
				if (!ListeSyn.contains(Syt)) {
					ListeSyn.add(Syt);
				}
			}
		}
		
		this.setListeSyntagme(ListeSyn);

	}

	
	public ArrayList<Syntagme> getListeSyntagme() {
		return ListeSyntagme;
	}

	public void setListeSyntagme(ArrayList<Syntagme> listeSyntagme) {
		ListeSyntagme = listeSyntagme;
	}

	public String getStructureKeyWord() {
		return StructureKeyWord;
	}

	public void setStructureKeyWord(String structureKeyWord) {
		StructureKeyWord = structureKeyWord;
	}

}
