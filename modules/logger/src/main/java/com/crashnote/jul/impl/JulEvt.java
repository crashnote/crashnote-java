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
package com.crashnote.jul.impl;

import com.crashnote.core.model.log.LogEvt;
import com.crashnote.core.model.types.LogLevel;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Implementation of a {@link LogEvt} for the 'Java Logging API (JUL)'
 */
public class JulEvt
    extends LogEvt<LogRecord> {

    private static final long serialVersionUID = 1L;

    // SETUP ======================================================================================

    public JulEvt(final LogRecord event) {
        super(event);
    }

    public JulEvt(final LogRecord event, final Map<String, Object> context) {
        super(event, context);
    }


    // GET ========================================================================================

    @Override
    public LogLevel getLevel() {
        return getLevel(event);
    }

    @Override
    public Throwable getThrowable() {
        return event.getThrown();
    }

    @Override
    public String getLoggerName() {
        return event.getLoggerName();
    }

    @Override
    public String getMessage() {
        return event.getMessage();
    }

    @Override
    public Object[] getArgs() {
        return event.getParameters();
    }

    @Override
    public String getThreadName() {
        return String.valueOf(event.getThreadID());
    }

    @Override
    public long getTimeStamp() {
        return event.getMillis();
    }


    // INTERNALS ==================================================================================

    private static LogLevel getLevel(final LogRecord evt) {
        final int l = evt.getLevel().intValue();
        if (l >= Level.SEVERE.intValue())
            return LogLevel.ERROR;
        if (l >= Level.WARNING.intValue())
            return LogLevel.WARN;
        if (l >= Level.CONFIG.intValue())
            return LogLevel.INFO;
        return LogLevel.DEBUG;
    }
}
