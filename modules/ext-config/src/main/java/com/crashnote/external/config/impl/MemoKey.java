/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.external.config.impl;

/** The key used to memoize already-traversed nodes when resolving substitutions */
final class MemoKey {
    MemoKey(final AbstractConfigValue value, final Path restrictToChildOrNull) {
        this.value = value;
        this.restrictToChildOrNull = restrictToChildOrNull;
    }

    final private AbstractConfigValue value;
    final private Path restrictToChildOrNull;

    @Override
    public final int hashCode() {
        final int h = System.identityHashCode(value);
        if (restrictToChildOrNull != null) {
            return h + 41 * (41 + restrictToChildOrNull.hashCode());
        } else {
            return h;
        }
    }

    @Override
    public final boolean equals(final Object other) {
        if (other instanceof MemoKey) {
            final MemoKey o = (MemoKey) other;
            if (o.value != this.value)
                return false;
            else if (o.restrictToChildOrNull == this.restrictToChildOrNull)
                return true;
            else if (o.restrictToChildOrNull == null || this.restrictToChildOrNull == null)
                return false;
            else
                return o.restrictToChildOrNull.equals(this.restrictToChildOrNull);
        } else {
            return false;
        }
    }
}
