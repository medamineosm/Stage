package com.pixalione.maillageinterne.semanticproximity.Services;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 *
 * @author Nazanin
 */
public final class Text_Extraction_ {
    //HTML source code of the given URL

    private String sourcepage;
    //HTML source code within <body></body>
    private String source_body = "";

    //the original extracted text
    private String extracted_text;
    public String NameFile;

    public Text_Extraction_(String url, String name, String path) throws IOException, MalformedURLException, URISyntaxException {
        String nameFile = path + name + ".txt/";
        //list of single words generated from sentences (lemmatized, to lowercase)
        //Getting the HTML source code for the given URL
        GetSourceCode source_code = new GetSourceCode(url);
        sourcepage = source_code.getSourcecode();

        if (sourcepage != null) {
            TagRemoval tag_removal = new TagRemoval(sourcepage);

            sourcepage = tag_removal.getCleanSourceCode();

            //Required contents are extracted from the remaining tags (no lemmatization/normalization is done here. The only thing is Mark_Replacement)
            Get_URL_Text get_url_text = new Get_URL_Text(sourcepage);

            //Retrieving the ORIGINAL extracted source code from body
//            source_body = get_url_text.getOriginalSourceBody();
            //get extracted text from meta, title and body
            extracted_text = get_url_text.getTextBody();
            String extracted_text1 = extracted_text.replaceAll("(\\. )+", ". ");
            String extracted_text2 = extracted_text1.replaceAll("(\\.)+", ".");
            String extracted_text3 = extracted_text2.replaceAll(" +", " ");
            String extracted_text4 = extracted_text3.replaceAll("(\\-)+", "-");
            String extracted_text5 = extracted_text4.replaceAll("\\.", "");
            String extracted_text6 = extracted_text5.replaceAll(":", "");
            String extracted_text7 = extracted_text6.replaceAll(",", "");

            String extracted_text8 = extracted_text7.replaceAll("\\)", "");
            String extracted_text9 = extracted_text8.replaceAll("\\(", "");
            String extracted_text10 = extracted_text9.replaceAll("\\]", "");
            String extracted_text11 = extracted_text10.replaceAll("\\[", "");
            String extracted_text12 = extracted_text11.replaceAll("\\:", "");
            String extracted_text13 = extracted_text12.replaceAll("\\/", "");
            String extracted_text14 = extracted_text13.replaceAll("/", "");
            String extracted_text15 = extracted_text14.replaceAll("\\°", "");

            String extracted_text16 = extracted_text15.replaceAll("»", "");
            String extracted_text17 = extracted_text16.replaceAll("«", "");
            String extracted_text18 = extracted_text17.replaceAll("'", " ");
            String extracted_text19 = extracted_text18.replaceAll("\\.", "");
            String extracted_text20 = extracted_text19.replaceAll("$", "");
            String extracted_text21 = extracted_text20.replaceAll("£", "");
            String extracted_text22 = extracted_text21.replaceAll("€", "");
            String extracted_text23 = extracted_text22.replaceAll("!", "");
            String extracted_text24 = extracted_text23.replaceAll(">", " ");
            String extracted_text25 = extracted_text24.replaceAll("\\?", " ");
            String extracted_text26 = extracted_text25.replaceAll("\\-", " ");
            String extracted_text27 = extracted_text26.replaceAll("\\–", " ");
            String extracted_text28 = extracted_text27.replaceAll("\\ +", " ");
            String extracted_text29 = extracted_text28.trim();

            extracted_text = extracted_text28;

            NameFile = nameFile;
            File file = new File(nameFile); // définir l'arborescence
            try {
                file.createNewFile();
            } catch (IOException ex) {
                if (ex.getMessage().equals("The system cannot find the path specified")) {
                    File dir = new File(path);
                    dir.mkdirs();
                }
                file.createNewFile();
            }
            System.out.println("Writing file");
            FileWriter fileW = new FileWriter(file);
            fileW.write("/ ");
            fileW.write(extracted_text);
            fileW.close();

        } else {
            try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(nameFile)))) {
                pw.print(""); // if request fail --> content is empty
            }
        }
    }

    public String getSourcepage() {
        return sourcepage;
    }

    public void setSourcepage(String sourcepage) {
        this.sourcepage = sourcepage;
    }

    public String getExtracted_text() {
        return extracted_text;
    }

    public void setExtracted_text(String extracted_text) {
        this.extracted_text = extracted_text;
    }
}
