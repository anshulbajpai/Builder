package com.idea.plugin.builder;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;

import java.util.List;

import static com.intellij.psi.JavaPsiFacade.getElementFactory;

public class BuilderGenerator {

    private final PsiClass ownerClass;
    private final List<PsiField> fieldsToGenerate;
    private final PsiElementFactory elementFactory;

    public BuilderGenerator(PsiClass ownerClass, List<PsiField> fieldsToGenerate) {
        this.ownerClass = ownerClass;
        this.fieldsToGenerate = fieldsToGenerate;
        elementFactory = getElementFactory(ownerClass.getProject());
    }

    public void generate() {
        new WriteCommandAction.Simple(ownerClass.getProject(), ownerClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                generateBuilderCode();
            }

        }.execute();
    }

    private void generateBuilderCode() {
        createBuilderClass();
        createPrivateConstructorWithBuilder();
        createBuilderFactoryMethod();
    }

    private void createPrivateConstructorWithBuilder() {
        StringBuilder methodText = new StringBuilder("private ").append(ownerClass.getName()).append("(Builder builder){\n");
        for (PsiField field : fieldsToGenerate) {
            methodText.append("this.").append(field.getName()).append(" = ").append("builder.").append(field.getName()).append(";\n");
        }
        methodText.append("}");
        PsiMethod method = createMethod(methodText);
        ownerClass.add(method);
    }

    private void createBuilderFactoryMethod() {
        StringBuilder methodText = new StringBuilder("public static Builder new").append(ownerClass.getName()).append("(){\n")
                .append("return new Builder();\n")
                .append("}");
        PsiMethod method = createMethod(methodText);
        ownerClass.add(method);
    }

    private void createBuilderClass() {
        PsiClass builderClass = elementFactory.createClass("Builder");
        builderClass.getModifierList().setModifierProperty(PsiModifier.FINAL, true);
        builderClass.getModifierList().setModifierProperty(PsiModifier.STATIC, true);
        PsiMethod constructor = elementFactory.createConstructor();
        constructor.getModifierList().setModifierProperty(PsiModifier.PRIVATE, true);
        builderClass.add(constructor);
        for (PsiField field : fieldsToGenerate) {
            builderClass.add(createBuilderField(field));
            builderClass.add(createBuilderMethod(field));
        }
        builderClass.add(createBuildMethod());
        ownerClass.add(builderClass);
    }

    private PsiElement createBuildMethod() {
        StringBuilder methodText = new StringBuilder("public ").append(ownerClass.getName()).append(" build(){\n")
                .append("return new ").append(ownerClass.getName()).append("(this);\n")
                .append("}");
        return createMethod(methodText);
    }

    private PsiElement createBuilderField(PsiField field) {
        PsiField newField = elementFactory.createField(field.getName(), field.getType());
        newField.getModifierList().setModifierProperty(PsiModifier.PRIVATE, true);
        return newField;
    }

    private PsiMethod createBuilderMethod(PsiField field) {
        StringBuilder methodText = new StringBuilder("public Builder ").append(field.getName())
                .append("(").append(field.getType().getPresentableText()).append(" ").append(field.getName()).append("){\n")
                .append("this.").append(field.getName()).append(" = ").append(field.getName()).append(";\n")
                .append("return this;\n")
                .append("}");
        return createMethod(methodText);
    }

    private PsiMethod createMethod(StringBuilder methodText) {
        return elementFactory.createMethodFromText(methodText.toString(), null);
    }

}
