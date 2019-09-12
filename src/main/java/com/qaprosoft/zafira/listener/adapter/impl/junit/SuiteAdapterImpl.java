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
import org.junit.runners.Suite;

import java.util.ArrayList;
import java.util.List;

public class SuiteAdapterImpl implements SuiteAdapter {

    private final Suite suite;

    public SuiteAdapterImpl(Suite suite) {
        this.suite = suite;
    }

    @Override
    public Object getSuite() {
        return suite;
    }

    @Override
    public String getSuiteParameter(String name) {
        return null;
    }

    @Override
    public String getSuiteFileName() {
        return suite.getTestClass().getName();
    }

    @Override
    public String getSuiteName() {
        return suite.getTestClass().getJavaClass().getSimpleName();
    }

    @Override
    public String[] getSuiteDependsOnMethods() {
        return new String[] {};
    }

    @Override
    public List<MethodAdapter> getMethodAdapters() {
        List<MethodAdapter> methodAdapters = new ArrayList<>();
        suite.getDescription().getChildren()
             .forEach(testClassDescription -> testClassDescription.getChildren()
                                                                  .forEach(testDescription -> methodAdapters.add(new MethodAdapterImpl(testDescription))));
        return methodAdapters;
    }
}
