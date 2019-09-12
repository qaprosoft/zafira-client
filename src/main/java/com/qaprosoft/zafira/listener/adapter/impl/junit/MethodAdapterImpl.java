package com.qaprosoft.zafira.listener.adapter.impl.junit;

import com.qaprosoft.zafira.listener.adapter.MethodAdapter;
import com.qaprosoft.zafira.listener.adapter.TestAnnotationAdapter;
import org.junit.runner.Description;

import java.lang.annotation.Annotation;

public class MethodAdapterImpl implements MethodAdapter {

    private final Description methodDescription;

    public MethodAdapterImpl(Description methodDescription) {
        this.methodDescription = methodDescription;
    }

    @Override
    public Object getMethod() {
        return methodDescription;
    }

    @Override
    public Annotation[] getMethodAnnotations() {
        return methodDescription.getAnnotations().toArray(new Annotation[0]);
    }

    @Override
    public String getMethodName() {
        return methodDescription.getMethodName();
    }

    @Override
    public String getDeclaredClassName() {
        return methodDescription.getClassName();
    }

    @Override
    public String getTestClassName() {
        return methodDescription.getTestClass().getName();
    }

    @Override
    public String getRealClassName() {
        return methodDescription.getTestClass().getName();
    }

    @Override
    public String[] getMethodDependsOnMethods() {
        return new String[] {};
    }

    @Override
    public boolean isBeforeClassConfiguration() {
        return false;
    }

    @Override
    public boolean isAfterClassConfiguration() {
        return false;
    }

    @Override
    public boolean isBeforeTestConfiguration() {
        return false;
    }

    @Override
    public boolean isAfterTestConfiguration() {
        return false;
    }

    @Override
    public TestAnnotationAdapter getTestAnnotationAdapter() {
         return new TestAnnotationAdapterImpl(methodDescription);
    }

}
