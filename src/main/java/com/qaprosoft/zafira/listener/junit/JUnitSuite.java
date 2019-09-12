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

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.List;

/**
 * Class overrides default behavior of JUnit {@link Suite} runner to handle test suite start event
 */
public class JUnitSuite extends Suite {

    private final ZafiraListener zafiraListener;

    public JUnitSuite(Class<?> klass) throws InitializationError {
        super(klass, new AllDefaultPossibilitiesBuilder(true));
        this.zafiraListener = new ZafiraListener();
    }

    @Override
    public void run(RunNotifier notifier) {
        notifier.addListener(zafiraListener);
        notifier.fireTestRunStarted(getDescription());
        super.run(notifier);
    }

    @Override
    protected List<TestRule> classRules() {
        List<TestRule> testRules = new ArrayList<>();
        ExternalResource resource =  new CustomExternalResource(this);
        testRules.add(resource);
        testRules.addAll(super.classRules());
        return testRules;
    }

    private class CustomExternalResource extends ExternalResource {

        private final Suite suite;

        CustomExternalResource(Suite suite) {
            this.suite = suite;
        }

        @Override
        protected void before() throws Throwable {
            zafiraListener.onSuiteStart(suite);
            super.before();
        }

        @Override
        protected void after() {
            zafiraListener.onSuiteFinish(suite);
            super.after();
        }

    }
}
