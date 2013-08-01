package com.crashnote.core.send;

import com.crashnote.core.util.Base64Util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;

public class HMAC {

    // INTERFACE ==================================================================================

    /**
     * Creates a HMAC (Hash-based Message Authentication Code) from:
     * - HTTP Method
     * - Content-MD5
     * - Content-Type
     * - Date header
     * - Path
     */
    public static String create(final String key,
                                final String verb,
                                final String contentMD5,
                                final String contentType,
                                final String date,
                                final String path) {

        final String input = verb + "\n" +
            contentMD5 + "\n" +
            contentType + "\n" +
            date + "\n" +
            path;

        // sign with SHA-1
        final byte[] raw = sign(key, input);

        // encode via Base64
        return new String(Base64Util.encode(raw));
    }


    // INTERNALS ==================================================================================

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private static byte[] sign(String secret, String data) {
        try {
            final SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA1_ALGORITHM);
            final Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            mac.reset();
            return mac.doFinal(data.getBytes());
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
