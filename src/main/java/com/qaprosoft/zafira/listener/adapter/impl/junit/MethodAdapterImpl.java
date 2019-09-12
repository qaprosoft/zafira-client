/*******************************************************************************
 * Copyright 2013-2019 Qaprosoft (http://www.qaprosoft.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
