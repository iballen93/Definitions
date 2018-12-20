package com.components.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public interface IDefinitionLookup {

    String getDefinition(final String term);

    default String getResponseAsString(String urlAsString) {
        String jsonResponse = "";

        try {
            URL url = new URL(urlAsString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                jsonResponse += line;
            }
            reader.close();
        } catch (IOException e) {

        }
        return jsonResponse;
    }
}
