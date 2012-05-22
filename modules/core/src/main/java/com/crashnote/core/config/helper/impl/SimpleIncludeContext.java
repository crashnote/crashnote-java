/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.core.config.helper.impl;

import com.crashnote.core.config.helper.ConfigIncludeContext;
import com.crashnote.core.config.helper.ConfigParseOptions;
import com.crashnote.core.config.helper.ConfigParseable;

class SimpleIncludeContext implements ConfigIncludeContext {

    private final Parseable parseable;

    SimpleIncludeContext(final Parseable parseable) {
        this.parseable = parseable;
    }

    SimpleIncludeContext withParseable(final Parseable parseable) {
        if (parseable == this.parseable)
            return this;
        else
            return new SimpleIncludeContext(parseable);
    }

    @Override
    public ConfigParseable relativeTo(final String filename) {
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
