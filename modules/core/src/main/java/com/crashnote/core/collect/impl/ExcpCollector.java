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
package com.crashnote.core.collect.impl;

import com.crashnote.core.collect.BaseCollector;
import com.crashnote.core.config.Config;
import com.crashnote.core.model.data.*;

/**
 * Collector for serializing a {@link Throwable} into a structured form.
 */
public class ExcpCollector<C extends Config>
    extends BaseCollector<C> {

    // SETUP ======================================================================================

    public ExcpCollector(final C config) {
        super(config);
    }

    // INTERFACE ==================================================================================

    public DataArray collect(final Throwable th) {
        if (th == null) return null;
        final DataArray excps = createDataArr();
        {
            Throwable t = th;
            while (t != null) {
                final DataObject excp = createDataObj();
                {
                    excp.put("message", simplifyMessage(t.getMessage()));
                    excp.put("class", t.getClass().getName());

                    final DataArray trace = createDataArr();
                    for (final StackTraceElement element : t.getStackTrace()) {
                        final int line = element.getLineNumber();
                        final String meth = element.getMethodName();
                        final String file = element.getFileName();
                        final String cls = element.getClassName();
                        final StringBuilder sb = new StringBuilder(cls)
                            .append(':').append(file)
                            .append(":").append(meth)
                            .append(":").append(line);
                        trace.add(sb.toString());
                    }
                    excp.put("trace", trace);
                }
                excps.add(excp);
                t = t.getCause();
            }
        }
        if (excps.size() == 0) return null;
        return excps;
    }

    // INTERNALS ==================================================================================

    private String simplifyMessage(final String msg) {
        if (msg == null) return msg;
        else {
            final int idx = msg.indexOf("; nested exception is");
            return idx != -1 ? msg.substring(0, idx) : msg;
        }
    }
}