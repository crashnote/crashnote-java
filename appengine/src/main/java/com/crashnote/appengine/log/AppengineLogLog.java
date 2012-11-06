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
package com.crashnote.appengine.log;

import com.crashnote.core.log.LogLog;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Specialized internal log, instead of using stdout/stderr it uses
 * AppEngine's default logger: JUL.
 */
public class AppengineLogLog extends LogLog {

    // VARS =======================================================================================

    private final Logger log;


    // SETUP ======================================================================================

    public AppengineLogLog(final String name, final boolean debug) {
        super(name, debug);

        // initialize logger instance
        log = Logger.getLogger(name);
    }


    // INTERFACE ==================================================================================

    @Override
    public void debug(final String msg, final Object... args) {
        log.fine(buildMsg(msg, args));
    }

    @Override
    public void info(final String msg, final Object... args) {
        log.info(buildMsg(msg, args));
    }

    @Override
    public void warn(final String msg, final Object... args) {
        log.warning(buildMsg(msg, args));
    }

    @Override
    public void warn(final String msg, final Throwable th) {
        log.log(Level.WARNING, msg, th);
    }

    @Override
    public void warn(final String msg, final Throwable th, final Object... args) {
        log.log(Level.WARNING, buildMsg(msg, args), th);
    }

    @Override
    public void error(final String msg, final Object... args) {
        log.severe(buildMsg(msg, args));
    }

    @Override
    public void error(final String msg, final Throwable th, final Object... args) {
        log.log(Level.SEVERE, buildMsg(msg, args), th);
    }

}
