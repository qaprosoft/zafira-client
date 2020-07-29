package com.qaprosoft.zafira.log;

import com.qaprosoft.zafira.client.BasicClient;
import com.qaprosoft.zafira.client.ZafiraClient;
import com.qaprosoft.zafira.client.ZafiraSingleton;
import com.qaprosoft.zafira.listener.ZafiraEventRegistrar;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * Effectively acts as an in-memory buffer for logs generated in scope of test run that is meant to reduce
 * number of outgoing requests issued to Zebrunner
 * <p>The {@code scheduleFlush} method schedules logs transfer with configurable delay.
 */
@Slf4j
public final class FlushingLogsBuffer {

    private static final ScheduledExecutorService FLUSH_EXECUTOR = Executors.newScheduledThreadPool(4);
    private static BasicClient API_CLIENT;
    private static final AtomicBoolean EXECUTOR_ENABLED = new AtomicBoolean();

    private static Queue<Log> QUEUE = new ConcurrentLinkedQueue<>();
    private final static Function<LoggingEvent, Log> CONVERTER = e -> Log.builder()
                                                                         .message(e.getRenderedMessage())
                                                                         .level(e.getLevel().toString())
                                                                         .timestamp(e.getTimeStamp())
                                                                         .build();

    static {
        ZafiraClient zafiraClient = ZafiraSingleton.INSTANCE.getClient();
        if (ZafiraSingleton.INSTANCE.isRunning() && zafiraClient != null) {
            API_CLIENT = zafiraClient.getClient();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(FlushingLogsBuffer::shutdown));
    }

    /**
     * Inserts specified event to the queue
     * @param event log event
     */
    public static void put(LoggingEvent event) {
        ZafiraEventRegistrar.getTest().ifPresent(testType -> {
            Log log = CONVERTER.apply(event);
            log.setTestId(testType.getId());
            QUEUE.add(log);

            // lazily enables buffer and schedules flushes on the very first event to be buffered
            if (EXECUTOR_ENABLED.compareAndSet(false, true)) {
                scheduleFlush();
            }
        });
    }

    private static void scheduleFlush() {
        FLUSH_EXECUTOR.scheduleWithFixedDelay(FlushingLogsBuffer::flush, 1, 1, TimeUnit.SECONDS);
    }

    private synchronized static void flush() {
        if (!QUEUE.isEmpty()) {
            ZafiraEventRegistrar.getTestRun().ifPresent(testRunType -> {
                Queue<Log> logsBatch = QUEUE;
                QUEUE = new ConcurrentLinkedQueue<>();
                API_CLIENT.sendLogs(logsBatch, testRunType.getId());
            });
        }
    }

    private static void shutdown() {
        FLUSH_EXECUTOR.shutdown();
        try {
            FLUSH_EXECUTOR.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        flush();
    }

}
