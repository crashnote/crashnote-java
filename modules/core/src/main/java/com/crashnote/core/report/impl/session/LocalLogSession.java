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
package com.crashnote.core.report.impl.session;

import com.crashnote.core.model.log.ILogSession;
import com.crashnote.core.model.log.LogEvt;

import java.util.List;
import java.util.Map;

/**
 * Implementation of a {@link ILogSession} which wraps a {@link SharedLogSession} within a
 * {@link ThreadLocal}. This way each thread that accesses the data has its own copy.
 * <p/>
 * This makes it a perfect candidate for usage in servlet environments with its
 * request/response threads.
 */
public class LocalLogSession
        implements ILogSession {

    // VARS =======================================================================================

    private final ThreadLocal<SharedLogSession> session =
            new InheritableThreadLocal<SharedLogSession>() {
                @Override
                protected SharedLogSession initialValue() {
                    return new SharedLogSession();
                }
            };


    // INTERFACE ==================================================================================

    @Override
    public ILogSession copy() {
        return new SharedLogSession(session.get());
    }

    @Override
    public void clear() {
        getSession().clear();
    }

    // ===== EVENTS

    @Override
    public List<LogEvt<?>> getEvents() {
        return getSession().getEvents();
    }

    @Override
    public void addEvent(final LogEvt<?> evt) {
        getSession().addEvent(evt);
    }

    @Override
    public void clearEvents() {
        getSession().clearEvents();
    }

    @Override
    public boolean isEmpty() {
        return getSession().isEmpty();
    }

    // ===== CONTEXT

    @Override
    public void putCtx(final String key, final Object val) {
        getSession().putCtx(key, val);
    }

    @Override
    public void removeCtx(final String key) {
        getSession().removeCtx(key);
    }

    @Override
    public void clearCtx() {
        getSession().clearCtx();
    }

    @Override
    public Map<String, Object> getContext() {
        return getSession().getContext();
    }

    @Override
    public boolean hasContext() {
        return getSession().hasContext();
    }


    // INTERNALS ==================================================================================

    protected ILogSession getSession() {
        return session.get();
    }
}
