/*
 * Copyright (c) 2017 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.vson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link TypeAdapterFactory} which allows you to modify the raw input-json and its type, based on its contents,
 * before it's actually fed into GSON for auto-mapping.
 */
public class OverrideAdapterFactory implements TypeAdapterFactory {
    protected final List<GsonOverrideHolder> parserOverrides = new ArrayList<>();

    /**
     * @param typeToken the TypeToken of the type this override should work on.
     * @param override  the override defining how the JSON should be modified.
     * @param <T>       the Type this override should work on.
     * @return this {@link OverrideAdapterFactory} instance for chaining.
     */
    public <T> OverrideAdapterFactory addOverride(TypeToken<T> typeToken, GsonOverride override) {
        return addOverride(false, typeToken, override);
    }

    /**
     * @param baseType the class this override should work on.
     * @param override the override defining how the JSON should be modified.
     * @param <T>      the Type this override should work on.
     * @return this {@link OverrideAdapterFactory} instance for chaining.
     */
    public <T> OverrideAdapterFactory addOverride(Class<?> baseType, GsonOverride override) {
        return addOverride(false, TypeToken.get(baseType), override);
    }

    /**
     * @param typeToken the TypeToken of the type this override should work on. The override is applied to subtypes of this types aswell.
     * @param override  the override defining how the JSON should be modified.
     * @param <T>       the Type this override should work on.
     * @return this {@link OverrideAdapterFactory} instance for chaining.
     */
    public <T> OverrideAdapterFactory addHierarchyOverride(TypeToken<T> typeToken, GsonOverride override) {
        return addOverride(true, typeToken, override);
    }

    /**
     * @param baseType the class this override should work on. The override is applied to subclasses of this class aswell.
     * @param override the override defining how the JSON should be modified.
     * @param <T>      the Type this override should work on.
     * @return this {@link OverrideAdapterFactory} instance for chaining.
     */
    public <T> OverrideAdapterFactory addHierarchyOverride(Class<?> baseType, GsonOverride override) {
        return addOverride(true, TypeToken.get(baseType), override);
    }

    protected OverrideAdapterFactory addOverride(boolean isHierarchy, TypeToken typeToken, GsonOverride gsonOverride) {
        if (typeToken == null) {
            throw new IllegalArgumentException("TypeToken should not be null.");
        }
        if (gsonOverride == null) {
            throw new IllegalArgumentException("ParserOverride should not be null.");
        }

        parserOverrides.add(0, new GsonOverrideHolder(isHierarchy, typeToken, gsonOverride));
        return this;
    }

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
        final TypeAdapter<T> delegateAdapter = gson.getDelegateAdapter(this, type);
        final GsonOverride override = getOverrideForType(type);

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegateAdapter.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                // Null JSON check
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }

                // Parse JsonObject
                JsonElement jsonElement = gson.getAdapter(JsonElement.class).read(in);

                // Preprocess
                TypeToken<T> typeTokenToUse = type;
                if (override != null) {
                    // Preprocess
                    JsonElement preProcessedJsonElement = override.jsonOverride(jsonElement);
                    if (preProcessedJsonElement == null) {
                        throw new JsonParseException("JsonElement null after preProcessing JSON: " + preProcessedJsonElement.toString() + "\n in Class: " + type.getRawType().getSimpleName());
                    }
                    jsonElement = preProcessedJsonElement;

                    // Override type if necessary
                    typeTokenToUse = override.typeOverride(jsonElement, type);
                }

                // Gson parse T instance from JsonObject
                return gson.getDelegateAdapter(OverrideAdapterFactory.this, typeTokenToUse).fromJsonTree(jsonElement);
            }
        };
    }

    protected GsonOverride getOverrideForType(TypeToken typeToken) {
        for (GsonOverrideHolder gsonOverrideHolder : parserOverrides) {
            boolean notHierarchyAndEquals = !gsonOverrideHolder.isHierarchy && gsonOverrideHolder.typeToken.equals(typeToken);
            boolean hierarchyAndAssignable = gsonOverrideHolder.isHierarchy && gsonOverrideHolder.typeToken.getRawType().isAssignableFrom(typeToken.getRawType());

            if (notHierarchyAndEquals || hierarchyAndAssignable) {
                return gsonOverrideHolder.gsonOverride;
            }
        }

        return null;
    }

    /**
     * An override defining how the raw JSON should be modified before being fed to GSON.
     */
    public interface GsonOverride {
        /**
         * @param raw the raw JSON input, fed into GSON.
         * @return the modified JSON, that GSON should work on.
         */
        JsonElement jsonOverride(JsonElement raw);

        /**
         * @param jsonElement the output of jsonOverride()
         * @param typeToken   the TypeToken GSON wants to parse this json into.
         * @return the actual typeToken GSON should use to parse.
         */
        TypeToken typeOverride(JsonElement jsonElement, TypeToken typeToken);
    }

    protected static class GsonOverrideHolder {
        final boolean isHierarchy;
        final TypeToken typeToken;
        final GsonOverride gsonOverride;

        public GsonOverrideHolder(boolean isHierarchy, TypeToken typeToken, GsonOverride gsonOverride) {
            this.isHierarchy = isHierarchy;
            this.typeToken = typeToken;
            this.gsonOverride = gsonOverride;
        }
    }
}
