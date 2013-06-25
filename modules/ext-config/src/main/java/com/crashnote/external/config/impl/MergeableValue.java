package com.crashnote.external.config.impl;

import com.crashnote.external.config.ConfigMergeable;
import com.crashnote.external.config.ConfigValue;

interface MergeableValue extends ConfigMergeable {
    // converts a Config to its root object and a ConfigValue to itself
    ConfigValue toFallbackValue();
}
