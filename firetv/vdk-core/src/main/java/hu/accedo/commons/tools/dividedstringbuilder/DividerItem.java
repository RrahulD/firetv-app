/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools.dividedstringbuilder;

import hu.accedo.commons.tools.dividedstringbuilder.DividedStringBuilder.Item;

class DividerItem implements Item {
    public final CharSequence divider;

    DividerItem(CharSequence divider) {
        this.divider = divider;
    }

    @Override
    public CharSequence build() {
        return divider;
    }
}
