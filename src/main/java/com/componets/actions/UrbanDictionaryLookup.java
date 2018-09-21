package com.componets.actions;

public class UrbanDictionaryLookup implements DefinitionLookup{

    private String word;

    public UrbanDictionaryLookup(String word){
        this.word = word;
    }
    //https://github.com/zdict/zdict/wiki/Urban-dictionary-API-documentation
    //https://github.com/stleary/JSON-java

    public String getDefinition(String term) {
        return "";
    }



}
