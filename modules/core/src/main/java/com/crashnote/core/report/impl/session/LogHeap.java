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

import com.crashnote.core.model.log.LogEvt;

import java.util.List;
import java.util.ArrayList;

/**
 * This class represents a collection of log events.
 */
public class LogHeap {

    // VARS =======================================================================================

    /**
     * list of exceptions
     */
    private final List<LogEvt<?>> excps;


    // SETUP ======================================================================================

    public LogHeap() {
        excps = new ArrayList<LogEvt<?>>(5);
    }

    /**
     * Copy-Constructor
     */
    public LogHeap(final LogHeap heap) {
        excps = new ArrayList<LogEvt<?>>(heap.getSize());
        for (final LogEvt<?> e : heap.excps) {
            // by deferring the local members, copies of the references they contain are created
            // - thus they can be processed in a separate thread
            e.copy();
            excps.add(e);
        }
    }


    // INTERFACE ==================================================================================

    public void clear() {
        excps.clear();
    }

    public boolean isEmpty() {
        return excps.isEmpty();
    }

    public void addEvt(final LogEvt<?> evt) {
        if (evt.isExcp())
            excps.add(evt);
    }


    // GET ========================================================================================

    public List<LogEvt<?>> getEvents() {
        return excps;
    }

    public int getSize() {
        return excps.size();
    }
}
