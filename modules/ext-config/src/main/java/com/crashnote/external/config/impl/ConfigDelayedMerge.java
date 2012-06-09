/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.external.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.crashnote.external.config.ConfigException;
import com.crashnote.external.config.ConfigOrigin;
import com.crashnote.external.config.ConfigRenderOptions;
import com.crashnote.external.config.ConfigValueType;

/**
 * The issue here is that we want to first merge our stack of config files, and
 * then we want to evaluate substitutions. But if two substitutions both expand
 * to an object, we might need to merge those two objects. Thus, we can't ever
 * "override" a substitution when we do a merge; instead we have to save the
 * stack of values that should be merged, and resolve the merge when we evaluate
 * substitutions.
 */
final class ConfigDelayedMerge extends AbstractConfigValue implements Unmergeable,
        ReplaceableMergeStack {

    // earlier items in the stack win
    final private List<AbstractConfigValue> stack;

    ConfigDelayedMerge(final ConfigOrigin origin, final List<AbstractConfigValue> stack) {
        super(origin);
        this.stack = stack;
        if (stack.isEmpty())
            throw new ConfigException.BugOrBroken(
                    "creating empty delayed merge value");

        for (final AbstractConfigValue v : stack) {
            if (v instanceof ConfigDelayedMerge || v instanceof ConfigDelayedMergeObject)
                throw new ConfigException.BugOrBroken(
                        "placed nested DelayedMerge in a ConfigDelayedMerge, should have consolidated stack");
        }
    }

    @Override
    public ConfigValueType valueType() {
        throw new ConfigException.NotResolved(
                "called valueType() on value with unresolved substitutions, need to Config#resolve() first, see API docs");
    }

    @Override
    public Object unwrapped() {
        throw new ConfigException.NotResolved(
                "called unwrapped() on value with unresolved substitutions, need to Config#resolve() first, see API docs");
    }

    @Override
    AbstractConfigValue resolveSubstitutions(final ResolveContext context)
            throws NotPossibleToResolve {
        return resolveSubstitutions(this, stack, context);
    }

    // static method also used by ConfigDelayedMergeObject
    static AbstractConfigValue resolveSubstitutions(final ReplaceableMergeStack replaceable,
            final List<AbstractConfigValue> stack, final ResolveContext context) throws NotPossibleToResolve {
        // to resolve substitutions, we need to recursively resolve
        // the stack of stuff to merge, and merge the stack so
        // we won't be a delayed merge anymore. If restrictToChildOrNull
        // is non-null, we may remain a delayed merge though.

        int count = 0;
        AbstractConfigValue merged = null;
        for (final AbstractConfigValue v : stack) {
            if (v instanceof ReplaceableMergeStack)
                throw new ConfigException.BugOrBroken(
                        "A delayed merge should not contain another one: " + replaceable);

            boolean replaced = false;
            // we only replace if we have a substitution, or
            // value-concatenation containing one. The Unmergeable
            // here isn't a delayed merge stack since we can't contain
            // another stack (see assertion above).
            if (v instanceof Unmergeable) {
                // If, while resolving 'v' we come back to the same
                // merge stack, we only want to look _below_ 'v'
                // in the stack. So we arrange to replace the
                // ConfigDelayedMerge with a value that is only
                // the remainder of the stack below this one.

                context.source().replace((AbstractConfigValue) replaceable,
                        replaceable.makeReplacer(count + 1));
                replaced = true;
            }

            AbstractConfigValue resolved;
            try {
                resolved = context.resolve(v);
            } finally {
                if (replaced)
                    context.source().unreplace((AbstractConfigValue) replaceable);
            }

            if (resolved != null) {
                if (merged == null)
                    merged = resolved;
                else
                    merged = merged.withFallback(resolved);
            }
            count += 1;
        }

        return merged;
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

    // static method also used by ConfigDelayedMergeObject
    static AbstractConfigValue makeReplacement(final ResolveContext context,
            final List<AbstractConfigValue> stack, final int skipping) throws NotPossibleToResolve {

        final List<AbstractConfigValue> subStack = stack.subList(skipping, stack.size());

        if (subStack.isEmpty()) {
            throw new NotPossibleToResolve(context);
        } else {
            // generate a new merge stack from only the remaining items
            AbstractConfigValue merged = null;
            for (final AbstractConfigValue v : subStack) {
                if (merged == null)
                    merged = v;
                else
                    merged = merged.withFallback(v);
            }
            return merged;
        }
    }

    @Override
    ResolveStatus resolveStatus() {
        return ResolveStatus.UNRESOLVED;
    }

    @Override
    ConfigDelayedMerge relativized(final Path prefix) {
        final List<AbstractConfigValue> newStack = new ArrayList<AbstractConfigValue>();
        for (final AbstractConfigValue o : stack) {
            newStack.add(o.relativized(prefix));
        }
        return new ConfigDelayedMerge(origin(), newStack);
    }

    // static utility shared with ConfigDelayedMergeObject
    static boolean stackIgnoresFallbacks(final List<AbstractConfigValue> stack) {
        final AbstractConfigValue last = stack.get(stack.size() - 1);
        return last.ignoresFallbacks();
    }

    @Override
    protected boolean ignoresFallbacks() {
        return stackIgnoresFallbacks(stack);
    }

    @Override
    protected AbstractConfigValue newCopy(final ConfigOrigin newOrigin) {
        return new ConfigDelayedMerge(newOrigin, stack);
    }

    @Override
    protected final ConfigDelayedMerge mergedWithTheUnmergeable(final Unmergeable fallback) {
        return (ConfigDelayedMerge) mergedWithTheUnmergeable(stack, fallback);
    }

    @Override
    protected final ConfigDelayedMerge mergedWithObject(final AbstractConfigObject fallback) {
        return (ConfigDelayedMerge) mergedWithObject(stack, fallback);
    }

    @Override
    protected ConfigDelayedMerge mergedWithNonObject(final AbstractConfigValue fallback) {
        return (ConfigDelayedMerge) mergedWithNonObject(stack, fallback);
    }

    @Override
    public Collection<AbstractConfigValue> unmergedValues() {
        return stack;
    }

    @Override
    protected boolean canEqual(final Object other) {
        return other instanceof ConfigDelayedMerge;
    }

    @Override
    public boolean equals(final Object other) {
        // note that "origin" is deliberately NOT part of equality
        if (other instanceof ConfigDelayedMerge) {
            return canEqual(other)
                    && this.stack.equals(((ConfigDelayedMerge) other).stack);
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
        render(stack, sb, indent, atKey, options);
    }

    // static method also used by ConfigDelayedMergeObject.
    static void render(final List<AbstractConfigValue> stack, final StringBuilder sb, final int indent, final String atKey,
            final ConfigRenderOptions options) {
        final boolean commentMerge = options.getComments();
        if (commentMerge) {
            sb.append("# unresolved merge of " + stack.size() + " values follows (\n");
            if (atKey == null) {
                indent(sb, indent, options);
                sb.append("# this unresolved merge will not be parseable because it's at the root of the object\n");
                indent(sb, indent, options);
                sb.append("# the HOCON format has no way to list multiple root objects in a single file\n");
            }
        }

        final List<AbstractConfigValue> reversed = new ArrayList<AbstractConfigValue>();
        reversed.addAll(stack);
        Collections.reverse(reversed);

        int i = 0;
        for (final AbstractConfigValue v : reversed) {
            if (commentMerge) {
                indent(sb, indent, options);
                if (atKey != null) {
                    sb.append("#     unmerged value " + i + " for key "
                            + ConfigImplUtil.renderJsonString(atKey) + " from ");
                } else {
                    sb.append("#     unmerged value " + i + " from ");
                }
                i += 1;
                sb.append(v.origin().description());
                sb.append("\n");

                for (final String comment : v.origin().comments()) {
                    indent(sb, indent, options);
                    sb.append("# ");
                    sb.append(comment);
                    sb.append("\n");
                }
            }
            indent(sb, indent, options);

            if (atKey != null) {
                sb.append(ConfigImplUtil.renderJsonString(atKey));
                if (options.getFormatted())
                    sb.append(" : ");
                else
                    sb.append(":");
            }
            v.render(sb, indent, options);
            sb.append(",");
            if (options.getFormatted())
                sb.append('\n');
        }
        // chop comma or newline
        sb.setLength(sb.length() - 1);
        if (options.getFormatted()) {
            sb.setLength(sb.length() - 1); // also chop comma
            sb.append("\n"); // put a newline back
        }
        if (commentMerge) {
            indent(sb, indent, options);
            sb.append("# ) end of unresolved merge\n");
        }
    }
}
