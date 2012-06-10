/*
    Copyright (C) Fang Yidong <fangyidong@yahoo.com.cn>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.crashnote.external.json;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public class JSONValue {

    /**
     * Encode an object into JSON text and write it to out.
     * <p/>
     * If this object is a Map or a List, and it's also a JSONStreamAware or a JSONAware, JSONStreamAware or JSONAware will be considered firstly.
     * <p/>
     * DO NOT call this method from writeJSONString(Writer) of a class that implements both JSONStreamAware and (Map or List) with
     * "this" as the first parameter, use JSONObject.writeJSONString(Map, Writer) or JSONArray.writeJSONString(List, Writer) instead.
     *
     * @param value
     * @param out
     * @see JSONObject#writeJSONString(Map, Writer)
     * @see JSONArray#writeJSONString(List, Writer)
     */
    public static void writeJSONString(final Object value, final Writer out) throws IOException {
        if (value == null) {
            out.write("null");
            return;
        }

        if (value instanceof String) {
            out.write('\"');
            out.write(escape((String) value));
            out.write('\"');
            return;
        }

        if (value instanceof Double) {
            if (((Double) value).isInfinite() || ((Double) value).isNaN())
                out.write("null");
            else
                out.write(value.toString());
            return;
        }

        if (value instanceof Float) {
            if (((Float) value).isInfinite() || ((Float) value).isNaN())
                out.write("null");
            else
                out.write(value.toString());
            return;
        }

        if (value instanceof Number) {
            out.write(value.toString());
            return;
        }

        if (value instanceof Boolean) {
            out.write(value.toString());
            return;
        }

        if ((value instanceof JSONStreamAware)) {
            ((JSONStreamAware) value).writeJSONString(out);
            return;
        }

        if ((value instanceof JSONAware)) {
            out.write(((JSONAware) value).toJSONString());
            return;
        }

        if (value instanceof Map) {
            JSONObject.writeJSONString((Map) value, out);
            return;
        }

        if (value instanceof List) {
            JSONArray.writeJSONString((List) value, out);
            return;
        }

        out.write(value.toString());
    }

    /**
     * Convert an object to JSON text.
     * <p/>
     * If this object is a Map or a List, and it's also a JSONAware, JSONAware will be considered firstly.
     * <p/>
     * DO NOT call this method from toJSONString() of a class that implements both JSONAware and Map or List with
     * "this" as the parameter, use JSONObject.toJSONString(Map) or JSONArray.toJSONString(List) instead.
     *
     * @param value
     * @return JSON text, or "null" if value is null or it's an NaN or an INF number.
     * @see JSONObject#toJSONString(Map)
     * @see JSONArray#toJSONString(List)
     */
    public static String toJSONString(final Object value) {
        if (value == null)
            return "null";

        if (value instanceof String)
            return "\"" + escape((String) value) + "\"";

        if (value instanceof Double) {
            if (((Double) value).isInfinite() || ((Double) value).isNaN())
                return "null";
            else
                return value.toString();
        }

        if (value instanceof Float) {
            if (((Float) value).isInfinite() || ((Float) value).isNaN())
                return "null";
            else
                return value.toString();
        }

        if (value instanceof Number)
            return value.toString();

        if (value instanceof Boolean)
            return value.toString();

        if ((value instanceof JSONAware))
            return ((JSONAware) value).toJSONString();

        if (value instanceof Map)
            return JSONObject.toJSONString((Map) value);

        if (value instanceof List)
            return JSONArray.toJSONString((List) value);

        return value.toString();
    }

    /**
     * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
     *
     * @param s
     * @return
     */
    public static String escape(final String s) {
        if (s == null)
            return null;
        final StringBuffer sb = new StringBuffer();
        escape(s, sb);
        return sb.toString();
    }

    /**
     * @param s  - Must not be null.
     * @param sb
     */
    static void escape(final String s, final StringBuffer sb) {
        for (int i = 0; i < s.length(); i++) {
            final char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                        final String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }//for
    }

}
