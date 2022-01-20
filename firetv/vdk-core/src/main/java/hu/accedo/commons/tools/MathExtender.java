/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

/**
 * Contains mathematical operations not present in java.lang.Math
 */
public class MathExtender {
    private static final float FLOAT_MAX_DIFF = .00001f;

    /**
     * Calculates the mathematical modulus, a mod b.
     * <p>
     * The difference between "a % b" (remainder) and "a mod b" is how they behave for negative numbers.
     * <p>
     * -2 mod 5 = 3
     *
     * @param a integer
     * @param b integer
     * @return the matematical modulus, a mod b
     */
    public static int mod(int a, int b) {
        //L.d("a: "+a+" b: "+b+" mod: "+((a % b + b) % b));
        if (b == 0) {
            return 0;
        }

        return (a % b + b) % b;
    }

    /**
     * Calculates the matematical modulus, a mod b.
     * <p>
     * The difference between "a % b" (remainder) and "a mod b" is how they behave for negative numbers.
     * <p>
     * -1.5 mod 5 = 3.5
     *
     * @param a float
     * @param b float
     * @return the mathematical modulus, a mod b
     */
    public static float mod(float a, float b) {
        //L.d("a: "+a+" b: "+b+" mod: "+((a % b + b) % b));
        if (b == 0) {
            return 0;
        }

        return (a % b + b) % b;
    }

    /**
     * Rounds down a value down by the given floor.
     * <p>
     * Eg:
     * floor(24, 5) == 20
     * floor(20, 5) == 20
     * floor(-2, 5) == -5
     * <p>
     * The reason this is better than just dividing integers, is that it works for negative numbers aswell. -2 / 5 == 0
     *
     * @param value   the value to round down
     * @param floorBy the value to round by
     * @return value - value mod floorBy
     */
    public static int floor(int value, int floorBy) {
        return value - MathExtender.mod(value, floorBy);
    }

    /**
     * Rounds up a value by the given ceil.
     * <p>
     * Eg:
     * ceil(24, 5) == 25
     * ceil(20, 5) == 20
     * ceil(-2, 5) == 0
     *
     * @param value  the value to round up
     * @param ceilBy the value to round by
     * @return the closest higher or equal to value dividable number by ceilBy
     */
    public static int ceil(int value, int ceilBy) {
        int mod = MathExtender.mod(value, ceilBy);
        return mod == 0 ? value : value - mod + ceilBy;
    }

    /**
     * Divides value by divBy, rounding down.
     * <p>
     * Eg:
     * divFloor(24, 5) == 4
     * divFloor(20, 5) == 4
     * divFloor(-2, 5) == -1
     *
     * @param value the value to divide
     * @param divBy the value to divide with
     * @return floor(value, divBy) / divBy
     */
    public static int divFloor(int value, int divBy) {
        return floor(value, divBy) / divBy;
    }

    /**
     * Divides value by divBy, rounding up.
     * <p>
     * Eg:
     * divCeil(24, 5) == 5
     * divFloor(20, 5) == 4
     * divFloor(-12, 5) == -2
     *
     * @param value the value to divide
     * @param divBy the value to divide with
     * @return ceil(value, divBy) / divBy
     */
    public static int divCeil(int value, int divBy) {
        return ceil(value, divBy) / divBy;
    }

    /**
     * Picks the number with the smaller absolute value.
     * Useful for picking the number that is closer in distance to a limit.
     *
     * @param a integer
     * @param b integer
     * @return {@code Math.abs(a)<Math.abs(b)? a : b}
     */
    public static int absMin(int a, int b) {
        return Math.abs(a) < Math.abs(b) ? a : b;
    }

    /**
     * Picks the number with the smaller absolute value.
     * Useful for picking the number that is closer in distance to a limit.
     *
     * @param a float
     * @param b float
     * @return {@code Math.abs(a)<Math.abs(b)? a : b}
     */
    public static float absMin(float a, float b) {
        return Math.abs(a) < Math.abs(b) ? a : b;
    }

    /**
     * Picks the number with the bigger absolute value.
     * Useful for picking the number that is further in distance to a limit.
     *
     * @param a integer
     * @param b integer
     * @return {@code Math.abs(a)>Math.abs(b)? a : b}
     */
    public static int absMax(int a, int b) {
        return Math.abs(a) > Math.abs(b) ? a : b;
    }

    /**
     * Picks the number with the bigger absolute value.
     * Useful for picking the number that is further in distance to a limit.
     *
     * @param a float
     * @param b float
     * @return {@code Math.abs(a)>Math.abs(b)? a : b}
     */
    public static float absMax(float a, float b) {
        return Math.abs(a) > Math.abs(b) ? a : b;
    }

    /**
     * Returns true, if the difference of the two floats is smaller than the given delta.
     *
     * @param a float
     * @param b float
     * @return Math.abs(a - b) < maximumDelta
     */
    public static boolean floatEquals(float a, float b, float maximumDelta) {
        return Math.abs(a - b) < maximumDelta;
    }

    /**
     * Returns true if the two floats are almost equal. Can be used instead of the otherwise not recommended ==.
     *
     * @param a float
     * @param b float
     * @return Math.abs(a - b) < .00001f;
     */
    public static boolean floatEquals(float a, float b) {
        return floatEquals(a, b, FLOAT_MAX_DIFF);
    }

    /**
     * @param startA int start value for range A, it should be lower than end
     * @param endA   int end value for range A
     * @param startB int start value for range A, it should be lower than end
     * @param endB   int end value for range B
     * @return true if the two ranges (A, B) overlapped at least partially. Returns false if there is a gap between range A and range B.
     */
    public static boolean isRangeOverlapping(int startA, int endA, int startB, int endB) {
        return !(endB < startA || endA < startB);
    }

    /**
     * @param startA int start value for range A, it should be lower than end
     * @param endA   int end value for range A
     * @param startB int start value for range A, it should be lower than end
     * @param endB   int end value for range B
     * @return true if the range A includes B, so range B is between startA and endA.
     */
    public static boolean isRangeInsideOf(int startA, int endA, int startB, int endB) {
        return startA <= startB && endB <= endA;
    }

}
