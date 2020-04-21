package com.qaprosoft.zafira.util.upload;

import com.qaprosoft.zafira.log.BaseAppenderTask;
import com.qaprosoft.zafira.log.LogAppenderService;
import com.qaprosoft.zafira.log.domain.MetaInfoMessage;
import com.qaprosoft.zafira.log.impl.LogAppenderServiceImpl;
import com.qaprosoft.zafira.log.log4j.layout.DirectJsonLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

final class DirectAppender {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectAppender.class);

    private static DirectAppender INSTANCE;

    private final LogAppenderService logAppenderService;

    private DirectAppender() {
        this.logAppenderService = LogAppenderServiceImpl.getInstance();
        Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdownHook));
        connectRabbitMQ();
    }

    /**
     * Submits {@link MetaInfoMessage} for publishing.
     *
     * @param message - meta info message
     */
    void append(MetaInfoMessage message) {
        BaseAppenderTask<MetaInfoMessage> task = new DirectAppenderTask(message, new DirectJsonLayout());
        logAppenderService.append(task);
    }

    /**
     * Creates the connection, channel to RabbitMQ. Declares exchange and queue
     */
    private void connectRabbitMQ() {
        try {
            logAppenderService.connectZafira();
        }  catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Closes the channel and connection to RabbitMQ when shutting down the appender
     */
    private void onShutdownHook() {
        try {
            logAppenderService.onClose();
        } catch (IOException | TimeoutException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    static synchronized DirectAppender getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DirectAppender();
        }
        return INSTANCE;
    }

}
