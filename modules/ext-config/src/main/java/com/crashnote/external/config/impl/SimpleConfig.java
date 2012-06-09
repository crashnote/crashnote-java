/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.external.config.impl;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.crashnote.external.config.Config;
import com.crashnote.external.config.ConfigException;
import com.crashnote.external.config.ConfigList;
import com.crashnote.external.config.ConfigMergeable;
import com.crashnote.external.config.ConfigObject;
import com.crashnote.external.config.ConfigOrigin;
import com.crashnote.external.config.ConfigResolveOptions;
import com.crashnote.external.config.ConfigValue;
import com.crashnote.external.config.ConfigValueType;

/**
 * One thing to keep in mind in the future: as Collection-like APIs are added
 * here, including iterators or size() or anything, they should be consistent
 * with a one-level java.util.Map from paths to non-null values. Null values are
 * not "in" the map.
 */
final class SimpleConfig implements Config, MergeableValue, Serializable {

    private static final long serialVersionUID = 1L;

    final private AbstractConfigObject object;

    SimpleConfig(final AbstractConfigObject object) {
        this.object = object;
    }

    @Override
    public AbstractConfigObject root() {
        return object;
    }

    @Override
    public ConfigOrigin origin() {
        return object.origin();
    }

    @Override
    public SimpleConfig resolve() {
        return resolve(ConfigResolveOptions.defaults());
    }

    @Override
    public SimpleConfig resolve(final ConfigResolveOptions options) {
        final AbstractConfigValue resolved = ResolveContext.resolve(object, object, options);

        if (resolved == object)
            return this;
        else
            return new SimpleConfig((AbstractConfigObject) resolved);
    }


    @Override
    public boolean hasPath(final String pathExpression) {
        final Path path = Path.newPath(pathExpression);
        final ConfigValue peeked;
        try {
            peeked = object.peekPath(path);
        } catch (ConfigException.NotResolved e) {
            throw ConfigImpl.improveNotResolved(path, e);
        }
        return peeked != null && peeked.valueType() != ConfigValueType.NULL;
    }

    @Override
    public boolean isEmpty() {
        return object.isEmpty();
    }

    private static void findPaths(final Set<Map.Entry<String, ConfigValue>> entries, final Path parent,
            final AbstractConfigObject obj) {
        for (final Map.Entry<String, ConfigValue> entry : obj.entrySet()) {
            final String elem = entry.getKey();
            final ConfigValue v = entry.getValue();
            Path path = Path.newKey(elem);
            if (parent != null)
                path = path.prepend(parent);
            if (v instanceof AbstractConfigObject) {
                findPaths(entries, path, (AbstractConfigObject) v);
            } else if (v instanceof ConfigNull) {
                // nothing; nulls are conceptually not in a Config
            } else {
                entries.add(new AbstractMap.SimpleImmutableEntry<String, ConfigValue>(path.render(), v));
            }
        }
    }

    @Override
    public Set<Map.Entry<String, ConfigValue>> entrySet() {
        final Set<Map.Entry<String, ConfigValue>> entries = new HashSet<Map.Entry<String, ConfigValue>>();
        findPaths(entries, null, object);
        return entries;
    }

    static private AbstractConfigValue findKey(final AbstractConfigObject self, final String key,
            final ConfigValueType expected, final Path originalPath) {
        AbstractConfigValue v = self.peekAssumingResolved(key, originalPath);
        if (v == null)
            throw new ConfigException.Missing(originalPath.render());

        if (expected != null)
            v = DefaultTransformer.transform(v, expected);

        if (v.valueType() == ConfigValueType.NULL)
            throw new ConfigException.Null(v.origin(), originalPath.render(),
                    expected != null ? expected.name() : null);
        else if (expected != null && v.valueType() != expected)
            throw new ConfigException.WrongType(v.origin(), originalPath.render(), expected.name(),
                    v.valueType().name());
        else
            return v;
    }

