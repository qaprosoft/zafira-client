package com.qaprosoft.zafira.listener;

import com.qaprosoft.zafira.listener.adapter.MethodAdapter;
import com.qaprosoft.zafira.listener.adapter.SuiteAdapter;
import com.qaprosoft.zafira.listener.adapter.TestResultAdapter;
import com.qaprosoft.zafira.listener.adapter.impl.MethodAdapterImpl;
import com.qaprosoft.zafira.listener.adapter.impl.SuiteAdapterImpl;
import com.qaprosoft.zafira.listener.adapter.impl.TestResultAdapterImpl;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener that provides integration with Zafira reporting web-service.
 * Accumulates test results and handles rerun failures logic.
 *
 * @author akhursevich
 */
public class ZebrunnerListener implements ISuiteListener, ITestListener, IHookable, IInvokedMethodListener {

    private final TestLifecycleAware listener;

    public ZebrunnerListener() {
        this.listener = new ZafiraEventRegistrar();
    }

    @Override
    public void onStart(ISuite suiteContext) {
        SuiteAdapter adapter = new SuiteAdapterImpl(suiteContext);
        listener.onSuiteStart(adapter);
    }

    @Override
    public void onTestStart(ITestResult result) {
        TestResultAdapter adapter = new TestResultAdapterImpl(result);
        listener.onTestStart(adapter);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        TestResultAdapter adapter = new TestResultAdapterImpl(result);
        listener.onTestSuccess(adapter);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        TestResultAdapter adapter = new TestResultAdapterImpl(result);
        listener.onTestFailure(adapter);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        TestResultAdapter adapter = new TestResultAdapterImpl(result);
        listener.onTestFailure(adapter);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        TestResultAdapter adapter = new TestResultAdapterImpl(result);
        listener.onTestSkipped(adapter);
    }

    @Override
    public void onFinish(ISuite suiteContext) {
        listener.onSuiteFinish();
    }

    @Override
    public void onStart(ITestContext context) {
        // Do nothing
    }

    @Override
    public void onFinish(ITestContext context) {
        // Do nothing
    }

    @Override
    public void run(IHookCallBack hookCallBack, ITestResult result) {
        TestResultAdapter adapter = new TestResultAdapterImpl(result);
        listener.onTestHook(adapterToRun -> hookCallBack.runTestMethod(result), adapter);
    }

    @Override
    public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult result) {
        TestResultAdapter adapter = new TestResultAdapterImpl(result);
        MethodAdapter methodAdapter = new MethodAdapterImpl(invokedMethod.getTestMethod());
        listener.beforeMethodInvocation(methodAdapter, adapter);
    }

    @Override
    public void afterInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
        // Do nothing
    }

}
