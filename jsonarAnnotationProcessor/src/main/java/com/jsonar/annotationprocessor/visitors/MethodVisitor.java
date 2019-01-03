package com.jsonar.annotationprocessor.visitors;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic;

public class MethodVisitor extends SimpleElementVisitor8<MethodSpec.Builder, ProcessingEnvironment> {
    @Override
    public MethodSpec.Builder visitExecutable(ExecutableElement e, ProcessingEnvironment processingEnvironment) {
//        debugInfo(e, processingEnvironment);

        MethodSpec.Builder method = MethodSpec.methodBuilder(e.getSimpleName().toString())
                .addModifiers(e.getModifiers())
                .returns(TypeName.get(e.getReturnType()));

        e.getParameters().forEach(parameter -> {
            ParameterSpec.Builder parameterSpec = ParameterSpec.builder(
                    TypeName.get(parameter.asType()),
                    parameter.getSimpleName().toString(),
                    parameter.getModifiers().toArray(new Modifier[parameter.getModifiers().size()])
            );

            parameter.getAnnotationMirrors().forEach(annotation -> parameterSpec.addAnnotation(AnnotationSpec.get(annotation)));
            method.addParameter(parameterSpec.build());
        });

        e.getThrownTypes().forEach(exception -> method.addException(TypeName.get(exception)));
        e.getAnnotationMirrors().forEach(annotation -> method.addAnnotation(AnnotationSpec.get(annotation)));

        return method;
    }

    private void debugInfo(ExecutableElement e, ProcessingEnvironment processingEnvironment) {
        note("\n>>> Visiting executable element", e, processingEnvironment);
        note("> Name: " + e.getSimpleName().toString(), null, processingEnvironment);
        note("> Return Type: " + e.getReturnType().toString(), null, processingEnvironment);
        note("> Modifiers: ", null, processingEnvironment);
        e.getModifiers().forEach(m -> note("  - " + m.toString(), null, processingEnvironment));
        note("> Annotations: ", null, processingEnvironment);
        e.getAnnotationMirrors().forEach(a -> note("  - " + a.toString(), null, processingEnvironment));
        note("> Parameters: ", null, processingEnvironment);
        e.getParameters().forEach(p -> {
            note("  - " + p.getSimpleName(), null, processingEnvironment);
            p.getAnnotationMirrors().forEach(a -> note("    @ " + a.getAnnotationType().toString(), null, processingEnvironment));
        });
    }

    private void note(String msg, Element element, ProcessingEnvironment processingEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg, element);
    }
}
