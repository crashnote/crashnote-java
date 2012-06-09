/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.external.config.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.crashnote.external.config.ConfigException;
import com.crashnote.external.config.ConfigFactory;
import com.crashnote.external.config.ConfigIncludeContext;
import com.crashnote.external.config.ConfigIncluder;
import com.crashnote.external.config.ConfigIncluderClasspath;
import com.crashnote.external.config.ConfigIncluderFile;
import com.crashnote.external.config.ConfigIncluderURL;
import com.crashnote.external.config.ConfigObject;
import com.crashnote.external.config.ConfigParseOptions;
import com.crashnote.external.config.ConfigParseable;
import com.crashnote.external.config.ConfigSyntax;

class SimpleIncluder implements FullIncluder {

    private ConfigIncluder fallback;

    SimpleIncluder(final ConfigIncluder fallback) {
        this.fallback = fallback;
    }

    // ConfigIncludeContext does this for us on its options
    static ConfigParseOptions clearForInclude(final ConfigParseOptions options) {
        // the class loader and includer are inherited, but not this other
        // stuff.
        return options.setSyntax(null).setOriginDescription(null).setAllowMissing(true);
    }

    // this is the heuristic includer
    @Override
    public ConfigObject include(final ConfigIncludeContext context, final String name) {
        final ConfigObject obj = includeWithoutFallback(context, name);

        // now use the fallback includer if any and merge
        // its result.
        if (fallback != null) {
            return obj.withFallback(fallback.include(context, name));
        } else {
            return obj;
        }
    }

    // the heuristic includer in static form
    static ConfigObject includeWithoutFallback(final ConfigIncludeContext context, final String name) {
        // the heuristic is valid URL then URL, else relative to including file;
        // relativeTo in a file falls back to classpath inside relativeTo().

        URL url;
        try {
            url = new URL(name);
        } catch (MalformedURLException e) {
            url = null;
        }

        if (url != null) {
            return includeURLWithoutFallback(context, url);
        } else {
            final NameSource source = new RelativeNameSource(context);
            return fromBasename(source, name, context.parseOptions());
        }
    }

    @Override
    public ConfigObject includeURL(final ConfigIncludeContext context, final URL url) {
        final ConfigObject obj = includeURLWithoutFallback(context, url);

        // now use the fallback includer if any and merge
        // its result.
        if (fallback != null && fallback instanceof ConfigIncluderURL) {
            return obj.withFallback(((ConfigIncluderURL) fallback).includeURL(context, url));
        } else {
            return obj;
        }
    }

    static ConfigObject includeURLWithoutFallback(final ConfigIncludeContext context, final URL url) {
        return ConfigFactory.parseURL(url, context.parseOptions()).root();
    }

    @Override
    public ConfigObject includeFile(final ConfigIncludeContext context, final File file) {
        final ConfigObject obj = includeFileWithoutFallback(context, file);

        // now use the fallback includer if any and merge
        // its result.
        if (fallback != null && fallback instanceof ConfigIncluderFile) {
            return obj.withFallback(((ConfigIncluderFile) fallback).includeFile(context, file));
        } else {
            return obj;
        }
    }

    static ConfigObject includeFileWithoutFallback(final ConfigIncludeContext context, final File file) {
        return ConfigFactory.parseFileAnySyntax(file, context.parseOptions()).root();
    }

    @Override
    public ConfigObject includeResources(final ConfigIncludeContext context, final String resource) {
        final ConfigObject obj = includeResourceWithoutFallback(context, resource);

        // now use the fallback includer if any and merge
        // its result.
        if (fallback != null && fallback instanceof ConfigIncluderClasspath) {
            return obj.withFallback(((ConfigIncluderClasspath) fallback).includeResources(context,
                    resource));
        } else {
            return obj;
        }
    }

    static ConfigObject includeResourceWithoutFallback(final ConfigIncludeContext context,
            final String resource) {
        return ConfigFactory.parseResourcesAnySyntax(resource, context.parseOptions()).root();
    }

    @Override
    public ConfigIncluder withFallback(final ConfigIncluder fallback) {
        if (this == fallback) {
            throw new ConfigException.BugOrBroken("trying to create includer cycle");
        } else if (this.fallback == fallback) {
            return this;
        } else if (this.fallback != null) {
            return new SimpleIncluder(this.fallback.withFallback(fallback));
        } else {
            return new SimpleIncluder(fallback);
        }
    }

    interface NameSource {
        ConfigParseable nameToParseable(String name, ConfigParseOptions parseOptions);
    }

    static private class RelativeNameSource implements NameSource {
        final private ConfigIncludeContext context;

        RelativeNameSource(final ConfigIncludeContext context) {
            this.context = context;
        }

