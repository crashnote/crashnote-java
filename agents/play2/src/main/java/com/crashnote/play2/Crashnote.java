package com.crashnote.play2;

import com.crashnote.play2.reporter.Play2Reporter;
import com.crashnote.play2.reporter.ReqHeader;
import play.api.mvc.RequestHeader;
import play.mvc.Http;

public class Crashnote {

    // VARS =======================================================================================

    private static Play2Reporter reporter;


    // INTERFACE ==================================================================================

    /**
     * Java API
     *
     * @param request original HTTP request
     * @param th      occurred exception
     */
    public static void report(final Http.RequestHeader request, final Throwable th) {
        if (reporter != null)
            reporter.uncaughtException(new ReqHeader(request), null, th);
    }

    /**
     * Scala API
     *
     * @param request original HTTP request
     * @param th      occurred exception
     */
    public static void report(final RequestHeader request, final Throwable th) {
        if (reporter != null)
            reporter.uncaughtException(new ReqHeader(request), null, th);
    }


    // GET / SET ==================================================================================

    public static void setReporter(final Play2Reporter reporter) {
        Crashnote.reporter = reporter;
    }
}
