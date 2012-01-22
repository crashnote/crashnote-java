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
package com.crashnote.core.report;

import com.crashnote.core.Lifecycle;
import com.crashnote.core.config.*;
import com.crashnote.core.log.LogLog;
import com.crashnote.core.model.log.*;
import com.crashnote.core.model.types.ApplicationType;
import com.crashnote.core.report.impl.ThrowableLogEvt;
import com.crashnote.core.report.impl.processor.Processor;
import com.crashnote.core.report.impl.processor.impl.*;
import com.crashnote.core.report.impl.session.*;

import java.io.Serializable;

/**
 * This class is the Grand Central station of the library because every log event goes through
 * here. It's main job is to take these events and put them into the {@link ILogSession},
 * the same goes for context data. It can automatically or manually flush the session
 * in order to send out a crash report by calling the internal {@link Processor}.
 */
public class Reporter<C extends Config>
    implements Thread.UncaughtExceptionHandler, Lifecycle, IConfigChangeListener<C> {

    private boolean started;

    private final LogLog logger;
    private Thread.UncaughtExceptionHandler defaultHandler;

    private final ILogSession session;
    private final Processor<C> processor;

    // configuration settings:
    private boolean enabled;

    // SETUP ======================================================================================

    public Reporter(final C config) {
        updateConfig(config);
        this.logger = config.getLogger(this.getClass());
        this.session = createSessionStore(config);
        this.processor = createProcessor(config);
    }

    public void updateConfig(final C config) {
        config.addListener(this);
        this.enabled = config.isEnabled();
    }

    // LIFECYCLE ==================================================================================

    public boolean start() {
        if (!started) {
            started = true;
            logger.debug("starting module [reporter]");

            processor.start();
            startSession();
        }
        return started;
    }

    public boolean stop() {
        if (started) {
            logger.debug("stopping module [reporter]");
            endSession();
            processor.stop();
            started = false;
        }
        return started;
    }

    // INTERFACE ==================================================================================

    // ===== Session

    public void startSession() {
        if (isOperable())
            session.clear();
    }

    public void flushSession() {
        if (isOperable() && session.hasEvents()) {
            processor.process(session);
        }
    }

    public void endSession() {
        if (isOperable()) {
            flushSession();
            session.clear();
        }
    }

    // ===== Log Context

    public Reporter<C> put(final String key, final Object val) {
        if (isOperable()) session.putCtx(key, val);
        return this;
    }

    public Reporter<C> remove(final String key) {
        if (isOperable()) session.removeCtx(key);
        return this;
    }

    public Reporter<C> clear() {
        if (isOperable()) session.clearCtx();
        return this;
    }

    // ===== Log Events

    public void reportLog(final LogEvt<?> evt) {
        if (isOperable()) {
            // add event to session
            session.addEvent(evt);

            // decide whether to send it immediately
            if (isAutoFlush()) endSession();
        }
    }

    // ===== Uncaught Exceptions

    public void uncaughtException(final Thread t, final Throwable th) {
        // first call custom handler ...
        if (isOperable()) reportLog(new ThrowableLogEvt(t, th));

        // ... then call default handler
        callUncaughtExceptionToDefaultHandler(t, th);
    }

    public final void callUncaughtExceptionToDefaultHandler(final Thread t, final Throwable th) {
        if (defaultHandler != null)
            defaultHandler.uncaughtException(t, th);
    }

    public void registerAsDefaultExcpHandler() {
        final Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (!(currentHandler instanceof Reporter)) {
            defaultHandler = currentHandler; // remember default handler
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }

    public void unregisterAsDefaultExcpHandler() {
        if (defaultHandler != null)
            Thread.setDefaultUncaughtExceptionHandler(defaultHandler);
    }

    // SHARED =====================================================================================

    protected boolean isAutoFlush() {
        return true;
    }

    protected final boolean isOperable() {
        return isEnabled() && isStarted();
    }

    // FACTORY ====================================================================================

    protected ILogSession createSessionStore(final C config) {
        if (config.getApplicationType() == ApplicationType.SERVER)
            return new SharedLogSession();
        else
            return new LocalLogSession();
    }

    protected Processor<C> createProcessor(final C config) {
        final SyncProcessor<C> syncPrc = new SyncProcessor<C>(config);
        if (config.isSync())
            return syncPrc;
        else
            return new AsyncProcessor<C>(config, syncPrc);
    }

    // GET ========================================================================================

    public Processor<C> getProcessor() {
        return processor;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isStarted() {
        return started;
    }

    public ILogSession getSession() {
        return session;
    }
}