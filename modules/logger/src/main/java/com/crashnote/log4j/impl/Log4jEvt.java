/**
 * Copyright (C) 2012 - 101loops.com <dev@101loops.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crashnote.log4j.impl;

import com.crashnote.core.model.log.LogEvt;
import com.crashnote.core.model.types.LogLevel;

import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.util.Map;

/**
 * Implementation of a {@link LogEvt} for the 'Log4J' library
 */
public class Log4jEvt
    extends LogEvt<LoggingEvent> {

    private static final long serialVersionUID = 1L;

    // SETUP ======================================================================================

    public Log4jEvt(final LoggingEvent event, final Map context) {
        super(event, context);
    }


    // GET ========================================================================================

    @Override
    public String getLoggerName() {
        return event.getLoggerName();
    }

    @Override
    public LogLevel getLevel() {
        return getLevel(event);
    }

    @Override
    public Throwable getThrowable() {
        final ThrowableInformation ti = event.getThrowableInformation();
        if (ti != null) return ti.getThrowable();
        else return null;
    }

    @Override
    public String getMessage() {
        final Object message = event.getMessage();
        return ((message != null) ? message.toString() : null);
    }

    @Override
    public Object[] getArgs() {
        return null;
    }

    @Override
    public String getThreadName() {
        return event.getThreadName();
    }

    @Override
    public long getTimeStamp() {
        return event.timeStamp;
    }


    // INTERNALS ==================================================================================

    private static LogLevel getLevel(final LoggingEvent evt) {
        switch (evt.getLevel().toInt()) {
            case Priority.FATAL_INT:
                return LogLevel.FATAL;
            case Priority.ERROR_INT:
                return LogLevel.ERROR;
            case Priority.WARN_INT:
                return LogLevel.WARN;
            case Priority.INFO_INT:
                return LogLevel.INFO;
            case Priority.DEBUG_INT:
                return LogLevel.DEBUG;
            default:
                return LogLevel.DEBUG;
        }
    }
}
