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

public enum Path {

    STATUS_PATH("/api/reporting/api/status"),
    PROFILE_PATH("/api/iam/v1/users/%d"),
    REFRESH_TOKEN_PATH("/api/iam/v1/auth/refresh"),
    USERS_PATH("/api/iam/v1/users"),
    JOBS_PATH("/api/reporting/api/jobs"),
    TESTS_PATH("/api/reporting/api/tests"),
    TEST_FINISH_PATH("/api/reporting/api/tests/%d/finish"),
    TEST_BY_ID_PATH("/api/reporting/api/tests/%d"),
    TEST_WORK_ITEMS_PATH("/api/reporting/api/tests/%d/workitems"),
    TEST_WORK_ITEM_PATH("/api/reporting/api/tests/%d/workitem"),
    TEST_WORK_ITEM_BY_TYPE_PATH("/api/reporting/api/tests/%d/workitem/%s"),
    TEST_ARTIFACTS_PATH("/api/reporting/api/tests/%d/artifacts"),
    TEST_SUITES_PATH("/api/reporting/api/tests/suites"),
    TEST_CASES_PATH("/api/reporting/api/tests/cases"),
    TEST_CASES_BATCH_PATH("/api/reporting/api/tests/cases/batch"),
    TEST_RUNS_PATH("/api/reporting/api/tests/runs"),
    TEST_RUNS_FINISH_PATH("/api/reporting/api/tests/runs/%d/finish"),
    TEST_RUNS_RESULTS_PATH("/api/reporting/api/tests/runs/%d/results"),
    TEST_RUNS_ABORT_PATH("/api/reporting/api/tests/runs/abort"),
    TEST_RUN_BY_ID_PATH("/api/reporting/api/tests/runs/%d"),
    AMAZON_SESSION_CREDENTIALS_PATH("/api/reporting/api/settings/amazon/creds"),
    PROJECTS_PATH("/api/reporting/api/projects/%s"),

    LOGS_PATH("/api/reporting/v1/test-runs/%d/logs"),
    SCREENSHOTS_PATH("/api/reporting/v1/test-runs/%d/tests/%d/screenshots");

    private final String relativePath;

    Path(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String build(Object... parameters) {
        return String.format(relativePath, parameters);
    }
    
}
