package com.qaprosoft.zafira.util.upload;

import com.qaprosoft.zafira.client.IntegrationClient;
import com.qaprosoft.zafira.client.ZafiraSingleton;
import com.qaprosoft.zafira.listener.ZafiraEventRegistrar;
import com.qaprosoft.zafira.listener.domain.ZafiraConfiguration;
import com.qaprosoft.zafira.log.domain.MetaInfoMessage;
import com.qaprosoft.zafira.util.ConfigurationUtil;
import com.qaprosoft.zafira.models.dto.aws.FileUploadType;
import com.qaprosoft.zafira.util.async.AsyncOperationHolder;
import org.apache.commons.configuration2.CombinedConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class UploadUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadUtil.class);

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(50);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy");

    private static final String HEADER_PATH = "AMAZON_PATH";
    private static final String HEADER_THUMB_PATH = "THUMB_AMAZON_PATH";
    private static final String HEADER_CORRELATION_ID = "AMAZON_PATH_CORRELATION_ID";
    private static final String HEADER_CI_TEST_ID = "CI_TEST_ID";

    private static final IntegrationClient client = ZafiraSingleton.INSTANCE.getClient();
    private static final int expirationInSeconds;

    private static DirectAppender appender;

    static {
        CombinedConfiguration config = ConfigurationUtil.getConfiguration();
        expirationInSeconds = (Integer) ZafiraConfiguration.ARTIFACT_EXPIRATION_SECONDS.get(config);
    }

    private UploadUtil() {
    }

    public static void uploadScreenshot(File screenshot, File screenshotThumbnail, String name, boolean asArtifact) {
        if (AwsService.isEnabled()) {
            lazyInit();

            final String correlationId = UUID.randomUUID().toString();
            final String ciTestId = ZafiraEventRegistrar.getThreadCiTestId();
            final FileUploadType.Type type = FileUploadType.Type.SCREENSHOTS;

            MetaInfoMessage beforeScreenshotUploadMessage = new MetaInfoMessage().addHeader(HEADER_PATH, null)
                                                                                 .addHeader(HEADER_CORRELATION_ID, correlationId);

            appender.append(beforeScreenshotUploadMessage);

            MetaInfoMessage beforeThumbnailUploadMessage = new MetaInfoMessage().addHeader(HEADER_THUMB_PATH, null)
                                                                                .addHeader(HEADER_CORRELATION_ID, correlationId);

            appender.append(beforeThumbnailUploadMessage);

            Optional<CompletableFuture<String>> screenshotUrlFuture = upload(screenshot, type, url -> {
                MetaInfoMessage afterScreenshotUploadMessage = new MetaInfoMessage().addHeader(HEADER_PATH, url)
                                                                                    .addHeader(HEADER_CI_TEST_ID, ciTestId)
                                                                                    .addHeader(HEADER_CORRELATION_ID, correlationId);

                appender.append(afterScreenshotUploadMessage);
            });

            Optional<CompletableFuture<String>> thumbnailUrlFuture = upload(screenshotThumbnail, type, url -> {
                MetaInfoMessage afterThumbnailUploadMessage = new MetaInfoMessage().addHeader(HEADER_THUMB_PATH, url)
                                                                                   .addHeader(HEADER_CI_TEST_ID, ciTestId)
                                                                                   .addHeader(HEADER_CORRELATION_ID, correlationId);

                appender.append(afterThumbnailUploadMessage);
            });

            screenshotUrlFuture.ifPresent(suf -> thumbnailUrlFuture.ifPresent(tuf -> {
                if (asArtifact) {
                    AsyncOperationHolder.addArtifact(suf, name, expirationInSeconds);
                    AsyncOperationHolder.addArtifact(tuf, name, expirationInSeconds);
                } else {
                    AsyncOperationHolder.addOperation(suf);
                    AsyncOperationHolder.addOperation(tuf);
                }
            }));
        }
    }

    public static void uploadArtifact(File file, String name, Integer expiresIn) {
        if (AwsService.isEnabled()) {
            lazyInit();

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
                LOGGER.debug("Uploading to AWS: " + file.getName() + ". Expires in " + expirationInSeconds + " seconds.");
                url = AwsService.uploadFile(file, expirationInSeconds, keyPrefix);
                LOGGER.debug("Uploaded to AWS: " + file.getName());
                urlConsumer.accept(url);
            } catch (Exception e) {
                LOGGER.debug("Can't save file to Amazon S3!", e);
            }
            return url;
        }, EXECUTOR).exceptionally(e -> {
            LOGGER.debug("Can't save file to Amazon S3!", e);
            return null;
        }));
    }

    private static void lazyInit() {
        appender = DirectAppender.getInstance();
    }
}
