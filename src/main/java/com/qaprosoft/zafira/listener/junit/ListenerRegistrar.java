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

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

public class ListenerRegistrar extends BlockJUnit4ClassRunner {

    private final ZafiraListener zafiraListener;

    public ListenerRegistrar(Class<?> klass) throws InitializationError {
        super(klass);
        this.zafiraListener = new ZafiraListener();
    }

    public void onSuiteStart(Suite suite) {
        zafiraListener.onSuiteStart(suite);
    }

    public void onSuiteFinish(Suite suite) {
        zafiraListener.onSuiteFinish(suite);
    }

    @Override
    public void run(RunNotifier notifier) {
        notifier.addListener(zafiraListener);
        notifier.fireTestRunStarted(getDescription());
        super.run(notifier);
    }
}
