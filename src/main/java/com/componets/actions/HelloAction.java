package com.componets.actions;

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

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import java.io.IOException;

public class HelloAction extends AnAction {

    private JavaTermsGlossary javaTermsGlossary;
    private UrbanDictionaryLookup urbanDictionaryLookup;
    private WordsAPILookup wordsAPILookup;

    private String elementKeyword;
    private String elementText;

    public void actionPerformed(AnActionEvent e) {
        javaTermsGlossary = new JavaTermsGlossary();
        urbanDictionaryLookup = new UrbanDictionaryLookup();
        wordsAPILookup = new WordsAPILookup();
        getPsiClassFromContext(e);
        displayDialog();
    }

    private void displayDialog() {
        String javaTermAndDefinition = "Java Glossary<br>Term: " + elementText + "<br>Definition: " + javaTermsGlossary.getDefinition(elementText);
        String wordsApiTermAndDefinition = "Term: " + elementText + "<br>Definition: " + wordsAPILookup.getDefinition(elementText);
        String urbanTermAndDefinition = "Urban Dictionary<br>Term: " + elementText + "<br>Definition: " + urbanDictionaryLookup.getDefinition(elementText);
        JEditorPane editorPane = new JEditorPane("text/html", "<font face=\"Nunito\">" + javaTermAndDefinition + "<br><br><br>" + wordsApiTermAndDefinition + "<br><br><br>" + urbanTermAndDefinition +"</font>");
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

    private void setElementText(String elementText) {
        this.elementText = elementText;
    }

    private void setElementKeyword(String elementKeyword) {
        this.elementKeyword = elementKeyword;
    }
}