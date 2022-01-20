/*
 * Copyright (c) 2017 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.vson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Util class containing tools to work with Gson's {@link JsonObject} and {@link JsonArray}.
 */
public class JsonTools {
    /**
     * Finds a {@link JsonObject} which contains a given key with a given value, inside a {@link JsonArray}.
     *
     * @param jsonArray  the array to search in.
     * @param fieldName  the name of the field to check.
     * @param fieldValue the value of that we are looking for in the field specified above.
     * @return The JsonObject found, or null.
     */
    public static JsonObject findElement(JsonArray jsonArray, final String fieldName, final String fieldValue) {
        JsonElement jsonElement = findElement(jsonArray, new Matcher() {
            @Override
            public boolean matches(JsonElement jsonElement) {
                return fieldValue.equalsIgnoreCase(jsonElement.getAsJsonObject().get(fieldName).getAsString());
            }
        });

        return jsonElement != null ? jsonElement.getAsJsonObject() : null;
    }

    /**
     * Finds a {@link JsonElement} in a given {@link JsonArray}, that matches the requirements of the given {@link Matcher}.
     *
     * @param jsonArray the array to search in.
     * @param matcher   the matcher to decide if this is the item we're looking for.
     * @return The {@link JsonElement} found, or null.
     */
    public static JsonElement findElement(JsonArray jsonArray, Matcher matcher) {
        if (jsonArray == null) {
            return null;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            if (matcher.matches(jsonArray.get(i))) {
                return jsonArray.get(i);
            }
        }

        return null;
    }

    /**
     * Sorts a {@link JsonArray} with the given comparator.
     *
     * @param jsonArray  the array to sort.
     * @param comparator the {@link Comparator} to use.
     */
    public static void sortArray(JsonArray jsonArray, Comparator<JsonElement> comparator) {
        if (jsonArray == null) {
            return;
        }

        // Turn into list
        ArrayList<JsonElement> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.get(i));
        }

        // Sort
        Collections.sort(list, comparator);

        // Remove all from jsonArray
        for (Iterator<JsonElement> iterator = jsonArray.iterator(); iterator.hasNext();) {
            iterator.next();
            iterator.remove();
        }

        // Put back sorted into jsonArray
        for (JsonElement jsonElement : list) {
            jsonArray.add(jsonElement);
        }
    }

    /**
     * An interface used for providing custom search queries while searching {@link JsonArray}.
     */
    public static interface Matcher {
        /**
         * @param jsonElement the element we're currently looking at in the {@link JsonArray}.
         * @return should return true, if this is the element we're looking for.
         */
        public boolean matches(JsonElement jsonElement);
    }
}
