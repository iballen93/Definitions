package com.componets.actions;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class JavaTermsGlossary implements DefinitionLookup {

    private static String[][] glossary = new String[250][3];

    private String getHTML(final String url) throws IOException {
        String html = new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next();
        html = html.replace("<P>", "<p>");
        html = html.replace("</A>", "</a>");
        html = html.replace("<B>", "<b>");
        html = html.replace("<DD>", "<dd>");
        html = html.replace("<D1>", "<d1>");
        html = html.replace("</DD>", "");
        html = html.replace("</DT>", "");
        return html;
    }

    public void buildGlossary() throws IOException {
        String html = getHTML("https://docs.oracle.com/javase/tutorial/information/glossary.html");
        String[] namesTermsDefinitions = html.split("<dl>")[1].split("<p>");
        int position = 0;

        for (String nameTermDefinition : namesTermsDefinitions) {
            if (!nameTermDefinition.startsWith("\n\n") && !nameTermDefinition.startsWith("\n<b>*") && nameTermDefinition.split("\"")[1].length() > 1) {
                glossary[position][0] = nameTermDefinition.split("\"")[1];
                glossary[position][1] = nameTermDefinition.split(glossary[position][0] + "\"" + ">")[1].split("</a>")[0];
                glossary[position][2] = nameTermDefinition.split("<dd>")[1].trim().replace("\n", " ").replaceAll(" +", " ");
                position++;
            }
        }
    }

    public String getDefinition(final String term) {
        if (isNullOrEmpty(term) || !term.matches("[a-zA-Z][a-zA-Z ]*")) {
            return "Please provide a valid term to receive a definition.";
        }
        for (String[] glossaryTerm : glossary) {
            if (!isNullOrEmpty(glossaryTerm[1]) && glossaryTerm[1].toLowerCase().equals(term)) {
                return glossaryTerm[2];
            }
        }
        for (String[] glossaryName : glossary) {
            if (!isNullOrEmpty(glossaryName[0]) && glossaryName[0].toLowerCase().equals(term)) {
                return glossaryName[2];
            }
        }
        String termGoogleUrl = "https://www.google.com/search?q=" + term.replace(" ", "%20");
        String javaTermGoogleUrl = "https://www.google.com/search?q=" + "Java%20" + term.replace(" ", "%20");
        return "Definition not found. <br><br>Try using <a href=\"" + termGoogleUrl + "\">" + termGoogleUrl + "</a>"
                + "<br>or " + "<a href=\"" + javaTermGoogleUrl + "\">" + javaTermGoogleUrl + "</a>";
    }

    private static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}