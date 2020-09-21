package com.qaprosoft.zafira.util;

import com.qaprosoft.zafira.client.BasicClient;
import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.client.ZafiraSingleton;
import com.qaprosoft.zafira.listener.ZafiraEventRegistrar;
import com.qaprosoft.zafira.models.dto.TestArtifactType;
import com.qaprosoft.zafira.models.dto.UploadResult;
import com.qaprosoft.zafira.util.http.HttpClient;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.function.BiConsumer;

@Slf4j
public class UploadUtil {

    private static BasicClient API_CLIENT;

    static {
        ZafiraClient zafiraClient = ZafiraSingleton.INSTANCE.getClient();
        if (ZafiraSingleton.INSTANCE.isRunning() && zafiraClient != null) {
            API_CLIENT = zafiraClient.getClient();
        }
    }

    /**
     * Sends screenshot captured in scope of current test execution to Zebrunner. Captured at timestamp accuracy
     * matters - it is strongly recommended to explicitly set this value. If {@code null} is provided - it will be
     * generated automatically
     *
     * @param screenshot       screenshot bytes
     * @param name             artifact name
     * @param capturedAtMillis unix timestamp representing a moment in time when screenshot got captured in milliseconds
     * @param asArtifact       indicates that uploaded screenshot will be attached to test as artifact
     */
    public static void uploadScreenshot(byte[] screenshot, String name, Long capturedAtMillis, boolean asArtifact) {
        Long capturedAt = capturedAtMillis != null ? capturedAtMillis : Instant.now().toEpochMilli();
        BiConsumer<Long, Long> screenshotUploader = (testId, testRunId) -> {
            HttpClient.Response<UploadResult> response = API_CLIENT.sendScreenshot(screenshot, testRunId, testId, capturedAt);
            boolean successStatus = String.valueOf(response.getStatus()).matches("(2..)");
            if (asArtifact && successStatus) {
                UploadResult result = response.getObject();
                TestArtifactHolder.add(new TestArtifactType(name, result.getKey(), testId));
            }
        };
        executeOnRegisteredTestItem(screenshotUploader,
                () -> log.trace("Screenshot taken: size={}, captureAtMillis={}", screenshot.length, capturedAtMillis));
    }

    public static void uploadScreenshot(File screenshot, String name, Long capturedAtMillis, boolean asArtifact) {
        try {
            byte[] content = Files.readAllBytes(screenshot.toPath());
            uploadScreenshot(content, name, capturedAtMillis, asArtifact);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void uploadArtifact(File artifact, String name) {
        BiConsumer<Long, Long> artifactUploader = (testId, testRunId) -> {
            HttpClient.Response<UploadResult> response = API_CLIENT.sendArtifact(artifact, testRunId, testId, name);
            boolean successStatus = String.valueOf(response.getStatus()).matches("(2..)");
            if (!successStatus) {
                log.trace("Unable to upload artifact: name={}", name);
            }
        };
        executeOnRegisteredTestItem(artifactUploader,
                () -> log.trace("Artifact taken: name={}", name));
    }

    private static void executeOnRegisteredTestItem(BiConsumer<Long, Long> execution, Runnable onRegistrationSkipped) {
        if (ZafiraSingleton.INSTANCE.isRunning()) {
            ZafiraEventRegistrar.getTestRun().ifPresent(
                    testRunType -> ZafiraEventRegistrar.getTest().ifPresent(testType -> {
                                Long testRunId = testRunType.getId();
                                Long testId = testType.getId();
                                execution.accept(testRunId, testId);
                            }
                    )
            );
        } else {
            onRegistrationSkipped.run();
        }
    }
}
