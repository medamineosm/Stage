package com.pixalione.maillageinterne.semanticproximity.Services;

import net.htmlparser.jericho.*;

import java.util.List;
/**
 *
 * @author Nazanin
 */
public final class TagRemoval {

    //This function removes all the useless tags from the extracted source code
    private String clean_sourcecode;


    public TagRemoval(String sourcepage) {
        clean_sourcecode = sourcepage;
        FooterRemoval();
        InputRemoval();
        LabelRemoval();
        OptionRemoval();
        NoscriptRemoval();
        ScriptRemoval();
        DisplayNoneRemoval();
        StyleRemoval();
        MenuRemoval();
        NavRemoval();
        Menu_id_class_Removal();

        //---------------------------------------------------------------------------------------------

    }

    private void DisplayNoneRemoval() {
        Source source = new Source(clean_sourcecode);
        //Get all the divs
        List<Element> divElements = source.getAllElements("div");
        for (Element divElement : divElements) {
            StartTag startTag = divElement.getStartTag();
            Attributes attributes = startTag.getAttributes();
            Attribute styleAttribute = attributes.get("style");
            if (styleAttribute != null) {
                //[style*=display:none], [style*=display: none], [style*=visibility:hidden], [style*=visibility: hidden]
                if (styleAttribute.toString().toLowerCase().contains("display:none")
                        || styleAttribute.toString().toLowerCase().contains("display: none")
                        || styleAttribute.toString().toLowerCase().contains("display : none")
                        || styleAttribute.toString().toLowerCase().contains("display :none")
                        || styleAttribute.toString().toLowerCase().contains("visibility:hidden")
                        || styleAttribute.toString().toLowerCase().contains("visibility : hidden")
                        || styleAttribute.toString().toLowerCase().contains("visibility: hidden")
                        || styleAttribute.toString().toLowerCase().contains("visibility :hidden")) {
                    clean_sourcecode = clean_sourcecode.replace(divElement, "");
                }
            }
        }

        //removing <p style="display:none"> case
        List<Element> pElements = source.getAllElements("p");
        for (Element pElement : pElements) {
            StartTag startTag = pElement.getStartTag();
            Attributes attributes = startTag.getAttributes();
            Attribute styleAttribute = attributes.get("style");
            if (styleAttribute != null) {
                //[style*=display:none], [style*=display: none], [style*=visibility:hidden], [style*=visibility: hidden]
                if (styleAttribute.toString().toLowerCase().contains("display:none")
                        || styleAttribute.toString().toLowerCase().contains("display: none")
                        || styleAttribute.toString().toLowerCase().contains("display : none")
                        || styleAttribute.toString().toLowerCase().contains("display :none")
                        || styleAttribute.toString().toLowerCase().contains("visibility:hidden")
                        || styleAttribute.toString().toLowerCase().contains("visibility : hidden")
                        || styleAttribute.toString().toLowerCase().contains("visibility: hidden")
                        || styleAttribute.toString().toLowerCase().contains("visibility :hidden")) {
                    clean_sourcecode = clean_sourcecode.replace(pElement, "");
                }
            }
        }
    }

    private void FooterRemoval() {
        Source source = new Source(clean_sourcecode);
        //Removing footer in HTML5
        if (clean_sourcecode.toLowerCase().contains("footer")) {
            List<Element> footerElements = source.getAllElements("footer");
            for (Element footerElement : footerElements) {
                clean_sourcecode = clean_sourcecode.replace(footerElement, "");
            }
        }
    }

    private void StyleRemoval() {
        Source source = new Source(clean_sourcecode);
        //Removing style in HTML5
        if (clean_sourcecode.toLowerCase().contains("style")) {
            List<Element> styleElements = source.getAllElements("style");
            for (Element styleElement : styleElements) {
                clean_sourcecode = clean_sourcecode.replace(styleElement, "");
            }
        }
    }

    private void MenuRemoval() {
        Source source = new Source(clean_sourcecode);
        //Removing Menu in HTML5
        if (clean_sourcecode.toLowerCase().contains("menu")) {
            List<Element> menuElements = source.getAllElements("menu");
            for (Element menuElement : menuElements) {
                clean_sourcecode = clean_sourcecode.replace(menuElement, "");
            }
        }
    }

