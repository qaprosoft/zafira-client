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
package com.qaprosoft.zafira.listener.junit;

import com.qaprosoft.zafira.listener.TestLifecycleAware;
import com.qaprosoft.zafira.listener.ZafiraEventRegistrar;
import com.qaprosoft.zafira.listener.adapter.SuiteAdapter;
import com.qaprosoft.zafira.listener.adapter.TestResultAdapter;
import com.qaprosoft.zafira.listener.adapter.impl.junit.SuiteAdapterImpl;
import com.qaprosoft.zafira.listener.adapter.impl.junit.TestResultAdapterImpl;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.Suite;

public class ZafiraListener extends RunListener implements ISuiteListener {

    private final TestLifecycleAware listener;

    public ZafiraListener() {
        this.listener = new ZafiraEventRegistrar();
    }

    @Override
    public void onSuiteStart(Suite suite) {
        SuiteAdapter suiteAdapter = new SuiteAdapterImpl(suite);
        listener.onSuiteStart(suiteAdapter);
    }

    @Override
    public void onSuiteFinish(Suite suite) {
        listener.onSuiteFinish();
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        super.testRunFinished(result);
    }

    @Override
    public void testStarted(Description description) throws Exception {
        TestResultAdapter adapter = new TestResultAdapterImpl(description);
        listener.onTestStart(adapter);
        super.testStarted(description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        TestResultAdapter adapter = new TestResultAdapterImpl(description);

        boolean failed = description.getChildren().contains(TestResultAdapterImpl.StatusDescription.FAILED.getDescription());
        boolean skipped = description.getChildren().contains(TestResultAdapterImpl.StatusDescription.SKIPPED.getDescription());
        if (!failed && skipped) {
            listener.onTestSkipped(adapter);
        } else {
            listener.onTestSuccess(adapter);
        }
        super.testFinished(description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        TestResultAdapterImpl.markAs(failure.getDescription(), TestResultAdapterImpl.StatusDescription.FAILED);
        TestResultAdapter adapter = new TestResultAdapterImpl(failure);
        listener.onTestFailure(adapter);
        super.testFailure(failure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        super.testAssumptionFailure(failure);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        TestResultAdapterImpl.markAs(description, TestResultAdapterImpl.StatusDescription.SKIPPED);
        super.testIgnored(description);
    }
}