    static private AbstractConfigValue find(final AbstractConfigObject self, final Path path,
            final ConfigValueType expected, final Path originalPath) {
        try {
            final String key = path.first();
            final Path next = path.remainder();
            if (next == null) {
                return findKey(self, key, expected, originalPath);
            } else {
                final AbstractConfigObject o = (AbstractConfigObject) findKey(self, key,
                        ConfigValueType.OBJECT,
                        originalPath.subPath(0, originalPath.length() - next.length()));
                assert (o != null); // missing was supposed to throw
                return find(o, next, expected, originalPath);
            }
        } catch (ConfigException.NotResolved e) {
            throw ConfigImpl.improveNotResolved(path, e);
        }
    }

    AbstractConfigValue find(final Path pathExpression, final ConfigValueType expected, final Path originalPath) {
        return find(object, pathExpression, expected, originalPath);
    }

    AbstractConfigValue find(final String pathExpression, final ConfigValueType expected) {
        final Path path = Path.newPath(pathExpression);
        return find(path, expected, path);
    }

    @Override
    public AbstractConfigValue getValue(final String path) {
        return find(path, null);
    }

    @Override
    public boolean getBoolean(final String path) {
        final ConfigValue v = find(path, ConfigValueType.BOOLEAN);
        return (Boolean) v.unwrapped();
    }

    private ConfigNumber getConfigNumber(final String path) {
        final ConfigValue v = find(path, ConfigValueType.NUMBER);
        return (ConfigNumber) v;
    }

    @Override
    public Number getNumber(final String path) {
        return getConfigNumber(path).unwrapped();
    }

    @Override
    public int getInt(final String path) {
        final ConfigNumber n = getConfigNumber(path);
        return n.intValueRangeChecked(path);
    }

    @Override
    public long getLong(final String path) {
        return getNumber(path).longValue();
    }

    @Override
    public double getDouble(final String path) {
        return getNumber(path).doubleValue();
    }

    @Override
    public String getString(final String path) {
        final ConfigValue v = find(path, ConfigValueType.STRING);
        return (String) v.unwrapped();
    }

    @Override
    public ConfigList getList(final String path) {
        final AbstractConfigValue v = find(path, ConfigValueType.LIST);
        return (ConfigList) v;
    }

    @Override
    public AbstractConfigObject getObject(final String path) {
        final AbstractConfigObject obj = (AbstractConfigObject) find(path, ConfigValueType.OBJECT);
        return obj;
    }

    @Override
    public SimpleConfig getConfig(final String path) {
        return getObject(path).toConfig();
    }

    @Override
    public Object getAnyRef(final String path) {
        final ConfigValue v = find(path, null);
        return v.unwrapped();
    }

    @Override
    public Long getBytes(final String path) {
        Long size = null;
        try {
            size = getLong(path);
        } catch (ConfigException.WrongType e) {
            final ConfigValue v = find(path, ConfigValueType.STRING);
            size = parseBytes((String) v.unwrapped(),
                    v.origin(), path);
        }
        return size;
    }

    @Override
    public Long getMilliseconds(final String path) {
        final long ns = getNanoseconds(path);
        final long ms = TimeUnit.NANOSECONDS.toMillis(ns);
        return ms;
    }

