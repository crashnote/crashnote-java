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
package com.crashnote.servlet.config;

import com.crashnote.external.config.Config;
import com.crashnote.servlet.report.ServletReporter;
import com.crashnote.web.config.WebConfig;

/**
 * Customized {@link WebConfig} for dealing with servlet environments.
 */
public class ServletConfig<C extends ServletConfig<C>>
        extends WebConfig<C> {

    // SETUP ======================================================================================

    public ServletConfig(final Config c) {
        super(c);
    }


    // INTERFACE ==================================================================================

    @Override
    public ServletReporter<C> getReporter() {
        return new ServletReporter(this);
    }

}
