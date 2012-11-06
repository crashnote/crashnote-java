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
package com.crashnote.logback.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;

import com.crashnote.core.model.log.LogEvt;
import com.crashnote.core.model.types.LogLevel;

import java.util.Map;

/**
 * Implementation of a {@link LogEvt} for the 'Logback' library
 */
public class LogbackEvt
    extends LogEvt<ILoggingEvent> {

    // VARS =======================================================================================

    private Throwable th;

    private static final long serialVersionUID = 1L;


    // SETUP ======================================================================================

    public LogbackEvt(final ILoggingEvent event, final Map context) {
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
    public String getMessage() {
        final Object message = event.getFormattedMessage();
        return ((message != null) ? message.toString() : null);
    }

    @Override
    public Throwable getThrowable() {
        if (th == null) {
            final IThrowableProxy throwableProxy = event.getThrowableProxy();
            if (throwableProxy != null) {
                if ((throwableProxy instanceof ThrowableProxy)) {
                    final ThrowableProxy throwableProxyImpl = (ThrowableProxy) throwableProxy;
                    th = throwableProxyImpl.getThrowable();
                }
            }
        }
        return th;
    }

    @Override
    public Object[] getArgs() {
        return event.getArgumentArray();
    }

    @Override
    public String getThreadName() {
        return event.getThreadName();
    }

    @Override
    public long getTimeStamp() {
        return event.getTimeStamp();
    }


    // INTERNALS ==================================================================================

    private static LogLevel getLevel(final ILoggingEvent evt) {
        switch (evt.getLevel().levelInt) {
            case Level.ERROR_INT:
                return LogLevel.ERROR;
            case Level.WARN_INT:
                return LogLevel.WARN;
            case Level.INFO_INT:
                return LogLevel.INFO;
            default:
                return LogLevel.DEBUG;
        }
    }
}
