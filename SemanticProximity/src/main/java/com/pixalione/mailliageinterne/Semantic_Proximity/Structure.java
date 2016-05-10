/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixalione.mailliageinterne.Semantic_Proximity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 *
 * @author test
 */
public enum Structure {

    INSTANCE;

    private Structure(){
        try {
			FileInputStream file = new FileInputStream(new File("data/Structure_Syntagmes.txt"));
			InputStreamReader Corpus=new InputStreamReader(file, Charset.forName("UTF8"));
			BufferedReader Reader=new BufferedReader(Corpus);
			String ligne;
			// lecture ligne par ligne )
			while ((ligne=Reader.readLine())!=null){
				if (ligne.length()==3)
                                    StructSyn1.add(ligne);
                                else
                                    if (ligne.length()==5)
                                        StructSyn2.add(ligne);
                                    else
                                        if (ligne.length()==7)
                                            StructSyn3.add(ligne);
                                    else
                                            StructSyn4.add(ligne);	
			}

			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    };
    
    public ArrayList<String> StructSyn1 = new ArrayList<String>();
    public ArrayList<String> StructSyn2 = new ArrayList<String>();
    public ArrayList<String> StructSyn3 = new ArrayList<String>();
    public ArrayList<String> StructSyn4 = new ArrayList<String>();
    
}
