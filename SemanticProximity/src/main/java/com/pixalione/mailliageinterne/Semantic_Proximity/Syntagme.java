package com.pixalione.mailliageinterne.Semantic_Proximity;



//import edu.stanford.nlp.ling.WordTag;

import com.pixalione.mailliageinterne.PixTagger.TaggedWord;

import java.util.ArrayList;

public class Syntagme {
	public ArrayList<TaggedWord> MotsPleins =new ArrayList<TaggedWord>();
	public ArrayList<TaggedWord> MotsDeFonctions = new ArrayList<TaggedWord>();
	public String Structure="";
	public Integer StructureSyntaxique= 0;
	
	
	
	public Syntagme() {
		super();
		
	}
	public Syntagme(Syntagme syn) {
		super();
		this.setMotsPleins(new ArrayList<TaggedWord>(syn.getMotsPleins()));
		this.setMotsDeFonctions  (new ArrayList<TaggedWord>(syn.getMotsDeFonctions()));
		this.setStructureSyntaxique ( syn.getStructureSyntaxique());
		this.setStructure(syn.getStructure());
	}
	
	
	public Syntagme(ArrayList<TaggedWord> motsPleins,
			ArrayList<TaggedWord> motsDeFonctions, Integer structureSyntaxique, String grpMot) {
		super();
		this.setMotsPleins(motsPleins);
		this.setMotsDeFonctions(motsDeFonctions);
		this.setStructureSyntaxique(structureSyntaxique);
		
	}
	public Syntagme(ArrayList<TaggedWord> motsPleins,
			ArrayList<TaggedWord> motsDeFonctions, Integer structureSyntaxique,String struct , String grpMot) {
		super();
		this.setMotsPleins(motsPleins);
		this.setMotsDeFonctions(motsDeFonctions);
		this.setStructureSyntaxique(structureSyntaxique);
		this.setStructure(struct);
	}
	
	public Syntagme(ArrayList<TaggedWord> motsPleins,
			ArrayList<TaggedWord> motsDeFonctions, Integer structureSyntaxique) {
		super();
		this.setMotsPleins(motsPleins);
		this.setMotsDeFonctions(motsDeFonctions);
		this.setStructureSyntaxique(structureSyntaxique);
		
	}
	
	public ArrayList<TaggedWord> getMotsPleins() {
		return MotsPleins;
	}
	public void setMotsPleins(ArrayList<TaggedWord> motsPleins) {
		MotsPleins = motsPleins;
	}
	public ArrayList<TaggedWord> getMotsDeFonctions() {
		return MotsDeFonctions;
	}
	public void setMotsDeFonctions(ArrayList<TaggedWord> motsDeFonctions) {
		MotsDeFonctions = motsDeFonctions;
	}
	public Integer getStructureSyntaxique() {
		return StructureSyntaxique;
	}
	public void setStructureSyntaxique(Integer structureSyntaxique) {
		StructureSyntaxique = structureSyntaxique;
	}
	
