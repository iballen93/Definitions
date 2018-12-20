package com.components.actions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class UrbanDictionary implements IDefinitionLookup {

    private String definition;
    private String example;

    public String getDefinition(String term) {
        String jsonResponse;
        String url = "http://api.urbandictionary.com/v0/define?term={" + term + "}";

        jsonResponse = getResponseAsString(url);
        definition = getElementFromJsonResponse(jsonResponse, "definition");
        return definition;
    }

    public String getExample(String term) {
        String jsonResponse;
        String url = "http://api.urbandictionary.com/v0/define?term={" + term + "}";
        jsonResponse = getResponseAsString(url);
        example = getElementFromJsonResponse(jsonResponse, "example");
        return example;
    }


    private String getElementFromJsonResponse(String jsonResponse, String element) {
        JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
        JsonArray jsonArray = jsonObject.getAsJsonArray("list");
        if (jsonArray.size() > 0) {
            jsonObject = jsonArray.get(0).getAsJsonObject();
            return (jsonObject.get(element).getAsString().replace("[", "").replace("]", ""));
        }
        return "";
    }

    public String getDef() {
        return this.definition;
    }

    public String getExm() {
        return this.example;
    }

}
