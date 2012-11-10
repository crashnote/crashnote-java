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

import java.util.Map;

/**
 * Abstract class to describe a log event.
 * Implements the {@link ILogEvt} interface and defines helper methods & variables.
 *
 * @param <E> type of the log event
 */
public abstract class LogEvt<E>
    implements ILogEvt {

    // VARS =======================================================================================

    protected final E event;

    protected Map<String, Object> mdc;


    // SETUP ======================================================================================

    public LogEvt(final E event, final Map context) {
        if (event == null)
            throw new IllegalArgumentException("argument must be non-null");

        this.event = event;
        this.mdc = null; //context;
    }


    // INTERFACE ==================================================================================

    @Override
    public ILogEvt copy() {
        return new LogEvtVO(this);
    }

    public final boolean isExcp() {
        return getLevel().isExcp();
    }


    // GET ========================================================================================

    @Override
    public Map<String, Object> getMDC() {
        return mdc;
    }
}
