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
package com.crashnote.core.util;

import java.net.URL;
import java.util.*;

/**
 * Utility class to access the system's settings and properties.
 */
public class SystemUtil {

    private final int MB = 1024 * 1024;

    // INTERFACE ==================================================================================

    /**
     * Checks if a system property for a given key exists.
     *
     * @param key the name of the system property
     */
    public boolean hasProperty(final String key) {
        return getProperty(key) != null;
    }

    /**
     * Gets the system property indicated by the specified key.
     *
     * @param key the name of the system property
     * @return the string value of the system property, or null if there is no property with that key
     */
    public String getProperty(final String key) {
        return getProperty(key, null);
    }

    public String getProperty(final String key, final String def) {
        return System.getProperty(key, def);
    }

    public Set<Object> getPropertyKeys() {
        return System.getProperties().keySet();
    }

    public Properties loadProperties(final String fileName) {
        final Properties p = new Properties();
        try {
            final URL url = ClassLoader.getSystemResource(fileName);
            p.load(url.openStream());
        } catch (Exception ignored) {
        }
        return p;
    }

    public Properties getProperties() {
        return System.getProperties();
    }

    // ==== Environment

    public Set<String> getEnvKeys() {
        return System.getenv().keySet();
    }

    /**
     * Gets the value of the specified environment variable.
     *
     * @param key the name of the environment variable
     * @return the string value of the variable, or null if the variable is not defined in the system environment
     */
    public String getEnv(final String key) {
        return System.getenv(key);
    }

    public String getEnv(final String key, final String def) {
        final String val = System.getenv(key);
        return val == null ? def : val;
    }

    public Properties getEnvProperties() {
        final Set<String> envKeys = getEnvKeys();
        final Properties props = new Properties();
        for (final String key : envKeys) {
            props.setProperty(key, getEnv(key));
        }
        return props;
    }

    // ==== Network

    public boolean isOffline() {
        return false; // just assume NO by default
    }

    public String getHostAddress() {
        return NetUtil.getHostAddress();
    }

    public String getHostName() {
        return NetUtil.getHostName();
    }

    // ==== Runtime

    public String getRuntimeName() {
        return getProperty("java.vm.name");
    }

    public String getRuntimeVersion() {
        return getProperty("java.version");
    }

    // ==== Hardware

    public Long getTotalMemorySize() {
        return Runtime.getRuntime().totalMemory() / MB;
    }

    public Long getAvailableMemorySize() {
        return Runtime.getRuntime().freeMemory() / MB;
    }

    public int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    public Long getSystemId() {
        return NetUtil.getMacAddress();
    }

    // ==== Locale

    /**
     * Returns the language code for this locale,
     * which will either be the empty string or a lowercase ISO 639 code.
     */
    public String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public String getTimezoneId() {
        return TimeZone.getDefault().getID();
    }

    /**
     * Returns the offset of this time zone from UTC at the specified date.
     *
     * @return the amount of time in minutes to add to UTC to get local time.
     */
    public long getTimezoneOffset() {
        return TimeZone.getDefault().getOffset(new Date().getTime()) / 60000L;
    }

    // ==== Operating System

    /**
     * @return the name of the operating system,  null if not found
     */
    public String getOSName() {
        return getProperty("os.name");
    }

    /**
     * @return the version of the operating system, null if not found
     */
    public String getOSVersion() {
        return getProperty("os.version");
    }

}
