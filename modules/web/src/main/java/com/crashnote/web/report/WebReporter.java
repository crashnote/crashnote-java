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
package com.crashnote.web.report;

import com.crashnote.logger.report.LoggerReporter;
import com.crashnote.web.collect.RequestCollector;
import com.crashnote.web.collect.SessionCollector;
import com.crashnote.web.config.WebConfig;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Customized implementation of the core {@link LoggerReporter}. Adds servlet-specific functionality.
 */
public abstract class WebReporter<C extends WebConfig, R>
        extends LoggerReporter<C> {

    // VARS =======================================================================================

    protected RequestCollector reqCollector;
    protected SessionCollector sesCollector;

    protected boolean ignoreLocalhost;
    protected final Set<String> localAddresses;


    // SETUP ======================================================================================

    public WebReporter(final C config) {
        super(config);

        // initialize list of local addresses (in order to distinguish remote calls from local ones)
        localAddresses = new HashSet<String>();
        try {
            localAddresses.add(InetAddress.getLocalHost().getHostAddress());
            for (final InetAddress inetAddress : InetAddress.getAllByName("localhost")) {
                localAddresses.add(inetAddress.getHostAddress());
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void updateConfig(final C config) {
        super.updateConfig(config);
        this.ignoreLocalhost = config.getIgnoreLocalRequests();
    }


    // INTERFACE ==================================================================================

    /**
     * Before each request, start the internal log session
     *
     * @param request the HTTP request
     */
    public void beforeRequest(final R request) {
        startSession();
    }

    /**
     * After each request, evaluate and finish the internal log session
     *
     * @param request the HTTP request
     */
    public void afterRequest(final R request) {
        if (!isSessionEmpty()) { // log session empty? just skip this then..
            if (!ignoreRequest(request)) {
                // enrich log context with detailed request information
                put("request", reqCollector.collect(request));
                put("session", sesCollector.collect(request));
                endSession();
            }
        }
    }

    /**
     * In case of an uncaught exception, process it
     *
     * @param request the HTTP request
     * @param t       the thread where the error occurred
     * @param th      the exception details
     */
    public void uncaughtException(final R request, final Thread t, final Throwable th) {
        if (!ignoreRequest(request))
            uncaughtException(t, th);
    }


    // SHARED =====================================================================================

    /**
     * Do not flush reports out immediately - wait till the session has finished
     */
    @Override
    protected boolean isAutoFlush() {
        return false;
    }

    protected abstract boolean ignoreRequest(final R req);

    protected boolean isLocalRequest(final String req) {
        return localAddresses.contains(req);
    }
}
