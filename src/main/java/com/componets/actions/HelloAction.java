package com.componets.actions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.Gray;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HelloAction extends AnAction {

    private String elementKeyword;
    private String elementText;

    public void actionPerformed(AnActionEvent e) {
        getPsiClassFromContext(e);
        displayDialog();
    }

    private void displayDialog() {
        String javaTermAndDefinition = "Java Term: " + elementText + "<br>Java Definition: " + getJavaDefinition(elementText);
        String urbanTermAndDefinition = "Urban Term: " + elementText + "<br>Definition " + getDefinitionFromJsonString(getJSONString("http://api.urbandictionary.com/v0/define?term={" + elementText + "}")).replace("<br><br>", "<br>");
        JEditorPane editorPane = new JEditorPane("text/html", "<font face=\"Nunito\">" + javaTermAndDefinition + "<br><br><br>" + urbanTermAndDefinition + "</font>");
        editorPane.addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                try {
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(e.getURL().toString()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        editorPane.setEditable(false);
        editorPane.setBackground(Gray._242);
        JOptionPane.showMessageDialog(null, editorPane, "Definitions", JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void update(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);
    }

    private PsiClass getPsiClassFromContext(AnActionEvent actionEvent) {
        PsiFile psiFile = actionEvent.getData(LangDataKeys.PSI_FILE);
        Editor editor = actionEvent.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement psiElement = psiFile.findElementAt(offset);

        setElementText(psiElement.getText().replace("\"", ""));

        //TODO
        /*try {
            setElementKeyword(((PsiKeywordImpl) psiElement).getTokenType().toString());
        } catch (Exception e) {
            setElementKeyword(((PsiIdentifierImpl) psiElement).getTokenType().toString());
        }*/

        return PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
    }

    private String getJavaDefinition(String term) {
        try {
            JavaTermsGlossary.buildGlossary();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JavaTermsGlossary.getDefinition(term);
    }

    private void setElementText(String elementText) {
        this.elementText = elementText;
    }

    private void setElementKeyword(String elementKeyword) {
        this.elementKeyword = elementKeyword;
    }

    private String getDefinitionFromJsonString(String JsonString) {
        JsonObject jsonObject = new Gson().fromJson(JsonString, JsonObject.class);
        JsonArray jsonArray = jsonObject.getAsJsonArray("list");
        jsonObject = jsonArray.get(0).getAsJsonObject();
        System.out.println(jsonObject.get("definition").getAsString());

        return jsonObject.get("definition").getAsString();
    }

    private String getJSONString(String url){
        String JsonString = "";
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            StringBuffer sb = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                JsonString += line;
            }
        } catch (MalformedURLException e){

        } catch (IOException e){

        }
        return JsonString;
    }
}