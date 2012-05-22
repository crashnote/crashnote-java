/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.core.config.helper.impl;

import com.crashnote.core.config.helper.ConfigIncluder;
import com.crashnote.core.config.helper.ConfigIncluderClasspath;
import com.crashnote.core.config.helper.ConfigIncluderFile;
import com.crashnote.core.config.helper.ConfigIncluderURL;

interface FullIncluder extends ConfigIncluder, ConfigIncluderFile, ConfigIncluderURL,
            ConfigIncluderClasspath {

}
