package com.components.actions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.fest.util.Strings;

public class Wiktionary implements IDefinitionLookup {

    private String definition;

    public String getDefinition(String term) {
        String url = "https://googledictionaryapi.eu-gb.mybluemix.net/?define=" + term;
        String jsonResponse = getResponseAsString(url);
        if (Strings.isNullOrEmpty(jsonResponse) ) {
            return "not found";
        }
        definition = getElementFromJsonResponse(jsonResponse, "definition");
        return definition;
    }

    //TODO get adjective, noun, then verb.
    //https://googledictionaryapi.eu-gb.mybluemix.net/?define=specious
    private String getElementFromJsonResponse(String jsonResponse, String element) {
        try {
            JsonObject jsonObject = new Gson().fromJson(jsonResponse, JsonObject.class);
            JsonObject jsonObject1 = jsonObject.getAsJsonObject("meaning");
            JsonArray jsonArray = jsonObject1.getAsJsonArray("noun");
            if (jsonArray == null) {
                return "not found";
            }
            jsonObject = jsonArray.get(0).getAsJsonObject();
            return (jsonObject.get(element).getAsString().replace("[", "").replace("]", ""));
        } catch (JsonSyntaxException jse) {
            return "not found";
        }
    }

    public String getDef(){
        return this.definition;
    }
}
