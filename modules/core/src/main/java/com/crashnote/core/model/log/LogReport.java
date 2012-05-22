/**
 * Copyright (C) 2011 - 101loops.com <dev@101loops.com>
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
package com.crashnote.core.model.log;

import com.crashnote.core.model.data.DataObject;

import java.io.*;

/**
 * This class represents the crash report that is sent to the server. Internally it uses a
 * {@link DataObject} that holds the actual data.
 */
public class LogReport {

    // VARS =======================================================================================

    private final DataObject dataObj;


    // SETUP ======================================================================================

    public LogReport(final DataObject data) {
        this.dataObj = data;
    }


    // INTERFACE ==================================================================================

    public boolean isEmpty() {
        return dataObj.isEmpty();
    }

    @Override
    public String toString() {
        return dataObj.toString();
    }

    public void streamTo(final Writer writer) throws IOException {
        dataObj.streamTo(writer);
    }
}
