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
package com.crashnote.core.model.types;

/**
 * Enumeration to define the different log levels from TRACE to FATAL.
 * Unifies the various framework definitions to one schema to allows easy comparison.
 */
public enum LogLevel {

    TRACE(12),
    DEBUG(10),
    INFO(8),
    WARN(6),
    ERROR(4),
    CRASH(2), // = Uncaught Exceptions
    FATAL(0);


    // SETUP ======================================================================================

    LogLevel(final int lvl) {
        this.lvl = lvl;
    }


    // FIELDS =====================================================================================

    private final int lvl;


    // INTERFACE ==================================================================================

    public boolean isExcp() {
        return (this.lvl <= LogLevel.ERROR.lvl);
    }

    public boolean covers(final LogLevel l) {
        return (this.lvl >= l.lvl);
    }

    public static LogLevel getMinLevel(final LogLevel lvl1, final LogLevel lvl2) {
        return (lvl1.lvl > lvl2.lvl) ? lvl1 : lvl2;
    }

    public static LogLevel getMaxLevel(final LogLevel lvl1, final LogLevel lvl2) {
        return (lvl1.lvl > lvl2.lvl) ? lvl2 : lvl1;
    }
}
