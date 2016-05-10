package com.pixalione.maillageinterne.semanticproximity.Services;

import net.htmlparser.jericho.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Nazanin
 */
public final class Get_URL_Text {
    //HTML source code within <title></title> tag

    private String sourcetitle = "";
    //Content of Meta description tag
    private String meta_text = "";
    //Content of body tag
    private String text_body = "";
    //Content of title tag
    private String text_title = "";
    //original_source_body contains the original sourcebody without replacing some tags with ". " (which is done for sentence generation)
    private String original_source_body = "";

    public Get_URL_Text(String sourcepage) {
        Get_title_source(sourcepage);
        Get_body_source(sourcepage);
        Get_meta_text(sourcepage);
    }

    //Get_body_source extracts the source code within <body></body> tag. The extracted code has no useless tags and links (they have been already removed in the previous steps)
    //Some tags are replaced by ". " for sentence generation purpose
    //It then extracts the plain text within the extracted source code
    //Emails and URLs are also removed from the extracted plain text, to make the result more precise
    private void Get_body_source(String sourcepage) {
        //HTML source code within <body></body> tag
        String sourcebody = "";
        //using jericho library
        Source source = new Source(sourcepage);

        List<Element> bodyElements = source.getAllElements("body");
        for (Element bodyElement : bodyElements) {
            sourcebody = sourcebody.concat(bodyElement.toString());
        }

        original_source_body = sourcebody;
        //jericho does not extract the content of different elements as different sentences.
        //For instance, "<li> Pixalione mission </li> <li> Employee </li> is extracted as just one sentence using jericho, which is "Pixalione mission Employee".
        //This provides wrong information about the co-occurrence of the words.
        //So we replace some of these elemenets with ". ", to break them into different sentences while generating sentences from the extracted text in further steps
        sourcebody = sourcebody.replace("<li>", ". ");
        sourcebody = sourcebody.replace("</li>", ". ");
        sourcebody = sourcebody.replace("<span>", ". ");
        sourcebody = sourcebody.replaceAll("<span(.[^>]*)>", ". ");
        sourcebody = sourcebody.replace("</span>", ". ");
        sourcebody = sourcebody.replace("<ul>", ". ");
        sourcebody = sourcebody.replace("</ul>", ". ");
        sourcebody = sourcebody.replace("<ol>", ". ");
        sourcebody = sourcebody.replace("</ol>", ". ");
        //sourcebody = sourcebody.replace("<br />", ". ");
        //sourcebody = sourcebody.replace("</br>", ". ");
        sourcebody = sourcebody.replaceAll("<p (.[^>]*)>", ". ");
        sourcebody = sourcebody.replace("</p>", ". ");
        sourcebody = sourcebody.replace("<label>", ". ");
        sourcebody = sourcebody.replace("</label>", ". ");
        sourcebody = sourcebody.replace("<h1>", ". ");
        sourcebody = sourcebody.replace("</h1>", ". ");
        sourcebody = sourcebody.replace("<h2>", ". ");
        sourcebody = sourcebody.replace("</h2>", ". ");
        sourcebody = sourcebody.replace("<h3>", ". ");
        sourcebody = sourcebody.replace("</h3>", ". ");
        sourcebody = sourcebody.replace("<h4>", ". ");
        sourcebody = sourcebody.replace("</h4>", ". ");
        sourcebody = sourcebody.replace("<h5>", ". ");
        sourcebody = sourcebody.replace("</h5>", ". ");
        sourcebody = sourcebody.replace("<h6>", ". ");
        sourcebody = sourcebody.replace("</h6>", ". ");
        sourcebody = sourcebody.replace("<p>", ". ");
        sourcebody = sourcebody.replace("</p>", ". ");
        sourcebody = sourcebody.replace("<div>", ". ");
        sourcebody = sourcebody.replace("</div>", ". ");
        sourcebody = sourcebody.replace("<tr(.[^>]*)>", ". ");
        sourcebody = sourcebody.replace("</tr>", ". ");
//        sourcebody = sourcebody.replace("&amp;", "&");

        //There is a problem with jericho text extractor. In jericho, having HTML source code as "A \newline B", the extracted text is "AB"
        //while having "A<br />\newline B", the extracted text is "A B". This extra space makes problem in duplicate content removal step.
        //to avoid having this problem, we replace "<br />" by "". Continuous sentences problem such as "F A B.C D E" (that might happen after this replacement) is also solved in Sentence_Lemmatize.java
        //sourcebody = sourcebody.replace("<br />", "");

        String[] sourcebody_parts = sourcebody.split("<br />|<br/>|<br>");
        String copy_source_body = sourcebody;
        sourcebody = "";

        // == 1 means that there is no <br /> in sourcebody
        if (sourcebody_parts.length > 1) {
            for (String part : sourcebody_parts) {
                if (!part.equals("") && !part.equals(" ")) {
                    //if source_body ends with a non-alphabitc and non-digit character
                    if (part.substring(part.length() - 1).matches("[0-9\\p{L}]")) {
                        sourcebody = sourcebody.concat(part).concat(" ");
                    } else {
                        sourcebody = sourcebody.concat(part);
                    }
                }
            }
        } else {
            //the initial value of sourcebody, i.e. before spliting by <br /> is inserted again in sourcebody, if there is no <br /> in sourcebody
            sourcebody = copy_source_body;
        }

        text_body = Get_text(sourcebody);

        //removing extra spaces from text_body
        text_body = text_body.trim().replaceAll(" +", " ");
        //replacing all ". . . . " with only one ". "
        text_body = text_body.replaceAll("(\\. )+", ". ");
        Mark_Replacement marks = new Mark_Replacement(text_body, true);
        text_body = marks.getNew_String();
        text_body = Email_URL_Removal(text_body);
        Mark_Replacement body_marks = new Mark_Replacement(text_body);
        text_body = body_marks.getNew_String();

        //NOT SURE about this replacement. The goal is to avoid replacing the elements by ". " makes any problem in comparison (in duplicates)
        //text_body = text_body.replaceAll("\\.{2}+", "").trim();
        //System.out.println("text body after replacing dots: "+text_body);
        //removing duplicate description from the extracted text of body
        //duplicate description is assumed as the longest common substring within the text
        //Longest_str duplicate_des = new Longest_str(text_body, text_title);
        //text_body = duplicate_des.getNo_Duplicate();
    }

