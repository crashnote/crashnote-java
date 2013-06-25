/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.external.config.impl;

import com.crashnote.external.config.ConfigIncludeContext;
import com.crashnote.external.config.ConfigParseOptions;
import com.crashnote.external.config.ConfigParseable;

class SimpleIncludeContext implements ConfigIncludeContext {

    private final Parseable parseable;

    SimpleIncludeContext(Parseable parseable) {
        this.parseable = parseable;
    }

    SimpleIncludeContext withParseable(Parseable parseable) {
        if (parseable == this.parseable)
            return this;
        else
            return new SimpleIncludeContext(parseable);
    }

    @Override
    public ConfigParseable relativeTo(String filename) {
        if (parseable != null)
            return parseable.relativeTo(filename);
        else
            return null;
    }

    @Override
    public ConfigParseOptions parseOptions() {
        return SimpleIncluder.clearForInclude(parseable.options());
    }
}
