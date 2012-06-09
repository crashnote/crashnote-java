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
package com.crashnote.servlet.config;

import com.crashnote.external.config.Config;
import com.crashnote.logger.config.LoggerConfigFactory;

import javax.servlet.FilterConfig;
import java.util.Enumeration;
import java.util.Properties;

public class ServletConfigFactory<C extends ServletConfig>
        extends LoggerConfigFactory<C> {

    // VARS =======================================================================================

    private final FilterConfig filterConfig;


    // SETUP ======================================================================================

    public ServletConfigFactory(final FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }


    // SHARED =====================================================================================

    @Override
    public C create() {
        return (C) new ServletConfig(readConf());
    }

    @Override
    protected Config readConf() {
        return super.readConf()
                    .withFallback(getConfFile("crashnote.servlet"));
    }

    @Override
    protected Config getSysDefault() {

        // extract properties from servlet ..
        final Properties props = new Properties();
        if (filterConfig != null) {
            final Enumeration params = filterConfig.getInitParameterNames();
            while (params.hasMoreElements()) {
                final String name = (String) params.nextElement();
                final String value = filterConfig.getInitParameter(name);
                props.setProperty("crashnote." + name, value);
            }
        }

        // .. and add them to the system defaults
        return super.getSysDefault()
                .withFallback(getConfProps(props, "servlet filter"));
    }
}
