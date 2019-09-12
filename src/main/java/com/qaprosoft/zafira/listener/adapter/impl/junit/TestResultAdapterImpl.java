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
import com.qaprosoft.zafira.listener.adapter.SuiteAdapter;
import com.qaprosoft.zafira.listener.adapter.TestResultAdapter;
import com.qaprosoft.zafira.listener.adapter.TestResultStatus;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.testng.SkipException;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class TestResultAdapterImpl implements TestResultAdapter {

    private static final String ERR_MSG_UNABLE_TO_FIND_TEST_METHOD = "Unable to recognize test method '%s' without parameters in class '%s'";

    private final Description resultDescription;
    private Throwable throwable;

    public TestResultAdapterImpl(Description resultDescription) {
        this.resultDescription = resultDescription;
    }

    public TestResultAdapterImpl(Failure failure) {
        this.resultDescription = failure.getDescription();
        this.throwable = failure.getException();
    }

    public enum StatusDescription {
        FAILED("failed"),
        SKIPPED("skipped");

        private final Description description;

        StatusDescription(String stringStatus) {
            this.description = Description.createTestDescription(stringStatus, stringStatus);
        }

        public Description getDescription() {
            return description;
        }
    }

    // TODO: 9/12/19 move to info adapter, create it
    @Override
    public String getName() {
        return resultDescription.getMethodName();
    }

    @Override
    public Object[] getParameters() {
        return getTestMethod().getParameters();
    }

    @Override
    public void setAttribute(String name, Object value) {
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public TestResultStatus getStatus() {
        return resultDescription.getChildren().contains(StatusDescription.SKIPPED.getDescription()) ? TestResultStatus.SKIP : TestResultStatus.UNKNOWN;
    }

    @Override
    public Set<TestResultAdapter> getFailedTestResults() {
        Set<TestResultAdapter> adapters = new HashSet<>();
        adapters.add(this);
        return adapters;
    }

    @Override
    public Set<TestResultAdapter> getSkippedTestResults() {
        Set<TestResultAdapter> adapters = new HashSet<>();
        adapters.add(this);
        return adapters;
    }

    @Override
    public Set<String> getKnownClassNames() {
        Set<String> adapters = new HashSet<>();
        adapters.add(resultDescription.getTestClass().getName());
        return adapters;
    }

    @Override
    public RuntimeException getSkipExceptionInstance(String message) {
        return new SkipException(message);
    }

    @Override
    public MethodAdapter getMethodAdapter() {
        return new MethodAdapterImpl(resultDescription);
    }

    @Override
    public SuiteAdapter getSuiteAdapter() {
        return null;
    }

    public static void markAs(Description description, StatusDescription statusDescription) {
        description.addChild(statusDescription.getDescription());
    }

    private Method getTestMethod() {
        try {
            return resultDescription.getTestClass().getDeclaredMethod(resultDescription.getMethodName());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format(ERR_MSG_UNABLE_TO_FIND_TEST_METHOD, resultDescription.getMethodName(), resultDescription.getTestClass().getName()));
        }
    }
}
