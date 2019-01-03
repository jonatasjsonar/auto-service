package com.jsonar.annotationprocessor.visitors;

import com.jsonar.annotation.JSonarService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic;

public class ClassVisitor extends SimpleElementVisitor8<TypeSpec.Builder, ProcessingEnvironment> {
    @Override
    public TypeSpec.Builder visitType(TypeElement e, ProcessingEnvironment processingEnvironment) {
        note(">>> Processing class", e, processingEnvironment);

        TypeSpec.Builder newClass = TypeSpec
                .classBuilder(e.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC);

        e.getAnnotationMirrors().stream()
                .filter(annotation -> !StringUtils.equals(JSonarService.class.getName(), annotation.getAnnotationType().toString()))
                .forEach(annotation -> newClass.addAnnotation(AnnotationSpec.get(annotation)));

        return newClass;
    }

    private void note(String msg, Element element, ProcessingEnvironment processingEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg, element);
    }
}
