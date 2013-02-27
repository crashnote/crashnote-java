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
package com.crashnote.core.util;

import java.util.zip.Adler32;

public class ChksumUtil {

    // INTERFACE ==================================================================================

    /**
     * Computes the hash value of a String.
     *
     * @param s the string to be hashed
     * @return the hash value
     */
    public static long hash(final String s) {
        final Adler32 chk = new Adler32();
        chk.update(s.getBytes());
        return chk.getValue();
    }
}