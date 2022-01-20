/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import hu.accedo.commons.tools.dividedstringbuilder.DividedStringBuilder;
import hu.accedo.commons.tools.dividedstringbuilder.DividedStringBuilder.Formatter;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DividedStringBuilderTest {
    @Test
    public void testDivider() {
        CharSequence result = new DividedStringBuilder()
                .append("Hello")
                .divider(" ")
                .append("world!")
                .build();

        assertEquals("Hello world!", result.toString());
    }

    @Test
    public void testNullDivider() {
        CharSequence result = new DividedStringBuilder()
                .append("Hello")
                .divider(null)
                .append("world!")
                .build();

        assertEquals("Helloworld!", result.toString());
    }

    @Test
    public void testNullStart() {
        CharSequence result = new DividedStringBuilder()
                .append(null)
                .divider(" ")
                .append("world!")
                .build();

        assertEquals("world!", result.toString());
    }

    @Test
    public void testNullEnd() {
        CharSequence result = new DividedStringBuilder()
                .append("Hello")
                .divider(" ")
                .append(null)
                .build();

        assertEquals("Hello", result.toString());
    }

    @Test
    public void testNullAll() {
        CharSequence result = new DividedStringBuilder()
                .append(null)
                .divider(null)
                .append(null)
                .build();

        assertEquals("", result.toString());
    }

    @Test
    public void testNullStartDivider() {
        CharSequence result = new DividedStringBuilder()
                .append(null)
                .divider(null)
                .append("world!")
                .build();

        assertEquals("world!", result.toString());
    }

    @Test
    public void testEmptyStringStart() {
        CharSequence result = new DividedStringBuilder()
                .append("Hello")
                .divider(" ")
                .append("")
                .divider(" ")
                .append("world!")
                .build();

        assertEquals("Hello world!", result.toString());
    }

    @Test
    public void testObjectDivider() {
        CharSequence result = new DividedStringBuilder()
                .append(new TestClass("Hello"), new Formatter<TestClass>() {
                    @Override
                    public String format(TestClass item) {
                        return item.data;
                    }
                })
                .divider(" ")
                .append("world!")
                .build();

        assertEquals("Hello world!", result.toString());
    }

    @Test
    public void testObjectDividerNoFormatterStart() {
        CharSequence result = new DividedStringBuilder()
                .append(new TestClass("Hello"), null)
                .divider(" ")
                .append("world!")
                .build();

        assertEquals("super.toString() world!", result.toString());
    }

    @Test
    public void testObjectDividerNoFormatterEnd() {
        CharSequence result = new DividedStringBuilder()
                .append("Hello")
                .divider(" ")
                .append(new TestClass("world!"), null)
                .build();

        assertEquals("Hello super.toString()", result.toString());
    }

    @Test
    public void testObjectDividerNoFormatterStartEnd() {
        CharSequence result = new DividedStringBuilder()
                .append(new TestClass("world!"), null)
                .divider(" ")
                .append(new TestClass("world!"), null)
                .build();

        assertEquals("super.toString() super.toString()", result.toString());
    }

    @Test
    public void testNullObjectDivider() {
        CharSequence result = new DividedStringBuilder()
                .append(null, new Formatter<TestClass>() {
                    @Override
                    public String format(TestClass item) {
                        return item.toString();
                    }
                })
                .divider(" ")
                .append("world!")
                .build();

        assertEquals("world!", result.toString());
    }

    @Test
    public void testStringList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Hello");
        list.add(" ");
        list.add("world!");

        CharSequence result = new DividedStringBuilder().appendStrings(list).build();

        assertEquals(result.toString(), "Hello world!");
    }

    @Test
    public void testStringListDivider() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Hello");
        list.add("world!");

        CharSequence result = new DividedStringBuilder().appendStrings(list, " ").build();

        assertEquals(result.toString(), "Hello world!");
    }

    @Test
    public void testStringListNullDivider() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Hello");
        list.add(" ");
        list.add("world!");

        CharSequence result = new DividedStringBuilder().appendStrings(list, null).build();

        assertEquals(result.toString(), "Hello world!");
    }

    @Test
    public void testStringListNullItems() {
        ArrayList<String> list = new ArrayList<>();
        list.add(null);
        list.add("world!");

        CharSequence result = new DividedStringBuilder().appendStrings(list, " ").build();

        assertEquals(result.toString(), "world!");
    }

    @Test
    public void testObjectList() {
        ArrayList<TestClass> list = new ArrayList<>();
        list.add(new TestClass("Hello"));
        list.add(null);
        list.add(new TestClass("you"));
        list.add(null);
        list.add(null);
        list.add(new TestClass("wonderful"));
        list.add(new TestClass("world!"));

        CharSequence result = new DividedStringBuilder()
                .appendObjects(list, new Formatter<TestClass>() {
                    @Override
                    public String format(TestClass item) {
                        return item.data;
                    }
                })
                .build();

        assertEquals(result.toString(), "Helloyouwonderfulworld!");
    }

    @Test
    public void testObjectListDivider() {
        ArrayList<TestClass> list = new ArrayList<>();
        list.add(new TestClass("Hello"));
        list.add(null);
        list.add(new TestClass("you"));
        list.add(null);
        list.add(null);
        list.add(new TestClass("wonderful"));
        list.add(new TestClass("world!"));

        CharSequence result = new DividedStringBuilder()
                .appendObjects(list, " ", new Formatter<TestClass>() {
                    @Override
                    public String format(TestClass item) {
                        return item.data;
                    }
                })
                .build();

        assertEquals(result.toString(), "Hello you wonderful world!");
    }

    @Test
    public void testNullStartObjectListDivider() {
        ArrayList<TestClass> list = new ArrayList<>();
        list.add(null);
        list.add(new TestClass("Hello"));
        list.add(new TestClass("you"));
        list.add(null);
        list.add(null);
        list.add(new TestClass("wonderful"));
        list.add(new TestClass("world!"));

        CharSequence result = new DividedStringBuilder()
                .appendObjects(list, " ", new Formatter<TestClass>() {
                    @Override
                    public String format(TestClass item) {
                        return item.data;
                    }
                })
                .build();

        assertEquals(result.toString(), "Hello you wonderful world!");
    }

    @Test
    public void testMixedObjectListDivider() {
        ArrayList<TestClass> list = new ArrayList<>();
        list.add(null);
        list.add(new TestClass("you"));
        list.add(null);
        list.add(null);
        list.add(new TestClass("wonderful"));

        CharSequence result = new DividedStringBuilder()
                .append("Hello")
                .divider(" ")
                .appendObjects(list, " ", new Formatter<TestClass>() {
                    @Override
                    public String format(TestClass item) {
                        return item.data;
                    }
                })
                .divider(" ")
                .append("world!")
                .build();

        assertEquals(result.toString(), "Hello you wonderful world!");
    }

    @Test
    public void testEmptytListDivider() {
        ArrayList<TestClass> list = new ArrayList<>();
        list.add(null);
        list.add(null);
        list.add(null);

        CharSequence result = new DividedStringBuilder()
                .append("Hello")
                .divider("-")
                .appendObjects(list, " ", new Formatter<TestClass>() {
                    @Override
                    public String format(TestClass item) {
                        return item.data;
                    }
                })
                .divider(" ")
                .append("world!")
                .build();

        assertEquals(result.toString(), "Hello world!");
    }

    @Test
    public void testMultiDivider() {
        CharSequence result = new DividedStringBuilder()
                .append("Hello")
                .divider("+")
                .divider("-")
                .divider("8==D")
                .divider(" ")
                .append("world!")
                .build();

        assertEquals(result.toString(), "Hello world!");
    }

    @Test
    public void testMultiDividerList() {
        ArrayList<TestClass> list = new ArrayList<>();
        list.add(null);
        list.add(null);
        list.add(new TestClass("you"));
        list.add(null);
        list.add(null);
        list.add(new TestClass("wonderful"));

        CharSequence result = new DividedStringBuilder()
                .append("Hello")
                .divider("+")
                .divider("-")
                .divider("8==D")
                .divider(" ")
                .appendObjects(list, " ", new Formatter<TestClass>() {
                    @Override
                    public String format(TestClass item) {
                        return item.data;
                    }
                })
                .divider(" ")
                .append("world!")
                .build();

        assertEquals(result.toString(), "Hello you wonderful world!");
    }

    private static class TestClass {
        public final String data;

        public TestClass(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "super.toString()";
        }
    }
}
