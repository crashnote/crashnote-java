package com.crashnote.play2;

import com.crashnote.play2.reporter.Play2Reporter;
import com.crashnote.play2.reporter.ReqHeader;
import play.api.mvc.RequestHeader;
import play.mvc.Http;

/**
 * Interface for a Play2 application.
 *
 * Allows to send errors from Java/Scala-based apps.
 */
public class Crashnote {

    // VARS =======================================================================================

    private static Play2Reporter reporter;


    // INTERFACE ==================================================================================

    /**
     * Java API
     *
     * <pre>{@code
     * // Global.java
     * @Override
     * public Result onError(RequestHeader request, Throwable th) {
     *   Crashnote.report(request, th);
     *   return super.onError(request, th);
     * }
     * }</pre>
     *
     * @param request originating HTTP request
     * @param th      occurred exception
     */
    public static void report(final Http.RequestHeader request, final Throwable th) {
        if (reporter != null)
            reporter.uncaughtException(new ReqHeader(request), null, th);
    }

    /**
     * Scala API
     *
     * <pre>{@code
     * // Global.scala
     * override def onError(request: RequestHeader, th: Throwable) = {
     *   Crashnote.report(request, th)
     *   super.onError(request, th)
     * }
     * }</pre>
     *
     * @param request originating HTTP request
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
