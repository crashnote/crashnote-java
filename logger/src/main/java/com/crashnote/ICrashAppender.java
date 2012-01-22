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
package com.crashnote;

import com.crashnote.core.model.types.LogLevel;

/**
 * Interface defines setter methods every appender - independent of the library -
 * should implement in order to be configurable by the user.
 */
public interface ICrashAppender {

    // === Internally

    public boolean isStarted();

    public void setLogLevel(LogLevel lvl);


    // === Externally

    public void setSync(String on);

    public void setPort(String port);

    public void setHost(String host);

    public void setKey(String key);

    public void setEnabled(String enabled);

    public void setSslPort(String sslPort);

    public void setSecure(String secure);
}