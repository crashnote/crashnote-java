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

import com.crashnote.jul.CrashHandler;
import com.crashnote.logger.config.LoggerConfig;
import com.crashnote.logger.helper.LogConnector;
import com.crashnote.logger.report.LoggerReporter;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Implementation of a {@link LogConnector} for the 'Java Logging API (JUL)':
 * Attaches the {@link CrashHandler} to JUL.
 */
public class JulConnector
        extends LogConnector<LoggerConfig, CrashHandler> {

    // VARS =======================================================================================

    private static final Logger log = CrashHandler.getTargetLogger(JulConnector.class);


    // INTERFACE ==================================================================================

    @Override
    public void err(final String msg, final Throwable th) {
        log.log(Level.SEVERE, msg, th);
    }

    @Override
    public void debug(final String msg) {
        log.config(msg);
    }


    // SHARED =====================================================================================

    @Override
    protected void attach(final LoggerConfig config, final LoggerReporter reporter) {

        // initialize
        final Logger rootLogger = Logger.getLogger(""); // LogManager.getLogManager() not allowed on GAE

        // attach
        myAppender = new CrashHandler(config, reporter);
        rootLogger.addHandler(myAppender);
    }

    @Override
    protected void detach() {
        final Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.removeHandler(myAppender);
    }

}
