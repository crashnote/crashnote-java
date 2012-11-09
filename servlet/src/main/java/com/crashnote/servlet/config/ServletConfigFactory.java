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

import com.crashnote.core.config.ConfigLoader;
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
        this(filterConfig, new ConfigLoader());
    }

    public ServletConfigFactory(final FilterConfig filterConfig, final ConfigLoader loader) {
        super(loader);
        this.filterConfig = filterConfig;
    }


    // SHARED =====================================================================================

    @Override
    public C create() {
        return (C) new ServletConfig(readConf());
    }

    /**
     * add additional configuration to initialization chain
     */
    @Override
    protected Config readCustomFileConf() {

        // extract properties of servlet configuration (from web.xml)
        final Properties props = new Properties();
        if (filterConfig != null) {
            final Enumeration params = filterConfig.getInitParameterNames();
            if (params != null) {
                while (params.hasMoreElements()) {
                    final String name = (String) params.nextElement();
                    final String value = filterConfig.getInitParameter(name);
                    props.setProperty("crashnote." + name, value);
                }
            }
        }

        return
            loader.fromProps(props, "servlet filter")       // #1 filter props
                .withFallback(super.readCustomFileConf());  // #2 other custom props
    }

    @Override
    protected Config readDefaultFileConf() {
        return
            loader.fromFile("crashnote.servlet")                // #1 servlet default props
                .withFallback(super.readDefaultFileConf());     // #2 other default props
    }
}
