package com.qaprosoft.zafira.listener.junit;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.internal.builders.JUnit4Builder;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.List;

/**
 * Class overrides default behavior of JUnit {@link Suite} runner to handle test suite start event
 */
public class JUnitSuite extends Suite {

    private static ListenerRegistrar listenerRegistrar;

    public JUnitSuite(Class<?> klass) throws InitializationError {
        super(klass, new CustomAllDefaultPossibilitiesBuilder());
        listenerRegistrar = new ListenerRegistrar(klass);
    }

    @Override
    protected List<TestRule> classRules() {
        List<TestRule> testRules = new ArrayList<>();
        ExternalResource resource =  new CustomExternalResource(this);
        testRules.add(resource);
        testRules.addAll(super.classRules());
        return testRules;
    }

    /**
     * Class overrides {@link AllDefaultPossibilitiesBuilder} behavior to provide custom junit builder with internal listener
     */
    private static class CustomAllDefaultPossibilitiesBuilder extends AllDefaultPossibilitiesBuilder {

        CustomAllDefaultPossibilitiesBuilder() {
            super(true);
        }

        @Override
        public JUnit4Builder junit4Builder() {
            return new CustomJUnit4Builder();
        }
    }

    /**
     * Class overrides {@link JUnit4Builder} to provide internal listener
     */
    private static class CustomJUnit4Builder extends JUnit4Builder {

        @Override
        public Runner runnerForClass(Class<?> testClass) throws Throwable {
            return super.runnerForClass(testClass);
//            listenerRegistrar = new ListenerRegistrar(testClass);
//            return listenerRegistrar;
        }
    }

    private static class CustomExternalResource extends ExternalResource {

        private final Suite suite;

        public CustomExternalResource(Suite suite) {
            this.suite = suite;
        }

        @Override
        protected void before() throws Throwable {
            listenerRegistrar.onSuiteStart(suite);
            super.before();
        }

        @Override
        protected void after() {
            listenerRegistrar.onSuiteFinish(suite);
            super.after();
        }

    }
}
