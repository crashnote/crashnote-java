/**
 * Copyright (C) 2012 - 101loops.com <dev@101loops.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crashnote.appengine.collect.impl;

import com.crashnote.core.collect.impl.LogCollector;
import com.crashnote.core.config.CrashConfig;
import com.crashnote.core.model.data.DataObject;
import com.crashnote.core.model.log.LogEvt;

/**
 * Customized {@link LogCollector} that post-processes the collected logging data.
 */
public class AppengineLogCollector
    extends LogCollector {

    // SETUP ======================================================================================

    public AppengineLogCollector(final CrashConfig config) {
        super(config);
    }


    // SHARED =====================================================================================

    @Override
    protected DataObject collectEvt(final LogEvt evt) {
        final DataObject res = super.collectEvt(evt);

        // remove prefix that AppEngine gives the logger name
        final String src = (String) res.get("source");
        if (src != null && src.startsWith("["))
            res.put("source", src.replaceFirst("\\[.*\\]\\.?", ""));

        return res;
    }
}
