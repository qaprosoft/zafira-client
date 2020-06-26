package com.qaprosoft.zafira.util;

import com.qaprosoft.zafira.client.BasicClient;
import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.client.ZafiraSingleton;
import com.qaprosoft.zafira.listener.ZafiraEventRegistrar;
import com.qaprosoft.zafira.listener.domain.ZafiraConfiguration;
import com.qaprosoft.zafira.models.dto.TestArtifactType;
import com.qaprosoft.zafira.models.dto.UploadResult;
import com.qaprosoft.zafira.models.dto.aws.FileUploadType;
import com.qaprosoft.zafira.util.async.AsyncOperationHolder;
import com.qaprosoft.zafira.util.http.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.CombinedConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
public class UploadUtil {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(50);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy");

    private static BasicClient API_CLIENT;
    private static final Integer expirationInSeconds;

    static {
        CombinedConfiguration config = ConfigurationUtil.getConfiguration();
        expirationInSeconds = (Integer) ZafiraConfiguration.ARTIFACT_EXPIRATION_SECONDS.get(config);
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
     * @param screenshot screenshot bytes
     * @param capturedAtMillis unix timestamp representing a moment in time when screenshot got captured in milliseconds
     */
    public static void uploadScreenshot(byte[] screenshot, String name, Long capturedAtMillis, boolean asArtifact) {
        if (ZafiraSingleton.INSTANCE.isRunning()) {
            Long capturedAt = capturedAtMillis != null ? capturedAtMillis : Instant.now().toEpochMilli();

            ZafiraEventRegistrar.getTestRun().ifPresent(
                    testRunType -> {
                        ZafiraEventRegistrar.getTest().ifPresent(testType -> {
                            Long testRunId = testRunType.getId();
                            Long testId = testType.getId();
                            HttpClient.Response<UploadResult> response = API_CLIENT.sendScreenshot(screenshot, testRunId, testId, capturedAt);
                            if (response.getStatus() == 200) {
                                UploadResult result = response.getObject();
                                TestArtifactHolder.add(new TestArtifactType(name, result.getKey(), testId, expirationInSeconds));
                            }
                        });
                    }
            );
        } else {
            log.trace("Screenshot taken: size={}, captureAtMillis={}", screenshot.length, capturedAtMillis);
        }
    }

    public static void uploadScreenshot(File screenshot, String name, Long capturedAtMillis, boolean asArtifact) {
        try {
            byte[] content = Files.readAllBytes(screenshot.toPath());
            uploadScreenshot(content, name, capturedAtMillis, asArtifact);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void uploadArtifact(File file, String name, Integer expiresIn) {
        if (AwsService.isEnabled()) {

            final FileUploadType.Type type = FileUploadType.Type.COMMON;

            Optional<CompletableFuture<String>> maybeArtifactUrlFuture = upload(file, type, s -> {
            });

            maybeArtifactUrlFuture.ifPresent(artifactUrlFuture -> AsyncOperationHolder.addArtifact(artifactUrlFuture, name, expiresIn));
        }
    }

    private static Optional<CompletableFuture<String>> upload(File file, FileUploadType.Type type, Consumer<String> urlConsumer) {
        final String keyPrefix = String.format(type.getPath() + "/%s/", DATE_FORMAT.format(new Date()));
        return Optional.ofNullable(CompletableFuture.supplyAsync(() -> {
            String url = null;
            try {
                log.debug("Uploading to AWS: " + file.getName() + ". Expires in " + expirationInSeconds + " seconds.");
                url = AwsService.uploadFile(file, expirationInSeconds, keyPrefix);
                log.debug("Uploaded to AWS: " + file.getName());
                urlConsumer.accept(url);
            } catch (Exception e) {
                log.debug("Can't save file to Amazon S3!", e);
            }
            return url;
        }, EXECUTOR).exceptionally(e -> {
            log.debug("Can't save file to Amazon S3!", e);
            return null;
        }));
    }
}
