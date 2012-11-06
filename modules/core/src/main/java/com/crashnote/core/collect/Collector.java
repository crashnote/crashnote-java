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
package com.crashnote.core.collect;

import com.crashnote.core.Lifecycle;
import com.crashnote.core.collect.impl.EnvCollector;
import com.crashnote.core.collect.impl.LogCollector;
import com.crashnote.core.config.CrashConfig;
import com.crashnote.core.model.data.DataObject;
import com.crashnote.core.model.log.ILogSession;
import com.crashnote.core.model.log.LogEvt;

import java.util.List;
import java.util.Map;

/**
 * This class provides the functionality to transform events, state, properties and context data
 * into a structured form that can be parsed by an external API. The structure is based on
 * abstract object and array containers.
 */
public class Collector<C extends CrashConfig>
    extends BaseCollector<C> implements Lifecycle {

    // VARS =======================================================================================

    private boolean started;
    private final EnvCollector<C> env_c;
    private final LogCollector<C> log_c;


    // SETUP ======================================================================================

    public Collector(final C config) {
        super(config);

        this.env_c = createEnvColl(config);
        this.log_c = createLogColl(config);
    }


    // LIFECYCLE ==================================================================================

    @Override
    public boolean start() {
        if (!started) {
            started = true;
            getLogger().debug("starting module [collector]");
        }
        return started;
    }

    @Override
    public boolean stop() {
        if (started) {
            started = false;
            getLogger().debug("stopping module [collector]");
        }
        return started;
    }


    // INTERFACE ==================================================================================

    public DataObject collectLog(final ILogSession session) {
        final DataObject data = createDataObj();
        {
            final List<LogEvt<?>> logs = session.getEvents();

            // log(s)
            if (logs.size() == 1)
                data.putObj("log", log_c.collect(logs.get(0)));
            else
                data.putArr("log", log_c.collect(logs));

            // ctx
            final DataObject ctx = createDataObj();
            {
                final Map<String, Object> map = session.getContext();
                for (final Map.Entry<String, Object> entry : map.entrySet())
                    ctx.put(entry.getKey(), entry.getValue());
            }
            data.putObj("ctx", ctx);

            // env
            data.putObj("env", env_c.collect());
        }
        return data;
    }


    // FACTORY ====================================================================================

    protected EnvCollector<C> createEnvColl(final C config) {
        return new EnvCollector<C>(config);
    }

    protected LogCollector<C> createLogColl(final C config) {
        return new LogCollector<C>(config);
    }


    // GET ========================================================================================

    public EnvCollector<C> getEnvCollector() {
        return env_c;
    }

    public LogCollector<C> getLogCollector() {
        return log_c;
    }
}
