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

import com.qaprosoft.zafira.config.CiConfig;
import com.qaprosoft.zafira.models.db.Initiator;
import com.qaprosoft.zafira.models.db.Status;
import com.qaprosoft.zafira.models.db.workitem.WorkItem;
import com.qaprosoft.zafira.models.dto.JobType;
import com.qaprosoft.zafira.models.dto.TagType;
import com.qaprosoft.zafira.models.dto.TestCaseType;
import com.qaprosoft.zafira.models.dto.TestRunType;
import com.qaprosoft.zafira.models.dto.TestSuiteType;
import com.qaprosoft.zafira.models.dto.TestType;

import java.util.List;
import java.util.Set;

public interface ExtendedClient {

    /**
     * Registers test case in Zafira, it may be a new one or existing returned by service.
     * @return registered test case
     */
    TestCaseType registerTestCase(Long suiteId, Long primaryOwnerId, Long secondaryOwnerId, String testClass, String testMethod);

    /**
     * Registers test work items.
     * @return test for which we registers work items
     */
    TestType registerWorkItems(Long testId, List<String> workItems);

    WorkItem registerWorkItem(Long testId, WorkItem workItem);

    /**
     * Registers test suite in Zafira, it may be a new one or existing returned by service.
     * @param suiteName - test suite name
     * @param fileName - TestNG xml file name
     * @param userId - suite owner user id
     * @return created test suite
     */
    TestSuiteType registerTestSuite(String suiteName, String fileName, Long userId);


    /**
     * Registers job in Zafira, it may be a new one or existing returned by service.
     * @param jobUrl - CI job URL
     * @param userId - job owner user id
     * @return created job
     */
    JobType registerJob(String jobUrl, Long userId);

    /**
     * Registers new test run
     * @return created test run
     */
    TestRunType registerTestRun(Long testSuiteId, Long userId, String configXML, Long jobId, Long parentJobId, CiConfig ciConfig, String workItem);

    /**
     * Registers new test run triggered by human.
     * @deprecated use {@link #registerTestRun} instead
     * @return created test run
     */
    @Deprecated
    TestRunType registerTestRunByHUMAN(Long testSuiteId, Long userId, String configXML, Long jobId, CiConfig ciConfig, Initiator startedBy, String workItem);

    /**
     * Registers new test run triggered by scheduler.
     * @deprecated use {@link #registerTestRun} instead
     * @return created test run
     */
    @Deprecated
    TestRunType registerTestRunBySCHEDULER(Long testSuiteId, String configXML, Long jobId, CiConfig ciConfig, Initiator startedBy, String workItem);

    /**
     * Registers new test run triggered by upstream job.
     * @deprecated use {@link #registerTestRun} instead
     * @return created test run
     */
    @Deprecated
    TestRunType registerTestRunUPSTREAM_JOB(Long testSuiteId, String configXML, Long jobId, Long parentJobId, CiConfig ciConfig, Initiator startedBy, String workItem);

    /**
     * Finalizes test run calculating test results.
     * @param testRun - test run object
     * @return updated test run
     */
    TestRunType registerTestRunResults(TestRunType testRun);

    /**
     * Registers test run in Zafira.
     * @return registered test
     */
    TestType registerTestStart(String name, String group, Status status, String testArgs, Long testRunId, Long testCaseId, int retryCount,
                               String configXML, String[] dependsOnMethods, String ciTestId, Set<TagType> tags);

    /**
     * Registers test re-run in Zafira.
     * @param test - test object
     * @return registered test
     */
    TestType registerTestRestart(TestType test);

}
