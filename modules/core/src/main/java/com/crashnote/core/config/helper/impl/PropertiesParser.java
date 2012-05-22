/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.core.config.helper.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.crashnote.core.config.helper.ConfigException;
import com.crashnote.core.config.helper.ConfigOrigin;

final class PropertiesParser {
    static AbstractConfigObject parse(final Reader reader,
            final ConfigOrigin origin) throws IOException {
        final Properties props = new Properties();
        props.load(reader);
        return fromProperties(origin, props);
    }

    static String lastElement(final String path) {
        final int i = path.lastIndexOf('.');
        if (i < 0)
            return path;
        else
            return path.substring(i + 1);
    }

    static String exceptLastElement(final String path) {
        final int i = path.lastIndexOf('.');
        if (i < 0)
            return null;
        else
            return path.substring(0, i);
    }

    static Path pathFromPropertyKey(final String key) {
        String last = lastElement(key);
        String exceptLast = exceptLastElement(key);
        Path path = new Path(last, null);
        while (exceptLast != null) {
            last = lastElement(exceptLast);
            exceptLast = exceptLastElement(exceptLast);
            path = new Path(last, path);
        }
        return path;
    }

    static AbstractConfigObject fromProperties(final ConfigOrigin origin,
            final Properties props) {
        final Map<Path, Object> pathMap = new HashMap<Path, Object>();
        for (final Map.Entry<Object, Object> entry : props.entrySet()) {
            final Object key = entry.getKey();
            if (key instanceof String) {
                final Path path = pathFromPropertyKey((String) key);
                pathMap.put(path, entry.getValue());
            }
        }
        return fromPathMap(origin, pathMap, true /* from properties */);
    }

    static AbstractConfigObject fromPathMap(final ConfigOrigin origin,
            final Map<?, ?> pathExpressionMap) {
        final Map<Path, Object> pathMap = new HashMap<Path, Object>();
        for (final Map.Entry<?, ?> entry : pathExpressionMap.entrySet()) {
            final Object keyObj = entry.getKey();
            if (!(keyObj instanceof String)) {
                throw new ConfigException.BugOrBroken(
                        "Map has a non-string as a key, expecting a path expression as a String");
            }
            final Path path = Path.newPath((String) keyObj);
            pathMap.put(path, entry.getValue());
        }
        return fromPathMap(origin, pathMap, false /* from properties */);
    }

    private static AbstractConfigObject fromPathMap(final ConfigOrigin origin,
            final Map<Path, Object> pathMap, final boolean convertedFromProperties) {
        /*
         * First, build a list of paths that will have values, either string or
         * object values.
         */
        final Set<Path> scopePaths = new HashSet<Path>();
        final Set<Path> valuePaths = new HashSet<Path>();
        for (final Path path : pathMap.keySet()) {
            // add value's path
            valuePaths.add(path);

            // all parent paths are objects
            Path next = path.parent();
            while (next != null) {
                scopePaths.add(next);
                next = next.parent();
            }
        }

        if (convertedFromProperties) {
            /*
             * If any string values are also objects containing other values,
             * drop those string values - objects "win".
             */
            valuePaths.removeAll(scopePaths);
        } else {
            /* If we didn't start out as properties, then this is an error. */
            for (final Path path : valuePaths) {
                if (scopePaths.contains(path)) {
                    throw new ConfigException.BugOrBroken(
                            "In the map, path '"
                                    + path.render()
                                    + "' occurs as both the parent object of a value and as a value. "
                                    + "Because Map has no defined ordering, this is a broken situation.");
                }
            }
        }

        /*
         * Create maps for the object-valued values.
         */
        final Map<String, AbstractConfigValue> root = new HashMap<String, AbstractConfigValue>();
        final Map<Path, Map<String, AbstractConfigValue>> scopes = new HashMap<Path, Map<String, AbstractConfigValue>>();

        for (final Path path : scopePaths) {
            final Map<String, AbstractConfigValue> scope = new HashMap<String, AbstractConfigValue>();
            scopes.put(path, scope);
        }

        /* Store string values in the associated scope maps */
        for (final Path path : valuePaths) {
            final Path parentPath = path.parent();
            final Map<String, AbstractConfigValue> parent = parentPath != null ? scopes
                    .get(parentPath) : root;

            final String last = path.last();
            final Object rawValue = pathMap.get(path);
            final AbstractConfigValue value;
            if (convertedFromProperties) {
                value = new ConfigString(origin, (String) rawValue);
            } else {
                value = ConfigImpl.fromAnyRef(pathMap.get(path), origin,
                        FromMapMode.KEYS_ARE_PATHS);
            }
            parent.put(last, value);
        }

        /*
         * Make a list of scope paths from longest to shortest, so children go
         * before parents.
         */
        final List<Path> sortedScopePaths = new ArrayList<Path>();
        sortedScopePaths.addAll(scopePaths);
        // sort descending by length
        Collections.sort(sortedScopePaths, new Comparator<Path>() {
            @Override
            public int compare(final Path a, final Path b) {
                // Path.length() is O(n) so in theory this sucks
                // but in practice we can make Path precompute length
                // if it ever matters.
                return b.length() - a.length();
            }
        });

        /*
         * Create ConfigObject for each scope map, working from children to
         * parents to avoid modifying any already-created ConfigObject. This is
         * where we need the sorted list.
         */
        for (final Path scopePath : sortedScopePaths) {
            final Map<String, AbstractConfigValue> scope = scopes.get(scopePath);

            final Path parentPath = scopePath.parent();
            final Map<String, AbstractConfigValue> parent = parentPath != null ? scopes
                    .get(parentPath) : root;

            final AbstractConfigObject o = new SimpleConfigObject(origin, scope,
                    ResolveStatus.RESOLVED, false /* ignoresFallbacks */);
            parent.put(scopePath.last(), o);
        }

        // return root config object
        return new SimpleConfigObject(origin, root, ResolveStatus.RESOLVED,
                false /* ignoresFallbacks */);
    }
}
