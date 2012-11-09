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
package com.crashnote.appengine.util;

import com.crashnote.core.util.SystemUtil;
import com.google.appengine.api.urlfetch.*;
import com.google.appengine.api.utils.SystemProperty;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Customized {@link SystemUtil} that adds methods to interact with the AppEngine API.
 */
public class AppengineUtil
    extends SystemUtil {

    // INTERFACE ==================================================================================

    /**
     * test whether the app runs on the AppEngine cloud (or locally)
     */
    public boolean isRunningOnAppengine() {
        return SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
    }

    /**
     * prepare a HTTP request
     */
    public HTTPRequest createRequest(final String url,
                                     final HTTPMethod method,
                                     final FetchOptions fetchOptions) throws MalformedURLException {
        return new HTTPRequest(new URL(url), method, fetchOptions);
    }

    /**
     * execute a HTTP request (synchronously or asynchronously)
     */
    public void execRequest(final HTTPRequest req, final boolean async) {
        try {
            final URLFetchService srv = URLFetchServiceFactory.getURLFetchService();
            if(async)
                srv.fetchAsync(req);
            else
                srv.fetch(req);
        } catch (Exception ignored) {
            // ignore exceptions
        }
    }
}
