package com.qaprosoft.zafira;

import com.qaprosoft.zafira.listener.junit.JUnitSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// TODO: 10/14/19 move to zafiras client examples after release
@RunWith(JUnitSuite.class)
@Suite.SuiteClasses({
        TestTest.class,
        TestTest1.class
})
public class TestSuite {

}
