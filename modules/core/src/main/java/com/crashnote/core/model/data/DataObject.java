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
package com.crashnote.core.model.data;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Generic interface for a data object (key/value schema), based on {@link Map}.
 */
public interface DataObject
        extends Map<String, Object> {

    void streamTo(Writer out) throws IOException;

    void appendTo(String key, Object data);

    Object putArr(String key, DataArray value);

    Object putObj(String key, DataObject value);
}
