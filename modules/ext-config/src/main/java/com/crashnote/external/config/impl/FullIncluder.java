/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.external.config.impl;

import com.crashnote.external.config.ConfigIncluder;
import com.crashnote.external.config.ConfigIncluderClasspath;
import com.crashnote.external.config.ConfigIncluderFile;
import com.crashnote.external.config.ConfigIncluderURL;

interface FullIncluder extends ConfigIncluder, ConfigIncluderFile, ConfigIncluderURL,
            ConfigIncluderClasspath {

}
