package com.crashnote.play2.config;

import com.crashnote.external.config.Config;
import com.crashnote.play2.reporter.Play2Reporter;
import com.crashnote.web.config.WebConfig;

/**
 * Customized {@link WebConfig}: Creates a custom Play2-reporter.
 */
public class Play2Config
    extends WebConfig {

    // SETUP ======================================================================================

    public Play2Config(final Config config) {
        super(config);
    }


    // INTERFACE ==================================================================================

    @Override
    public Play2Reporter getReporter() {
        return new Play2Reporter(this);
    }
}
