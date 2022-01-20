/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

public class StringParser {
    /**
     * Tries to parse the given string into an integer.
     * On failure, instead of throwing a NumberFormatException, returns defaultValue.
     *
     * @param input        the String to parse.
     * @param defaultValue the value to return if parsing has failed.
     * @return the parsed input.
     */
    public static int tryParseInt(String input, int defaultValue) {
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Tries to parse the given string into an Integer object.
     * On failure, instead of throwing a NumberFormatException, returns defaultValue.
     *
     * @param input        the String to parse.
     * @param defaultValue the value to return if parsing has failed.
     * @return the parsed input.
     */
    public static Integer tryParseInteger(String input, Integer defaultValue) {
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Tries to parse the given string into a long.
     * On failure, instead of throwing a NumberFormatException, returns defaultValue.
     *
     * @param input        the String to parse.
     * @param defaultValue the value to return if parsing has failed.
     * @return the parsed input.
     */
    public static long tryParseLong(String input, long defaultValue) {
        try {
            return Long.parseLong(input);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Tries to parse the given string into a double.
     * On failure, instead of throwing a NumberFormatException, returns defaultValue.
     *
     * @param input        the String to parse.
     * @param defaultValue the value to return if parsing has failed.
     * @return the parsed input.
     */
    public static double tryParseDouble(String input, double defaultValue) {
        try {
            return Double.parseDouble(input);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Tries to create a new String instance from the given input data and encoding.
     * On failure, instead of throwing a NullPointer or UnsupportedEncodingException, returns defaultValue.
     *
     * @param input        the bytearray to try and create a string from.
     * @param encoding     the encoding to use in the new string.
     * @param defaultValue the value to return if parsing has failed.
     * @return the parsed input.
     */
    public static String tryCreateString(byte[] input, String encoding, String defaultValue) {
        try {
            return new String(input, encoding);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
