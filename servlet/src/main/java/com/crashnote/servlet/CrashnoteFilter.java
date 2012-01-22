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
package com.crashnote.servlet;

import com.crashnote.logger.helper.AutoLogConnector;
import com.crashnote.servlet.config.ServletConfigFactory;
import com.crashnote.servlet.report.ServletReporter;

import javax.servlet.*;
import java.io.IOException;

/**
 * TODO
 */
public class CrashnoteFilter
    implements Filter {

    private ServletReporter reporter;
    private AutoLogConnector connector;

    // INTERFACE ==================================================================================

    public void init(final FilterConfig filterConfig) throws ServletException {

        // at first, parse filter configuration
        final com.crashnote.servlet.config.ServletConfig config = getConfig(filterConfig);

        // if the configuration enables the filter...
        if (config.isEnabled()) {

            // ... based on config, create the central reporter service
            reporter = config.getReporter();
            reporter.start();

            // ... finally install the appender(s)
            connector = config.getLogConnector(reporter);
            connector.start();
        }
    }

    /**
     * Every request the web application receives must go through this method first. In case the
     * filter is disabled, the request is simply handed down the filter chain; if not, the
     * request is wrapped in a before/after corset and exceptions are handled.
     * <p/>
     * PS: Everything inside doFilter() must be thread-safe!
     */
    public void doFilter(final ServletRequest request, final ServletResponse response,
                         final FilterChain chain) throws IOException, ServletException {

        if (reporter == null) {

            // filter disabled? simply hand request down the chain
            chain.doFilter(request, response);

        } else {
            try {
                // initialize reporting with current request
                reporter.beforeRequest(request, response);

                // proceed with filter chain...
                chain.doFilter(request, response);

            } catch (Throwable e) {
                // an exception occurred! -> report it
                reporter.uncaughtException(request, Thread.currentThread(), e);

                // handle the error somehow
                dealWithException(e);

            } finally {
                // cleanup after request
                reporter.afterRequest(request, response);
            }
        }
    }

    public void destroy() {

        // disconnect the appenders
        if (connector != null) connector.stop();

        // close the reporter (and let it quickly flush cached data)
        if (reporter != null) reporter.stop();
    }

    // SHARED =====================================================================================

    /**
     * Method called when an error occurred and after it was reported to crashnote.
     * By default it re-throws the exception to let the container deal with it.
     *
     * @param e the error that occurred within the request chain
     * @throws IOException
     * @throws ServletException
     */
    protected void dealWithException(final Throwable e) throws IOException, ServletException {

        if (e instanceof IOException)
            throw (IOException) e;
        else if (e instanceof ServletException)
            throw (ServletException) e;
        else if (e instanceof RuntimeException)
            throw (RuntimeException) e;
        else
            throw new RuntimeException(e);
    }

    protected com.crashnote.servlet.config.ServletConfig getConfig(final FilterConfig filterConfig) {
        return new ServletConfigFactory<com.crashnote.servlet.config.ServletConfig>(filterConfig).get();
    }
}