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
package com.qaprosoft.zafira.log.log4j.layout;

import static com.qaprosoft.zafira.log.log4j.level.MetaInfoLevel.META_INFO;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.qaprosoft.zafira.log.domain.MetaInfoMessage;

class LoggingEventBuilder {

    private final JSONObject object;

    LoggingEventBuilder(JSONObject object) {
        this.object = object;
    }

    /**
     * Converts LoggingEvent Throwable to JSON object
     * @param event - event from logger
     * @throws JSONException - unable to parse json
     */
    LoggingEventBuilder writeThrowable(LoggingEvent event) throws JSONException {
        ThrowableInformation throwableInformation = event.getThrowableInformation();
        if (throwableInformation != null) {
            Throwable t = throwableInformation.getThrowable();
            writeThrowable(t);
        }
        return this;
    }

    private void writeThrowable(Throwable t) throws JSONException {
        JSONObject throwable = new JSONObject();

        throwable.put("message", t.getMessage());
        throwable.put("className", t.getClass().getCanonicalName());
        List<JSONObject> traceObjects = new ArrayList<>();
        for (StackTraceElement ste : t.getStackTrace()) {
            JSONObject element = new JSONObject();
            element.put("class", ste.getClassName());
            element.put("method", ste.getMethodName());
            element.put("line", ste.getLineNumber());
            element.put("file", ste.getFileName());
            traceObjects.add(element);
        }

        object.put("stackTrace", traceObjects);
        object.put("throwable", throwable);
    }


    /**
     * Converts basic LoggingEvent properties to JSON object
     * @param event - event from logger
     * @throws JSONException - unable to parse json
     */
    LoggingEventBuilder writeBasic(LoggingEvent event) throws JSONException {
        writeBasic(
                event.getThreadName(),
                event.getLevel(),
                event.getMessage(),
                event.getLoggerName()
        );
        return this;
    }

    LoggingEventBuilder writeDirect(MetaInfoMessage message) throws JSONException {
        writeBasic(null, META_INFO, message, null);
        return this;
    }

    void writeBasic(String threadName, Level level, Object message, String loggerName) throws JSONException {
        object.put("threadName", threadName);
        object.put("level", level.toString());
        object.put("timestamp", System.currentTimeMillis());
        if (META_INFO.equals(level)) {
            MetaInfoMessage metaInfoMessage = (MetaInfoMessage) message;
            object.put("message", metaInfoMessage.getMessage());
            object.put("headers", new JSONObject(metaInfoMessage.getHeaders()));
        } else {
            object.put("message", message);
        }
        object.put("logger", loggerName);
    }

    /**
     * Converts result json object to string
     * @return result json payload
     */
    String build() {
        return object.toString();
    }

}
