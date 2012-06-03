/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.core.config.helper.impl;

import java.util.List;
import java.util.ArrayList;

import com.crashnote.core.config.helper.ConfigException;
import com.crashnote.core.config.helper.ConfigResolveOptions;
import com.crashnote.core.config.helper.impl.AbstractConfigValue.NotPossibleToResolve;

final class ResolveContext {
    // this is unfortunately mutable so should only be shared among
    // ResolveContext in the same traversal.
    final private ResolveSource source;

    // this is unfortunately mutable so should only be shared among
    // ResolveContext in the same traversal.
    final private ResolveMemos memos;

    final private ConfigResolveOptions options;
    // the current path restriction, used to ensure lazy
    // resolution and avoid gratuitous cycles. without this,
    // any sibling of an object we're traversing could
    // cause a cycle "by side effect"
    // CAN BE NULL for a full resolve.
    final private Path restrictToChild;

    // another mutable unfortunate. This is
    // used to make nice error messages when
    // resolution fails.
    final private List<SubstitutionExpression> expressionTrace;

    ResolveContext(final ResolveSource source, final ResolveMemos memos, final ConfigResolveOptions options,
            final Path restrictToChild, final List<SubstitutionExpression> expressionTrace) {
        this.source = source;
        this.memos = memos;
        this.options = options;
        this.restrictToChild = restrictToChild;
        this.expressionTrace = expressionTrace;
    }

    ResolveContext(final AbstractConfigObject root, final ConfigResolveOptions options, final Path restrictToChild) {
        // LinkedHashSet keeps the traversal order which is at least useful
        // in error messages if nothing else
        this(new ResolveSource(root), new ResolveMemos(), options, restrictToChild,
                new ArrayList<SubstitutionExpression>());
    }

    ResolveSource source() {
        return source;
    }

    ConfigResolveOptions options() {
        return options;
    }

    boolean isRestrictedToChild() {
        return restrictToChild != null;
    }

    Path restrictToChild() {
        return restrictToChild;
    }

    ResolveContext restrict(final Path restrictTo) {
        if (restrictTo == restrictToChild)
            return this;
        else
            return new ResolveContext(source, memos, options, restrictTo, expressionTrace);
    }

    ResolveContext unrestricted() {
        return restrict(null);
    }

    void trace(final SubstitutionExpression expr) {
        expressionTrace.add(expr);
    }

    void untrace() {
        expressionTrace.remove(expressionTrace.size() - 1);
    }

    String traceString() {
        final String separator = ", ";
        final StringBuilder sb = new StringBuilder();
        for (final SubstitutionExpression expr : expressionTrace) {
            sb.append(expr.toString());
            sb.append(separator);
        }
        if (sb.length() > 0)
            sb.setLength(sb.length() - separator.length());
        return sb.toString();
    }

    AbstractConfigValue resolve(final AbstractConfigValue original) throws NotPossibleToResolve {
        // a fully-resolved (no restrictToChild) object can satisfy a
        // request for a restricted object, so always check that first.
        final MemoKey fullKey = new MemoKey(original, null);
        MemoKey restrictedKey = null;

        AbstractConfigValue cached = memos.get(fullKey);

        // but if there was no fully-resolved object cached, we'll only
        // compute the restrictToChild object so use a more limited
        // memo key
        if (cached == null && isRestrictedToChild()) {
            restrictedKey = new MemoKey(original, restrictToChild());
            cached = memos.get(restrictedKey);
        }

        if (cached != null) {
            return cached;
        } else {
            final AbstractConfigValue resolved = source.resolveCheckingReplacement(this, original);

            if (resolved == null || resolved.resolveStatus() == ResolveStatus.RESOLVED) {
                // if the resolved object is fully resolved by resolving
                // only the restrictToChildOrNull, then it can be cached
                // under fullKey since the child we were restricted to
                // turned out to be the only unresolved thing.
                memos.put(fullKey, resolved);
            } else {
                // if we have an unresolved object then either we did a
                // partial resolve restricted to a certain child, or it's
                // a bug.
                if (isRestrictedToChild()) {
                    if (restrictedKey == null) {
                        throw new ConfigException.BugOrBroken(
                                "restrictedKey should not be null here");
                    }
                    memos.put(restrictedKey, resolved);
                } else {
                    throw new ConfigException.BugOrBroken(
                            "resolveSubstitutions() did not give us a resolved object");
                }
            }

            return resolved;
        }
    }

    static AbstractConfigValue resolve(final AbstractConfigValue value, final AbstractConfigObject root,
            final ConfigResolveOptions options, final Path restrictToChildOrNull) {
        final ResolveContext context = new ResolveContext(root, options, null /* restrictToChild */);

        try {
            return context.resolve(value);
        } catch (NotPossibleToResolve e) {
            // ConfigReference was supposed to catch NotPossibleToResolve
            throw new ConfigException.BugOrBroken(
                    "NotPossibleToResolve was thrown from an outermost resolve", e);
        }
    }

    static AbstractConfigValue resolve(final AbstractConfigValue value, final AbstractConfigObject root,
            final ConfigResolveOptions options) {
        return resolve(value, root, options, null);
    }
}
