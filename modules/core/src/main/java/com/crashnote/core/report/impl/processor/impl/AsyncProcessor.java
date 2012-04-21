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

import com.crashnote.core.config.Config;
import com.crashnote.core.model.log.ILogSession;
import com.crashnote.core.report.impl.processor.Processor;

import java.util.concurrent.*;

/**
 * This implementation of the {@link Processor} works asynchronous. It uses a scheduler
 * to wrap the {@link SyncProcessor} in a separate thread and regularly process
 * the incoming {@link ILogSession}s.
 * <p/>
 * Because it uses {@link ScheduledExecutorService} the library requires at least JDK 1.5.
 */
public class AsyncProcessor<C extends Config>
    extends Processor<C> {

    private final Processor<C> delegate;
    private final ScheduledExecutorService scheduler;

    // SETUP ======================================================================================

    public AsyncProcessor(final C config, final Processor<C> delegate) {
        super(config);
        this.delegate = delegate;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    // LIFECYCLE ==================================================================================

    /**
     * Start the processor.
     */
    public boolean start() {
        if (!started) {
            started = true;
            getLogger().debug("starting async processor");
            delegate.start();
        }
        return started;
    }

    /**
     * Stop the processor,
     * also shutdown the async scheduler (exit the thread) and wait some time for it to finish.
     */
    public boolean stop() {
        if (started) {
            started = false;
            getLogger().debug("stopping async processor");

            scheduler.shutdown(); // NOTE: tried shutdownNow() & executing rest syncr., but one was always missing
            try {
                scheduler.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            } finally {
                delegate.stop();
            }
        }
        return started;
    }

    // SHARED =====================================================================================

    @Override
    protected void doProcess(final ILogSession session) {
        getLogger().debug("deferring log session");
        scheduler.submit(new SendTask(delegate, session.copy()));
    }

    // INTERNALS ==================================================================================

    private class SendTask implements Callable<Void> {

        private final ILogSession session;
        private final Processor delegate;

        public SendTask(final Processor delegate, final ILogSession session) {
            this.session = session;
            this.delegate = delegate;
        }

        public Void call() throws Exception {
            delegate.process(session);
            return null;
        }
    }

}