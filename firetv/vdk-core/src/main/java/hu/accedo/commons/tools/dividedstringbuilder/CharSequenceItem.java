/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools.dividedstringbuilder;

import hu.accedo.commons.tools.dividedstringbuilder.DividedStringBuilder.Item;

class CharSequenceItem implements Item {
    public final CharSequence charSequence;

    public CharSequenceItem(CharSequence charSequence) {
        this.charSequence = charSequence;
    }

    @Override
    public CharSequence build() {
        return charSequence;
    }
}
