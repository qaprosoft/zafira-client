package com.qaprosoft.zafira.listener.adapter;

public interface TestContextAdapter {

    SuiteAdapter getSuiteAdapter();

    TestResultAdapter getTestResultAdapter(MethodAdapter adapter);

}
