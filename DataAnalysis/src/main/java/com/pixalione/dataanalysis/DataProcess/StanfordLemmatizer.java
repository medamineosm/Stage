package com.pixalione.dataanalysis.DataProcess;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;
import java.util.Properties;

/**
 * Created by OUASMINE Mohammed Amine on 09/05/2016.
 */
public class StanfordLemmatizer {


    public static void lemmatize(String documentText)
    {
        StanfordCoreNLP pipeline = new StanfordCoreNLP(new Properties(){{
            setProperty("annotators", "tokenize,ssplit,pos,lemma");
        }});

        Annotation tokenAnnotation = new Annotation("wedding");
        pipeline.annotate(tokenAnnotation);  // necessary for the LemmaAnnotation to be set.
        List<CoreMap> list = tokenAnnotation.get(CoreAnnotations.SentencesAnnotation.class);
        String tokenLemma = list
                .get(0).get(CoreAnnotations.TokensAnnotation.class)
                .get(0).get(CoreAnnotations.LemmaAnnotation.class);

    }

    public static void main(String[] args){
        StanfordLemmatizer.lemmatize("am looking for a lemmatisation " +
                "implementation for English in Java. I found" +
                " a few already, but I need something that d" +
                "oes not need to much memory to run (1 GB top)" +
                ". Thanks. I do not need a stemmer.");
    }
}
