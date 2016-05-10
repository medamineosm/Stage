/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pixalione.mailliageinterne.PixTagger;

/** This class is a structure to stock information of a Tagged Word,
 * These informations are :
 *   - Token : the initial word
 *   - Tag   : the Part of speech tag of the word / The tag
 *   - Lemme : the lemma of the word
 *
 * @author Pixalione2
 */

    
public class TaggedWord {
    
    private String token;
    private String tag;
    private String lemme;

    

    /** Default Constructor
     *
     */
        public TaggedWord() {
    }
    


    /** First Customized Constructor
     * This Constructor takes three parameters to set the values of the three attributes
     *
     * @param token The token which is the word in its initial form
     * @param tag The tag is the result of the Part Of Speech Tagging (POS Tagging) - ie:ADJ
     * @param lemme The lemma is the citation form of the word - ie: the lemma of a noun is the singular
     */
        public TaggedWord(String token, String tag, String lemme) {
        this.token = token;
        this.tag = tag;
        this.lemme = lemme;
    }


    /** Second Customized Constructor
     * This Constructor takes only one parameter - a Tagged-Lemmatized word
     *
     * @param lemmatised The parameter is a Tagged-Lemmatized word ie: like/V (lemme/short_form_tag)
     */
        public TaggedWord(String lemmatised) {
        String[] fen = lemmatised.split("/");
        this.token=fen[0];
        this.lemme=fen[0];
        if (fen[1].equals("V"))
            this.tag="VER";
        if (fen[1].equals("N"))
            this.tag="NOM";
        if (fen[1].equals("A"))
            this.tag="ADJ";
    }
    

    /** Third Customized Constructor
     * This constructor takes a TaggedWord as parameter to cnstruct a new TaggedWord
     *
     * @param tw An object of type TaggedWord
     */
        public TaggedWord(TaggedWord tw) {
        this.token = tw.token;
        this.tag = tw.tag;
        this.lemme = tw.lemme;
    }

    
    
    
    
    // 

    /** Return the short forme of the tag
     *
     * @return the short forme of the tag (String)
     */
        public String tagformemin(){
        return tag.charAt(0)+"";
    }
    
    // 

    /** Return the lemme with the tag in short form (lemme/tag_short_form)
     * ie : marcher/V
     *
     * @return Lemme+Tag
     */
        public String getLemmeToString(){
        if (tag.length()>2){
        if (tag.substring(0, 3).equals("ADV"))
            return lemme+"/"+tag.substring(0, 3);
        }
        return lemme+"/"+tag.charAt(0);
    }
    
    // 

    /** Return the token with the tag in short form (token/tag_short_form)
     * ie : marchait/V
     *
     * @return Token+Tag
     */
        public String getTokenToString(){
        if (tag.length()>2){
        if (tag.substring(0, 3).equals("ADV"))
            return token+"/"+tag.substring(0, 3);
        }
        return token+"/"+tag.charAt(0);
    }
    
    
    
    /**
     * Overriding of equals to allow the comparision of 2 objects 
     */
    
        @Override
    public boolean equals(Object object)
    {
        /*boolean isEqual= false;
        boolean vari=false;
        if (object != null && object instanceof TaggedWord)
        {
            String[] tab1= (this.lemme).split("\\|");
            String[] tab2= (((TaggedWord) object).lemme).split("\\|");
            for (int i=0;i<tab1.length;i++){
                for (int j=0;j<tab2.length;j++){
                    if (tab1[i].equals(tab2[j])){
                        vari=true;
                        break;
                    }
                }
            }
            
            isEqual = ((this.tag.equals(((TaggedWord) object).tag)&&this.lemme.equals(((TaggedWord) object).lemme)||vari)||(this.tag.equals(((TaggedWord) object).tag)&&this.token.equals(((TaggedWord) object).token)));
            //isEqual = (this.tag.equals(((TaggedWord) object).tag)&&this.lemme.equals(((TaggedWord) object).lemme));
        }*/
        //return this.tag.equals(((TaggedWord) object).tag)&&
        return  this.lemme.equals(((TaggedWord) object).lemme);
    }

    
    /**
     * Overriding of hashCode to allow the comparision of 2 objects 
     */
    @Override
    public int hashCode() {
        return this.lemme.hashCode();//+this.tag.hashCode();
        //return this.tag.length();
    }



    /** Token Getter
     *
     * @return The value of the token field
     */
    public String getToken() {
        return token;
    }

    /** Token Setter
     *
     * @param token The value to set to the Token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /** Tag Getter
     *
     * @return The value of the tag field
     */
    public String getTag() {
        return tag;
    }
    
    public String getTag2() {
        return tag;
    }
    
    public String getTagShortFomre() {
        if (tag.equals("ADV"))
                return "X";
        if ((tag.equals("PUN"))||(tag.equals("SENT")))
                return "Y";
        return tag.substring(0,1);
    }

    /** Tag Setter
     *
     * @param tag The value to set to the Tag
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /** Lemme Getter
     *
     * @return The value of the lemme field
     */
    public String getLemme() {
        return lemme;
    }

    /** Lemme Setter
     *
     * @param lemme The value to set to the Lemme
     */
    public void setLemme(String lemme) {
        this.lemme = lemme;
    }
    
        @Override
    public String toString() {
        return getLemme();
    }
}
