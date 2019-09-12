package com.qaprosoft.zafira.listener.junit;

import org.junit.runners.Suite;

public interface ISuiteListener {

    void onSuiteStart(Suite suite);

    void onSuiteFinish(Suite suite);
}