    @Override
    public Long getNanoseconds(final String path) {
        Long ns = null;
        try {
            ns = TimeUnit.MILLISECONDS.toNanos(getLong(path));
        } catch (ConfigException.WrongType e) {
            final ConfigValue v = find(path, ConfigValueType.STRING);
            ns = parseDuration((String) v.unwrapped(), v.origin(), path);
        }
        return ns;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getHomogeneousUnwrappedList(final String path,
            final ConfigValueType expected) {
        final List<T> l = new ArrayList<T>();
        final List<? extends ConfigValue> list = getList(path);
        for (final ConfigValue cv : list) {
            // variance would be nice, but stupid cast will do
            AbstractConfigValue v = (AbstractConfigValue) cv;
            if (expected != null) {
                v = DefaultTransformer.transform(v, expected);
            }
            if (v.valueType() != expected)
                throw new ConfigException.WrongType(v.origin(), path,
                        "list of " + expected.name(), "list of "
                                + v.valueType().name());
            l.add((T) v.unwrapped());
        }
        return l;
    }

    @Override
    public List<Boolean> getBooleanList(final String path) {
        return getHomogeneousUnwrappedList(path, ConfigValueType.BOOLEAN);
    }

    @Override
    public List<Number> getNumberList(final String path) {
        return getHomogeneousUnwrappedList(path, ConfigValueType.NUMBER);
    }

    @Override
    public List<Integer> getIntList(final String path) {
        final List<Integer> l = new ArrayList<Integer>();
        final List<AbstractConfigValue> numbers = getHomogeneousWrappedList(path, ConfigValueType.NUMBER);
        for (final AbstractConfigValue v : numbers) {
            l.add(((ConfigNumber) v).intValueRangeChecked(path));
        }
        return l;
    }

    @Override
    public List<Long> getLongList(final String path) {
        final List<Long> l = new ArrayList<Long>();
        final List<Number> numbers = getNumberList(path);
        for (final Number n : numbers) {
            l.add(n.longValue());
        }
        return l;
    }

    @Override
    public List<Double> getDoubleList(final String path) {
        final List<Double> l = new ArrayList<Double>();
        final List<Number> numbers = getNumberList(path);
        for (final Number n : numbers) {
            l.add(n.doubleValue());
        }
        return l;
    }

    @Override
    public List<String> getStringList(final String path) {
        return getHomogeneousUnwrappedList(path, ConfigValueType.STRING);
    }

    @SuppressWarnings("unchecked")
    private <T extends ConfigValue> List<T> getHomogeneousWrappedList(
            final String path, final ConfigValueType expected) {
        final List<T> l = new ArrayList<T>();
        final List<? extends ConfigValue> list = getList(path);
        for (final ConfigValue cv : list) {
            // variance would be nice, but stupid cast will do
            AbstractConfigValue v = (AbstractConfigValue) cv;
            if (expected != null) {
                v = DefaultTransformer.transform(v, expected);
            }
            if (v.valueType() != expected)
                throw new ConfigException.WrongType(v.origin(), path,
                        "list of " + expected.name(), "list of "
                                + v.valueType().name());
            l.add((T) v);
        }
        return l;
    }

    @Override
    public List<ConfigObject> getObjectList(final String path) {
        return getHomogeneousWrappedList(path, ConfigValueType.OBJECT);
    }

    @Override
    public List<? extends Config> getConfigList(final String path) {
        final List<ConfigObject> objects = getObjectList(path);
        final List<Config> l = new ArrayList<Config>();
        for (final ConfigObject o : objects) {
            l.add(o.toConfig());
        }
        return l;
    }

    @Override
    public List<? extends Object> getAnyRefList(final String path) {
        final List<Object> l = new ArrayList<Object>();
        final List<? extends ConfigValue> list = getList(path);
        for (final ConfigValue v : list) {
            l.add(v.unwrapped());
        }
        return l;
    }

    @Override
    public List<Long> getBytesList(final String path) {
        final List<Long> l = new ArrayList<Long>();
        final List<? extends ConfigValue> list = getList(path);
        for (final ConfigValue v : list) {
            if (v.valueType() == ConfigValueType.NUMBER) {
                l.add(((Number) v.unwrapped()).longValue());
            } else if (v.valueType() == ConfigValueType.STRING) {
                final String s = (String) v.unwrapped();
                final Long n = parseBytes(s, v.origin(), path);
                l.add(n);
            } else {
                throw new ConfigException.WrongType(v.origin(), path,
                        "memory size string or number of bytes", v.valueType()
                                .name());
            }
        }
        return l;
    }

    @Override
    public List<Long> getMillisecondsList(final String path) {
        final List<Long> nanos = getNanosecondsList(path);
        final List<Long> l = new ArrayList<Long>();
        for (final Long n : nanos) {
            l.add(TimeUnit.NANOSECONDS.toMillis(n));
        }
        return l;
    }

    @Override
    public List<Long> getNanosecondsList(final String path) {
        final List<Long> l = new ArrayList<Long>();
        final List<? extends ConfigValue> list = getList(path);
        for (final ConfigValue v : list) {
            if (v.valueType() == ConfigValueType.NUMBER) {
                l.add(TimeUnit.MILLISECONDS.toNanos(((Number) v.unwrapped())
                        .longValue()));
            } else if (v.valueType() == ConfigValueType.STRING) {
                final String s = (String) v.unwrapped();
                final Long n = parseDuration(s, v.origin(), path);
                l.add(n);
            } else {
                throw new ConfigException.WrongType(v.origin(), path,
                        "duration string or number of nanoseconds", v
                                .valueType().name());
            }
        }
        return l;
    }

    @Override
    public AbstractConfigObject toFallbackValue() {
        return object;
    }

    @Override
    public SimpleConfig withFallback(final ConfigMergeable other) {
        // this can return "this" if the withFallback doesn't need a new
        // ConfigObject
        return object.withFallback(other).toConfig();
    }

    @Override
    public final boolean equals(final Object other) {
        if (other instanceof SimpleConfig) {
            return object.equals(((SimpleConfig) other).object);
        } else {
            return false;
        }
    }

    @Override
    public final int hashCode() {
        // we do the "41*" just so our hash code won't match that of the
        // underlying object. there's no real reason it can't match, but
        // making it not match might catch some kinds of bug.
        return 41 * object.hashCode();
    }

    @Override
    public String toString() {
        return "Config(" + object.toString() + ")";
    }

    private static String getUnits(final String s) {
        int i = s.length() - 1;
        while (i >= 0) {
            final char c = s.charAt(i);
            if (!Character.isLetter(c))
                break;
            i -= 1;
        }
        return s.substring(i + 1);
    }

    /**
     * Parses a duration string. If no units are specified in the string, it is
     * assumed to be in milliseconds. The returned duration is in nanoseconds.
     * The purpose of this function is to implement the duration-related methods
     * in the ConfigObject interface.
     *
     * @param input
     *            the string to parse
     * @param originForException
     *            origin of the value being parsed
     * @param pathForException
     *            path to include in exceptions
     * @return duration in nanoseconds
     * @throws ConfigException
     *             if string is invalid
     */
    public static long parseDuration(final String input,
            final ConfigOrigin originForException, final String pathForException) {
        final String s = ConfigImplUtil.unicodeTrim(input);
        final String originalUnitString = getUnits(s);
        String unitString = originalUnitString;
        final String numberString = ConfigImplUtil.unicodeTrim(s.substring(0, s.length()
                - unitString.length()));
        TimeUnit units = null;

        // this would be caught later anyway, but the error message
        // is more helpful if we check it here.
        if (numberString.length() == 0)
            throw new ConfigException.BadValue(originForException,
                    pathForException, "No number in duration value '" + input
                            + "'");

        if (unitString.length() > 2 && !unitString.endsWith("s"))
            unitString = unitString + "s";

        // note that this is deliberately case-sensitive
        if (unitString.equals("") || unitString.equals("ms")
                || unitString.equals("milliseconds")) {
            units = TimeUnit.MILLISECONDS;
        } else if (unitString.equals("us") || unitString.equals("microseconds")) {
            units = TimeUnit.MICROSECONDS;
        } else if (unitString.equals("ns") || unitString.equals("nanoseconds")) {
            units = TimeUnit.NANOSECONDS;
        } else if (unitString.equals("d") || unitString.equals("days")) {
            units = TimeUnit.DAYS;
        } else if (unitString.equals("h") || unitString.equals("hours")) {
            units = TimeUnit.HOURS;
        } else if (unitString.equals("s") || unitString.equals("seconds")) {
            units = TimeUnit.SECONDS;
        } else if (unitString.equals("m") || unitString.equals("minutes")) {
            units = TimeUnit.MINUTES;
        } else {
            throw new ConfigException.BadValue(originForException,
                    pathForException, "Could not parse time unit '"
                            + originalUnitString
                            + "' (try ns, us, ms, s, m, d)");
        }

        try {
            // if the string is purely digits, parse as an integer to avoid
            // possible precision loss;
            // otherwise as a double.
            if (numberString.matches("[0-9]+")) {
                return units.toNanos(Long.parseLong(numberString));
            } else {
                final long nanosInUnit = units.toNanos(1);
                return (long) (Double.parseDouble(numberString) * nanosInUnit);
            }
        } catch (NumberFormatException e) {
            throw new ConfigException.BadValue(originForException,
                    pathForException, "Could not parse duration number '"
                            + numberString + "'");
        }
    }

    private static enum MemoryUnit {
        BYTES("", 1024, 0),

        KILOBYTES("kilo", 1000, 1),
        MEGABYTES("mega", 1000, 2),
        GIGABYTES("giga", 1000, 3),
        TERABYTES("tera", 1000, 4),
        PETABYTES("peta", 1000, 5),
        EXABYTES("exa", 1000, 6),
        ZETTABYTES("zetta", 1000, 7),
        YOTTABYTES("yotta", 1000, 8),

        KIBIBYTES("kibi", 1024, 1),
        MEBIBYTES("mebi", 1024, 2),
        GIBIBYTES("gibi", 1024, 3),
        TEBIBYTES("tebi", 1024, 4),
        PEBIBYTES("pebi", 1024, 5),
        EXBIBYTES("exbi", 1024, 6),
        ZEBIBYTES("zebi", 1024, 7),
        YOBIBYTES("yobi", 1024, 8);

        final String prefix;
        final int powerOf;
        final int power;
        final long bytes;

        MemoryUnit(final String prefix, final int powerOf, final int power) {
            this.prefix = prefix;
            this.powerOf = powerOf;
            this.power = power;
            int i = power;
            long bytes = 1;
            while (i > 0) {
                bytes *= powerOf;
                --i;
            }
            this.bytes = bytes;
        }

        private static Map<String, MemoryUnit> makeUnitsMap() {
            final Map<String, MemoryUnit> map = new HashMap<String, MemoryUnit>();
            for (final MemoryUnit unit : MemoryUnit.values()) {
                map.put(unit.prefix + "byte", unit);
                map.put(unit.prefix + "bytes", unit);
                if (unit.prefix.length() == 0) {
                    map.put("b", unit);
                    map.put("B", unit);
                    map.put("", unit); // no unit specified means bytes
                } else {
                    final String first = unit.prefix.substring(0, 1);
                    final String firstUpper = first.toUpperCase();
                    if (unit.powerOf == 1024) {
                        map.put(first, unit);             // 512m
                        map.put(firstUpper, unit);        // 512M
                        map.put(firstUpper + "i", unit);  // 512Mi
                        map.put(firstUpper + "iB", unit); // 512MiB
                    } else if (unit.powerOf == 1000) {
                        if (unit.power == 1) {
                            map.put(first + "B", unit);      // 512kB
                        } else {
                            map.put(firstUpper + "B", unit); // 512MB
                        }
                    } else {
                        throw new RuntimeException("broken MemoryUnit enum");
                    }
                }
            }
            return map;
        }

        private static Map<String, MemoryUnit> unitsMap = makeUnitsMap();

        static MemoryUnit parseUnit(final String unit) {
            return unitsMap.get(unit);
        }
    }

    /**
     * Parses a size-in-bytes string. If no units are specified in the string,
     * it is assumed to be in bytes. The returned value is in bytes. The purpose
     * of this function is to implement the size-in-bytes-related methods in the
     * Config interface.
     *
     * @param input
     *            the string to parse
     * @param originForException
     *            origin of the value being parsed
     * @param pathForException
     *            path to include in exceptions
     * @return size in bytes
     * @throws ConfigException
     *             if string is invalid
     */
    public static long parseBytes(final String input, final ConfigOrigin originForException,
            final String pathForException) {
        final String s = ConfigImplUtil.unicodeTrim(input);
        final String unitString = getUnits(s);
        final String numberString = ConfigImplUtil.unicodeTrim(s.substring(0,
                s.length() - unitString.length()));

        // this would be caught later anyway, but the error message
        // is more helpful if we check it here.
        if (numberString.length() == 0)
            throw new ConfigException.BadValue(originForException,
                    pathForException, "No number in size-in-bytes value '"
                            + input + "'");

        final MemoryUnit units = MemoryUnit.parseUnit(unitString);

        if (units == null) {
            throw new ConfigException.BadValue(originForException, pathForException,
                    "Could not parse size-in-bytes unit '" + unitString
                            + "' (try k, K, kB, KiB, kilobytes, kibibytes)");
        }

        try {
            // if the string is purely digits, parse as an integer to avoid
            // possible precision loss; otherwise as a double.
            if (numberString.matches("[0-9]+")) {
                return Long.parseLong(numberString) * units.bytes;
            } else {
                return (long) (Double.parseDouble(numberString) * units.bytes);
            }
        } catch (NumberFormatException e) {
            throw new ConfigException.BadValue(originForException, pathForException,
                    "Could not parse size-in-bytes number '" + numberString + "'");
        }
    }

    private AbstractConfigValue peekPath(final Path path) {
        return root().peekPath(path);
    }

    private static void addProblem(final List<ConfigException.ValidationProblem> accumulator, final Path path,
            final ConfigOrigin origin, final String problem) {
        accumulator.add(new ConfigException.ValidationProblem(path.render(), origin, problem));
    }

    private static String getDesc(final ConfigValue refValue) {
        if (refValue instanceof AbstractConfigObject) {
            final AbstractConfigObject obj = (AbstractConfigObject) refValue;
            if (obj.isEmpty())
                return "object";
            else
                return "object with keys " + obj.keySet();
        } else if (refValue instanceof SimpleConfigList) {
            return "list";
        } else {
            return refValue.valueType().name().toLowerCase();
        }
    }

    private static void addMissing(final List<ConfigException.ValidationProblem> accumulator,
            final ConfigValue refValue, final Path path, final ConfigOrigin origin) {
        addProblem(accumulator, path, origin, "No setting at '" + path.render() + "', expecting: "
                + getDesc(refValue));
    }

    private static void addWrongType(final List<ConfigException.ValidationProblem> accumulator,
            final ConfigValue refValue, final AbstractConfigValue actual, final Path path) {
        addProblem(accumulator, path, actual.origin(), "Wrong value type at '" + path.render()
                + "', expecting: " + getDesc(refValue) + " but got: "
                        + getDesc(actual));
    }

    private static boolean couldBeNull(final AbstractConfigValue v) {
        return DefaultTransformer.transform(v, ConfigValueType.NULL)
                .valueType() == ConfigValueType.NULL;
    }

    private static boolean haveCompatibleTypes(final ConfigValue reference, final AbstractConfigValue value) {
        if (couldBeNull((AbstractConfigValue) reference) || couldBeNull(value)) {
            // we allow any setting to be null
            return true;
        } else if (reference instanceof AbstractConfigObject) {
            if (value instanceof AbstractConfigObject) {
                return true;
            } else {
                return false;
            }
        } else if (reference instanceof SimpleConfigList) {
            if (value instanceof SimpleConfigList) {
                return true;
            } else {
                return false;
            }
        } else if (reference instanceof ConfigString) {
            // assume a string could be gotten as any non-collection type;
            // allows things like getMilliseconds including domain-specific
            // interpretations of strings
            return true;
        } else if (value instanceof ConfigString) {
            // assume a string could be gotten as any non-collection type
            return true;
        } else {
            if (reference.valueType() == value.valueType()) {
                return true;
            } else {
                return false;
            }
        }
    }

    // path is null if we're at the root
    private static void checkValidObject(final Path path, final AbstractConfigObject reference,
            final AbstractConfigObject value,
            final List<ConfigException.ValidationProblem> accumulator) {
        for (final Map.Entry<String, ConfigValue> entry : reference.entrySet()) {
            final String key = entry.getKey();

            final Path childPath;
            if (path != null)
                childPath = Path.newKey(key).prepend(path);
            else
                childPath = Path.newKey(key);

            final AbstractConfigValue v = value.get(key);
            if (v == null) {
                addMissing(accumulator, entry.getValue(), childPath, value.origin());
            } else {
                checkValid(childPath, entry.getValue(), v, accumulator);
            }
        }
    }

    private static void checkValid(final Path path, final ConfigValue reference, final AbstractConfigValue value,
            final List<ConfigException.ValidationProblem> accumulator) {
        // Unmergeable is supposed to be impossible to encounter in here
        // because we check for resolve status up front.

        if (haveCompatibleTypes(reference, value)) {
            if (reference instanceof AbstractConfigObject && value instanceof AbstractConfigObject) {
                checkValidObject(path, (AbstractConfigObject) reference,
                        (AbstractConfigObject) value, accumulator);
            } else if (reference instanceof SimpleConfigList && value instanceof SimpleConfigList) {
                final SimpleConfigList listRef = (SimpleConfigList) reference;
                final SimpleConfigList listValue = (SimpleConfigList) value;
                if (listRef.isEmpty() || listValue.isEmpty()) {
                    // can't verify type, leave alone
                } else {
                    final AbstractConfigValue refElement = listRef.get(0);
                    for (final ConfigValue elem : listValue) {
                        final AbstractConfigValue e = (AbstractConfigValue) elem;
                        if (!haveCompatibleTypes(refElement, e)) {
                            addProblem(accumulator, path, e.origin(), "List at '" + path.render()
                                    + "' contains wrong value type, expecting list of "
                                    + getDesc(refElement) + " but got element of type "
                                    + getDesc(e));
                            // don't add a problem for every last array element
                            break;
                        }
                    }
                }
            }
        } else {
            addWrongType(accumulator, reference, value, path);
        }
    }

    @Override
    public void checkValid(final Config reference, final String... restrictToPaths) {
        final SimpleConfig ref = (SimpleConfig) reference;

        // unresolved reference config is a bug in the caller of checkValid
        if (ref.root().resolveStatus() != ResolveStatus.RESOLVED)
            throw new ConfigException.BugOrBroken(
                    "do not call checkValid() with an unresolved reference config, call Config#resolve(), see Config#resolve() API docs");

        // unresolved config under validation is a bug in something,
        // NotResolved is a more specific subclass of BugOrBroken
        if (root().resolveStatus() != ResolveStatus.RESOLVED)
            throw new ConfigException.NotResolved(
                    "need to Config#resolve() each config before using it, see the API docs for Config#resolve()");

        // Now we know that both reference and this config are resolved

        final List<ConfigException.ValidationProblem> problems = new ArrayList<ConfigException.ValidationProblem>();

        if (restrictToPaths.length == 0) {
            checkValidObject(null, ref.root(), root(), problems);
        } else {
            for (final String p : restrictToPaths) {
                final Path path = Path.newPath(p);
                final AbstractConfigValue refValue = ref.peekPath(path);
                if (refValue != null) {
                    final AbstractConfigValue child = peekPath(path);
                    if (child != null) {
                        checkValid(path, refValue, child, problems);
                    } else {
                        addMissing(problems, refValue, path, origin());
                    }
                }
            }
        }

        if (!problems.isEmpty()) {
            throw new ConfigException.ValidationFailed(problems);
        }
    }

    @Override
    public SimpleConfig withOnlyPath(final String pathExpression) {
        final Path path = Path.newPath(pathExpression);
        return new SimpleConfig(root().withOnlyPath(path));
    }

    @Override
    public SimpleConfig withoutPath(final String pathExpression) {
        final Path path = Path.newPath(pathExpression);
        return new SimpleConfig(root().withoutPath(path));
    }

    // serialization all goes through SerializedConfigValue
    private Object writeReplace() throws ObjectStreamException {
        return new SerializedConfigValue(this);
    }
}
