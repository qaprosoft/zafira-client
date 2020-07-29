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
import com.qaprosoft.zafira.client.ExtendedClient;
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
import com.qaprosoft.zafira.models.dto.user.UserType;
import com.qaprosoft.zafira.util.http.HttpClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.qaprosoft.zafira.client.ClientDefaults.USER;

public class ExtendedClientImpl implements ExtendedClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendedClientImpl.class);

    private final BasicClient client;

    public ExtendedClientImpl(BasicClient client) {
        this.client = client;
    }

    @Override
    public UserType registerUser(String userName, String email, String firstName, String lastName) {
        if (StringUtils.isEmpty(userName) || userName.equals("$BUILD_USER_ID")) {
            userName = USER;
        }
        userName = userName.toLowerCase();

        String userDetails = "userName: %s, email: %s, firstName: %s, lastName: %s";
        LOGGER.debug("User details for registration:" + String.format(userDetails, userName, email, firstName, lastName));

        UserType user = new UserType(userName, email, firstName, lastName);
        HttpClient.Response<UserType> response = client.createUser(user);
        user = response.getObject();

        if (user == null) {
            throw new RuntimeException("Unable to register user '" + userName + "' for zafira service: " + client.getServiceUrl());
        } else {
            LOGGER.debug("Registered user details:"
                    + String.format(userDetails, user.getUsername(), user.getEmail(), user.getFirstName(), user.getLastName()));
        }
        return user;
    }

    @Override
    public TestCaseType registerTestCase(Long suiteId, Long primaryOwnerId, Long secondaryOwnerId, String testClass, String testMethod) {
        TestCaseType testCase = new TestCaseType(testClass, testMethod, "", suiteId, primaryOwnerId, secondaryOwnerId);
        String testCaseDetails = String.format("testClass: %s, testMethod: %s, testSuiteId: %d, primaryOwnerId: %d, secondaryOwnerId: %d",
                testClass, testMethod, suiteId, primaryOwnerId, secondaryOwnerId);
        LOGGER.debug("Test Case details for registration:" + testCaseDetails);
        HttpClient.Response<TestCaseType> response = client.createTestCase(testCase);
        testCase = response.getObject();
        if (testCase == null) {
            throw new RuntimeException("Unable to register test case '"
                    + String.format(testCaseDetails, testClass, testMethod, suiteId, primaryOwnerId)
                    + "' for zafira service: " + client.getServiceUrl());
        } else {
            LOGGER.debug("Registered test case details:" + testCaseDetails);
        }
        return testCase;
    }

    @Override
    public TestType registerWorkItems(Long testId, List<String> workItems) {
        TestType test = null;
        if (workItems != null && workItems.size() > 0) {
            HttpClient.Response<TestType> response = client.createTestWorkItems(testId, workItems);
            test = response.getObject();
        }
        return test;
    }

    @Override
    public WorkItem registerWorkItem(Long testId, WorkItem workItem) {
        WorkItem result = null;
        if (workItem != null) {
            HttpClient.Response<WorkItem> response = client.createOrUpdateTestWorkItem(testId, workItem);
            result = response.getObject();
        }
        return result;
    }

    @Override
    public TestSuiteType registerTestSuite(String suiteName, String fileName, Long userId) {
        TestSuiteType testSuite = new TestSuiteType(suiteName, fileName, userId);
        String testSuiteDetails = "suiteName: %s, fileName: %s, userId: %s";
        LOGGER.debug("Test Suite details for registration:" + String.format(testSuiteDetails, suiteName, fileName, userId));

        HttpClient.Response<TestSuiteType> response = client.createTestSuite(testSuite);
        testSuite = response.getObject();

        if (testSuite == null) {
            throw new RuntimeException("Unable to register test suite '" + suiteName + "' for zafira service: " + client.getServiceUrl());
        } else {
            LOGGER.debug("Registered test suite details:"
                    + String.format(testSuiteDetails, testSuite.getName(), testSuite.getFileName(), testSuite.getUserId()));
        }
        return testSuite;
    }

    @Override
    public JobType registerJob(String jobUrl, Long userId) {
        // JobsService uses the same logics in createOrUpdateJobByURL method
        jobUrl = jobUrl.replaceAll("/$", "");
        String jobName = StringUtils.substringAfterLast(jobUrl, "/");
        String jenkinsHost = StringUtils.EMPTY;
        if (jobUrl.contains("/view/")) {
            jenkinsHost = jobUrl.split("/view/")[0];
        } else if (jobUrl.contains("/job/")) {
            jenkinsHost = jobUrl.split("/job/")[0];
        }

        String jobDetails = "jobName: %s, jenkinsHost: %s, userId: %s";
        LOGGER.debug("Job details for registration:" + String.format(jobDetails, jobName, jenkinsHost, userId));

        JobType job = new JobType(jobName, jobUrl, jenkinsHost, userId);
        HttpClient.Response<JobType> response = client.createJob(job);
        job = response.getObject();

        if (job == null) {
            throw new RuntimeException("Unable to register job for zafira service: " + client.getServiceUrl());
        } else {
            LOGGER.debug("Registered job details:" + String.format(jobDetails, job.getName(), job.getJenkinsHost(), job.getUserId()));
        }

        return job;
    }

    @Override
    public TestRunType registerTestRun(Long testSuiteId, Long userId, String configXML, Long jobId, Long parentJobId, CiConfig ciConfig, String workItem) {
        TestRunType registeredTestRun;
        Initiator initiator = findInitiator(ciConfig.getCiBuildCause());
        TestRunType testRun = TestRunType.builder()
                                         .ciRunId(ciConfig.getCiRunId())
                                         .testSuiteId(testSuiteId)
                                         .userId(userId)
                                         .scmURL(ciConfig.getGitUrl())
                                         .scmBranch(ciConfig.getGitBranch())
                                         .scmCommit(ciConfig.getGitCommit())
                                         .configXML(configXML)
                                         .jobId(jobId)
                                         .upstreamJobId(parentJobId)
                                         .upstreamJobBuildNumber(ciConfig.getCiParentBuild())
                                         .buildNumber(ciConfig.getCiBuild())
                                         .startedBy(initiator)
                                         .workItem(workItem)
                                         .build();
        LOGGER.debug("Test Run details for registration:" + testRun.toString());
        HttpClient.Response<TestRunType> response = client.startTestRun(testRun);
        registeredTestRun = response.getObject();
        if (registeredTestRun == null) {
            throw new RuntimeException("Unable to register test run '" + testRun.toString() + "' for zafira service: " + client.getServiceUrl());
        } else {
            LOGGER.debug("Registered test run details:" + registeredTestRun.toString());
        }
        return registeredTestRun;
    }

    private Initiator findInitiator(CiConfig.BuildCase buildCase) {
        Initiator initiator;
        switch (buildCase) {
            case UPSTREAMTRIGGER:
                initiator = Initiator.UPSTREAM_JOB;
                break;
            case TIMERTRIGGER:
            case SCMTRIGGER:
                initiator = Initiator.SCHEDULER;
                break;
            case MANUALTRIGGER:
                initiator = Initiator.HUMAN;
                break;
            default:
                throw new RuntimeException("Unable to register test run for zafira service: " + client.getServiceUrl() +
                        " due to the misses build cause: '" + buildCase + "'");
        }
        return initiator;
    }

    @Override
    public TestRunType registerTestRunByHUMAN(Long testSuiteId, Long userId, String configXML, Long jobId, CiConfig ciConfig, Initiator startedBy,
                                              String workItem) {
        TestRunType testRun = new TestRunType(ciConfig.getCiRunId(), testSuiteId, userId, ciConfig.getGitUrl(), ciConfig.getGitBranch(),
                ciConfig.getGitCommit(), configXML, jobId, ciConfig.getCiBuild(), startedBy, workItem);
        String testRunDetails = "testSuiteId: %s, userId: %s, scmURL: %s, scmBranch: %s, scmCommit: %s, jobId: %s, buildNumber: %s, startedBy: %s, workItem";
        LOGGER.debug("Test Run details for registration:" + String.format(testRunDetails, testSuiteId, userId, ciConfig.getGitUrl(),
                ciConfig.getGitBranch(), ciConfig.getGitCommit(), jobId, ciConfig.getCiBuild(), startedBy, workItem));

        HttpClient.Response<TestRunType> response = client.startTestRun(testRun);
        testRun = response.getObject();
        if (testRun == null) {
            throw new RuntimeException("Unable to register test run '" + String.format(testRunDetails, testSuiteId, userId,
                    ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), jobId, ciConfig.getCiBuild(), startedBy, workItem)
                    + "' for zafira service: " + client.getServiceUrl());
        } else {
            LOGGER.debug("Registered test run details:"
                    + String.format(testRunDetails, testSuiteId, userId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(),
                    jobId, ciConfig.getCiBuild(), startedBy, workItem));
        }
        return testRun;
    }

    @Override
    public TestRunType registerTestRunBySCHEDULER(Long testSuiteId, String configXML, Long jobId, CiConfig ciConfig, Initiator startedBy,
                                                  String workItem) {
        TestRunType testRun = new TestRunType(ciConfig.getCiRunId(), testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(),
                ciConfig.getGitCommit(), configXML, jobId, ciConfig.getCiBuild(), startedBy, workItem);
        String testRunDetails = "testSuiteId: %s, scmURL: %s, scmBranch: %s, scmCommit: %s, jobId: %s, buildNumber: %s, startedBy: %s, workItem";
        LOGGER.debug("Test Run details for registration:" + String.format(testRunDetails, testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(),
                ciConfig.getGitCommit(), jobId, ciConfig.getCiBuild(), startedBy, workItem));

        HttpClient.Response<TestRunType> response = client.startTestRun(testRun);
        testRun = response.getObject();
        if (testRun == null) {
            throw new RuntimeException("Unable to register test run '"
                    + String.format(testRunDetails, testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), jobId,
                    ciConfig.getCiBuild(), startedBy, workItem)
                    + "' for zafira service: " + client.getServiceUrl());
        } else {
            LOGGER.debug("Registered test run details:" + String.format(testRunDetails, testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(),
                    ciConfig.getGitCommit(), jobId, ciConfig.getCiBuild(), startedBy, workItem));
        }
        return testRun;
    }

    @Override
    public TestRunType registerTestRunUPSTREAM_JOB(Long testSuiteId, String configXML, Long jobId, Long parentJobId, CiConfig ciConfig,
                                                   Initiator startedBy, String workItem) {
        TestRunType testRun = new TestRunType(ciConfig.getCiRunId(), testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(),
                ciConfig.getGitCommit(), configXML, jobId, parentJobId, ciConfig.getCiParentBuild(),
                ciConfig.getCiBuild(), startedBy, workItem);
        String testRunDetails = "testSuiteId: %s, scmURL: %s, scmBranch: %s, scmCommit: %s, jobId: %s, parentJobId: %s, parentBuildNumber: %s, buildNumber: %s, startedBy: %s, workItem";
        LOGGER.debug("Test Run details for registration:"
                + String.format(testRunDetails, testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), jobId,
                parentJobId, ciConfig.getCiParentBuild(), ciConfig.getCiBuild(), startedBy, workItem));

        HttpClient.Response<TestRunType> response = client.startTestRun(testRun);
        testRun = response.getObject();
        if (testRun == null) {
            throw new RuntimeException("Unable to register test run '"
                    + String.format(testRunDetails, testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(), ciConfig.getGitCommit(), jobId,
                    parentJobId, ciConfig.getCiParentBuild(), ciConfig.getCiBuild(), startedBy, workItem)
                    + "' for zafira service: " + client.getServiceUrl());
        } else {
            LOGGER.debug("Registered test run details:" + String.format(testRunDetails, testSuiteId, ciConfig.getGitUrl(), ciConfig.getGitBranch(),
                    ciConfig.getGitCommit(), jobId, parentJobId, ciConfig.getCiParentBuild(), ciConfig.getCiBuild(), startedBy, workItem));
        }
        return testRun;
    }

    @Override
    public TestRunType registerTestRunResults(TestRunType testRun) {
        client.updateTestRun(testRun);
        HttpClient.Response<TestRunType> response = client.finishTestRun(testRun.getId());
        return response.getObject();
    }

    @Override
    public TestType registerTestStart(String name, String group, Status status, String testArgs, Long testRunId, Long testCaseId, int retryCount,
                                      String configXML, String[] dependsOnMethods, String ciTestId, Set<TagType> tags) {
        // TODO: remove "Set<TagType> tags" param later
        Long startTime = new Date().getTime();

        String testDetails = "name: %s, status: %s, testArgs: %s, testRunId: %s, testCaseId: %s, startTime: %s, retry: %d";

        TestType test = new TestType(name, status, testArgs, testRunId, testCaseId, startTime, null, retryCount, configXML);
        LOGGER.debug("Test details for startup registration:"
                + String.format(testDetails, name, status, testArgs, testRunId, testCaseId, startTime, retryCount));

        test.setCiTestId(ciTestId);
        test.setTestGroup(group);
        if (tags != null) {
            test.setTags(tags);
        }
        if (dependsOnMethods != null) {
            StringBuilder sb = new StringBuilder();
            for (String method : dependsOnMethods) {
                sb.append(StringUtils.substringAfterLast(method, ".")).append(StringUtils.SPACE);
            }
            test.setDependsOnMethods(sb.toString());
        }

        HttpClient.Response<TestType> response = client.startTest(test);
        test = response.getObject();
        if (test == null) {
            throw new RuntimeException(
                    "Unable to register test '" + String.format(testDetails, name, status, testArgs, testRunId, testCaseId, startTime, retryCount)
                            + "' startup for zafira service: " + client.getServiceUrl());
        } else {
            LOGGER.debug(
                    "Registered test startup details:" + String.format(testDetails, name, status, testArgs, testRunId, testCaseId, startTime, retryCount));
        }
        return test;
    }

    @Override
    public TestType registerTestRestart(TestType test) {
        String testName = test.getName();
        HttpClient.Response<TestType> response = client.startTest(test);
        test = response.getObject();
        if (test == null) {
            throw new RuntimeException("Unable to register test '" + testName + "' restart for zafira service: " + client.getServiceUrl());
        } else {
            LOGGER.debug("Registered test restart details:'" + testName + "'; startTime: " + new Date(test.getStartTime()));
        }
        return test;
    }

}
