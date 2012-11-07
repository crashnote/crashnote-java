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
package com.crashnote.logger.helper;

import com.crashnote.ICrashAppender;
import com.crashnote.logger.config.LoggerConfig;
import com.crashnote.logger.report.LoggerReporter;

/**
 * Basic, abstract class to connect an appender to a logging framework.
 * The implementations define the actual attaching .
 */
public abstract class LogConnector<C extends LoggerConfig, A extends ICrashAppender> {

    // VARS =======================================================================================

    protected A myAppender;


    // LIFECYCLE ==================================================================================

    public boolean start(final C config, final LoggerReporter reporter) {
        try {
            attach(config, reporter);
            debug("running");
            return true;
        } catch (Exception e) {
            err("unable to attach appender", e);
        }
        return false;
    }

    public void stop() {
        if (myAppender != null) {
            debug("shutting down");
            detach();
            debug("stopped");
        }
    }


    // INTERFACE ==================================================================================

    public abstract void err(String msg, Throwable th);

    public abstract void debug(String msg);


    // SHARED =====================================================================================

    protected abstract void attach(C config, LoggerReporter reporter);

    protected abstract void detach();

}
