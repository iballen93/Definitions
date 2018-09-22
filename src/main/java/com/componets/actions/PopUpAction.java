package com.componets.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.Gray;
import org.fest.util.Strings;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;

public class PopUpAction extends AnAction {

    private JavaTermsGlossary javaTermsGlossary;
    private UrbanDictionaryLookup urbanDictionaryLookup;
    private Wiktionary wiktionary;

    private String elementKeyword;
    private String elementLanguage;
    private String elementText;

    public void actionPerformed(AnActionEvent e) {
        javaTermsGlossary = new JavaTermsGlossary();
        urbanDictionaryLookup = new UrbanDictionaryLookup();
        wiktionary = new Wiktionary();
        getPsiClassFromContext(e);
        showUsersPopup(e);
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
        setElementLanguage(psiElement.getLanguage().getDisplayName());

        //TODO
        /*try {
            setElementKeyword(((PsiKeywordImpl) psiElement).getTokenType().toString());
        } catch (Exception e) {
            setElementKeyword(((PsiIdentifierImpl) psiElement).getTokenType().toString());
        }*/

        return PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
    }

    public void showUsersPopup(AnActionEvent event) {
        JEditorPane editorPane = new JEditorPane("text/html", "<font style=\"font-family:'monospace'\">" + getDefinitions() + "</font>");
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

        ComponentPopupBuilder builder = JBPopupFactory.getInstance().createComponentPopupBuilder(editorPane, null);
        builder.setCancelOnClickOutside(true);

        JBPopup popup = builder.createPopup();

        popup.setSize(new Dimension(550, 500)); // dont see a good way for adjustable height.
        popup.moveToFitScreen();
        popup.showInBestPositionFor(PlatformDataKeys.EDITOR.getData(event.getDataContext()));

    }

    //TODO refactor out the checking of strings. Limit the max size of the intellij popup
    private String getDefinitions() {
        String definitions = "";
        String javaTermAndDefinition = "<h3>Java Glossary</h3>Term: " + elementText + "<br>Definition: " + javaTermsGlossary.getDefinition(elementText);
        definitions += javaTermAndDefinition;

        String dictionaryTerm = "<h3>Dictionary</h3>Term: " + elementText + "<br>Definition: " + wiktionary.getDefinition(elementText);
        definitions += "<br><br><br>" + dictionaryTerm;

        String urbanDefinition = urbanDictionaryLookup.getDefinition(elementText);
        if (!Strings.isNullOrEmpty(urbanDefinition)) {
            String urbanTermAndDefinition = "<h3>Urban Dictionary</h3>Term: " + elementText + "<br>Definition: " + urbanDefinition;
            String urbanExample = "<br>Example: " + urbanDictionaryLookup.getExample(elementText);
            definitions += "<br><br><br>" + urbanTermAndDefinition + urbanExample;
        }
        return definitions;
    }

    private void setElementText(String elementText) {
        this.elementText = elementText;
    }

    private void setElementLanguage(String elementLanguage) {
        this.elementLanguage = elementLanguage;
    }

    private void setElementKeyword(String elementKeyword) {
        this.elementKeyword = elementKeyword;
    }
}