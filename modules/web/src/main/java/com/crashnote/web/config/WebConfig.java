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
package com.crashnote.web.config;

import com.crashnote.external.config.Config;
import com.crashnote.logger.config.LoggerConfig;

import java.util.List;

/**
 * Customized {@link LoggerConfig} for dealing with servlet environments.
 */
public class WebConfig
        extends LoggerConfig {

    // SETUP ======================================================================================

    public WebConfig(final Config c) {
        super(c);
    }


    // GET ========================================================================================

    public boolean getSkipSessionData() {
        return getBool("request.exclude-session");
    }

    public boolean getIgnoreLocalRequests() {
        return getBool("request.ignore-localhost");
    }

    public boolean getSkipHeaderData() {
        return getBool("request.exclude-headers");
    }

    public boolean getHashRemoteIP() {
        return getBool("request.hash-ip");
    }

    public int getMaxRequestParameterSize() {
        return getInt("request.max-parameter-size");
    }

    public List<String> getRequestFilters() {
        return getStrings("filter.request");
    }

}
