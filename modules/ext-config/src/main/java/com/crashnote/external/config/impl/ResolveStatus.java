/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.external.config.impl;

import java.util.Collection;

/**
 * Status of substitution resolution.
 */
enum ResolveStatus {
    UNRESOLVED, RESOLVED;

    final static ResolveStatus fromValues(
            final Collection<? extends AbstractConfigValue> values) {
        for (final AbstractConfigValue v : values) {
            if (v.resolveStatus() == ResolveStatus.UNRESOLVED)
                return ResolveStatus.UNRESOLVED;
        }
        return ResolveStatus.RESOLVED;
    }

    final static ResolveStatus fromBoolean(final boolean resolved) {
        return resolved ? ResolveStatus.RESOLVED : ResolveStatus.UNRESOLVED;
    }
}
