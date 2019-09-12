package com.qaprosoft.zafira.listener.adapter.impl.junit;

import com.qaprosoft.zafira.listener.adapter.TestAnnotationAdapter;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;

import java.lang.annotation.Annotation;

public class TestAnnotationAdapterImpl implements TestAnnotationAdapter {

    private final Description methodDescription;

    public TestAnnotationAdapterImpl(Description methodDescription) {
        this.methodDescription = methodDescription;
    }

    @Override
    public Class<? extends Annotation> getTestAnnotationClass() {
        return Test.class;
    }

    @Override
    public String getDataProviderName() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return methodDescription.getAnnotation(Ignore.class) != null;
    }
}
