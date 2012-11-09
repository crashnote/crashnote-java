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
package com.crashnote.appengine;

import com.crashnote.appengine.config.AppengineConfig;
import com.crashnote.appengine.config.AppengineConfigFactory;

import javax.servlet.FilterConfig;

/**
 * Customized servlet filter that uses a specialized configuration for the AppEngine platform.
 */
public class CrashnoteFilter
        extends com.crashnote.servlet.CrashnoteFilter {

    // SHARED =====================================================================================

    @Override
    protected AppengineConfig getConfig(final FilterConfig filterConfig) {
        return new AppengineConfigFactory(filterConfig).get();
    }

    @Override
    protected void checkForAppengine() {
        if(System.getProperty("com.google.appengine.runtime.environment") == null) {
            throw new RuntimeException("Unsupported Platform! It seems you are NOT developing for / running on " +
                    "Google's AppEngine. This library (crashnote-appengine) only works with it - " +
                    "you need the more general crashnote-servlet that runs any servlet-based app. " +
                    "Please consult the online docs of Crashnote for further details.");
        }
    }

}