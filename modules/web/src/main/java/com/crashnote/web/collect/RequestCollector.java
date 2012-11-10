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
import com.crashnote.core.model.data.DataArray;
import com.crashnote.core.model.data.DataObject;
import com.crashnote.core.util.ChksumUtil;
import com.crashnote.web.config.WebConfig;

import java.util.Enumeration;
import java.util.List;

import static com.crashnote.core.util.FilterUtil.doFilter;

public abstract class RequestCollector<R>
        extends BaseCollector {

    // VARS =======================================================================================

    protected List<String> requestFilters;
    protected boolean skipHeaderData;
    protected boolean hashRemoteIP;
    protected int maxRequestParamSize;

    protected String[] filtereds = new String[]{ filtered };


    // SETUP ======================================================================================

    public <C extends WebConfig> RequestCollector(final C config) {
        super(config);

        this.hashRemoteIP = config.getHashRemoteIP();
        this.requestFilters = config.getRequestFilters();
        this.skipHeaderData = config.getSkipHeaderData();
        this.maxRequestParamSize = config.getMaxRequestParameterSize();
    }


    // INTERFACE ==================================================================================

    public DataObject collect(final R req) {
        final DataObject data = collectReqBase(req);
        {
            // collect HTTP parameters
            data.putObj("parameters", collectReqParams(req));

            // collect HTTP headers
            if (!skipHeaderData) data.putObj("headers", collectReqHeader(req));
        }
        return data;
    }


    // SHARED =====================================================================================

    protected abstract DataObject collectReqBase(final R req);

    protected abstract DataObject collectReqParams(final R req);

    protected abstract DataObject collectReqHeader(final R req);


    // == utility methods

    protected void addIP(final DataObject data, final String remoteIP) {
        if (hashRemoteIP) // hash or raw?
            data.put("ip_hash", ChksumUtil.hash(remoteIP));
        else
            data.put("ip", remoteIP);
    }

    protected void addHeader(final DataObject data, final String name, final List<String> values) {
        final int size = values.size();
        if (size > 0) {
            if (size == 1)
                data.put(name, values.get(0));
            else
                data.put(name, createDataArr(values));
        }
    }

    protected void addHeader(final DataObject data, final String name, final String[] values) {
        final int size = values.length;
        if (size > 0) {
            if (size == 1)
                data.put(name, values[0]);
            else
                data.put(name, createDataArr(values));
        }
    }

    /**
     * collect parameter with multiple values
     */
    protected void addParam(final DataObject data, final String name, final String[] values) {
        if (values.length > 0) {
            if (values.length == 1) {
                addParam(data, name, values[0]);
            } else {
                final String[] filteredValues = doFilter(name, requestFilters) ? filtereds : values;
                final DataArray arr = createDataArr();
                for (final String value : filteredValues)
                    arr.add(limitParam(value));
                data.put(name, arr);
            }
        }
    }

    /**
     * collect parameter with single value
     */
    protected void addParam(final DataObject data, final String name, final String value) {
        final String filteredValue = doFilter(name, requestFilters) ? filtered : value;
        data.put(name, limitParam(filteredValue));
    }


    // INTERNALS ==================================================================================

    private String limitParam(final String value) {
        if (value.length() > maxRequestParamSize)
            return value.substring(0, maxRequestParamSize);
        else
            return value;
    }
}
