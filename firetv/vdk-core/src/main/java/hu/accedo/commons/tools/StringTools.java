/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

import java.util.List;

/**
 * @deprecated use DividedStringBuilder instead
 */
@Deprecated
public class StringTools {
    /**
     * Joins the list with the given separator. For example: {"apple", "pear"} joined by "," is "apple,pear"
     *
     * @param list      the list to join the items of, using the items' .toString() method. If the list is null or empty, an empty string will be returned.
     * @param separator the divider to use. If the separator is null, the items will be concatenated without a divider.
     * @return the joint string, or an empty string if the list was null or empty.
     */
    public static String join(List<? extends Object> list, String separator) {
        StringBuilder stringBuilder = new StringBuilder();

        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                stringBuilder.append(list.get(i));

                if (i < list.size() - 1 && separator != null) {
                    stringBuilder.append(separator);
                }
            }
        }

        return stringBuilder.toString();
    }
}
