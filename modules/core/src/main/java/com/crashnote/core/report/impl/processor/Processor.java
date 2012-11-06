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
package com.crashnote.core.report.impl.processor;

import com.crashnote.core.Lifecycle;
import com.crashnote.core.config.CrashConfig;
import com.crashnote.core.log.LogLog;
import com.crashnote.core.model.log.ILogSession;
import com.crashnote.core.model.log.LogReport;
import com.crashnote.core.send.Sender;

/**
 * Once a crash report should be sent, the processor comes into the picture. It is responsible for
 * receiving a {@link ILogSession}, transform it into a {@link LogReport} and give it to the
 * {@link Sender}.
 * <p/>
 * The class is abstract in order to allow sub-classes to define the way they want to get the job
 * done themselves.
 */
public abstract class Processor<C extends CrashConfig>
        implements Lifecycle {

    // VARS =======================================================================================

    protected boolean started;
    private final LogLog logger;


    // SETUP ======================================================================================

    public Processor(final C config) {
        this.logger = config.getLogger(this.getClass());
    }


    // INTERFACE ==================================================================================

    public void process(final ILogSession session) {
        if (started)
            doProcess(session);
    }


    // SHARED =====================================================================================

    protected abstract void doProcess(ILogSession session);


    // GET ========================================================================================

    public LogLog getLogger() {
        return logger;
    }
}
