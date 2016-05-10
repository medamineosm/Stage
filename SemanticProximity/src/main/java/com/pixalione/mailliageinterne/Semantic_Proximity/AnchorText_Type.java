/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pixalione.mailliageinterne.Semantic_Proximity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fabrice
 */
public class AnchorText_Type {

    public static final List<String> BrandList1 = new ArrayList(Arrays.asList("avenuedesjeux", "avenue jeux", "avenue jeu","avenue des jeux"));
    public static final List<String> BrandList2 = new ArrayList(Arrays.asList("www", "http", "https"));
    public static final List<String> NeutralWords = new ArrayList(Arrays.asList("\\+ d'infos sur le site internet","en savoir plus", "en savoir \\+", "\\+ d'infos", "voir plus", "lire la suite", "﻿\\+d'infos", "\\+de detail", "accéder à la boutique", "accéder a l'article", "acceder à l'article", "accéder à l'article", "accéder à leur site web", "accéder au site", "accèder au site", "accéder au site du fabricant", "accéder au site du fournisseur", "accéder au site internet", "accéder au site partenaire", "accédez a l'article", "accedez à l'article", "accédez à l'article", "accédez à leur site", "accédez au site", "accèdez au site", "accédez au site du partenaire", "accèdez au site web", "accédez aux offres", "accès au site", "accès au site du fournisseur", "accès au site web", "accès espace client", "accès site internet", "afficher le site", "afficher le site web", "aller à l'offre", "aller sur la page web", "aller sur le site", "allez sur le site", "apple source", "article", "autre lien", "c’est ici", "c’est par ici", "ça", "ça ", "ce lien", "ce modèle", "ce site", "ceci", "celle-ci", "celles ci me semble-t-il", "celui la", "celui là", "c'est ici", "cette page", "cette page du site", "ceux-là", "cliquant ici", "clique ici", "cliquer ici", "cliquer ici pour accéder au site de cette entreprise", "cliquer ici pour accéder au site de cette société", "cliquer ici pour visiter le site de cette entreprise", "cliquer ici pour visiter le site de cette société", "cliquer ici pour voir le site", "cliquer-ici", "cliquer-ici pour voir le site", "cliquez", "cliquez ", "cliquez ici", "cliquez ici pour accéder au site de cette entreprise", "cliquez ici pour accéder au site de cette société", "cliquez ici pour continuer", "cliquez ici pour participer", "cliquez ici pour suivre le lien", "cliquez ici pour visiter le site de cette entreprise", "cliquez ici pour visiter le site de cette société", "cliquez ici pour visiter leur site internet", "cliquez ici pour voir la page", "cliquez ici pour voir le site", "cliquez ici pour voir l'offre", "cliquez ici.", "cliquez pour accéder au site", "cliquez pour voir l'image", "cliquez-ici", "cliquez-ici pour voir le site", "comme celui-ci", "consulte o website", "consulter le site", "consulter le site internet", "consulter le site web", "consulter son site internet", "contact", "contact web", "contacter ici", "disponible ici", "en cliquant ici", "en ligne", "en savoir +", "en savoir plus", "en savoir plus ", "en savoir plus sur ce partenaire", "en savoir plus sur ce produit", "en savoir plus sur l'entreprise", "en savoir plus", "entreprise", "est à découvrir via ce lien", "exemple", "ici", "informations", "infos pratiques", "là", "la boutique", "la société", "le site ", "le site de notre compagnie", "le site d'origine ouvrir le lien", "le site internet de la compagnie", "leur site", "leur site internet", "lien", "lien ", "lien direct", "lien ici", "lien vers le site", "lien web", "liens ", "lire l'article", "lire l’article", "lire la suite", "lire la suite", "lire l'article", "lis", "ouvrir le lien", "ouvrir page web", "page", "page contact", "par exemple", "par exemple ça", "par ici", "plan d’accès", "plan du site", "plus d’info", "plus d’infos", "plus d’infos pour", "plus de détail", "plus d'info", "plus d'informations", "plus d'informations ", "plus d'informations sur l'évènement", "plus d'informations, suivre ce lien", "plus d'infos", "qui sommes-nous ", "rendez vous ici", "rendez-vous ici", "site", "site complet", "site de la compagnie.", "site de la société", "site de l'établissement", "site internet", "site internet ", "site internet de la société émettrice", "site officiel", "site officiel de la marque", "site web", "site web de la fondation", "site web pour en savoir plus", "site web pro", "site web.", "siteweb", "sitio web", "son site", "sont à découvrir via ce lien", "source", "sur ce site", "sur leur site", "sur leur site internet", "sur son site", "télécharger", "un site web", "url", "url image", "ver sitio", "visiter", "visiter ce site", "visiter cette page", "visiter le lien favori", "visiter le site", "visiter le site ", "visiter le site de l'exposant", "visiter le site internet", "visiter le site web", "visiter le site web.", "visiter leur site", "visiter notre site", "visiter notre site officiel", "visiter notre site web", "visiter sur internet", "visitez", "visitez le lien", "visitez le site", "visitez le site internet", "visitez le site web", "visitez le site web.", "visitez leur site", "voir", "voir ici", "voir la vidéo", "voir l'article", "voir le lien", "voir le plan", "voir le projet", "voir le site", "voir le site ", "voir le site de cet organisme", "voir le site de la nouveauté", "voir le site du partenaire", "voir le site internet", "voir le site web", "voir le site", "voir les tarifs", "voir l'offre", "voir son site", "vous trouverez en cliquant ici plus d'informations ", "web", "web info", "société", "lire l article"));

