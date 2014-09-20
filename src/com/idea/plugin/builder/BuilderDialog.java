package com.idea.plugin.builder;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import javax.swing.*;

import java.util.ArrayList;

import static com.intellij.openapi.ui.LabeledComponent.create;
import static com.intellij.ui.ToolbarDecorator.createDecorator;

public class BuilderDialog extends DialogWrapper {


    private final LabeledComponent<JPanel> component;
    private final JList fieldList;

    public BuilderDialog(PsiClass psiClass) {
        super(psiClass.getProject());

        setTitle("Select Fields for Builder");

        CollectionListModel<PsiField> fields = new CollectionListModel<PsiField>(getFieldsToAdd(psiClass));
        fieldList = new JBList(fields);
        fieldList.setCellRenderer(new DefaultPsiElementCellRenderer());
        ToolbarDecorator decorator = createDecorator(fieldList);
        decorator.disableAddAction();
        decorator.disableRemoveAction();
        decorator.disableUpDownActions();
        JPanel panel = decorator.createPanel();
        component = create(panel, "Fields to include in builder");

        init();
    }

    private PsiField[] getFieldsToAdd(PsiClass psiClass) {
        PsiField[] allFields = psiClass.getAllFields();
        PsiClass[] innerClasses = psiClass.getAllInnerClasses();
        for (PsiClass innerClass : innerClasses) {
            if(innerClass.getName().equals("Builder")){
                return additionalFields(allFields, innerClass.getAllFields());
            }
        }
        return allFields;
    }

    private PsiField[] additionalFields(PsiField[] allFields, PsiField[] existingFields) {
        ArrayList<PsiField> additionalFields = new ArrayList<PsiField>();
        for (PsiField psiField : allFields) {
            if(isANonExistingField(psiField, existingFields)){
                additionalFields.add(psiField);
            }
        }
        return additionalFields.toArray(new PsiField[]{});
    }

    private boolean isANonExistingField(PsiField psiField, PsiField[] existingFields) {
        for (PsiField existingField : existingFields) {
            if(existingField.getName().equals(psiField.getName())){
                return false;
            }
        }
        return true;
    }

    @Override
    protected JComponent createCenterPanel() {
        return component;
    }

    public java.util.List<PsiField> getFields() {
        return fieldList.getSelectedValuesList();
    }
}
