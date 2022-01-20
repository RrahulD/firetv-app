/*
 * Copyright (c) 2017 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.vson;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides more flexible mapping for primitives. Can handle:
 * - string values containing parseable primitives "15" as a string instead of 15 in a json.
 * - "null" or null passed down in primitive json fields. Skips the field in this case, leaving in its default value provided in the class.
 * - Supported types: byte, double, float, int, long, short, boolean; both boxed and unboxed.
 * - May be extended with more {@link PrimitiveParser}-s.
 */
public class PrimitiveAdapterFactory implements TypeAdapterFactory {
    protected Map<Class, PrimitiveParser> supportedClasses = new HashMap<>();

    /**
     * @param clazz           the class to parse differently.
     * @param primitiveParser the parser to use to parse the given class.
     * @param <T>             the type of the class. May be primitive.
     * @return this {@link PrimitiveAdapterFactory} instance for chaining.
     */
    public <T> PrimitiveAdapterFactory addPrimitiveParser(Class<T> clazz, PrimitiveParser<T> primitiveParser) {
        if (clazz == null || primitiveParser == null) {
            throw new IllegalArgumentException("Provided Class and PrimitiveParser should not be null.");
        }
        supportedClasses.put(clazz, primitiveParser);
        return this;
    }

    public PrimitiveAdapterFactory() {
        supportedClasses.put(byte.class, new ByteParser());
        supportedClasses.put(Byte.class, new ByteParser());
        supportedClasses.put(double.class, new DoubleParser());
        supportedClasses.put(Double.class, new DoubleParser());
        supportedClasses.put(float.class, new FloatParser());
        supportedClasses.put(Float.class, new FloatParser());
        supportedClasses.put(int.class, new IntegerParser());
        supportedClasses.put(Integer.class, new IntegerParser());
        supportedClasses.put(long.class, new LongParser());
        supportedClasses.put(Long.class, new LongParser());
        supportedClasses.put(short.class, new ShortParser());
        supportedClasses.put(Short.class, new ShortParser());
        supportedClasses.put(boolean.class, new BooleanParser());
        supportedClasses.put(Boolean.class, new BooleanParser());
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
        if (!supportedClasses.containsKey(type.getRawType())) {
            return null;
        }

        final TypeAdapter<T> delegateAdapter = gson.getDelegateAdapter(this, type);
        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegateAdapter.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                if (in.peek() != JsonToken.STRING) {
                    return delegateAdapter.read(in);
                }

                Object parseResult = supportedClasses.get(type.getRawType()).parse(in.nextString());
                //Need to pass down "null" because "(T) null" is 0 if clazz==integer.class
                //If null is returned, the field will be skipped, and the already present default value will be left in place.
                return parseResult == null ? null : (T) parseResult;
            }
        };
    }

    public static class ByteParser implements PrimitiveParser<Byte> {
        @Override
        public Byte parse(String input) {
            if (TextUtils.isEmpty(input)) {
                return null;
            }

            return Byte.valueOf(input);
        }
    }

    public static class DoubleParser implements PrimitiveParser<Double> {
        @Override
        public Double parse(String input) {
            if (TextUtils.isEmpty(input)) {
                return null;
            }

            return Double.valueOf(input);
        }
    }

    public static class FloatParser implements PrimitiveParser<Float> {
        @Override
        public Float parse(String input) {
            if (TextUtils.isEmpty(input)) {
                return null;
            }

            return Float.valueOf(input);
        }
    }

    public static class IntegerParser implements PrimitiveParser<Integer> {
        @Override
        public Integer parse(String input) {
            if (TextUtils.isEmpty(input)) {
                return null;
            }

            return Integer.valueOf(input);
        }
    }

    public static class LongParser implements PrimitiveParser<Long> {
        @Override
        public Long parse(String input) {
            if (TextUtils.isEmpty(input)) {
                return null;
            }

            return Long.valueOf(input);
        }
    }

    public static class ShortParser implements PrimitiveParser<Short> {
        @Override
        public Short parse(String input) {
            if (TextUtils.isEmpty(input)) {
                return null;
            }

            return Short.valueOf(input);
        }
    }

    public static class BooleanParser implements PrimitiveParser<Boolean> {
        @Override
        public Boolean parse(String input) {
            if (TextUtils.isEmpty(input)) {
                return null;
            }

            return Boolean.valueOf(input);
        }
    }

    public static interface PrimitiveParser<T> {
        public T parse(String input);
    }
}
