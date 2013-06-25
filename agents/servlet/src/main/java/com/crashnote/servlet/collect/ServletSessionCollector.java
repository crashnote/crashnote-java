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
package com.crashnote.servlet.collect;

import com.crashnote.core.model.data.DataObject;
import com.crashnote.servlet.config.ServletConfig;
import com.crashnote.web.collect.SessionCollector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * Collector to transform a HTTP session into a structured data format.
 */
public class ServletSessionCollector
        extends SessionCollector<HttpServletRequest> {

    // VARS =======================================================================================

    protected boolean skipSessionData;


    // SETUP ======================================================================================

    public <C extends ServletConfig> ServletSessionCollector(final C config) {
        super(config);

        this.skipSessionData = config.getSkipSessionData();
    }


    // INTERFACE ==================================================================================

    @Override
    public DataObject collect(final HttpServletRequest req) {
        final DataObject data = createDataObj();
        {
            final HttpSession session = req.getSession();

            // collect basic info
            data.put("id", session.getId());
            data.put("startedAt", formatTimestamp(session.getCreationTime()));

            // collect session data
            if (!skipSessionData) {
                final DataObject content = createDataObj();
                {
                    final Enumeration<?> names = session.getAttributeNames();
                    while (names.hasMoreElements()) {
                        final String name = (String) names.nextElement();
                        final String value = session.getAttribute(name).toString();
                        content.put(name, value);
                    }
                }
                data.putObj("data", content);
            }
        }
        return data;
    }
}