    public String AnchorText_Type(String Anchor) {
        String type = "";
        Anchor = Anchor.toLowerCase();

        if (Anchor.matches("")) {
            type = "Image (empty)";
        }
        Anchor = Anchor.replaceAll("\\ +", " ");
        Anchor = Anchor.replaceAll("[\\-\\.\\-\\®\\$\\`\\,\\%\\/\\\\\\&\\[\\!\\#\\£\\&\\*\\§\\:\\@\\¨\\_\\^\\(\\/\\)\\{\\}\\{\\]\\~\\¤\\°\\=\\?\\;\\<\\>\\»\\«\\€\\|\\/]", "");
        Anchor = Anchor.replaceAll("à", "a");
        Anchor = Anchor.replaceAll("â", "a");
        Anchor = Anchor.replaceAll("á", "a");
        Anchor = Anchor.replaceAll("â", "a");
        Anchor = Anchor.replaceAll("ã", "a");
        Anchor = Anchor.replaceAll("ä", "a");
        Anchor = Anchor.replaceAll("å", "a");
        Anchor = Anchor.replaceAll("â", "a");
        Anchor = Anchor.replaceAll("æ", "ae");
        Anchor = Anchor.replaceAll("ç", "c");
        Anchor = Anchor.replaceAll("é", "e");
        Anchor = Anchor.replaceAll("è", "e");
        Anchor = Anchor.replaceAll("ê", "e");
        Anchor = Anchor.replaceAll("ë", "e");
        Anchor = Anchor.replaceAll("ì", "i");
        Anchor = Anchor.replaceAll("í", "i");
        Anchor = Anchor.replaceAll("î", "i");
        Anchor = Anchor.replaceAll("ï", "i");
        Anchor = Anchor.replaceAll("ð", "o");
        Anchor = Anchor.replaceAll("ò", "o");
        Anchor = Anchor.replaceAll("ñ", "n");
        Anchor = Anchor.replaceAll("œ", "oe");
        Anchor = Anchor.replaceAll("ô", "o");
        Anchor = Anchor.replaceAll("ó", "o");
        Anchor = Anchor.replaceAll("õ", "o");
        Anchor = Anchor.replaceAll("ö", "o");
        Anchor = Anchor.replaceAll("ù", "u");
        Anchor = Anchor.replaceAll("ú", "u");
        Anchor = Anchor.replaceAll("û", "u");
        Anchor = Anchor.replaceAll("ü", "u");
        Anchor = Anchor.replaceAll("ô", "o");
        Anchor = Anchor.replaceAll("ô", "o");
        Anchor = Anchor.trim();
        for (String c1 : BrandList1) {

            if (Anchor.matches(c1.replaceAll("-",""))) {
                type = "Brand";
                break;
            } else {
                for (String c3 : BrandList2) {
                    if (Anchor.contains(c3) && !Anchor.contains(" ")) {
                        type = "Brand";
                        break;

                    } else {
                        if (Anchor.contains(c1.replaceAll("-","")) | Anchor.contains(c3)) {
                            type = "Hybrid";
                            break;
                        }
                    }
                }
            }
        }

        for (String c2 : NeutralWords) {

            if (Anchor.matches(c2)) {
                type = "Neutral Word";
                break;
            }
        }

        if (type.equalsIgnoreCase("")) {
            type = "No Brand";
        }
        System.out.println(type);

        return type;
    }

//    public static void main(String[] args) throws IOException {
//        InputStream ips = new FileInputStream("C:\\Users\\Fabrice\\Desktop\\test-type of anchor text.txt");
//        InputStreamReader ipsr = new InputStreamReader(ips);
//        BufferedReader br = new BufferedReader(ipsr);
//        String ligne;
//        while ((ligne = br.readLine()) != null) {
//            AnchorText_Type a = new AnchorText_Type();
//            a.AnchorText_Type(ligne);
//        }
//
//    }
}
