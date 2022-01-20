/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import hu.accedo.commons.tools.MathExtender;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MathExtenderTest {
    private static final float FLOAT_MAX_DIFF = .00001f;

    // --- int mod(int a, int b) ---

    @Test
    public void testModPositive() {
        assertEquals(MathExtender.mod(3, 10), 3);
    }

    @Test
    public void testModNegative() {
        assertEquals(MathExtender.mod(-2, 5), 3);
    }

    // --- float mod(float a, float b) ---
    @Test
    public void testFloatModPositive() {
        assertTrue(MathExtender.floatEquals(MathExtender.mod(11.27f, 10f), 1.27f));
    }

    @Test
    public void testFloatModNegative() {
        assertTrue(MathExtender.floatEquals(MathExtender.mod(-1.5f, 5f), 3.5f));
    }

    // --- int floor(int value, int floorBy) ---

    @Test
    public void testFloorPositive() {
        assertEquals(MathExtender.floor(24, 5), 20);
    }

    @Test
    public void testFloorZero() {
        assertEquals(MathExtender.floor(20, 5), 20);
    }

    @Test
    public void testFloorNegative() {
        assertEquals(MathExtender.floor(-2, 5), -5);
    }

    // --- int ceil(int value, int ceilBy) ---

    @Test
    public void testCeilPositive() {
        assertEquals(MathExtender.ceil(24, 5), 25);
    }

    @Test
    public void testCeilZero() {
        assertEquals(MathExtender.ceil(20, 5), 20);
    }

    @Test
    public void testCeilNegative() {
        assertEquals(MathExtender.ceil(-2, 5), 0);
    }

    // --- int divFloor(int value, int divBy) ---

    @Test
    public void testDivFloorPositive() {
        assertEquals(MathExtender.divFloor(24, 5), 4);
    }

    @Test
    public void testDivFloorZero() {
        assertEquals(MathExtender.divFloor(20, 5), 4);
    }

    @Test
    public void testDivFloorNegative() {
        assertEquals(MathExtender.divFloor(-2, 5), -1);
    }

    // --- int divCeil(int value, int divBy) ---

    @Test
    public void testDivCeilPositive() {
        assertEquals(MathExtender.divCeil(24, 5), 5);
    }

    @Test
    public void testDivCeilZero() {
        assertEquals(MathExtender.divCeil(20, 5), 4);
    }

    @Test
    public void testDivCeilNegative() {
        assertEquals(MathExtender.divCeil(-12, 5), -2);
    }

    // --- int absMin(int a, int b) ---

    @Test
    public void testAbsMinPositive() {
        assertEquals(MathExtender.absMin(24, 5), 5);
    }

    @Test
    public void testAbsMinEquals() {
        assertEquals(MathExtender.absMin(-24, -24), -24);
    }

    @Test
    public void testAbsMinNegative() {
        assertEquals(MathExtender.absMin(-24, -5), -5);
    }

    // --- float absMin(float a, float b) ---

    @Test
    public void testFloatAbsMinPositive() {
        assertEquals(MathExtender.absMin(24.3f, 5.1f), 5.1f, FLOAT_MAX_DIFF);
    }

    @Test
    public void testFloatAbsMinEquals() {
        assertEquals(MathExtender.absMin(-24.3f, -24.3f), -24.3f, FLOAT_MAX_DIFF);
    }

    @Test
    public void testFloatAbsMinNegative() {
        assertEquals(MathExtender.absMin(-24f, -5f), -5f, FLOAT_MAX_DIFF);
    }

    // --- int absMax(int a, int b) ---

    @Test
    public void testAbsMaxPositive() {
        assertEquals(MathExtender.absMax(24, 5), 24);
    }

    @Test
    public void testAbsMaxEquals() {
        assertEquals(MathExtender.absMax(-24, -24), -24);
    }

    @Test
    public void testAbsMaxNegative() {
        assertEquals(MathExtender.absMax(-24, -5), -24);
    }

    // --- float absMax(float a, float b) ---

    @Test
    public void testFloatAbsMaxPositive() {
        assertEquals(MathExtender.absMax(24.3f, 5.1f), 24.3f, FLOAT_MAX_DIFF);
    }

    @Test
    public void testFloatAbsMaxEquals() {
        assertEquals(MathExtender.absMax(-24.3f, -24.3f), -24.3f, FLOAT_MAX_DIFF);
    }

    @Test
    public void testFloatAbsMaxNegative() {
        assertEquals(MathExtender.absMax(-24f, -5f), -24f, FLOAT_MAX_DIFF);
    }

    @Test
    public void testFloatEquals() {
        assertTrue(MathExtender.floatEquals(1f, 1.000001f));
        assertFalse(MathExtender.floatEquals(1f, 1.01f));
    }

    // --- boolean isRangeOverlapping(int startA, int endA, int startB, int endB) {

    @Test
    public void testRangeOverlapping() {
        // B is between A
        assertTrue(MathExtender.isRangeOverlapping(100, 200, 120, 160));
        // B partially lower than A, partially overlaps
        assertTrue(MathExtender.isRangeOverlapping(100, 200, 80, 120));
        // B partially greater than A, partially overlaps
        assertTrue(MathExtender.isRangeOverlapping(100, 200, 160, 240));
        // B fully lower than A, no overlap
        assertFalse(MathExtender.isRangeOverlapping(100, 200, 40, 80));
        // B fully greater than A, no overlap
        assertFalse(MathExtender.isRangeOverlapping(100, 200, 240, 280));
        // A is between B
        assertTrue(MathExtender.isRangeOverlapping(100, 200, 80, 280));
        // A = B
        assertTrue(MathExtender.isRangeOverlapping(100, 200, 100, 200));
        // A start = B start, but B is smaller
        assertTrue(MathExtender.isRangeOverlapping(100, 200, 100, 160));
        // A start = B start, but B is larger
        assertTrue(MathExtender.isRangeOverlapping(100, 200, 100, 240));
        // B is between A - negative
        assertTrue(MathExtender.isRangeOverlapping(-200, -100, -160, -120));
    }

    // --- boolean isRangeInsideOf(int startA, int endA, int startB, int endB) {
    @Test
    public void testRangeInsideOf() {
        // B is between A
        assertTrue(MathExtender.isRangeInsideOf(100, 200, 120, 160));
        // B partially lower than A, partially overlaps so it's not fully inside
        assertFalse(MathExtender.isRangeInsideOf(100, 200, 80, 120));
        // B partially greater than A, partially overlaps so it's not fully inside
        assertFalse(MathExtender.isRangeInsideOf(100, 200, 160, 240));
        // B fully lower than A, no overlap
        assertFalse(MathExtender.isRangeInsideOf(100, 200, 40, 80));
        // B fully greater than A, no overlap
        assertFalse(MathExtender.isRangeInsideOf(100, 200, 240, 280));
        // A is between B, but because of start and end are out of A, it should be false
        assertFalse(MathExtender.isRangeInsideOf(100, 200, 80, 280));
        // A = B
        assertTrue(MathExtender.isRangeInsideOf(100, 200, 100, 200));
        // A start = B start, but B is smaller
        assertTrue(MathExtender.isRangeInsideOf(100, 200, 100, 160));
        // A start = B start, but B is larger
        assertFalse(MathExtender.isRangeInsideOf(100, 200, 100, 240));
        // B is between A - negative
        assertTrue(MathExtender.isRangeInsideOf(-200, -100, -160, -120));
    }
}
