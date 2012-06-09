/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.external.config.impl;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crashnote.external.config.Config;
import com.crashnote.external.config.ConfigException;
import com.crashnote.external.config.ConfigList;
import com.crashnote.external.config.ConfigObject;
import com.crashnote.external.config.ConfigOrigin;
import com.crashnote.external.config.ConfigValue;
import com.crashnote.external.config.ConfigValueType;

/**
 * Deliberately shoving all the serialization code into this class instead of
 * doing it OO-style with each subclass. Seems better to have it all in one
 * place. This class implements a lame serialization format that supports
 * skipping unknown fields, so it's moderately more extensible than the default
 * Java serialization format.
 */
class SerializedConfigValue extends AbstractConfigValue implements Externalizable {

    // this is the version used by Java serialization, if it increments it's
    // essentially an ABI break and bad
    private static final long serialVersionUID = 1L;

    // this is how we try to be extensible
    static enum SerializedField {
        // represents a field code we didn't recognize
        UNKNOWN,

        // end of a list of fields
        END_MARKER,

        // Fields at the root
        ROOT_VALUE,
        ROOT_WAS_CONFIG,

        // Fields that make up a value
        VALUE_DATA,
        VALUE_ORIGIN,

        // Fields that make up an origin
        ORIGIN_DESCRIPTION,
        ORIGIN_LINE_NUMBER,
        ORIGIN_END_LINE_NUMBER,
        ORIGIN_TYPE,
        ORIGIN_URL,
        ORIGIN_COMMENTS,
        ORIGIN_NULL_URL,
        ORIGIN_NULL_COMMENTS;

        static SerializedField forInt(final int b) {
            if (b < values().length)
                return values()[b];
            else
                return UNKNOWN;
        }
    };

    private static enum SerializedValueType {
        // the ordinals here are in the wire format, caution
        NULL(ConfigValueType.NULL),
        BOOLEAN(ConfigValueType.BOOLEAN),
        INT(ConfigValueType.NUMBER),
        LONG(ConfigValueType.NUMBER),
        DOUBLE(ConfigValueType.NUMBER),
        STRING(ConfigValueType.STRING),
        LIST(ConfigValueType.LIST),
        OBJECT(ConfigValueType.OBJECT);

        ConfigValueType configType;

        SerializedValueType(final ConfigValueType configType) {
            this.configType = configType;
        }

        static SerializedValueType forInt(final int b) {
            if (b < values().length)
                return values()[b];
            else
                return null;
        }

        static SerializedValueType forValue(final ConfigValue value) {
            final ConfigValueType t = value.valueType();
            if (t == ConfigValueType.NUMBER) {
                if (value instanceof ConfigInt)
                    return INT;
                else if (value instanceof ConfigLong)
                    return LONG;
                else if (value instanceof ConfigDouble)
                    return DOUBLE;
            } else {
                for (final SerializedValueType st : values()) {
                    if (st.configType == t)
                        return st;
                }
            }

            throw new ConfigException.BugOrBroken("don't know how to serialize " + value);
        }
    };

    private ConfigValue value;
    private boolean wasConfig;

    // this has to be public for the Java deserializer
    public SerializedConfigValue() {
        super(null);
    }

    SerializedConfigValue(final ConfigValue value) {
        this();
        this.value = value;
        this.wasConfig = false;
    }

    SerializedConfigValue(final Config conf) {
        this(conf.root());
        this.wasConfig = true;
    }

    // when Java deserializer reads this object, return the contained
    // object instead.
    private Object readResolve() throws ObjectStreamException {
        if (wasConfig)
            return ((ConfigObject) value).toConfig();
        else
            return value;
    }

    private static class FieldOut {
        final SerializedField code;
        final ByteArrayOutputStream bytes;
        final DataOutput data;

        FieldOut(final SerializedField code) {
            this.code = code;
            this.bytes = new ByteArrayOutputStream();
            this.data = new DataOutputStream(bytes);
        }
    }

    // this is a separate function to prevent bugs writing to the
    // outer stream instead of field.data
    private static void writeOriginField(final DataOutput out, final SerializedField code, final Object v)
            throws IOException {
        switch (code) {
        case ORIGIN_DESCRIPTION:
            out.writeUTF((String) v);
            break;
        case ORIGIN_LINE_NUMBER:
            out.writeInt((Integer) v);
            break;
        case ORIGIN_END_LINE_NUMBER:
            out.writeInt((Integer) v);
            break;
        case ORIGIN_TYPE:
            out.writeByte((Integer) v);
            break;
        case ORIGIN_URL:
            out.writeUTF((String) v);
            break;
        case ORIGIN_COMMENTS:
            @SuppressWarnings("unchecked") final
            List<String> list = (List<String>) v;
            final int size = list.size();
            out.writeInt(size);
            for (final String s : list) {
                out.writeUTF(s);
            }
            break;
        case ORIGIN_NULL_URL: // FALL THRU
        case ORIGIN_NULL_COMMENTS:
            // nothing to write out besides code and length
            break;
        default:
            throw new IOException("Unhandled field from origin: " + code);
        }
    }

