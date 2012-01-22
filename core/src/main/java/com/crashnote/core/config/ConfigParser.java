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
package com.crashnote.core.config;

public class ConfigParser {

    // INTERFACE ==================================================================================

    public int parseInt(final String str) {
        try {
            return Integer.parseInt(cleanup(str));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("value " + str + " is not a number", e);
        }
    }

    public String parseString(final String value) {
        return cleanup(value);
    }

    public boolean parseBool(final String str) {
        final String s = cleanup(str);
        return "true".equals(s) || "yes".equals(s) || "y".equals(s) || "on".equals(s);
    }

    // INTERNALS ==================================================================================

    private static String cleanup(final String str) {
        if (str != null) return str.trim().toLowerCase();
        else return str;
    }
}
