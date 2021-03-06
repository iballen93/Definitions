package com.components.actions;

import com.google.common.base.Strings;
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
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Objects;

import static com.components.actions.HtmlFormatter.applyBold;
import static com.components.actions.HtmlFormatter.applyFontFace;
import static com.components.actions.HtmlFormatter.applyFontSize;

public class PopUpAction extends AnAction {

    private static final String DEFINITION = "<b>Definition: </b>";
    private static final String TERM = "<b>Term: </b>";
    private static final String LANGUAGE = "<b>Language: </b>";
    private static final String TOKEN_TYPE = "<b>Token Type: </b>";

    private JavaTermsGlossary javaTermsGlossary;
    private UrbanDictionary urbanDictionary;
    private Wiktionary wiktionary;

    private String elementKeyword;
    private String elementLanguage;
    private String elementText;

    public void actionPerformed(AnActionEvent e) {
        javaTermsGlossary = new JavaTermsGlossary();
        urbanDictionary = new UrbanDictionary();
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

        setElementText(psiElement != null ? psiElement.getText().replace("\"", "") : "No Text");
        setElementLanguage(psiElement != null ? psiElement.getLanguage().getDisplayName() : "No Language");
        setElementTokenType(psiElement != null ? ((PsiJavaToken) psiElement).getTokenType().toString() : "No Token Type");

        return PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
    }

    private void showUsersPopup(AnActionEvent event) {
        JEditorPane editorPane = new JEditorPane("text/html", applyFontFace("Monospaced", getDefinitions()));
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
        if (UIUtil.isUnderDarcula()) {
            editorPane.setBackground(JBColor.WHITE);
        } else {
            editorPane.setBackground(Gray._242);
        }

        ComponentPopupBuilder builder = JBPopupFactory.getInstance().createComponentPopupBuilder(editorPane, null);
        builder.setCancelOnClickOutside(true);

        JBPopup popup = builder.createPopup();
        popup.setSize(new Dimension(550, 500)); // dont see a good way for adjustable height.
        popup.moveToFitScreen();
        popup.showInBestPositionFor(Objects.requireNonNull(PlatformDataKeys.EDITOR.getData(event.getDataContext())));
    }

    //TODO make this behave like javaTerms when def not found
    private String getDefinitions() {
        String definitions = "";
        definitions += applyFontSize(4, TERM + elementText);
        definitions += "<br>";
        definitions += applyFontSize(4, LANGUAGE + elementLanguage);
        definitions += "<br>";
        definitions += applyFontSize(4, TOKEN_TYPE + elementKeyword);
        definitions += "<br><br>";

        definitions += applyFontSize(5, applyBold("Java Glossary<br>"));
        long start = System.nanoTime();
        definitions += applyFontSize(4, DEFINITION + javaTermsGlossary.getDefinition(elementText));
        System.out.println(String.format("Java Terms Glossary Performance: %sms", (System.nanoTime() - start) / 1000000));
        definitions += "<br><br>";

        definitions += applyFontSize(5, applyBold("Dictionary<br>"));
        start = System.nanoTime();
        definitions += applyFontSize(4, DEFINITION + (Strings.isNullOrEmpty(wiktionary.getDefinition(elementText)) ? "not found" : wiktionary.getDef()));
        System.out.println(String.format("Wiktionary Performance: %sms", (System.nanoTime() - start) / 1000000));
        definitions += "<br><br>";

        definitions += applyFontSize(5, applyBold("Urban Dictionary<br>"));
        start = System.nanoTime();
        definitions += applyFontSize(4, DEFINITION + (Strings.isNullOrEmpty(urbanDictionary.getDefinition(elementText)) ? "not found" : urbanDictionary.getDef()));
        definitions += "<br><br>";
        definitions += applyFontSize(4, (Strings.isNullOrEmpty(urbanDictionary.getExample(elementText)) ? "" : applyBold("Example: ") + urbanDictionary.getExm()));
        System.out.println(String.format("Urban Dictionary Performance: %sms", (System.nanoTime() - start) / 1000000));
        return definitions;
    }

    private void setElementText(String elementText) {
        this.elementText = elementText;
    }

    private void setElementLanguage(String elementLanguage) {
        this.elementLanguage = elementLanguage;
    }

    private void setElementTokenType(String elementKeyword) {
        this.elementKeyword = elementKeyword;
    }
}