package com.componets.actions;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;

public class GenerateDialogue extends DialogWrapper {
    private final LabeledComponent<JPanel> myComponents;

    public GenerateDialogue(PsiClass psiClass, String elementText) {
        super(psiClass.getProject());
        init();
        setTitle("Definition");
        setOKButtonText("Thanks");

        setErrorText(getJavaDefinition(elementText));

        CollectionListModel<PsiField> myFields = new CollectionListModel<PsiField>(psiClass.getAllFields());

        JList fieldList = new JList(myFields);
        fieldList.setCellRenderer(new DefaultPsiElementCellRenderer());

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(fieldList);
        decorator.disableAddAction();

        JPanel panel = decorator.createPanel();
        myComponents = LabeledComponent.create(panel, "Fields");

    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return myComponents;
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