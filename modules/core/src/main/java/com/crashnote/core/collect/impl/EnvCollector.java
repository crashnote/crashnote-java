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
package com.crashnote.core.collect.impl;

import com.crashnote.core.collect.BaseCollector;
import com.crashnote.core.config.CrashConfig;
import com.crashnote.core.model.data.DataObject;

import java.util.List;

import static com.crashnote.core.util.FilterUtil.doFilter;

/**
 * Collector to transform an application's environment data (e.g. version, system hardware etc.)
 * into a structured form.
 */
public class EnvCollector
    extends BaseCollector {

    // VARS =======================================================================================

    // configuration settings:
    private final Long startTime;
    private final String env;
    private final String version;
    private final String build;
    private final String clientInfo;
    private final List<String> envFilters;


    // SETUP ======================================================================================

    public <C extends CrashConfig> EnvCollector(final C config) {
        super(config);

        this.env = config.getAppEnv();
        this.build = config.getAppBuild();
        this.version = config.getAppVersion();
        this.startTime = config.getStartTime();
        this.clientInfo = config.getClientInfo();
        this.envFilters = config.getEnvironmentFilters();
    }


    // INTERFACE ==================================================================================

    public DataObject collect() {
        final DataObject data = createDataObj();
        {
            data.putObj("app", getAppData());
            data.putObj("runtime", getRtData());
            data.putObj("system", getSysData());
            data.putObj("device", getDevData());
        }
        return data;
    }


    // SHARED =====================================================================================

    protected DataObject getAppData() {
        final DataObject appData = createDataObj();
        {
            appData.put("build", build);
            appData.put("env", env);
            appData.put("version", version);
            appData.put("agent", clientInfo);
            appData.put("startedAt", formatTimestamp(startTime));
        }
        return appData;
    }

    protected DataObject getRtData() {
        final DataObject rtData = createDataObj();
        {
            rtData.put("type", "java");
            rtData.put("name", getSysUtil().getRuntimeName());
            rtData.put("version", getSysUtil().getRuntimeVersion());

            // properties
            final DataObject props = createDataObj();
            {
                for (final Object key : getSysUtil().getPropertyKeys()) {
                    final String name = key.toString();
                    final String value = getSysUtil().getProperty(name);
                    if (!ignoreProperty(name, value)) props.put(name, value);
                }
            }
            rtData.put("properties", props);
        }
        return rtData;
    }

    protected DataObject getSysData() {
        final DataObject sysData = createDataObj();
        {
            // identity
            sysData.put("id", getSysUtil().getSystemId());
            sysData.put("ip", getSysUtil().getHostAddress());
            sysData.put("name", getSysUtil().getHostName());

            // settings
            sysData.put("timezone", getSysUtil().getTimezoneId());
            sysData.put("timezoneOffset", getSysUtil().getTimezoneOffset());
            //sysData.put("language", getSysUtil().getLanguage());

            // OS
            sysData.put("osName", getSysUtil().getOSName());
            sysData.put("osVersion", getSysUtil().getOSVersion());

            // environment properties
            final DataObject props = createDataObj();
            {
                for (final String name : getSysUtil().getEnvKeys())
                    props.put(name, doFilter(name, envFilters) ? filtered : getSysUtil().getEnv(name));
            }
            sysData.putObj("properties", props);
        }
        return sysData;
    }

    protected DataObject getDevData() {
        final DataObject devData = createDataObj();
        {
            // CPU
            devData.put("cores", getSysUtil().getAvailableProcessors());

            // RAM
            /*
            devData.put("ram", getSysUtil().getTotalMemorySize());
            devData.put("ram_free", getSysUtil().getAvailableMemorySize());
            */
        }
        return devData;
    }

    protected boolean ignoreProperty(final String name, final String value) {
        return value.length() > 255
            || name.startsWith("java.")
            || name.startsWith("user.")
            || name.startsWith("os.")
            || name.startsWith("awt.")
            || name.startsWith("jna.")
            || name.startsWith("file.")
            || name.startsWith("sun.")
            || name.endsWith(".separator");
    }


    // GET / SET ==================================================================================

    public Long getStartTime() {
        return startTime;
    }
}
