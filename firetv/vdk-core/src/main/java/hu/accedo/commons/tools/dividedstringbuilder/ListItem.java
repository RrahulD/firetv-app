/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools.dividedstringbuilder;

import android.text.TextUtils;

import java.util.List;

import hu.accedo.commons.tools.dividedstringbuilder.DividedStringBuilder.Formatter;
import hu.accedo.commons.tools.dividedstringbuilder.DividedStringBuilder.Item;

class ListItem implements Item {
    public final CharSequence divider;
    public final List list;
    public final Formatter formatter;

    ListItem(List list, CharSequence divider, Formatter formatter) {
        this.divider = divider;
        this.list = list;
        this.formatter = formatter;
    }

    @Override
    public CharSequence build() {
        StringBuilder result = new StringBuilder();

        if (list == null) {
            return result.toString();
        }

        for (int i = 0; i < list.size(); i++) {
            Object listItem = list.get(i);

            if (listItem != null) {
                // Get string to append
                CharSequence toAppend = formatter != null ? formatter.format(listItem) : listItem.toString();

                // Append if necessary
                if (!TextUtils.isEmpty(toAppend)) {
                    // Add divider if necessary
                    if (result.length() > 0 && !TextUtils.isEmpty(divider)) {
                        result.append(divider);
                    }

                    // Append string
                    result.append(toAppend);
                }
            }
        }

        return result.toString();
    }
}
