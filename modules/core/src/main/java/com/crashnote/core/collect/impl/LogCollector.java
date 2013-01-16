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
package com.crashnote.core.collect.impl;

import com.crashnote.core.collect.BaseCollector;
import com.crashnote.core.config.CrashConfig;
import com.crashnote.core.model.data.DataArray;
import com.crashnote.core.model.data.DataObject;
import com.crashnote.core.model.log.LogEvt;

import java.util.List;
import java.util.Map;

/**
 * Collector to transform one or multiple {@link LogEvt}(s) into a structured data format.
 */
public class LogCollector
    extends BaseCollector {

    // VARS =======================================================================================

    private final ExcpCollector excpCollector;


    // SETUP ======================================================================================

    public <C extends CrashConfig> LogCollector(final C config) {
        super(config);
        this.excpCollector = createExcpCollector(config);
    }


    // INTERFACE ==================================================================================

    public DataObject collect(final LogEvt<?> evt) {
        return collectEvt(evt);
    }

    public DataArray collect(final List<LogEvt<?>> evts) {
        return collectEvts(evts);
    }


    // FACTORY ====================================================================================

    protected <C extends CrashConfig> ExcpCollector createExcpCollector(final C config) {
        return new ExcpCollector(config);
    }

    // SHARED =====================================================================================

    protected DataObject collectEvt(final LogEvt<?> evt) {
        final DataObject res = createDataObj();
        {
            // meta data
            res.put("occurredAt", evt.getTimeStamp());
            res.put("message", evt.getMessage());
            res.put("source", evt.getLoggerName());
            res.put("thread", evt.getThreadName());
            res.put("level", evt.getLevel().toString());

            final Object[] msgArgs = evt.getArgs();
            if (msgArgs != null && msgArgs.length > 0) {
                final DataArray args = createDataArr();
                for (final Object obj : msgArgs)
                    args.add(obj.toString());
                res.putArr("messageArgs", args);
            }

            // context
            if (evt.isExcp()) {
                final Map<String, Object> ctx = evt.getMDC();
                if (ctx != null && ctx.size() > 0) {
                    final DataObject context = createDataObj();
                    for (final String key : ctx.keySet())
                        context.put(key, ctx.get(key).toString());
                    res.putObj("context", context);
                }
            }

            // exception data
            final Throwable th = evt.getThrowable();
            if (th != null) res.putArr("exceptions", collectExcp(th));
        }
        return res;
    }


    // INTERNALS ==================================================================================

    private DataArray collectEvts(final List<LogEvt<?>> evts) {
        final DataArray data = createDataArr();
        {
            for (final LogEvt<?> evt : evts)
                data.add(collectEvt(evt));
        }
        return data;
    }

    private DataArray collectExcp(final Throwable th) {
        return excpCollector.collect(th);
    }
}