    private void NavRemoval() {
        Source source = new Source(clean_sourcecode);
        //Removing nav in HTML5
        if (clean_sourcecode.toLowerCase().contains("nav")) {
            List<Element> navElements = source.getAllElements("nav");
            for (Element navElement : navElements) {
                clean_sourcecode = clean_sourcecode.replace(navElement, "");
            }
        }
    }

    private void Menu_id_class_Removal() {
        Source source = new Source(clean_sourcecode);
        //Get all the divs
        List<Element> divElements = source.getAllElements("div");
        for (Element divElement : divElements) {
            StartTag startTag = divElement.getStartTag();
            Attributes attributes = startTag.getAttributes();
            Attribute idAttribute = attributes.get("id");
            Attribute classAttribute = attributes.get("class");
            Attribute roleAttribute = attributes.get("role");
            if (idAttribute != null) {
                if (idAttribute.toString().toLowerCase().contains("menu")) {
                    clean_sourcecode = clean_sourcecode.replace(divElement, "");
                }

                if (idAttribute.toString().toLowerCase().contains("nav")) {
                    clean_sourcecode = clean_sourcecode.replace(divElement, "");
                }

                if (idAttribute.toString().toLowerCase().contains("slider")) {
                    clean_sourcecode = clean_sourcecode.replace(divElement, "");
                }
                if (idAttribute.toString().toLowerCase().contains("footer")) {
                    clean_sourcecode = clean_sourcecode.replace(divElement, "");
                }
            }

            if (classAttribute != null) {
                if (classAttribute.toString().toLowerCase().contains("menu")) {
                    clean_sourcecode = clean_sourcecode.replace(divElement, "");
                }

                if (classAttribute.toString().toLowerCase().contains("nav"))
                { clean_sourcecode = clean_sourcecode.replace(divElement,
                        ""); }

                if (classAttribute.toString().toLowerCase().contains("slider")) {
                    clean_sourcecode = clean_sourcecode.replace(divElement, "");
                }
                if (classAttribute.toString().toLowerCase().contains("footer")) {
                    clean_sourcecode = clean_sourcecode.replace(divElement, "");
                }
            }

            if (roleAttribute != null) {
                if (roleAttribute.toString().toLowerCase().contains("menu")) {
                    clean_sourcecode = clean_sourcecode.replace(divElement, "");
                }

                if (roleAttribute.toString().toLowerCase().contains("nav")) {
                    clean_sourcecode = clean_sourcecode.replace(divElement, "");
                }

                if (roleAttribute.toString().toLowerCase().contains("slider")) {
                    clean_sourcecode = clean_sourcecode.replace(divElement, "");
                }
                if (roleAttribute.toString().toLowerCase().contains("footer")) {
                    clean_sourcecode = clean_sourcecode.replace(divElement, "");
                }
            }
        }
        //end of div

        //Get all the navs
        /*
         * List<Element> navElements = source.getAllElements("nav"); for
         * (Element navElement : navElements) { StartTag startTag =
         * navElement.getStartTag(); Attributes attributes =
         * startTag.getAttributes(); Attribute idAttribute =
         * attributes.get("id"); Attribute classAttribute =
         * attributes.get("class"); Attribute roleAttribute =
         * attributes.get("role"); if (idAttribute != null) { if
         * (idAttribute.toString().toLowerCase().contains("menu")) {
         * clean_sourcecode = clean_sourcecode.replace(navElement, ""); } } if
         * (classAttribute != null) { if
         * (classAttribute.toString().toLowerCase().contains("menu")) {
         * clean_sourcecode = clean_sourcecode.replace(navElement, ""); } } if
         * (roleAttribute != null) { if
         * (roleAttribute.toString().toLowerCase().contains("menu")) {
         * clean_sourcecode = clean_sourcecode.replace(navElement, ""); } } }
         */
        //end of nav

        //Get all the uls
        List<Element> ulElements = source.getAllElements("ul");
        for (Element ulElement : ulElements) {
            StartTag startTag = ulElement.getStartTag();
            Attributes attributes = startTag.getAttributes();
            Attribute idAttribute = attributes.get("id");
            Attribute classAttribute = attributes.get("class");
            Attribute roleAttribute = attributes.get("role");
            if (idAttribute != null) {
                if (idAttribute.toString().toLowerCase().contains("menu")) {
                    clean_sourcecode = clean_sourcecode.replace(ulElement, "");
                }

                if (idAttribute.toString().toLowerCase().contains("nav")) {
                    clean_sourcecode = clean_sourcecode.replace(ulElement, ""); }

                if (idAttribute.toString().toLowerCase().contains("slider")) {
                    clean_sourcecode = clean_sourcecode.replace(ulElement, "");
                }
                if (idAttribute.toString().toLowerCase().contains("footer")) {
                    clean_sourcecode = clean_sourcecode.replace(ulElement, "");
                }
            }
            if (classAttribute != null) {
                if (classAttribute.toString().toLowerCase().contains("menu")) {
                    clean_sourcecode = clean_sourcecode.replace(ulElement, "");
                }

                if (classAttribute.toString().toLowerCase().contains("nav"))
                { clean_sourcecode = clean_sourcecode.replace(ulElement, "");
                }

                if (classAttribute.toString().toLowerCase().contains("slider")) {
                    clean_sourcecode = clean_sourcecode.replace(ulElement, "");
                }
                if (classAttribute.toString().toLowerCase().contains("footer")) {
                    clean_sourcecode = clean_sourcecode.replace(ulElement, "");
                }
            }
            if (roleAttribute != null) {
                if (roleAttribute.toString().toLowerCase().contains("menu")) {
                    clean_sourcecode = clean_sourcecode.replace(ulElement, "");
                }

                if (roleAttribute.toString().toLowerCase().contains("nav")) {
                    clean_sourcecode = clean_sourcecode.replace(ulElement, ""); }

                if (roleAttribute.toString().toLowerCase().contains("slider")) {
                    clean_sourcecode = clean_sourcecode.replace(ulElement, "");
                }
                if (roleAttribute.toString().toLowerCase().contains("footer")) {
                    clean_sourcecode = clean_sourcecode.replace(ulElement, "");
                }
            }
        }
        //end of ul

        //Get all the lis
        List<Element> liElements = source.getAllElements("li");
        for (Element liElement : liElements) {
            StartTag startTag = liElement.getStartTag();
            Attributes attributes = startTag.getAttributes();
            Attribute idAttribute = attributes.get("id");
            Attribute classAttribute = attributes.get("class");
            Attribute roleAttribute = attributes.get("role");
            if (idAttribute != null) {
                if (idAttribute.toString().toLowerCase().contains("menu")) {
                    clean_sourcecode = clean_sourcecode.replace(liElement, "");
                }

                if (idAttribute.toString().toLowerCase().contains("nav")) {
                    clean_sourcecode = clean_sourcecode.replace(liElement, ""); }

                if (idAttribute.toString().toLowerCase().contains("slider")) {
                    clean_sourcecode = clean_sourcecode.replace(liElement, "");
                }
                if (idAttribute.toString().toLowerCase().contains("footer")) {
                    clean_sourcecode = clean_sourcecode.replace(liElement, "");
                }
            }
            if (classAttribute != null) {
                if (classAttribute.toString().toLowerCase().contains("menu")) {
                    clean_sourcecode = clean_sourcecode.replace(liElement, "");
                }

                if (classAttribute.toString().toLowerCase().contains("nav"))
                { clean_sourcecode = clean_sourcecode.replace(liElement, "");
                }

                if (classAttribute.toString().toLowerCase().contains("slider")) {
                    clean_sourcecode = clean_sourcecode.replace(liElement, "");
                }
                if (classAttribute.toString().toLowerCase().contains("footer")) {
                    clean_sourcecode = clean_sourcecode.replace(liElement, "");
                }
            }
            if (roleAttribute != null) {
                if (roleAttribute.toString().toLowerCase().contains("menu")) {
                    clean_sourcecode = clean_sourcecode.replace(liElement, "");
                }

                if (roleAttribute.toString().toLowerCase().contains("nav")) {
                    clean_sourcecode = clean_sourcecode.replace(liElement, ""); }

                if (roleAttribute.toString().toLowerCase().contains("slider")) {
                    clean_sourcecode = clean_sourcecode.replace(liElement, "");
                }
                if (roleAttribute.toString().toLowerCase().contains("footer")) {
                    clean_sourcecode = clean_sourcecode.replace(liElement, "");
                }
            }
        }
        //end of li

        //Get all the spans
        List<Element> spanElements = source.getAllElements("span");
        for (Element spanElement : spanElements) {
            if (spanElement.getContent().toString().startsWith("{") && spanElement.getContent().toString().endsWith("}")) {
                clean_sourcecode = clean_sourcecode.replace(spanElement, "");
            } else {
                StartTag startTag = spanElement.getStartTag();
                Attributes attributes = startTag.getAttributes();
                Attribute idAttribute = attributes.get("id");
                Attribute classAttribute = attributes.get("class");
                Attribute roleAttribute = attributes.get("role");
                if (idAttribute != null) {
                    if (idAttribute.toString().toLowerCase().contains("menu")) {
                        clean_sourcecode = clean_sourcecode.replace(spanElement, "");
                    }

                    if (idAttribute.toString().toLowerCase().contains("nav"))
                    { clean_sourcecode =
                            clean_sourcecode.replace(spanElement, ""); }

                    if (idAttribute.toString().toLowerCase().contains("slider")) {
                        clean_sourcecode = clean_sourcecode.replace(spanElement, "");
                    }
                    if (idAttribute.toString().toLowerCase().contains("footer")) {
                        clean_sourcecode = clean_sourcecode.replace(spanElement, "");
                    }
                }
                if (classAttribute != null) {
                    if (classAttribute.toString().toLowerCase().contains("menu")) {
                        clean_sourcecode = clean_sourcecode.replace(spanElement, "");
                    }

                    if
                            (classAttribute.toString().toLowerCase().contains("nav"))
                    { clean_sourcecode =
                            clean_sourcecode.replace(spanElement, ""); }

                    if (classAttribute.toString().toLowerCase().contains("slider")) {
                        clean_sourcecode = clean_sourcecode.replace(spanElement, "");
                    }
                    if (classAttribute.toString().toLowerCase().contains("footer")) {
                        clean_sourcecode = clean_sourcecode.replace(spanElement, "");
                    }
                }
                if (roleAttribute != null) {
                    if (roleAttribute.toString().toLowerCase().contains("menu")) {
                        clean_sourcecode = clean_sourcecode.replace(spanElement, "");
                    }

                    if
                            (roleAttribute.toString().toLowerCase().contains("nav"))
                    { clean_sourcecode =
                            clean_sourcecode.replace(spanElement, ""); }

                    if (roleAttribute.toString().toLowerCase().contains("slider")) {
                        clean_sourcecode = clean_sourcecode.replace(spanElement, "");
                    }
                    if (roleAttribute.toString().toLowerCase().contains("footer")) {
                        clean_sourcecode = clean_sourcecode.replace(spanElement, "");
                    }
                }
            }
        }
        //end of span
    }

