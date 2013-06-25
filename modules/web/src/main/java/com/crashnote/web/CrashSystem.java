package com.crashnote.web;

import com.crashnote.logger.config.LoggerConfig;
import com.crashnote.logger.helper.AutoLogConnector;
import com.crashnote.logger.report.LoggerReporter;

public class CrashSystem<C extends LoggerConfig, R extends LoggerReporter> {

    // VARS =======================================================================================

    private R reporter;

    private boolean started = false;

    private AutoLogConnector connector;


    // INTERFACE ==================================================================================

    public boolean start(final C config) {

        // if the configuration enables Crashnote ...
        if (!started && config.isEnabled()) {

            // ... create the central reporter service
            reporter = (R) config.getReporter();
            reporter.start();

            // ... and install the log appender(s)
            connector = config.getLogConnector(reporter);
            connector.start();

            started = true;
        }

        return started;
    }

    public void stop() {

        // disconnect the appenders
        if (connector != null) connector.stop();

        // close the reporter (and let it quickly flush cached data)
        if (reporter != null) reporter.stop();

        started = false;
    }


    // GET ========================================================================================

    public R getReporter() {
        return reporter;
    }

    public boolean isStarted() {
        return started;
    }
}
