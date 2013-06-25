package com.crashnote.play2;

import com.crashnote.play2.config.Play2Config;
import com.crashnote.play2.config.Play2ConfigFactory;
import com.crashnote.play2.reporter.Play2Reporter;
import com.crashnote.web.CrashSystem;
import play.Application;
import play.Logger;
import play.Plugin;

public class CrashnotePlugin extends Plugin {

    // VARS =======================================================================================

    private final Application application;

    private CrashSystem<Play2Config, Play2Reporter> system;


    // SETUP ======================================================================================

    public CrashnotePlugin(Application app) {
        this.application = app;
    }


    // LIFECYCLE ==================================================================================

    @Override
    public void onStart() {
        system = new CrashSystem<Play2Config, Play2Reporter>();
        if (system.start(new Play2ConfigFactory(application).get())) {
            Crashnote.setReporter(system.getReporter());
            Logger.info("Plugin 'Crashnote' has started");
        } else {
            Logger.info("Plugin 'Crashnote' was NOT started");
        }
    }

    @Override
    public void onStop() {
        if (system != null) system.stop();
        Logger.info("Plugin 'Crashnote' has stopped");
    }

}