    private static void writeOrigin(final DataOutput out, final SimpleConfigOrigin origin,
            final SimpleConfigOrigin baseOrigin) throws IOException {
        final Map<SerializedField, Object> m = origin.toFieldsDelta(baseOrigin);
        for (final Map.Entry<SerializedField, Object> e : m.entrySet()) {
            final FieldOut field = new FieldOut(e.getKey());
            final Object v = e.getValue();
            writeOriginField(field.data, field.code, v);
            writeField(out, field);
        }
        writeEndMarker(out);
    }

    private static SimpleConfigOrigin readOrigin(final DataInput in, final SimpleConfigOrigin baseOrigin)
            throws IOException {
        final Map<SerializedField, Object> m = new EnumMap<SerializedField, Object>(SerializedField.class);
        while (true) {
            Object v = null;
            final SerializedField field = readCode(in);
            switch (field) {
            case END_MARKER:
                return SimpleConfigOrigin.fromBase(baseOrigin, m);
            case ORIGIN_DESCRIPTION:
                in.readInt(); // discard length
                v = in.readUTF();
                break;
            case ORIGIN_LINE_NUMBER:
                in.readInt(); // discard length
                v = in.readInt();
                break;
            case ORIGIN_END_LINE_NUMBER:
                in.readInt(); // discard length
                v = in.readInt();
                break;
            case ORIGIN_TYPE:
                in.readInt(); // discard length
                v = in.readUnsignedByte();
                break;
            case ORIGIN_URL:
                in.readInt(); // discard length
                v = in.readUTF();
                break;
            case ORIGIN_COMMENTS:
                in.readInt(); // discard length
                final int size = in.readInt();
                final List<String> list = new ArrayList<String>(size);
                for (int i = 0; i < size; ++i) {
                    list.add(in.readUTF());
                }
                v = list;
                break;
            case ORIGIN_NULL_URL: // FALL THRU
            case ORIGIN_NULL_COMMENTS:
                // nothing to read besides code and length
                in.readInt(); // discard length
                v = ""; // just something non-null to put in the map
                break;
            case ROOT_VALUE:
            case ROOT_WAS_CONFIG:
            case VALUE_DATA:
            case VALUE_ORIGIN:
                throw new IOException("Not expecting this field here: " + field);
            case UNKNOWN:
                // skip unknown field
                skipField(in);
                break;
            }
            if (v != null)
                m.put(field, v);
        }
    }

    private static void writeValueData(final DataOutput out, final ConfigValue value) throws IOException {
        final SerializedValueType st = SerializedValueType.forValue(value);
        out.writeByte(st.ordinal());
        switch (st) {
        case BOOLEAN:
            out.writeBoolean(((ConfigBoolean) value).unwrapped());
            break;
        case NULL:
            break;
        case INT:
            // saving numbers as both string and binary is redundant but easy
            out.writeInt(((ConfigInt) value).unwrapped());
            out.writeUTF(((ConfigNumber) value).transformToString());
            break;
        case LONG:
            out.writeLong(((ConfigLong) value).unwrapped());
            out.writeUTF(((ConfigNumber) value).transformToString());
            break;
        case DOUBLE:
            out.writeDouble(((ConfigDouble) value).unwrapped());
            out.writeUTF(((ConfigNumber) value).transformToString());
            break;
        case STRING:
            out.writeUTF(((ConfigString) value).unwrapped());
            break;
        case LIST:
            final ConfigList list = (ConfigList) value;
            out.writeInt(list.size());
            for (final ConfigValue v : list) {
                writeValue(out, v, (SimpleConfigOrigin) list.origin());
            }
            break;
        case OBJECT:
            final ConfigObject obj = (ConfigObject) value;
            out.writeInt(obj.size());
            for (final Map.Entry<String, ConfigValue> e : obj.entrySet()) {
                out.writeUTF(e.getKey());
                writeValue(out, e.getValue(), (SimpleConfigOrigin) obj.origin());
            }
            break;
        }
    }

