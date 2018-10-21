package com.components.actions;

import org.fest.util.Strings;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import static com.components.actions.HtmlFormatter.applyLink;

public class JavaTermsGlossary implements IDefinitionLookup {

    private static String[][] glossary = new String[250][3];

    @Override
    public String getDefinition(final String term) {
        try {
            buildGlossary();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Strings.isNullOrEmpty(term) || !term.matches("[a-zA-Z][a-zA-Z ]*")) {
            return "not found";
        }
        for (String[] glossaryTerm : glossary) {
            if (!Strings.isNullOrEmpty(glossaryTerm[1]) && glossaryTerm[1].toLowerCase().equals(term)) {
                return glossaryTerm[2];
            }
        }
        for (String[] glossaryName : glossary) {
            if (!Strings.isNullOrEmpty(glossaryName[0]) && glossaryName[0].toLowerCase().equals(term)) {
                return glossaryName[2];
            }
        }
        String termGoogleUrl = "https://www.google.com/search?q=" + term.replace(" ", "%20");
        String javaTermGoogleUrl = "https://www.google.com/search?q=" + "Java%20" + term.replace(" ", "%20");
        return "not found <br><br>Try using" + applyLink(termGoogleUrl, termGoogleUrl)
                + "<br>or " + applyLink(javaTermGoogleUrl, javaTermGoogleUrl);

    }

    private void buildGlossary() throws IOException {
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

    private String getHTML(final String url) throws IOException {
        return new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next()
                .replace("<P>", "<p>")
                .replace("</A>", "</a>")
                .replace("<B>", "<b>")
                .replace("<DD>", "<dd>")
                .replace("<D1>", "<d1>")
                .replace("</DD>", "")
                .replace("</DT>", "");
    }
}