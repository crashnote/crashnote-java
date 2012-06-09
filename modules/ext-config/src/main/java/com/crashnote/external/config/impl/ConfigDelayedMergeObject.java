/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.external.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.crashnote.external.config.ConfigException;
import com.crashnote.external.config.ConfigList;
import com.crashnote.external.config.ConfigMergeable;
import com.crashnote.external.config.ConfigOrigin;
import com.crashnote.external.config.ConfigRenderOptions;
import com.crashnote.external.config.ConfigValue;

// This is just like ConfigDelayedMerge except we know statically
// that it will turn out to be an object.
final class ConfigDelayedMergeObject extends AbstractConfigObject implements Unmergeable,
        ReplaceableMergeStack {

    final private List<AbstractConfigValue> stack;

    ConfigDelayedMergeObject(final ConfigOrigin origin, final List<AbstractConfigValue> stack) {
        super(origin);
        this.stack = stack;

        if (stack.isEmpty())
            throw new ConfigException.BugOrBroken(
                    "creating empty delayed merge object");
        if (!(stack.get(0) instanceof AbstractConfigObject))
            throw new ConfigException.BugOrBroken(
                    "created a delayed merge object not guaranteed to be an object");

        for (final AbstractConfigValue v : stack) {
            if (v instanceof ConfigDelayedMerge || v instanceof ConfigDelayedMergeObject)
                throw new ConfigException.BugOrBroken(
                        "placed nested DelayedMerge in a ConfigDelayedMergeObject, should have consolidated stack");
        }
    }

    @Override
    protected ConfigDelayedMergeObject newCopy(final ResolveStatus status, final ConfigOrigin origin) {
        if (status != resolveStatus())
            throw new ConfigException.BugOrBroken(
                    "attempt to create resolved ConfigDelayedMergeObject");
        return new ConfigDelayedMergeObject(origin, stack);
    }

    @Override
    AbstractConfigObject resolveSubstitutions(final ResolveContext context)
            throws NotPossibleToResolve {
        final AbstractConfigValue merged = ConfigDelayedMerge.resolveSubstitutions(this, stack, context);
        if (merged instanceof AbstractConfigObject) {
            return (AbstractConfigObject) merged;
        } else {
            throw new ConfigException.BugOrBroken(
                    "somehow brokenly merged an object and didn't get an object, got " + merged);
        }
    }

    @Override
    public ResolveReplacer makeReplacer(final int skipping) {
        return new ResolveReplacer() {
            @Override
            protected AbstractConfigValue makeReplacement(final ResolveContext context)
                    throws NotPossibleToResolve {
                return ConfigDelayedMerge.makeReplacement(context, stack, skipping);
            }
        };
    }

    @Override
    ResolveStatus resolveStatus() {
        return ResolveStatus.UNRESOLVED;
    }

    @Override
    ConfigDelayedMergeObject relativized(final Path prefix) {
        final List<AbstractConfigValue> newStack = new ArrayList<AbstractConfigValue>();
        for (final AbstractConfigValue o : stack) {
            newStack.add(o.relativized(prefix));
        }
        return new ConfigDelayedMergeObject(origin(), newStack);
    }

    @Override
    protected boolean ignoresFallbacks() {
        return ConfigDelayedMerge.stackIgnoresFallbacks(stack);
    }

    @Override
    protected final ConfigDelayedMergeObject mergedWithTheUnmergeable(final Unmergeable fallback) {
        requireNotIgnoringFallbacks();

        return (ConfigDelayedMergeObject) mergedWithTheUnmergeable(stack, fallback);
    }

    @Override
    protected final ConfigDelayedMergeObject mergedWithObject(final AbstractConfigObject fallback) {
        return mergedWithNonObject(fallback);
    }

    @Override
    protected final ConfigDelayedMergeObject mergedWithNonObject(final AbstractConfigValue fallback) {
        requireNotIgnoringFallbacks();

        return (ConfigDelayedMergeObject) mergedWithNonObject(stack, fallback);
    }

    @Override
    public ConfigDelayedMergeObject withFallback(final ConfigMergeable mergeable) {
        return (ConfigDelayedMergeObject) super.withFallback(mergeable);
    }

    @Override
    public ConfigDelayedMergeObject withOnlyKey(final String key) {
        throw notResolved();
    }

    @Override
    public ConfigDelayedMergeObject withoutKey(final String key) {
        throw notResolved();
    }

    @Override
    protected AbstractConfigObject withOnlyPathOrNull(final Path path) {
        throw notResolved();
    }

    @Override
    AbstractConfigObject withOnlyPath(final Path path) {
        throw notResolved();
    }

    @Override
    AbstractConfigObject withoutPath(final Path path) {
        throw notResolved();
    }

    @Override
    public Collection<AbstractConfigValue> unmergedValues() {
        return stack;
    }

    @Override
    protected boolean canEqual(final Object other) {
        return other instanceof ConfigDelayedMergeObject;
    }

    @Override
    public boolean equals(final Object other) {
        // note that "origin" is deliberately NOT part of equality
        if (other instanceof ConfigDelayedMergeObject) {
            return canEqual(other)
                    && this.stack
                            .equals(((ConfigDelayedMergeObject) other).stack);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // note that "origin" is deliberately NOT part of equality
        return stack.hashCode();
    }

    @Override
    protected void render(final StringBuilder sb, final int indent, final String atKey, final ConfigRenderOptions options) {
        ConfigDelayedMerge.render(stack, sb, indent, atKey, options);
    }

    @Override
    protected void render(final StringBuilder sb, final int indent, final ConfigRenderOptions options) {
        render(sb, indent, null, options);
    }

    private static ConfigException notResolved() {
        return new ConfigException.NotResolved(
                "need to Config#resolve() before using this object, see the API docs for Config#resolve()");
    }

    @Override
    public Map<String, Object> unwrapped() {
        throw notResolved();
    }

    @Override
    public AbstractConfigValue get(final Object key) {
        throw notResolved();
    }

    @Override
    public boolean containsKey(final Object key) {
        throw notResolved();
    }

    @Override
    public boolean containsValue(final Object value) {
        throw notResolved();
    }

    @Override
    public Set<java.util.Map.Entry<String, ConfigValue>> entrySet() {
        throw notResolved();
    }

    @Override
    public boolean isEmpty() {
        throw notResolved();
    }

    @Override
    public Set<String> keySet() {
        throw notResolved();
    }

    @Override
    public int size() {
        throw notResolved();
    }

    @Override
    public Collection<ConfigValue> values() {
        throw notResolved();
    }

    @Override
    protected AbstractConfigValue attemptPeekWithPartialResolve(final String key) {
        // a partial resolve of a ConfigDelayedMergeObject always results in a
        // SimpleConfigObject because all the substitutions in the stack get
        // resolved in order to look up the partial.
        // So we know here that we have not been resolved at all even
        // partially.
        // Given that, all this code is probably gratuitous, since the app code
        // is likely broken. But in general we only throw NotResolved if you try
        // to touch the exact key that isn't resolved, so this is in that
        // spirit.

        // we'll be able to return a key if we have a value that ignores
        // fallbacks, prior to any unmergeable values.
        for (final AbstractConfigValue layer : stack) {
            if (layer instanceof AbstractConfigObject) {
                final AbstractConfigObject objectLayer = (AbstractConfigObject) layer;
                final AbstractConfigValue v = objectLayer.attemptPeekWithPartialResolve(key);
                if (v != null) {
                    if (v.ignoresFallbacks()) {
                        // we know we won't need to merge anything in to this
                        // value
                        return v;
                    } else {
                        // we can't return this value because we know there are
                        // unmergeable values later in the stack that may
                        // contain values that need to be merged with this
                        // value. we'll throw the exception when we get to those
                        // unmergeable values, so continue here.
                        continue;
                    }
                } else if (layer instanceof Unmergeable) {
                    // an unmergeable object (which would be another
                    // ConfigDelayedMergeObject) can't know that a key is
                    // missing, so it can't return null; it can only return a
                    // value or throw NotPossibleToResolve
                    throw new ConfigException.BugOrBroken(
                            "should not be reached: unmergeable object returned null value");
                } else {
                    // a non-unmergeable AbstractConfigObject that returned null
                    // for the key in question is not relevant, we can keep
                    // looking for a value.
                    continue;
                }
            } else if (layer instanceof Unmergeable) {
                throw new ConfigException.NotResolved("Key '" + key + "' is not available at '"
                        + origin().description() + "' because value at '"
                        + layer.origin().description()
                        + "' has not been resolved and may turn out to contain or hide '" + key
                        + "'."
                        + " Be sure to Config#resolve() before using a config object.");
            } else if (layer.resolveStatus() == ResolveStatus.UNRESOLVED) {
                // if the layer is not an object, and not a substitution or
                // merge,
                // then it's something that's unresolved because it _contains_
                // an unresolved object... i.e. it's an array
                if (!(layer instanceof ConfigList))
                    throw new ConfigException.BugOrBroken("Expecting a list here, not " + layer);
                // all later objects will be hidden so we can say we won't find
                // the key
                return null;
            } else {
                // non-object, but resolved, like an integer or something.
                // has no children so the one we're after won't be in it.
                // we would only have this in the stack in case something
                // else "looks back" to it due to a cycle.
                // anyway at this point we know we can't find the key anymore.
                if (!layer.ignoresFallbacks()) {
                    throw new ConfigException.BugOrBroken(
                            "resolved non-object should ignore fallbacks");
                }
                return null;
            }
        }
        // If we get here, then we never found anything unresolved which means
        // the ConfigDelayedMergeObject should not have existed. some
        // invariant was violated.
        throw new ConfigException.BugOrBroken(
                "Delayed merge stack does not contain any unmergeable values");

    }
}
