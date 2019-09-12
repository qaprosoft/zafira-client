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
