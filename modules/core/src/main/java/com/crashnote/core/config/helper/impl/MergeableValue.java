package com.crashnote.core.config.helper.impl;

import com.crashnote.core.config.helper.ConfigMergeable;
import com.crashnote.core.config.helper.ConfigValue;

interface MergeableValue extends ConfigMergeable {
    // converts a Config to its root object and a ConfigValue to itself
    ConfigValue toFallbackValue();
}
