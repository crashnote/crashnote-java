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
package com.crashnote.appengine.collect;

import com.crashnote.appengine.collect.impl.AppengineEnvCollector;
import com.crashnote.appengine.collect.impl.AppengineLogCollector;
import com.crashnote.appengine.config.AppengineConfig;
import com.crashnote.core.collect.Collector;
import com.crashnote.core.collect.impl.EnvCollector;
import com.crashnote.core.collect.impl.LogCollector;
import com.crashnote.core.config.CrashConfig;

public class AppengineCollector
    extends Collector {

    // SETUP ======================================================================================

    public AppengineCollector(final AppengineConfig config) {
        super(config);
    }


    // FACTORY ====================================================================================

    @Override
    protected EnvCollector createEnvColl(final CrashConfig config) {
        return new AppengineEnvCollector(config);
    }

    @Override
    protected LogCollector createLogColl(final CrashConfig config) {
        return new AppengineLogCollector(config);
    }
}
