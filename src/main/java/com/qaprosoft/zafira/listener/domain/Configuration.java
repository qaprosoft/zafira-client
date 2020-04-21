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
package com.qaprosoft.zafira.listener.domain;

import com.qaprosoft.zafira.listener.adapter.SuiteAdapter;

interface Configuration<T> {

    boolean canOverride();
    String getConfigName();
    T getDefaultValue();
    Class<T> getConfigClass();

    default T get(SuiteAdapter adapter) {
        String configValue = adapter.getSuiteParameter(getConfigName());
        return getConfigClass().cast(configValue);
    }

    default T get(org.apache.commons.configuration2.Configuration config) {
        return config.get(getConfigClass(), getConfigName(), getDefaultValue());
    }

    default T get(org.apache.commons.configuration2.Configuration config, SuiteAdapter adapter) {
        return canOverride() && adapter != null && get(adapter) != null ? get(adapter) : get(config);
    }

}
