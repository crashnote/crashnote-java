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
package com.crashnote.appengine.config;

import com.crashnote.appengine.collect.AppengineCollector;
import com.crashnote.appengine.log.AppengineLogLogFactory;
import com.crashnote.appengine.send.AppengineSender;
import com.crashnote.appengine.util.AppengineUtil;
import com.crashnote.core.collect.Collector;
import com.crashnote.external.config.Config;
import com.crashnote.core.log.LogLogFactory;
import com.crashnote.core.send.Sender;
import com.crashnote.servlet.config.ServletConfig;

/**
 * Customized {@link ServletConfig} that adapts to the restrictions on the AppEngine platform.
 */
public class AppengineConfig
    extends ServletConfig {

    // SETUP ======================================================================================

    public AppengineConfig(final Config c) {
        super(c);
    }


    // INTERFACE ==================================================================================

    /**
     * use a specialized sender for the AppEngine
     */
    @Override
    public Sender getSender() {
        return new AppengineSender(this);
    }

    @Override
    public Collector getCollector() {
        return new AppengineCollector(this);
    }

    @Override
    public AppengineUtil getSystemUtil() {
        return new AppengineUtil();
    }


    // SHARED ==================================================================================

    @Override
    protected LogLogFactory getLogFactory() {
        if (logFactory == null) logFactory = new AppengineLogLogFactory(this);
        return logFactory;
    }


    // GET ========================================================================================

    /**
     * Override to always return true (because AppEngine does not support spawning Threads)
     */
    @Override
    public boolean isSync() {
        return true;
    }

}
