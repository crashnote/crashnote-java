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
package com.crashnote.core.report.impl;

import com.crashnote.core.model.log.LogEvt;
import com.crashnote.core.model.types.LogLevel;

import java.util.Date;

/**
 * Implementation of a generic {@link LogEvt} which uses a simple {@link Throwable} as the
 * underlying event object. Because it is a pure Java implementations without external dependencies,
 * it can be used in any context.
 */
public class ThrowableLogEvt
        extends LogEvt<Throwable> {

    // VARS =======================================================================================

    private final long time;
    private final Object[] args;
    private final LogLevel lvl;
    private final String msg;
    private final String threadName;


    // SETUP ======================================================================================

    public ThrowableLogEvt(final Thread t, final Throwable th, final LogLevel lvl,
                           final String msg, final String... args) {
        super(th, null);
        this.lvl = lvl;
        this.msg = (msg != null) ? msg : th.getMessage();
        this.args = args;
        this.time = new Date().getTime();
        this.threadName = (t != null) ? t.getName() : "";
    }

    public ThrowableLogEvt(final Thread t, final Throwable th) {
        this(Thread.currentThread(), th, LogLevel.CRASH, th.getMessage());
    }


    // GET ========================================================================================

    @Override
    public String getThreadName() {
        return threadName;
    }

    @Override
    public String getLoggerName() {
        return null;
    }

    @Override
    public long getTimeStamp() {
        return time;
    }

    @Override
    public LogLevel getLevel() {
        return lvl;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public Throwable getThrowable() {
        return event;
    }

    @Override
    public Object[] getArgs() {
        return args;
    }
}
