package com.componets.actions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Wiktionary implements IDefinitionLookup {

    public String getDefinition(String term) {
        String url = "https://googledictionaryapi.eu-gb.mybluemix.net/?define=" + term;
        String jsonResponse = getResponseAsString(url);
        return getElementFromJsonResponse(jsonResponse, "definition");
    }

    private String getResponseAsString(String urlAsString) {
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

    //TODO get adjective, noun, then verb.
    //https://googledictionaryapi.eu-gb.mybluemix.net/?define=specious
    private String getElementFromJsonResponse(String jsonResponse, String element) {
        JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
        JsonObject jsonObject1 = jsonObject.getAsJsonObject("meaning");
        JsonArray jsonArray = jsonObject1.getAsJsonArray("noun");
        if (jsonArray.size() > 0) {
            jsonObject = jsonArray.get(0).getAsJsonObject();
            return (jsonObject.get(element).getAsString().replace("[", "").replace("]", ""));
        }

        return "";
    }
}