    private static AbstractConfigValue readValueData(final DataInput in, final SimpleConfigOrigin origin)
            throws IOException {
        final int stb = in.readUnsignedByte();
        final SerializedValueType st = SerializedValueType.forInt(stb);
        if (st == null)
            throw new IOException("Unknown serialized value type: " + stb);
        switch (st) {
        case BOOLEAN:
            return new ConfigBoolean(origin, in.readBoolean());
        case NULL:
            return new ConfigNull(origin);
        case INT:
            final int vi = in.readInt();
            final String si = in.readUTF();
            return new ConfigInt(origin, vi, si);
        case LONG:
            final long vl = in.readLong();
            final String sl = in.readUTF();
            return new ConfigLong(origin, vl, sl);
        case DOUBLE:
            final double vd = in.readDouble();
            final String sd = in.readUTF();
            return new ConfigDouble(origin, vd, sd);
        case STRING:
            return new ConfigString(origin, in.readUTF());
        case LIST:
            final int listSize = in.readInt();
            final List<AbstractConfigValue> list = new ArrayList<AbstractConfigValue>(listSize);
            for (int i = 0; i < listSize; ++i) {
                final AbstractConfigValue v = readValue(in, origin);
                list.add(v);
            }
            return new SimpleConfigList(origin, list);
        case OBJECT:
            final int mapSize = in.readInt();
            final Map<String, AbstractConfigValue> map = new HashMap<String, AbstractConfigValue>(mapSize);
            for (int i = 0; i < mapSize; ++i) {
                final String key = in.readUTF();
                final AbstractConfigValue v = readValue(in, origin);
                map.put(key, v);
            }
            return new SimpleConfigObject(origin, map);
        }
        throw new IOException("Unhandled serialized value type: " + st);
    }

    private static void writeValue(final DataOutput out, final ConfigValue value, final SimpleConfigOrigin baseOrigin)
            throws IOException {
        final FieldOut origin = new FieldOut(SerializedField.VALUE_ORIGIN);
        writeOrigin(origin.data, (SimpleConfigOrigin) value.origin(),
                baseOrigin);
        writeField(out, origin);

        final FieldOut data = new FieldOut(SerializedField.VALUE_DATA);
        writeValueData(data.data, value);
        writeField(out, data);

        writeEndMarker(out);
    }

    private static AbstractConfigValue readValue(final DataInput in, final SimpleConfigOrigin baseOrigin)
            throws IOException {
        AbstractConfigValue value = null;
        SimpleConfigOrigin origin = null;
        while (true) {
            final SerializedField code = readCode(in);
            if (code == SerializedField.END_MARKER) {
                if (value == null)
                    throw new IOException("No value data found in serialization of value");
                return value;
            } else if (code == SerializedField.VALUE_DATA) {
                if (origin == null)
                    throw new IOException("Origin must be stored before value data");
                in.readInt(); // discard length
                value = readValueData(in, origin);
            } else if (code == SerializedField.VALUE_ORIGIN) {
                in.readInt(); // discard length
                origin = readOrigin(in, baseOrigin);
            } else {
                // ignore unknown field
                skipField(in);
            }
        }
    }

    private static void writeField(final DataOutput out, final FieldOut field) throws IOException {
        final byte[] bytes = field.bytes.toByteArray();
        out.writeByte(field.code.ordinal());
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    private static void writeEndMarker(final DataOutput out) throws IOException {
        out.writeByte(SerializedField.END_MARKER.ordinal());
    }

    private static SerializedField readCode(final DataInput in) throws IOException {
        final int c = in.readUnsignedByte();
        if (c == SerializedField.UNKNOWN.ordinal())
            throw new IOException("field code " + c + " is not supposed to be on the wire");
        return SerializedField.forInt(c);
    }

    private static void skipField(final DataInput in) throws IOException {
        final int len = in.readInt();
        // skipBytes doesn't have to block
        final int skipped = in.skipBytes(len);
        if (skipped < len) {
            // wastefully use readFully() if skipBytes didn't work
            final byte[] bytes = new byte[(len - skipped)];
            in.readFully(bytes);
        }
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        if (((AbstractConfigValue) value).resolveStatus() != ResolveStatus.RESOLVED)
            throw new NotSerializableException(
                    "tried to serialize a value with unresolved substitutions, need to Config#resolve() first, see API docs");
        FieldOut field = new FieldOut(SerializedField.ROOT_VALUE);
        writeValue(field.data, value, null /* baseOrigin */);
        writeField(out, field);

        field = new FieldOut(SerializedField.ROOT_WAS_CONFIG);
        field.data.writeBoolean(wasConfig);
        writeField(out, field);

        writeEndMarker(out);
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        while (true) {
            final SerializedField code = readCode(in);
            if (code == SerializedField.END_MARKER) {
                return;
            } else if (code == SerializedField.ROOT_VALUE) {
                in.readInt(); // discard length
                this.value = readValue(in, null /* baseOrigin */);
            } else if (code == SerializedField.ROOT_WAS_CONFIG) {
                in.readInt(); // discard length
                this.wasConfig = in.readBoolean();
            } else {
                // ignore unknown field
                skipField(in);
            }
        }
    }

    private static ConfigException shouldNotBeUsed() {
        return new ConfigException.BugOrBroken(SerializedConfigValue.class.getName()
                + " should not exist outside of serialization");
    }

    @Override
    public ConfigValueType valueType() {
        throw shouldNotBeUsed();
    }

    @Override
    public Object unwrapped() {
        throw shouldNotBeUsed();
    }

    @Override
    protected SerializedConfigValue newCopy(final ConfigOrigin origin) {
        throw shouldNotBeUsed();
    }
}
