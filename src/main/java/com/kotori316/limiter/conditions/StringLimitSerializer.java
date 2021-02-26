package com.kotori316.limiter.conditions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;

import com.kotori316.limiter.TestSpawn;

abstract class StringLimitSerializer<T extends TestSpawn, Value> extends TestSpawn.Serializer<T> {
    protected abstract Value fromString(String s);

    protected abstract String valueToString(Value value);

    protected abstract String saveKey();

    protected abstract T instance(Value value);

    protected abstract Value getter(T t);

    @Override
    public Set<String> propertyKeys() {
        return Collections.singleton(saveKey());
    }

    @Override
    public <T1> T from(Dynamic<T1> dynamic) {
        String valueString = dynamic.get(saveKey()).asString("INVALID");
        Value value = fromString(valueString);
        if (value == null) {
            throw new IllegalArgumentException("Value is null, by input: " + valueString + ", whole: " + dynamic.getValue());
        }
        return instance(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T1> T1 to(TestSpawn a, DynamicOps<T1> ops) {
        Value value = getter(((T) a));
        String valueString = valueToString(value);
        Map<T1, T1> map = new HashMap<>();
        map.put(ops.createString(saveKey()), ops.createString(valueString));
        return ops.createMap(map);
    }

    public static <Type extends TestSpawn, Value> StringLimitSerializer<Type, Value> fromFunction(
        Function<Type, Value> getter, Function<Value, Type> instance,
        Function<Value, String> asString, Function<String, Value> fromString,
        String saveKey, String typeName, Value[] values
    ) {
        return new StringLimitSerializer<Type, Value>() {
            @Override
            protected Value fromString(String s) {
                return fromString.apply(s);
            }

            @Override
            protected String valueToString(Value value) {
                return asString.apply(value);
            }

            @Override
            protected String saveKey() {
                return saveKey;
            }

            @Override
            protected Type instance(Value value) {
                return instance.apply(value);
            }

            @Override
            protected Value getter(Type type) {
                return getter.apply(type);
            }

            @Override
            public String getType() {
                return typeName;
            }

            @Override
            public Set<String> possibleValues(String property) {
                if (property.equals(saveKey())) {
                    return Arrays.stream(values).map(this::valueToString).collect(Collectors.toSet());
                }
                return Collections.emptySet();
            }
        };
    }
}
