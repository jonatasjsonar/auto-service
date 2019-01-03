package com.jsonar.annotationprocessor.visitors;

import com.jsonar.annotation.JSonarService;
import com.jsonar.annotationprocessor.processors.JSonarServiceProcessor;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;

public class ProxyInterfaceVisitor extends SimpleElementVisitor8<TypeSpec.Builder, ProcessingEnvironment> {
    @Override
    public TypeSpec.Builder visitType(TypeElement e, ProcessingEnvironment processingEnvironment) {
        TypeSpec.Builder newInterface = TypeSpec
                .interfaceBuilder(e.getSimpleName().toString() + JSonarServiceProcessor.PROXY_SUFFIX)
                .addModifiers(Modifier.PUBLIC);

        e.getAnnotationMirrors().stream()
                .filter(annotation -> !StringUtils.equals(JSonarService.class.getName(), annotation.getAnnotationType().toString()))
                .forEach(annotation -> newInterface.addAnnotation(AnnotationSpec.get(annotation)));

        return newInterface;
    }
}
