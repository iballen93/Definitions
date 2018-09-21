package com.componets.actions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

public class UrbanDictionaryLookup implements IDefinitionLookup {
    //https://github.com/zdict/zdict/wiki/Urban-dictionary-API-documentation
    //https://github.com/stleary/JSON-java


    public String getDefinition(String term) {
        String jsonResponse;
        String url = "http://api.urbandictionary.com/v0/define?term={" + term + "}";

        jsonResponse = getResponseAsString(url);
        return getDefinitionFromJsonString(jsonResponse);
    }

    private String getDefinitionFromJsonString(String JsonString) {
        JsonObject jsonObject = new Gson().fromJson(JsonString, JsonObject.class);
        JsonArray jsonArray = jsonObject.getAsJsonArray("list");
        jsonObject = jsonArray.get(0).getAsJsonObject();
        System.out.println(jsonObject.get("definition").getAsString());

        return jsonObject.get("definition").getAsString().replace("[", "").replace("]", "");
    }

    private String getResponseAsString(String urlAsString){
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
