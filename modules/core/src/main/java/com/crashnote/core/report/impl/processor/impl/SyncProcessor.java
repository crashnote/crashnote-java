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
package com.crashnote.core.report.impl.processor.impl;

import com.crashnote.core.collect.Collector;
import com.crashnote.core.config.*;
import com.crashnote.core.model.log.*;
import com.crashnote.core.report.impl.processor.Processor;
import com.crashnote.core.send.Sender;

/**
 * This implementation of the {@link Processor} works synchronous. This means that whenever
 * it receives a {@link ILogSession} it creates a {@link LogReport} and sends it right away,
 * blocking the current thread.
 */
public class SyncProcessor<C extends CrashConfig>
        extends Processor<C> {

    // VARS =======================================================================================

    private final Sender<C> sender;
    private final Collector<C> collector;


    // SETUP ======================================================================================

    public SyncProcessor(final C config) {
        super(config);
        this.sender = config.getSender();
        this.collector = config.getCollector();
    }


    // LIFECYCLE ==================================================================================

    @Override
    public boolean start() {
        if (!started) {
            started = true;
            getLogger().debug("starting sync processor");

            // start the sub modules
            collector.start();
        }
        return started;
    }

    @Override
    public boolean stop() {
        if (started) {
            started = false;
            getLogger().debug("stopping sync processor");

            // stop sub-classes
            collector.stop();
        }
        return started;
    }


    // SHARED =====================================================================================

    @Override
    protected void doProcess(final ILogSession session) {
        sender.send(new LogReport(collector.collectLog(session)));
    }

}
