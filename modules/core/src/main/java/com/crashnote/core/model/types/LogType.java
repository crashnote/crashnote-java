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
package com.crashnote.core.model.types;

/**
 * Enumeration to define the type of logging data that is sent
 */
public enum LogType {

    ENV("env", "Environment"),
    ERR("err", "Error");

    // SETUP ======================================================================================

    LogType(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    // FIELDS =====================================================================================

    private final String code;
    private final String name;

    // INTERFACE ==================================================================================

    public boolean hasEnvData() {
        return this == LogType.ENV;
    }

    public static LogType createFromFileName(final String fileName) {
        if (fileName.endsWith(ENV.getExt()))
            return ENV;
        else
            return ERR;
    }

    public String getExt() {
        return "." + code + ".log";
    }

    // GET ========================================================================================

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

}
