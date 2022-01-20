/*
 * Copyright (c) 2017 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import hu.accedo.commons.VsonTest.PojoOverride.Person;
import hu.accedo.commons.tools.IOUtilsLite;
import hu.accedo.commons.vson.OverrideAdapterFactory.GsonOverride;
import hu.accedo.commons.vson.PathAdapterFactory.Path;
import hu.accedo.commons.vson.PrimitiveAdapterFactory.PrimitiveParser;
import hu.accedo.commons.vson.Vson;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import org.junit.Test;
import org.junit.runner.RunWith;

import static hu.accedo.commons.Shared.getContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class VsonTest {

    // String tests
    @Test
    public void testSimpleText() {
        PojoString pojo = fromJson(PojoString.class);
        assertEquals("Hello World", pojo.helloWorld);
    }

    // Integer tests
    @Test
    public void testGoodInteger() {
        PojoInteger pojo = fromJson(PojoInteger.class);
        assertEquals(123, pojo.goodNumber);
    }

    @Test
    public void testBadInteger() {
        PojoInteger pojo = fromJson(PojoInteger.class);
        assertEquals(666, pojo.badNumber);
    }

    @Test
    public void testNoPrimitiveParserInteger() {
        try {
            new Vson().setPrimitiveAdapterFactoryEnabled(false).toGson().fromJson(getJson(), PojoInteger.class);
            fail();
        } catch (JsonParseException e) {
        }
    }

    // Boolean tests
    @Test
    public void testGoodBoolean() {
        PojoBoolean pojo = fromJson(PojoBoolean.class);
        assertTrue(pojo.goodBoolean);
    }

    @Test
    public void testBadBoolean() {
        PojoBoolean pojo = fromJson(PojoBoolean.class);
        assertFalse(pojo.badBoolean);
    }

    @Test
    public void testCustomBooleanParser() {
        PrimitiveParser<Boolean> customParser = new PrimitiveParser<Boolean>() {
            @Override
            public Boolean parse(String input) {
                return "yup".equalsIgnoreCase(input);
            }
        };

        PojoBoolean pojo = new Vson()
                .addPrimitiveParser(boolean.class, customParser)
                .toGson()
                .fromJson(getJson(), PojoBoolean.class);

        assertTrue(pojo.badBoolean);
    }

    // Overrides
    @Test
    public void testJsonOverride() {
        // Override the ugly "theKeysAreValues" things into an array, making the key a
        // field called "name"
        GsonOverride gsonOverride = new GsonOverride() {
            @Override
            public JsonElement jsonOverride(JsonElement raw) {
                JsonArray output = new JsonArray();

                for (Entry<String, JsonElement> entry : raw.getAsJsonObject().entrySet()) {
                    JsonObject value = entry.getValue().getAsJsonObject();
                    value.addProperty("name", entry.getKey());
                    output.add(value);
                }

                return output;
            }

            @Override
            public TypeToken typeOverride(JsonElement jsonElement, TypeToken typeToken) {
                return typeToken;
            }
        };

        PojoOverride pojoOverride = new Vson()
                .addHierarchyOverride(new TypeToken<List<Person>>() {
                }, gsonOverride)
                .toGson()
                .fromJson(getJson(), PojoOverride.class);
        // Add the override, for the type List<Person>

        // Check results, profit
        assertEquals("Jack", pojoOverride.people.get(0).name);
        assertEquals("Jill", pojoOverride.people.get(1).name);
    }

    // Array paths
    @Test
    public void testArraySearch() {
        PojoArraySearch pojo = fromJson(PojoArraySearch.class);

        assertEquals("Pinky", pojo.catName);
        assertEquals("image", pojo.firstItemType);
        assertEquals("https://c1.staticflickr.com/4/3542/3643545316_0453264f6d.jpg", pojo.firstImageUrl);
    }

    // Util methods
    private <T> T fromJson(Class<T> clazz) {
        return new Vson().toGson().fromJson(getJson(), clazz);
    }

    private String getJson() {
        try {
            return IOUtilsLite.toString(getContext().getClassLoader().getResource("vson_test.json").openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        return null;
    }

    // Pojos
    static class PojoString {
        @Path("unnecessryDepth/yetAnotherUnnecessryDepth/string")
        String helloWorld;
    }

    static class PojoInteger {
        @Path("unnecessryDepth/yetAnotherUnnecessryDepth/") //Slash tells it to add the field name to the end
                int goodNumber;

        @Path("unnecessryDepth/yetAnotherUnnecessryDepth/badNumber") //Name explicitly added to path
                int badNumber = 666; //Default value
    }

    static class PojoBoolean {
        @Path("unnecessryDepth/yetAnotherUnnecessryDepth/goodBoolean")
        boolean goodBoolean;

        @Path("unnecessryDepth/yetAnotherUnnecessryDepth/badBoolean")
        boolean badBoolean = false; // Default value
    }

    static class PojoOverride {
        @Path("unnecessryDepth/yetAnotherUnnecessryDepth/theKeysAreValues")
        List<Person> people;

        static class Person {
            String name;
            String gender;
            String birthday;
        }
    }

    static class PojoArraySearch {
        @Path("unnecessryDepth/yetAnotherUnnecessryDepth/weirdImageArray[type=cat]/name")
        String catName;

        @Path("unnecessryDepth/yetAnotherUnnecessryDepth/weirdImageArray[0]/type")
        String firstItemType;

        @Path("unnecessryDepth/yetAnotherUnnecessryDepth/weirdImageArray[type=image]/image/url/href/target")
        String firstImageUrl;
    }
}