        @Override
        public ConfigParseable nameToParseable(final String name, final ConfigParseOptions options) {
            final ConfigParseable p = context.relativeTo(name);
            if (p == null) {
                // avoid returning null
                return Parseable
                        .newNotFound(name, "include was not found: '" + name + "'", options);
            } else {
                return p;
            }
        }
    };

    // this function is a little tricky because there are three places we're
    // trying to use it; for 'include "basename"' in a .conf file, for
    // loading app.{conf,json,properties} from classpath, and for
    // loading app.{conf,json,properties} from the filesystem.
    static ConfigObject fromBasename(final NameSource source, final String name, final ConfigParseOptions options) {
        ConfigObject obj;
        if (name.endsWith(".conf") || name.endsWith(".json") || name.endsWith(".properties")) {
            final ConfigParseable p = source.nameToParseable(name, options);

            obj = p.parse(p.options().setAllowMissing(options.getAllowMissing()));
        } else {
            final ConfigParseable confHandle = source.nameToParseable(name + ".conf", options);
            final ConfigParseable jsonHandle = source.nameToParseable(name + ".json", options);
            final ConfigParseable propsHandle = source.nameToParseable(name + ".properties", options);
            boolean gotSomething = false;
            final List<String> failMessages = new ArrayList<String>();

            final ConfigSyntax syntax = options.getSyntax();

            obj = SimpleConfigObject.empty(SimpleConfigOrigin.newSimple(name));
            if (syntax == null || syntax == ConfigSyntax.CONF) {
                try {
                    obj = confHandle.parse(confHandle.options().setAllowMissing(false)
                            .setSyntax(ConfigSyntax.CONF));
                    gotSomething = true;
                } catch (ConfigException.IO e) {
                    failMessages.add(e.getMessage());
                }
            }

            if (syntax == null || syntax == ConfigSyntax.JSON) {
                try {
                    final ConfigObject parsed = jsonHandle.parse(jsonHandle.options()
                            .setAllowMissing(false).setSyntax(ConfigSyntax.JSON));
                    obj = obj.withFallback(parsed);
                    gotSomething = true;
                } catch (ConfigException.IO e) {
                    failMessages.add(e.getMessage());
                }
            }

            if (syntax == null || syntax == ConfigSyntax.PROPERTIES) {
                try {
                    final ConfigObject parsed = propsHandle.parse(propsHandle.options()
                            .setAllowMissing(false).setSyntax(ConfigSyntax.PROPERTIES));
                    obj = obj.withFallback(parsed);
                    gotSomething = true;
                } catch (ConfigException.IO e) {
                    failMessages.add(e.getMessage());
                }
            }

            if (!options.getAllowMissing() && !gotSomething) {
                final String failMessage;
                if (failMessages.isEmpty()) {
                    // this should not happen
                    throw new ConfigException.BugOrBroken(
                            "should not be reached: nothing found but no exceptions thrown");
                } else {
                    final StringBuilder sb = new StringBuilder();
                    for (final String msg : failMessages) {
                        sb.append(msg);
                        sb.append(", ");
                    }
                    sb.setLength(sb.length() - 2);
                    failMessage = sb.toString();
                }
                throw new ConfigException.IO(SimpleConfigOrigin.newSimple(name), failMessage);
            }
        }

        return obj;
    }

    // the Proxy is a proxy for an application-provided includer that uses our
    // default implementations when the application-provided includer doesn't
    // have an implementation.
    static private class Proxy implements FullIncluder {
        final ConfigIncluder delegate;

        Proxy(final ConfigIncluder delegate) {
            this.delegate = delegate;
        }

        @Override
        public ConfigIncluder withFallback(final ConfigIncluder fallback) {
            // we never fall back
            return this;
        }

        @Override
        public ConfigObject include(final ConfigIncludeContext context, final String what) {
            return delegate.include(context, what);
        }

        @Override
        public ConfigObject includeResources(final ConfigIncludeContext context, final String what) {
            if (delegate instanceof ConfigIncluderClasspath)
                return ((ConfigIncluderClasspath) delegate).includeResources(context, what);
            else
                return includeResourceWithoutFallback(context, what);
        }

        @Override
        public ConfigObject includeURL(final ConfigIncludeContext context, final URL what) {
            if (delegate instanceof ConfigIncluderURL)
                return ((ConfigIncluderURL) delegate).includeURL(context, what);
            else
                return includeURLWithoutFallback(context, what);
        }

        @Override
        public ConfigObject includeFile(final ConfigIncludeContext context, final File what) {
            if (delegate instanceof ConfigIncluderFile)
                return ((ConfigIncluderFile) delegate).includeFile(context, what);
            else
                return includeFileWithoutFallback(context, what);
        }
    }

    static FullIncluder makeFull(final ConfigIncluder includer) {
        if (includer instanceof FullIncluder)
            return (FullIncluder) includer;
        else
            return new Proxy(includer);
    }
}
