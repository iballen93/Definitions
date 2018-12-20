package com.components.actions;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class Wiktionary implements IDefinitionLookup {

    private String definition;

    public String getDefinition(String term) {
        String url = "https://googledictionaryapi.eu-gb.mybluemix.net/?define=" + term;
        String jsonResponse = getResponseAsString(url);

        if (Strings.isNullOrEmpty(jsonResponse)) {
            return "";
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
                return "";
            }
            jsonObject = jsonArray.get(0).getAsJsonObject();
            return (jsonObject.get(element).getAsString().replace("[", "").replace("]", ""));
        } catch (JsonSyntaxException jse) {
            return "";
        }
    }

    public String getDef() {
        return this.definition;
    }
}
