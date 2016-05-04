package com.pixalione.dataanalysis.StringMatringAlgorithme;

import info.debatty.java.stringsimilarity.JaroWinkler;

/**
 * Created by OUASMINE Mohammed Amine on 04/05/2016.
 */
public abstract class MatchingAlgorithme {


    public static void main(String[] args){
        // substitution of s and t
        System.out.println(new JaroWinkler().similarity("My string", "My string"));

        // substitution of s and n
        System.out.println(new JaroWinkler().similarity("My string", "Myamine"));

    }
}
