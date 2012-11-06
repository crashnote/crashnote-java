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
package com.crashnote.web.collect;

import com.crashnote.core.collect.BaseCollector;
import com.crashnote.core.config.IConfigChangeListener;
import com.crashnote.core.model.data.DataObject;
import com.crashnote.web.config.WebConfig;

public abstract class SessionCollector<C extends WebConfig, R>
    extends BaseCollector<C> implements IConfigChangeListener<C> {

    // VARS =======================================================================================

    protected boolean skipSessionData;


    // SETUP ======================================================================================

    public SessionCollector(final C config) {
        super(config);
        updateConfig(config);
    }

    @Override
    public void updateConfig(final C config) {
        config.addListener(this);
        this.skipSessionData = config.getSkipSessionData();
    }


    // INTERFACE ==================================================================================

    public abstract DataObject collect(final R req);
}
