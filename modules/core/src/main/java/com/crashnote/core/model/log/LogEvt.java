/**
 * Copyright (C) 2011 - 101loops.com <dev@101loops.com>
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
package com.crashnote.core.model.log;

import com.crashnote.core.model.types.LogLevel;

import java.util.*;

/**
 * Abstract class to describe a log event/statement including its Mapped Diagnostic Context (MDC).
 * By abstracting this, the library becomes independent of the underlying logging framework.
 */
public abstract class LogEvt<E> {

    protected E event;
    protected Map<String, Object> mdc;

    // SETUP ======================================================================================

    public LogEvt(final E event, final Map context) {
        if (event == null) throw new IllegalArgumentException("argument must be non-null");

        this.mdc = context;
        this.event = event;
    }

    // INTERFACE ==================================================================================

    public void defer() {
        if (mdc != null) this.mdc = new HashMap<String, Object>(mdc);
    }

    public final boolean isExcp() {
        return getLevel().isExcp();
    }

    // GET ========================================================================================

    public Map<String, Object> getMDC() {
        return mdc;
    }

    public abstract String getThreadName();

    public abstract String getLoggerName();

    public abstract long getTimeStamp();

    public abstract LogLevel getLevel();

    public abstract String getMessage();

    public abstract Throwable getThrowable();

    public abstract Object[] getArgs();
}
