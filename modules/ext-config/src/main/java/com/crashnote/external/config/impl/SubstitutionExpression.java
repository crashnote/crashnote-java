/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.external.config.impl;

final class SubstitutionExpression {

    final private Path path;
    final private boolean optional;

    SubstitutionExpression(final Path path, final boolean optional) {
        this.path = path;
        this.optional = optional;
    }

    Path path() {
        return path;
    }

    boolean optional() {
        return optional;
    }

    SubstitutionExpression changePath(final Path newPath) {
        if (newPath == path)
            return this;
        else
            return new SubstitutionExpression(newPath, optional);
    }

    @Override
    public String toString() {
        return "${" + (optional ? "?" : "") + path.render() + "}";
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof SubstitutionExpression) {
            final SubstitutionExpression otherExp = (SubstitutionExpression) other;
            return otherExp.path.equals(this.path) && otherExp.optional == this.optional;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int h = 41 * (41 + path.hashCode());
        h = 41 * (h + (optional ? 1 : 0));
        return h;
    }
}
