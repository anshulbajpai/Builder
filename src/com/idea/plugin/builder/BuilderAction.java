package com.idea.plugin.builder;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;

import java.util.List;

import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;
import static com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE;
import static com.intellij.psi.util.PsiTreeUtil.getParentOfType;

public class BuilderAction extends AnAction {

    public void actionPerformed(AnActionEvent e) {
        PsiClass psiClass = getPsiClass(e);
        BuilderDialog dialog = new BuilderDialog(psiClass);
        dialog.show();
        if (dialog.isOK()) {
            List<PsiField> fields = dialog.getFields();
            if(fields.size() > 0){
                new BuilderGenerator(psiClass, fields).generate();
            }
        }
    }

    @Override
    public void update(AnActionEvent e) {
        PsiClass psiClass = getPsiClass(e);
        e.getPresentation().setEnabled(psiClass != null);
    }

    private PsiClass getPsiClass(AnActionEvent e) {
        PsiFile psiFile = e.getData(PSI_FILE);
        Editor editor = e.getData(EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        return getParentOfType(elementAt, PsiClass.class);
    }
}
