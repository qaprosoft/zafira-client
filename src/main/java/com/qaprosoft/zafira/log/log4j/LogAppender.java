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
package com.qaprosoft.zafira.log.log4j;

import com.qaprosoft.zafira.log.FlushingLogsBuffer;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class LogAppender extends AppenderSkeleton {

    /**
     * Submits LoggingEvent for publishing if it reaches severity threshold.
     *
     * @param loggingEvent - log event
     */
    @Override
    protected void append(LoggingEvent loggingEvent) {
        FlushingLogsBuffer.put(loggingEvent);
    }

    @Override
    public void close() {
    }

    /**
     * Ensures that a Layout property is required
     *
     * @return requires layout flag
     */
    @Override
    public boolean requiresLayout() {
        return false;
    }

}
