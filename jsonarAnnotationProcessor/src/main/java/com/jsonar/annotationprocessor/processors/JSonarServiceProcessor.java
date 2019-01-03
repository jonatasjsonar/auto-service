package com.jsonar.annotationprocessor.processors;

import com.jsonar.annotationprocessor.dtos.ClassDto;
import com.jsonar.annotationprocessor.visitors.ClassVisitor;
import com.jsonar.annotationprocessor.visitors.MethodVisitor;
import com.jsonar.annotationprocessor.visitors.ProxyInterfaceVisitor;
import com.squareup.javapoet.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"com.jsonar.annotation.JSonarService"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({"generatedSourcesDirectory", "servicePath", "serviceAddress"})
public class JSonarServiceProcessor extends AbstractProcessor {

    public static final String GENERATED_SOURCES_DIRECTORY_OPTION = "generatedSourcesDirectory";
    public static final String SERVICE_PATH_OPTION = "servicePath";
    public static final String SERVICE_ADDRESS_OPTION = "serviceAddress";
    public static final String SOURCE_SUFFIX = ".java";
    public static final String PROXY_SUFFIX = "Proxy";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, String> options = this.processingEnv.getOptions();

        if (!isValidOptions(options)) {
            error("Invalid option(s) or missing one or more mandatory options.", null);
            return true;
        }

        for (TypeElement annotation : annotations) {
            List<Element> toProcess = getElementsToProcess(roundEnv, annotation);
            if (toProcess.isEmpty()) {
                continue;
            }

            toProcess.forEach(annotatedClass -> {
                TypeElement typeClass = (TypeElement) annotatedClass;

                TypeSpec.Builder classBuilder = typeClass.accept(new ClassVisitor(), processingEnv);
                List<ExecutableElement> classMethods = new ArrayList<>();

                TypeSpec.Builder interfaceBuilder = typeClass.accept(new ProxyInterfaceVisitor(), processingEnv);
                Set<ClassDto> sourcesToWrite = new LinkedHashSet<>();

                for (Element element : processingEnv.getElementUtils().getAllMembers(typeClass)) {
                    if (Objects.equals(ElementKind.METHOD, element.getKind()) && Objects.nonNull(element.getAnnotation(Path.class))) {
                        ExecutableElement methodElement = (ExecutableElement) element;
                        classMethods.add(methodElement);

                        // Create the method for ProxyInterface
                        MethodSpec.Builder proxyMethodBuilder = methodElement.accept(new MethodVisitor(), processingEnv);
                        proxyMethodBuilder.addModifiers(Modifier.ABSTRACT);
                        MethodSpec method = proxyMethodBuilder.build();
                        interfaceBuilder.addMethod(method);

                        checkForDependencies(element, sourcesToWrite);
                    }
                }

                TypeSpec newInterfaceType = interfaceBuilder.build();
                sourcesToWrite.add(new ClassDto(
                        typeClass.getSimpleName().toString() + PROXY_SUFFIX,
                        processingEnv.getElementUtils().getPackageOf(typeClass).getQualifiedName().toString(),
                        newInterfaceType)
                );

                classMethods.forEach((methodElement) -> {
                    MethodSpec.Builder builder = methodElement.accept(new MethodVisitor(), processingEnv);
                    MethodSpec method = builder.build();
                    List<String> parameterNameStream = methodElement.getParameters()
                            .stream()
                            .map(parameter -> parameter.getSimpleName().toString())
                            .collect(Collectors.toList());

                    builder.addStatement("$1T client = ($1T) $2T.newBuilder().register($3T.class).build()", ResteasyClient.class, ClientBuilder.class, ResteasyJackson2Provider.class);
                    builder.addStatement("$T target = client.target($T.fromPath($S))", ResteasyWebTarget.class, UriBuilder.class, options.get(SERVICE_ADDRESS_OPTION) + options.get(SERVICE_PATH_OPTION));
                    builder.addStatement("$1N proxy = target.proxy($1N.class)", newInterfaceType);
                    builder.addStatement("return proxy.$N($L)", method, StringUtils.join(parameterNameStream, ","));

                    classBuilder.addMethod(builder.build());
                });

                sourcesToWrite.add(new ClassDto(
                        typeClass.getSimpleName().toString(),
                        processingEnv.getElementUtils().getPackageOf(typeClass).getQualifiedName().toString(),
                        classBuilder.build())
                );

                sourcesToWrite.forEach(source -> writeSourceFile(source, options));
            });
        }


