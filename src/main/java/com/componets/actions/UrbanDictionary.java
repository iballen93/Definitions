package com.componets.actions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

public class UrbanDictionary implements IDefinitionLookup {

    public String getDefinition(String term) {
        String jsonResponse;
        String url = "http://api.urbandictionary.com/v0/define?term={" + term + "}";

        jsonResponse = getResponseAsString(url);
        return getElementFromJsonResponse(jsonResponse, "definition");
    }

    public String getExample(String term){
        String jsonResponse;
        String url = "http://api.urbandictionary.com/v0/define?term={" + term + "}";
        jsonResponse = getResponseAsString(url);
        return getElementFromJsonResponse(jsonResponse, "example");
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

    private String getElementFromJsonResponse(String jsonResponse, String element){
        JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
        JsonArray jsonArray = jsonObject.getAsJsonArray("list");
        if(jsonArray.size() > 0){
            jsonObject = jsonArray.get(0).getAsJsonObject();
            return(jsonObject.get(element).getAsString().replace("[", "").replace("]", ""));
        }
        return "";
    }
}
