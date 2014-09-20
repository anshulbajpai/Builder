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

    private PsiField[] getFieldsToAdd(PsiClass ownerClass) {
        PsiClass builderClass = ownerClass.findInnerClassByName("Builder", true);
        if(builderClass != null){
            return additionalFields(ownerClass, builderClass);
        }
        return ownerClass.getAllFields();
    }

    private PsiField[] additionalFields(PsiClass ownerClass, PsiClass builderClass) {
        ArrayList<PsiField> additionalFields = new ArrayList<PsiField>();
        for (PsiField psiField : ownerClass.getFields()) {
            PsiField field = builderClass.findFieldByName(psiField.getName(), true);
            if(field == null){
                additionalFields.add(psiField);
            }
        }
        return additionalFields.toArray(new PsiField[]{});
    }

    @Override
    protected JComponent createCenterPanel() {
        return component;
    }

    public java.util.List<PsiField> getFields() {
        return fieldList.getSelectedValuesList();
    }
}
