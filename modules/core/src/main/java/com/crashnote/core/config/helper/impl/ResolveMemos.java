/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.core.config.helper.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * This exists because we have to memoize resolved substitutions as we go
 * through the config tree; otherwise we could end up creating multiple copies
 * of values or whole trees of values as we follow chains of substitutions.
 */
final class ResolveMemos {
    // note that we can resolve things to undefined (represented as Java null,
    // rather than ConfigNull) so this map can have null values.
    final private Map<MemoKey, AbstractConfigValue> memos;

    ResolveMemos() {
        this.memos = new HashMap<MemoKey, AbstractConfigValue>();
    }

    AbstractConfigValue get(final MemoKey key) {
        return memos.get(key);
    }

    void put(final MemoKey key, final AbstractConfigValue value) {
        memos.put(key, value);
    }
}
