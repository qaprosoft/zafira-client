package com.qaprosoft.zafira.util.async;

import com.qaprosoft.zafira.models.dto.TestArtifactType;
import com.qaprosoft.zafira.util.AsyncUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class AsyncOperationHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationHolder.class);

    private static final List<AsyncArtifact> ARTIFACTS = Collections.synchronizedList(new ArrayList<>());
    private static final List<CompletableFuture<?>> OPERATIONS = Collections.synchronizedList(new ArrayList<>());
    private static final int WAITING_TIMEOUT_IN_SECONDS = 60;

    public static void clear() {
        ARTIFACTS.clear();
        OPERATIONS.clear();
    }

    public static void addArtifact(CompletableFuture<String> urlFuture, String name, Integer expiresIn) {
        ARTIFACTS.add(new AsyncArtifact(urlFuture, name, expiresIn));
    }

    public static void addOperation(CompletableFuture<?> operation) {
        OPERATIONS.add(operation);
    }

    public synchronized static Set<TestArtifactType> getTestArtifacts() {
        return ARTIFACTS.stream().map(AsyncOperationHolder::asyncArtifactToTestArtifact)
                        .filter(testArtifact -> !StringUtils.isEmpty(testArtifact.getLink()))
                        .collect(Collectors.toSet());
    }

    public static void waitUntilAllComplete() {
        List<CompletableFuture<?>> allFutures = retrieveFutures(ARTIFACTS);
        allFutures.addAll(OPERATIONS);
        waitCompletableFutures(WAITING_TIMEOUT_IN_SECONDS, allFutures);
    }

    private static TestArtifactType asyncArtifactToTestArtifact(AsyncArtifact asyncArtifact) {
        String url = AsyncUtil.getAsync(asyncArtifact.getUrlFuture());
        return new TestArtifactType(asyncArtifact.getName(), url, asyncArtifact.getExpiresIn());
    }

    private static void waitCompletableFutures(long timeout, List<CompletableFuture<?>> futures) {
        try {
            if(!futures.isEmpty()) {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                                 .get(timeout, TimeUnit.SECONDS);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            clear();
        }
    }

    private static List<CompletableFuture<?>> retrieveFutures(List<AsyncArtifact> asyncArtifacts) {
        return asyncArtifacts.stream()
                             .map(AsyncArtifact::getUrlFuture)
                             .collect(Collectors.toList());
    }

}