        // Stocker les mots pleins et leur tag et les mots de fonction et leur tag dans la meme ligne
	public String SynToGrpWords(){
		String rslt = "";
		if(Structure.equals("")){
			for (int i=0;i<MotsPleins.size();i++){
				rslt = rslt+ MotsPleins.get(i).getTokenToString()+" ";
				//if((MotsDeFonctions.size()<i)&(MotsDeFonctions.size()!=0)){
				//	rslt = rslt+ MotsDeFonctions.get(i).word()+" ";				
				//}
			}
			for (int i=0;i<MotsDeFonctions.size();i++){
				rslt = rslt+ MotsDeFonctions.get(i).getTokenToString()+" ";
			}

                }
		else{
			int CompteurMotsPleins = 0;
			int CompteurMotsDeFonctions = 0;
			String[] struct = this.getStructure().split(" ");
			for(int i=0;i<struct.length;i++){
				String tag = struct[i];
                                
				if(tag.equals("V")||tag.equals("A")||tag.equals("N")){
					rslt = rslt+ MotsPleins.get(CompteurMotsPleins).getTokenToString()+" ";
					CompteurMotsPleins++;
				}
				else{
					rslt = rslt+ MotsDeFonctions.get(CompteurMotsDeFonctions).getTokenToString()+" ";
					CompteurMotsDeFonctions++;
				}
			}
		}
		return rslt;
		          
	}
	// Stocker les mots pleins  et les mots de fonction dans la meme ligne
	public String SynToGrpWordsNoTag(){
		/*String rslt = "";
		ArrayList<WordTag> MotsPleins = this.getMotsPleins();
		ArrayList<WordTag> MotsDeFonctions = this.getMotsDeFonctions();
		if(Structure.equals("")){
			for (int i=0;i<MotsPleins.size();i++){
				rslt = rslt+ MotsPleins.get(i).toString()+" ";
				//if((MotsDeFonctions.size()<i)&(MotsDeFonctions.size()!=0)){
				//	rslt = rslt+ MotsDeFonctions.get(i).word()+" ";				
				//}
			}
			for (int i=0;i<MotsDeFonctions.size();i++){
				rslt = rslt+ MotsDeFonctions.get(i).word()+" ";
			}
		}
		else{
			int CompteurMotsPleins = 0;
			int CompteurMotsDeFonctions = 0;
			String[] struct = this.getStructure().split(" ");
			for(int i=0;i<struct.length;i++){
				String tag = struct[i];
				if(tag.equals("V")||tag.equals("A")||tag.equals("N")){
					rslt = rslt+ MotsPleins.get(CompteurMotsPleins).word()+" ";
					CompteurMotsPleins++;
				}
				else{
					rslt = rslt+ MotsDeFonctions.get(CompteurMotsDeFonctions).word()+" ";
					CompteurMotsDeFonctions++;
				}
			}
		}*/
		String rslt = SynToGrpWords();
		rslt=rslt.replace("/ADV", "").replace("/V", "").replace("/N", "").replace("/P", "").replace("/D", "").replace("/A", "");
		return rslt;
		
	}
	// Stocker les lemmes des mots pleins et les lemmes des mots de fonction dans la meme ligne
	public String SynToWtGrpWords(){
		String rslt = "";
		if(Structure.equals("")){
			for (int i=0;i<MotsPleins.size();i++){
				rslt = rslt+ MotsPleins.get(i).getLemme()+" ";
				//if((MotsDeFonctions.size()<i)&(MotsDeFonctions.size()!=0)){
				//	rslt = rslt+ MotsDeFonctions.get(i).word()+" ";				
				//}
			}
			for (int i=0;i<MotsDeFonctions.size();i++){
				rslt = rslt+ MotsDeFonctions.get(i).getLemme()+" ";
			}
		}
		else{
			int CompteurMotsPleins = 0;
			int CompteurMotsDeFonctions = 0;
			String[] struct = this.getStructure().split(" ");
			for(int i=0;i<struct.length;i++){
				String tag = struct[i];
				if(tag.equals("V")||tag.equals("A")||tag.equals("N")){
					rslt = rslt+ MotsPleins.get(CompteurMotsPleins).getLemme()+" ";
					CompteurMotsPleins++;
				}
				else{
					rslt = rslt+ MotsDeFonctions.get(CompteurMotsDeFonctions).getLemme()+" ";
					CompteurMotsDeFonctions++;
				}
			}
		}
		
		return rslt;
		
	}
	
	//amelioration de l'egalit�
        //equals() permet de tester l'égalité de deux objets d'un point de vue sémantique.
	/**/ @Override public boolean equals(Object other) {
	        boolean result = false;
	        if (other instanceof Syntagme) {
	        	Syntagme that = (Syntagme) other;
	            result = (that.canEqual(this) && this.getMotsDeFonctions().equals(that.getMotsDeFonctions()) && this.getMotsPleins().equals(that.getMotsPleins())&& this.getStructureSyntaxique() == that.getStructureSyntaxique());
	        }
	        return result;
	    }

	    @Override public int hashCode() {
         	        return (41 *(41 + getMotsDeFonctions().hashCode())) + getMotsPleins().hashCode()+getStructureSyntaxique();
	    }

	    public boolean canEqual(Object other) {
	        return (other instanceof Syntagme);
	    }

		public String getStructure() {
			return Structure;
		}

		public void setStructure(String structure) {
			Structure = structure;
		}
                

    
}