    private void InputRemoval() {
        Source source = new Source(clean_sourcecode);
        //Removing input in HTML5
        if (clean_sourcecode.toLowerCase().contains("<input")) {
            List<Element> inputElements = source.getAllElements("input");
            for (Element inputElement : inputElements) {
                clean_sourcecode = clean_sourcecode.replace(inputElement, "");
            }
        }
    }

    private void LabelRemoval() {
        Source source = new Source(clean_sourcecode);
        //Removing input in HTML5
        if (clean_sourcecode.toLowerCase().contains("<label")) {
            List<Element> labelElements = source.getAllElements("label");
            for (Element labelElement : labelElements) {
                clean_sourcecode = clean_sourcecode.replace(labelElement, "");
            }
        }
    }

    private void OptionRemoval() {
        Source source = new Source(clean_sourcecode);
        //Removing input in HTML5
        if (clean_sourcecode.toLowerCase().contains("<option")) {
            List<Element> optionElements = source.getAllElements("option");
            for (Element optionElement : optionElements) {
                clean_sourcecode = clean_sourcecode.replace(optionElement, "");
            }
        }
    }

    private void ScriptRemoval() {
        Source source = new Source(clean_sourcecode);
        if (clean_sourcecode.toLowerCase().contains("script")) {
            List<Element> script_Elements = source.getAllElements("script");
            for (Element script_Element : script_Elements) {
                clean_sourcecode = clean_sourcecode.replace(script_Element, "");
            }
        }
    }

    private void NoscriptRemoval() {
        Source source = new Source(clean_sourcecode);
        //Removing noscript in HTML5
        if (clean_sourcecode.toLowerCase().contains("noscript")) {
            List<Element> noscriptElements = source.getAllElements("noscript");
            for (Element noscriptElement : noscriptElements) {
                clean_sourcecode = clean_sourcecode.replace(noscriptElement, "");
            }
        }
    }


    public String getCleanSourceCode() {
        return clean_sourcecode;
    }


}

