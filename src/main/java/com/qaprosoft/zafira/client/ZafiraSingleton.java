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
package com.qaprosoft.zafira.client;

import com.qaprosoft.zafira.client.impl.ZafiraClientImpl;
import com.qaprosoft.zafira.listener.domain.ZafiraConfiguration;
import com.qaprosoft.zafira.models.dto.auth.AuthTokenType;
import com.qaprosoft.zafira.util.AsyncUtil;
import com.qaprosoft.zafira.util.ConfigurationUtil;
import com.qaprosoft.zafira.util.http.HttpClient;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * ZafiraSingleton - singleton wrapper around {@link ZafiraClientImpl}.
 *
 * @author Alexey Khursevich (hursevich@gmail.com)
 */
public enum ZafiraSingleton {

    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ZafiraSingleton.class);

    private final CompletableFuture<HttpClient.Response<AuthTokenType>> INIT_FUTURE;

    private ZafiraClient zafiraClient;

    ZafiraSingleton() {
        INIT_FUTURE = CompletableFuture.supplyAsync(() -> {
            HttpClient.Response<AuthTokenType> result = null;
            try {
                CombinedConfiguration config = ConfigurationUtil.getConfiguration(false);
                // TODO: 2019-04-12 it`s make sense to throw an exception until zafira client instance is static in log appender class
                // and CombinedConfiguration doesn`t save singleton initialization 'injection'
                // config.setThrowExceptionOnMissing(false);

                boolean enabled = (Boolean) ZafiraConfiguration.ENABLED.get(config);
                String url = (String) ZafiraConfiguration.SERVICE_URL.get(config);
                String token = (String) ZafiraConfiguration.ACCESS_TOKEN.get(config);
                LoggerFactory.getLogger(ZafiraSingleton.class).debug("Reporting enabled: " + enabled);
                LoggerFactory.getLogger(ZafiraSingleton.class).debug("Reporting url: " + url);
                LoggerFactory.getLogger(ZafiraSingleton.class).debug("Reporting token: " + token);

                zafiraClient = new ZafiraClientImpl(url);
                boolean isAvailable = zafiraClient.isAvailable();
                LoggerFactory.getLogger(ZafiraSingleton.class).debug("Reporting isAvailable: " + isAvailable);
                if (enabled && isAvailable) {
                    result = zafiraClient.refreshToken(token);
                }
            } catch (Exception e) {
                LoggerFactory.getLogger(ZafiraSingleton.class).error("Error initializing zafira client", e);
                throw new RuntimeException(e.getMessage(), e);
            }
            return result;
        });

        INIT_FUTURE.thenAccept(auth -> {
            if (auth != null && auth.getStatus() == 200) {
                LoggerFactory.getLogger(ZafiraSingleton.class).debug("Auth token: " + auth.getObject().getAuthToken());
                zafiraClient.setAuthData(auth.getObject());
            }
        });
    }

    /**
     * @return {@link ZafiraClientImpl} instance
     */
    public ZafiraClient getClient() {
        return isRunning() ? zafiraClient : null;
    }

    /**
     * @return Zafira integration status
     */
    public boolean isRunning() {
        boolean status = false;
        try {
            HttpClient.Response<AuthTokenType> response = AsyncUtil.getAsync(INIT_FUTURE, "Cannot connect to zafira");
            LOGGER.info("Initialization response status code: " + response.getStatus());
            status = response.getStatus() == 200;
        } catch (Exception e) {
            LOGGER.error("Error obtaining value from initialization future", e);
        }
        return status;
    }

}
