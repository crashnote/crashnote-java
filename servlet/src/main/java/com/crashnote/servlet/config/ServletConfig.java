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

import com.crashnote.logger.config.LoggerConfig;
import com.crashnote.servlet.report.ServletReporter;

/**
 * Customized {@link LoggerConfig} for dealing with servlet environments.
 */
public class ServletConfig<C extends ServletConfig<C>>
    extends LoggerConfig<C> {

    /**
     * Property names of servlet-specific settings
     */
    public static final String PROP_REP_REQ_FILTER = "requestFilter";
    public static final String PROP_REP_HEADER_SKIP = "skipRequestHeader";
    public static final String PROP_REP_SESSION_SKIP = "skipRequestSession";

    // SETUP ======================================================================================

    public ServletConfig() {
        super();
    }

    @Override
    public void initDefaults() {
        super.initDefaults();

        // filter common request parameter by default
        addRequestFilters(".*password.*");
        addRequestFilters(".*creditcard.*");
        addRequestFilters(".*secret.*");

        // DO report header data
        setSkipHeaderData(false);

        // DO NOT report session data (yet)
        setSkipSessionData(true);
    }

    // INTERFACE ==================================================================================

    @Override
    public ServletReporter<C> getReporter() {
        return new ServletReporter(this);
    }

    // GET ========================================================================================

    public boolean getSkipSessionData() {
        return getBoolSetting(PROP_REP_SESSION_SKIP);
    }

    public boolean getSkipHeaderData() {
        return getBoolSetting(PROP_REP_HEADER_SKIP);
    }

    public String[] getRequestFilters() {
        final String filters = getStringSetting(PROP_REP_REQ_FILTER);
        if (filters == null || filters.length() == 0) return new String[0];
        else return filters.split(":");
    }

    // SET+ =======================================================================================

    public void addRequestFilters(final String filter) {
        String filters = getStringSetting(PROP_REP_REQ_FILTER);
        if (filters == null || filters.length() == 0) filters = filter;
        else filters += ":" + filter;
        addSetting(PROP_REP_REQ_FILTER, filters.toLowerCase());
    }

    // SET ========================================================================================

    public void setSkipSessionData(final boolean skip) {
        addSetting(PROP_REP_SESSION_SKIP, skip);
    }

    public void setSkipSessionData(final String skip) {
        addBoolSetting(PROP_REP_SESSION_SKIP, skip);
    }

    public void setSkipHeaderData(final boolean skip) {
        addSetting(PROP_REP_HEADER_SKIP, skip);
    }

    public void setSkipHeaderData(final String skip) {
        addBoolSetting(PROP_REP_HEADER_SKIP, skip);
    }
}
