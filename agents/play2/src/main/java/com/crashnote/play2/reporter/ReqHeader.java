package com.crashnote.play2.reporter;

import play.api.mvc.RequestHeader;
import play.mvc.Http;

/**
 * Wrapper around Play2's RequestHeader, Scala and Java respectively
 */
public class ReqHeader {

    // VARS =======================================================================================

    private final RequestHeader scalaReq;
    private final Http.RequestHeader javaReq;


    // SETUP ======================================================================================

    public ReqHeader(final RequestHeader rh) {
        this.scalaReq = rh;
        this.javaReq = null;
    }

    public ReqHeader(final Http.RequestHeader rh) {
        this.javaReq = rh;
        this.scalaReq = null;
    }

    // INTERFACE ==================================================================================

    public String method() {
        if (scalaReq == null)
            return javaReq.method();
        else
            return scalaReq.method();
    }

    public String host() {
        if (scalaReq == null)
            return javaReq.host();
        else
            return scalaReq.host();
    }

    public String uri() {
        if (scalaReq == null)
            return javaReq.uri();
        else
            return scalaReq.uri();
    }
}
