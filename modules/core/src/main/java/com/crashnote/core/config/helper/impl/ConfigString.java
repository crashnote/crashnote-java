/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.core.config.helper.impl;

import java.io.ObjectStreamException;
import java.io.Serializable;

import com.crashnote.core.config.helper.ConfigOrigin;
import com.crashnote.core.config.helper.ConfigRenderOptions;
import com.crashnote.core.config.helper.ConfigValueType;

final class ConfigString extends AbstractConfigValue implements Serializable {

    private static final long serialVersionUID = 2L;

    final private String value;

    ConfigString(final ConfigOrigin origin, final String value) {
        super(origin);
        this.value = value;
    }

    @Override
    public ConfigValueType valueType() {
        return ConfigValueType.STRING;
    }

    @Override
    public String unwrapped() {
        return value;
    }

    @Override
    String transformToString() {
        return value;
    }

    @Override
    protected void render(final StringBuilder sb, final int indent, final ConfigRenderOptions options) {
        sb.append(ConfigImplUtil.renderJsonString(value));
    }

    @Override
    protected ConfigString newCopy(final ConfigOrigin origin) {
        return new ConfigString(origin, value);
    }

    // serialization all goes through SerializedConfigValue
    private Object writeReplace() throws ObjectStreamException {
        return new SerializedConfigValue(this);
    }
}
