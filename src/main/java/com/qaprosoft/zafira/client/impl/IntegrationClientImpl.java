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
package com.qaprosoft.zafira.client.impl;

import com.qaprosoft.zafira.client.BasicClient;
import com.qaprosoft.zafira.client.IntegrationClient;
import com.qaprosoft.zafira.client.Path;
import com.qaprosoft.zafira.models.dto.aws.SessionCredentials;
import com.qaprosoft.zafira.util.http.HttpClient;

public class IntegrationClientImpl implements IntegrationClient {

    private static final String ERR_MSG_GET_AWS_CREDENTIALS = "Unable to get AWS session credentials";

    private final BasicClient client;

    public IntegrationClientImpl(BasicClient client) {
        this.client = client;
    }

    /**
     * Gets Amazon S3 temporary credentials
     *
     * @return Amazon S3 temporary credentials
     */
    public HttpClient.Response<SessionCredentials> getAmazonSessionCredentials() {
        return HttpClient.uri(Path.AMAZON_SESSION_CREDENTIALS_PATH, client.getServiceUrl())
                         .withAuthorization(client.getAuthToken())
                         .onFailure(ERR_MSG_GET_AWS_CREDENTIALS)
                         .get(SessionCredentials.class);
    }

}
