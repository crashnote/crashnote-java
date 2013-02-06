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