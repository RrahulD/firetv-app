/*
 * Copyright (c) 2017 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.vson;

import android.text.TextUtils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides path mapping functionality to GSON. With this {@link TypeAdapterFactory}, you can annotate fields like:
 * <p>
 * {@literal
 * >@Path("asset/image/href")
 * >private String imageUrl;
 * <p>
 * Thus creating more flat structures. Also features array indexing and array search. For example:
 * >@Path("asset/images[0]/href")
 * >private String imageUrl; //Contains the first image's url from the images array.
 * <p>
 * >@Path("asset/images[type=teaser]/href")
 * >private String teaserImageUrl; //Contains the first image's url from the images array, that has the type field set to "teaser".
 * }
 */
public class PathAdapterFactory implements TypeAdapterFactory {
    protected static final Pattern PATTERN_ARRAY_INDEX = Pattern.compile("(.+?)\\[([0-9]+?)\\]");
    protected static final Pattern PATTERN_ARRAY_SEARCH = Pattern.compile("(.+?)\\[(.+?)=(.+?)\\]");

    /**
     * Needs to be applied onto the GsonBuilder using PathAdapterFactory
     */
    public static final ExclusionStrategy EXCLUSION_STRATEGY = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(Path.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    };

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
        final TypeAdapter<T> delegateTypeAdapter = gson.getDelegateAdapter(this, type);

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegateTypeAdapter.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                // Null JSON check
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }

                // We only handle objects
                if (in.peek() != JsonToken.BEGIN_OBJECT) {
                    return delegateTypeAdapter.read(in);
                }

                // Check if we should escape if there's are no paths to do
                Map<String, Field> pathFields = getPathFields(type);
                if (pathFields.isEmpty()) {
                    return gson.getDelegateAdapter(PathAdapterFactory.this, type).read(in);
                }

                // Parse JsonObject as well as T instance
                JsonObject jsonObject = gson.getAdapter(JsonObject.class).read(in);
                T instance = gson.getDelegateAdapter(PathAdapterFactory.this, type).fromJsonTree(jsonObject);

                // Iterate fields with "Path" annotation
                for (String path : pathFields.keySet()) {
                    try {
                        // Get the field we want to fill
                        Field field = pathFields.get(path);

                        // Auto fill fieldname if necessary.
                        if (path.endsWith("/")) {
                            path += field.getName();
                        }

                        // Split
                        String[] pathParts = path.split("/");

                        // Travel to the bottom
                        JsonObject traverser = jsonObject;
                        for (int i = 0; i < pathParts.length - 1 && traverser != null; i++) {
                            JsonElement target = traverse(traverser, pathParts[i]);
                            traverser = target != null ? target.getAsJsonObject() : null;
                        }

                        // We're at the bottom, lets see what we found.
                        if (traverser != null) {
                            // JsonElement target = traverser.get(pathParts[pathParts.length - 1]);
                            JsonElement target = traverse(traverser, pathParts[pathParts.length - 1]);

                            if (target != null) {
                                Type fieldType = $Gson$Types.resolve(type.getType(), type.getRawType(), field.getGenericType());
                                Object fieldValue = gson.getAdapter(TypeToken.get(fieldType)).fromJsonTree(target);
                                if (fieldValue != null) {
                                    field.set(instance, fieldValue);
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw new JsonParseException("Failed to parse following path: " + path + " in class " + type.getRawType().getSimpleName(), e);
                    }
                }

                return instance;
            }
        };
    }

    protected JsonElement traverse(JsonObject traverser, String pathElement) {
        Matcher matcherArrayIndex = PATTERN_ARRAY_INDEX.matcher(pathElement);
        Matcher matcherArraySearch = PATTERN_ARRAY_SEARCH.matcher(pathElement);

        if (matcherArrayIndex.find()) {
            // An indexed element from an array. Eg: items[2]
            JsonArray jsonArray = traverser.getAsJsonArray(matcherArrayIndex.group(1));
            int index = Integer.parseInt(matcherArrayIndex.group(2));
            return jsonArray.get(index).getAsJsonObject();

        } else if (matcherArraySearch.find()) {
            // An element from an array, with a given value. Eg: items[name=Timmy]
            JsonArray jsonArray = traverser.getAsJsonArray(matcherArraySearch.group(1));
            return JsonTools.findElement(jsonArray, matcherArraySearch.group(2), matcherArraySearch.group(3));

        } else {
            // A boring old object
            return traverser.get(pathElement);
        }
    }

    protected Map<String, Field> getPathFields(TypeToken<?> type) {
        Class<?> raw = type.getRawType();
        HashMap<String, Field> result = new HashMap<>();

        if (raw.isInterface()) {
            return result;
        }

        Type declaredType = type.getType();
        while (raw != Object.class) {
            Field[] fields = raw.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);

                Path path = field.getAnnotation(Path.class);
                if (path != null && !TextUtils.isEmpty(path.value())) {
                    String pathString = path.value();

                    if (result.containsKey(pathString)) {
                        throw new IllegalArgumentException(declaredType + " declares multiple JSON fields with the path " + pathString);
                    }

                    result.put(pathString, field);
                }
            }
            type = TypeToken.get($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
            raw = type.getRawType();
        }
        return result;
    }

    /**
     * Specifies where the value of the annotated field is in the json tree.
     * Can contain names "/" separated names, and also indexes or items with given attributes can be addressed in arrays. E.g.:
     * <p>
     * "asset/image/href"
     * "asset/images[0]/href"
     * "asset/images[type=teaser]/href"
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Path {
        /**
         * @return the desired name of the field when it is serialized
         */
        String value();
    }
}
