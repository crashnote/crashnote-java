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
package com.crashnote.core.model.log;

import com.crashnote.core.model.types.LogLevel;

import java.util.Map;

public class LogEvtVO
    implements ILogEvt {

    // VARS =======================================================================================

    private final String threadName;
    private final String loggerName;
    private final long timeStamp;
    private final LogLevel level;
    private final String message;
    private final Throwable throwable;
    private final String id;

    private final Object[] args;
    private final Map<String, Object> mdc;


    // SETUP ======================================================================================

    public LogEvtVO(final ILogEvt event) {
        if (event == null)
            throw new IllegalArgumentException("argument must be non-null");

        threadName = event.getThreadName();
        loggerName = event.getLoggerName();
        timeStamp = event.getTimeStamp();
        level = event.getLevel();
        message = event.getMessage();
        throwable = event.getThrowable();
        mdc = event.getMDC();  //if (mdc != null) this.mdc = new HashMap<String, Object>(mdc);
        args = event.getArgs();
        id = event.getID();
    }


    // INTERFACE ==================================================================================

    @Override
    public ILogEvt copy() {
        return this;
    }

    // GET ========================================================================================

    @Override
    public LogLevel getLevel() {
        return level;
    }

    @Override
    public String getLoggerName() {
        return loggerName;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Map<String, Object> getMDC() {
        return mdc;
    }

    @Override
    public String getThreadName() {
        return threadName;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public Object[] getArgs() {
        return args;
    }

    @Override
    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String getID() {
        return id;
    }
}
