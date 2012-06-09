/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.external.config.impl;

// caution: ordinals used in serialization
enum OriginType {
    GENERIC,
    FILE,
    URL,
    RESOURCE
}
