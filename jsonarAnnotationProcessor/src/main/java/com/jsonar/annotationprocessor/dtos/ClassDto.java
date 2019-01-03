package com.jsonar.annotationprocessor.dtos;

import com.squareup.javapoet.TypeSpec;

import java.util.Objects;

public class ClassDto {
    private String className;
    private String packageName;
    private TypeSpec source;

    public ClassDto() {
    }

    public ClassDto(String className, String packageName, TypeSpec source) {
        this.className = className;
        this.packageName = packageName;
        this.source = source;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public TypeSpec getSource() {
        return source;
    }

    public void setSource(TypeSpec source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassDto dto = (ClassDto) o;
        return Objects.equals(className, dto.className) &&
                Objects.equals(packageName, dto.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, packageName);
    }
}
