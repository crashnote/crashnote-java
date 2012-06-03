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

import java.net.*;

/**
 * Utility class to read a system's network settings (e.g. IP, Name, Mac Address)
 */
public class NetUtil {

    // SETUP ======================================================================================

    private NetUtil() {
        // singleton
    }


    // INTERFACE ==================================================================================

    public static String getHostAddress() {
        try {
            return getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public static String getHostName() {
        try {
            return getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /**
     * Reads the MAC address of the machine. Since this only works on JDK 1.6+ it is wrapped in a
     * big try/catch block and returns null for older JDKs.
     *
     * @return numeric value describing the mac address
     */
    public static Long getMacAddress() {
        try {
            final InetAddress addr = getLocalHost();
            final NetworkInterface ni = NetworkInterface.getByInetAddress(addr);
            final byte[] mac = ni.getHardwareAddress();
            return ((long) mac[5] & 0xff)
                    + (((long) mac[4] & 0xff) << 8)
                    + (((long) mac[3] & 0xff) << 16)
                    + (((long) mac[2] & 0xff) << 24)
                    + (((long) mac[1] & 0xff) << 32)
                    + (((long) mac[0] & 0xff) << 40);
        } catch (Throwable e) {
            return null;
        }
    }


    // INTERNALS ==================================================================================

    private static InetAddress getLocalHost() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }
}