        return true;
    }

    private Set<ClassDto> checkForDependencies(Element element, Set<ClassDto> sourcesToWrite) {
        try {
            List<TypeMirror> mightBeDependecy = new ArrayList<>();

            switch (element.getKind()) {
                case METHOD:
                    ExecutableElement method = (ExecutableElement) element;
                    mightBeDependecy.add(method.getReturnType());
                    method.getParameters().forEach(param -> mightBeDependecy.add(param.asType()));
                    mightBeDependecy.addAll(method.getThrownTypes());

                    break;

                case FIELD:
                    VariableElement field = (VariableElement) element;
                    mightBeDependecy.add(field.asType());

                    break;
            }

            for (TypeMirror typeMirror : mightBeDependecy) {
                if (typeMirror.getKind().isPrimitive()
                        || StringUtils.startsWith(typeMirror.toString(), "java.lang")) {
                    continue;
                }

                Element typeElement = processingEnv.getTypeUtils().asElement(typeMirror);
                if (typeElement == null) {
                    continue;
                }

                ClassDto newDependencyDto = new ClassDto();
                newDependencyDto.setClassName(typeElement.getSimpleName().toString());
                newDependencyDto.setPackageName(processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString());

                if (sourcesToWrite.contains(newDependencyDto)) {
                    continue;
                }

                try {
                    // If success, the dependency already exists and there's no need to create it
                    Class.forName(typeMirror.toString());

                } catch (ClassNotFoundException e) {
                    TypeSpec.Builder newDependency = typeElement.accept(new ClassVisitor(), processingEnv);
                    List<Element> newDependenciesToCheck = new ArrayList<>();

                    typeElement.getEnclosedElements().forEach(ele -> {
                        if (Objects.equals(ElementKind.METHOD, ele.getKind())) {
                            MethodSpec.Builder methodBuilder = ele.accept(new MethodVisitor(), processingEnv);
                            processDependencyMethod((ExecutableElement) ele, methodBuilder);
                            newDependency.addMethod(methodBuilder.build());
                            newDependenciesToCheck.add(ele);

                        } else if (Objects.equals(ElementKind.FIELD, ele.getKind())) {
                            FieldSpec.Builder fieldBuilder = FieldSpec.builder(
                                    TypeName.get(ele.asType()),
                                    ele.getSimpleName().toString(),
                                    ele.getModifiers().toArray(new Modifier[ele.getModifiers().size()])
                            );

                            ele.getAnnotationMirrors().forEach(annotationMirror -> fieldBuilder.addAnnotation(AnnotationSpec.get(annotationMirror)));
                            newDependency.addField(fieldBuilder.build());
                            newDependenciesToCheck.add(ele);
                        }
                    });

                    newDependencyDto.setSource(newDependency.build());
                    sourcesToWrite.add(newDependencyDto);
                    newDependenciesToCheck.forEach(ele -> checkForDependencies(ele, sourcesToWrite));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            error("Error: " + e.getMessage() + "\nStacktrace: " + ExceptionUtils.getStackTrace(e), element);
        }

        return sourcesToWrite;
    }

    private MethodSpec.Builder processDependencyMethod(ExecutableElement method, MethodSpec.Builder builder) {
        String methodName = method.getSimpleName().toString();

        if (StringUtils.startsWith(methodName, "set")) {
            String attr = StringUtils.removeStart(methodName, "set");
            attr = StringUtils.uncapitalize(attr);

            builder.addStatement("this.$L = $L", attr, method.getParameters().get(0).getSimpleName().toString());

        } else if (StringUtils.startsWith(methodName, "get")) {
            String attr = StringUtils.removeStart(methodName, "get");
            attr = StringUtils.uncapitalize(attr);

            builder.addStatement("return $L", attr);

        } else {
            builder.addComment("Implementation not available");

            switch (method.getReturnType().getKind()) {
                case INT:
                case LONG:
                case DOUBLE:
                    builder.addStatement("return 0");
                    break;

                case BOOLEAN:
                    builder.addStatement("return false");
                    break;

                case DECLARED:
                    builder.addStatement("return null");
                    break;
            }
        }

        return builder;
    }

    private void writeSourceFile(ClassDto source, Map<String, String> options) {
        try {
            StringBuilder path = new StringBuilder();
            path.append(options.get(GENERATED_SOURCES_DIRECTORY_OPTION))
                    .append(File.separator)
                    .append(StringUtils.replace(source.getPackageName(), ".", File.separator))
                    .append(File.separator);

            Files.createDirectories(Paths.get(path.toString()));
            path.append(source.getClassName())
                    .append(SOURCE_SUFFIX);

            try (Writer out = new FileWriter(path.toString(), false)) {
                JavaFile.builder(source.getPackageName(), source.getSource())
                        .build()
                        .writeTo(out);
            }

        } catch (IOException e) {
            e.printStackTrace();
            error(e.getMessage(), null);
        }
    }

    private List<Element> getElementsToProcess(RoundEnvironment roundEnv, TypeElement annotation) {
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

        Map<Boolean, List<Element>> annotatedClasses = annotatedElements.stream().collect(
                Collectors.partitioningBy(element -> ElementKind.CLASS == element.getKind())
        );

        List<Element> toProcess = annotatedClasses.get(true);
        List<Element> wrongUseAnnotation = annotatedClasses.get(false);

        wrongUseAnnotation.forEach(element -> error("@" + annotation.getSimpleName().toString() + " must be applied to classes", element));
        return toProcess;
    }

    private boolean isValidOptions(Map<String, String> options) {
        List<String> optionsNotFound = new ArrayList<>();

        if (!options.containsKey(GENERATED_SOURCES_DIRECTORY_OPTION)) {
            optionsNotFound.add(GENERATED_SOURCES_DIRECTORY_OPTION);
        }

        if (!options.containsKey(SERVICE_PATH_OPTION)) {
            optionsNotFound.add(SERVICE_PATH_OPTION);
        }

        if (!options.containsKey(SERVICE_ADDRESS_OPTION)) {
            optionsNotFound.add(SERVICE_ADDRESS_OPTION);
        }

        if (!optionsNotFound.isEmpty()) {
            error("Error: Required option(s) (" + StringUtils.join(optionsNotFound, ", ") + ") not found. Use the compiler argument -Akey=value to inform it.", null);
            return false;
        }

        return true;
    }

    private void error(String msg, Element element) {
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, element);
    }

    private void note(String msg, Element element) {
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg, element);
    }
}
