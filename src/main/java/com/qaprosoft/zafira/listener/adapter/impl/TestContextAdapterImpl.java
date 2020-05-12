package com.qaprosoft.zafira.listener.adapter.impl;

import com.qaprosoft.zafira.listener.adapter.MethodAdapter;
import com.qaprosoft.zafira.listener.adapter.SuiteAdapter;
import com.qaprosoft.zafira.listener.adapter.TestContextAdapter;
import com.qaprosoft.zafira.listener.adapter.TestResultAdapter;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.internal.TestResult;

public class TestContextAdapterImpl implements TestContextAdapter {

    private static final String ERR_MSG_CONTEXT_REQUIRED = "TestNG context is required to apply its data";

    private final ITestContext context;

    public TestContextAdapterImpl(ITestContext context) {
        this.context = context;
    }

    @Override
    public SuiteAdapter getSuiteAdapter() {
        testContextNotNull();
        return new SuiteAdapterImpl(context.getSuite());
    }

    @Override
    public TestResultAdapter getTestResultAdapter(MethodAdapter adapter) {
        testContextNotNull();
        ITestNGMethod method = (ITestNGMethod) adapter.getMethod();
        TestResult testResult = TestResult.newContextAwareTestResult(method, context);
        return new TestResultAdapterImpl(testResult);
    }

    private void testContextNotNull() {
        if(context == null) {
            throw new RuntimeException(ERR_MSG_CONTEXT_REQUIRED);
        }
    }

}
