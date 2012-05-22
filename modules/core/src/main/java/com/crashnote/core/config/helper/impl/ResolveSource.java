package com.crashnote.core.config.helper.impl;

import java.util.IdentityHashMap;
import java.util.Map;

import com.crashnote.core.config.helper.ConfigException;
import com.crashnote.core.config.helper.impl.AbstractConfigValue.NotPossibleToResolve;

/**
 * This class is the source for values for a substitution like ${foo}.
 */
final class ResolveSource {
    final private AbstractConfigObject root;
    // Conceptually, we transform the ResolveSource whenever we traverse
    // a substitution or delayed merge stack, in order to remove the
    // traversed node and therefore avoid circular dependencies.
    // We implement it with this somewhat hacky "patch a replacement"
    // mechanism instead of actually transforming the tree.
    final private Map<AbstractConfigValue, ResolveReplacer> replacements;

    ResolveSource(final AbstractConfigObject root) {
        this.root = root;
        this.replacements = new IdentityHashMap<AbstractConfigValue, ResolveReplacer>();
    }

    static private AbstractConfigValue findInObject(final AbstractConfigObject obj,
            final ResolveContext context, final SubstitutionExpression subst)
            throws NotPossibleToResolve {
        return obj.peekPath(subst.path(), context);
    }

    AbstractConfigValue lookupSubst(final ResolveContext context, final SubstitutionExpression subst,
            final int prefixLength) throws NotPossibleToResolve {
        context.trace(subst);
        try {
            // First we look up the full path, which means relative to the
            // included file if we were not a root file
            AbstractConfigValue result = findInObject(root, context, subst);

            if (result == null) {
                // Then we want to check relative to the root file. We don't
                // want the prefix we were included at to be used when looking
                // up env variables either.
                final SubstitutionExpression unprefixed = subst.changePath(subst.path().subPath(
                        prefixLength));

                // replace the debug trace path
                context.untrace();
                context.trace(unprefixed);

                if (prefixLength > 0) {
                    result = findInObject(root, context, unprefixed);
                }

                if (result == null && context.options().getUseSystemEnvironment()) {
                    result = findInObject(ConfigImpl.envVariablesAsConfigObject(), context,
                            unprefixed);
                }
            }

            if (result != null) {
                result = context.resolve(result);
            }

            return result;
        } finally {
            context.untrace();
        }
    }

    void replace(final AbstractConfigValue value, final ResolveReplacer replacer) {
        final ResolveReplacer old = replacements.put(value, replacer);
        if (old != null)
            throw new ConfigException.BugOrBroken("should not have replaced the same value twice: "
                    + value);
    }

    void unreplace(final AbstractConfigValue value) {
        final ResolveReplacer replacer = replacements.remove(value);
        if (replacer == null)
            throw new ConfigException.BugOrBroken("unreplace() without replace(): " + value);
    }

    private AbstractConfigValue replacement(final ResolveContext context, final AbstractConfigValue value)
            throws NotPossibleToResolve {
        final ResolveReplacer replacer = replacements.get(value);
        if (replacer == null) {
            return value;
        } else {
            return replacer.replace(context);
        }
    }

    /**
     * Conceptually, this is key.value().resolveSubstitutions() but using the
     * replacement for key.value() if any.
     */
    AbstractConfigValue resolveCheckingReplacement(final ResolveContext context,
            final AbstractConfigValue original) throws NotPossibleToResolve {
        final AbstractConfigValue replacement;

        replacement = replacement(context, original);

        if (replacement != original) {
            // start over, checking if replacement was memoized
            return context.resolve(replacement);
        } else {
            final AbstractConfigValue resolved;

            resolved = original.resolveSubstitutions(context);

            return resolved;
        }
    }
}