    //The same as "Get_body_source" method, but for <title> tag
    private void Get_title_source(String sourcepage) {
        Source source = new Source(sourcepage);
        List<Element> titleElements = source.getAllElements("title");
        for (Element titleElement : titleElements) {
            sourcetitle = sourcetitle.concat(titleElement.toString());
        }

        text_title = Get_text(sourcetitle);
        Mark_Replacement marks = new Mark_Replacement(text_title, true);
        text_title = marks.getNew_String();
        text_title = Email_URL_Removal(text_title);
        Mark_Replacement title_marks = new Mark_Replacement(text_title);
        text_title = title_marks.getNew_String();
    }

    //The content of the Meta description tag is extracted. Then URLs and emails are removed from the extracted text
    private void Get_meta_text(String sourcepage) {
        Source source = new Source(sourcepage);
        List<Element> metaElements = source.getAllElements("meta");
        for (Element metaElement : metaElements) {
            StartTag startTag = metaElement.getStartTag();
            Attributes attributes = startTag.getAttributes();
            Attribute nameAttribute = attributes.get("name");
            if (nameAttribute != null) {
                if (nameAttribute.toString().equalsIgnoreCase("name=\"Description\"")) {
                    Attribute contentAttribute = attributes.get("content");
                    if (contentAttribute != null) {
                        meta_text = contentAttribute.getValue().replace("content=", " ");
//                        meta_text = contentAttribute.toString().replace("content=", " ");
                    }
                }
            }
        }
        Mark_Replacement marks = new Mark_Replacement(meta_text, true);
        meta_text = marks.getNew_String();
        meta_text = Email_URL_Removal(meta_text);
        Mark_Replacement meta_marks = new Mark_Replacement(meta_text);
        meta_text = meta_marks.getNew_String();
        //removing the script from meta description of some AirLiquide URLs
        meta_text = meta_text.replaceAll("var ML img ServerOnline(.*)ImageError\\(\\)\\{ \\}", "");
    }

    //Using jericho library, this function extracts the plain text from any given tag
    private String Get_text(String input_string) {
        String plain_text = extractAllText(input_string);
        return plain_text;
    }

    public String extractAllText(String htmlText) {
        Source source = new Source(htmlText);
        return source.getTextExtractor().toString();
    }

    public String getTextBody() {
        return text_body;
    }

    public String getTextTitle() {
        return text_title;
    }

    public String getMetaText() {
        return meta_text;
    }

    public String getOriginalSourceBody() {
        return original_source_body;
    }

    //Emails and URLs are removed from the extracted plain text
    private String Email_URL_Removal(String input_string) {
        ArrayList<String> tok = new ArrayList<>();
        boolean flag;
        String strs[] = input_string.split(" ");
        tok.addAll(Arrays.asList(strs));
        //Pattern patt_email = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        //Regex allowing email addresses permitted by RFC 5322
        Pattern patt_email = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", Pattern.CASE_INSENSITIVE);
        Pattern patt_url = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", Pattern.CASE_INSENSITIVE);
        Pattern patt_url2 = Pattern.compile("/(.*).\\b(html|aspx|htm|fr|en|es|de)\\b");
        //to remove something like: Legrend.fr from the text
        Pattern patt_url3 = Pattern.compile("(.*).\\b(html|aspx|htm|fr|en|es|de)\\b");
        //Pattern patt_www = Pattern.compile("www.(.[^. ]*).(.[^ ]*)");
        Pattern patt_www = Pattern.compile("(.*)www.(.[^\\. ]*)\\.(.[^ \\.]*)(.*)");
        for (String tok1 : tok) {
            flag = true;
            Matcher matcher_email = patt_email.matcher(tok1);
            Matcher matcher_url = patt_url.matcher(tok1);
            Matcher matcher_url2 = patt_url2.matcher(tok1);
            Matcher matcher_url3 = patt_url3.matcher(tok1);
            Matcher matcher_www = patt_www.matcher(tok1);
            if (matcher_email.find()) {
                flag = false;
            }
            if (matcher_url.find()) {
                flag = false;
            }
            if (matcher_url2.find()) {
                flag = false;
            }
            if (matcher_url3.find()) {
                flag = false;
            }
            if (matcher_www.find()) {
                flag = false;
            }
            if (flag == false) {
                input_string = input_string.replace(tok1, " ");
            }
        }
        input_string = input_string.replace(".com", " ").replace(".org", " ").replace(".net", " ").replace(".int", " ").replace(".edu", " ").replace(".gov", " ").replace(".mil", " ");
        input_string = input_string.trim().replaceAll(" +", " ");
        if (input_string.endsWith(" ")) {
            input_string = input_string.substring(0, input_string.length() - 1);
        }
        if (input_string.startsWith(" ")) {
            input_string = input_string.substring(1, input_string.length());
        }
        return input_string;
    }
}