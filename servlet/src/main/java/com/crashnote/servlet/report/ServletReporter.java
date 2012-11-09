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
package com.crashnote.servlet.report;

import com.crashnote.servlet.collect.ServletRequestCollector;
import com.crashnote.servlet.collect.ServletSessionCollector;
import com.crashnote.servlet.config.ServletConfig;
import com.crashnote.web.report.WebReporter;

import javax.servlet.http.HttpServletRequest;

/**
 * Customized implementation of the core {@link WebReporter}. Adds servlet-specific functionality.
 */
public class ServletReporter
        extends WebReporter<HttpServletRequest> {


    // SETUP ======================================================================================

    public <C extends ServletConfig> ServletReporter(final C config) {
        super(config);

        this.reqCollector = new ServletRequestCollector(config);
        this.sesCollector = new ServletSessionCollector(config);
    }


    // SHARED =====================================================================================

    @Override
    protected boolean ignoreRequest(final HttpServletRequest req) {
        if (ignoreLocalhost && isLocalRequest(req.getRemoteAddr())) {
            getLogger().debug("error for '{} {}' is ignored (local requests are disabled in config)",
                    req.getMethod(), req.getRequestURL().toString());
            return true;
        } else {
            return false;
        }
    }
}
