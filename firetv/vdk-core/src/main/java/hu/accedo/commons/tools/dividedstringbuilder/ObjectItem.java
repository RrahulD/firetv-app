/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools.dividedstringbuilder;

import hu.accedo.commons.tools.dividedstringbuilder.DividedStringBuilder.Formatter;
import hu.accedo.commons.tools.dividedstringbuilder.DividedStringBuilder.Item;

class ObjectItem implements Item {
    public final Object object;
    public final Formatter formatter;

    public ObjectItem(Object object, Formatter formatter) {
        this.object = object;
        this.formatter = formatter;
    }

    @Override
    public CharSequence build() {
        if (object == null) {
            return null;
        }

        if (formatter == null) {
            return object.toString();
        }

        return formatter.format(object);
    }
}
