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
package com.crashnote.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.crashnote.ICrashAppender;
import com.crashnote.core.model.types.LogLevel;
import com.crashnote.logback.impl.LogbackEvt;
import com.crashnote.logger.config.LoggerConfig;
import com.crashnote.logger.config.LoggerConfigFactory;
import com.crashnote.logger.report.LoggerReporter;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Appender that writes logs from 'Logback' to the cloud
 */
public class CrashAppender
        extends AppenderBase<ILoggingEvent> implements ICrashAppender {

    // VARS =======================================================================================

    private Level threshold;

    private LoggerReporter reporter;

    // config
    private LoggerConfig config;
    private final LoggerConfigFactory configFactory;


    // SETUP ======================================================================================

    public CrashAppender() {
        this(new LoggerConfigFactory());
    }

    public CrashAppender(final LoggerConfigFactory configFactory) {
        this.configFactory = configFactory;

        addFilter(new Filter<ILoggingEvent>() {
            @Override
            public FilterReply decide(final ILoggingEvent event) {
                final boolean res =
                        event.getLevel().isGreaterOrEqual(threshold) &&
                                getReporter().doAcceptLog(event.getLoggerName());
                return res ? FilterReply.ACCEPT : FilterReply.DENY;
            }
        });
    }

    public CrashAppender(final LoggerConfig config, final LoggerReporter reporter) {
        this();
        this.config = config;
        this.reporter = reporter;
    }


    // INTERFACE ==================================================================================

    @Override
    public void start() {
        if (!started) {
            setLogLevel(getConfig().getLogLevel());
            getReporter().start();
            super.start();
        }
    }

    @Override
    public void stop() {
        if (started) {
            getReporter().stop();
            super.stop();
        }
    }

    @Override
    public void setLogLevel(final LogLevel lvl) {
        if (lvl == LogLevel.DEBUG)
            threshold = Level.DEBUG;
        else if (lvl == LogLevel.INFO)
            threshold = Level.INFO;
        else if (lvl == LogLevel.WARN)
            threshold = Level.WARN;
        else
            threshold = Level.ERROR;
    }

    public static Logger getTargetLogger(final Class clazz) {
        return (Logger) LoggerFactory.getLogger(clazz);
    }


    // SHARED =====================================================================================

    @Override
    protected void append(final ILoggingEvent event) {
        if (started)
            getReporter().reportLog(new LogbackEvt(event, MDC.getCopyOfContextMap()));
    }


    // INTERNALS ==================================================================================

    private LoggerConfig getConfig() {
        if (config == null)
            config = (LoggerConfig) configFactory.get();
        return config;
    }

    private LoggerReporter getReporter() {
        if (reporter == null) reporter = getConfig().getReporter();
        return reporter;
    }
}