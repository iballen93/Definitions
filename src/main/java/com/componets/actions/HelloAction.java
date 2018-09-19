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

import javax.swing.*;
import java.io.IOException;

public class HelloAction extends AnAction {

    private String elementText;

    public void actionPerformed(AnActionEvent e) {
        getPsiClassFromContext(e);
        JFrame frame = new JFrame("FrameDemo");
        JOptionPane.showMessageDialog(frame, getJavaDefinition(elementText));
    }

    @Override
    public void update(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        e.getPresentation().setEnabled(psiClass != null);
    }


    private PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement psiElement = psiFile.findElementAt(offset);
        setElementText(psiElement.getText().replace("\"", ""));
        return PsiTreeUtil.getParentOfType(psiElement, PsiClass.class);
    }

    private void setElementText(String elementText) {
        this.elementText = elementText;
    }

    private String getJavaDefinition(String term) {
        try {
            JavaTermsGlossary.buildGlossary();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JavaTermsGlossary.getDefinition(term);
    }
}