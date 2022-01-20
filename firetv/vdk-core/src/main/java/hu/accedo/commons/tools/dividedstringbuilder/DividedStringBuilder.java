/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools.dividedstringbuilder;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * StringBuilder for easily concatenating lists, placing dividers in between them.
 * <p>
 * The general rule of thumb for this class is that dividers should never get placed at the start or the end of the text,
 * and two dividers should never be next to each others.
 * <p>
 * DividedStringBuilder is nullsafe, and also saves you the trouble of ommiting dividers if some items are null from your list.
 * <p>
 * For example:
 * {@literal We want a string like this: "2004 | Action & Horror & Romance"}
 * <p>
 * How we can get that:
 * {@code
 * new DividedStringBuilder()
 * .append(getYearString(asset)) //may be null
 * .divider(" | ")
 * .appendObjects(asset.getCategories(), " & ", new Formatter<Category>() { //asset.getCategories() may be null or empty. Individual listitems may also be null.
 * \@Override
 * public CharSequence format(Category item) {
 * return item.getTitle();
 * }
 * })
 * .build();
 * }
 */
public class DividedStringBuilder {
    private ArrayList<Item> items = new ArrayList<>();

    //Single items

    /**
     * Appends a single CharSequence to the builder.
     *
     * @param charSequence may be null or empty, won't be appended if so.
     * @return this DividedStringBuilder instance for chaining.
     */
    public DividedStringBuilder append(CharSequence charSequence) {
        items.add(new CharSequenceItem(charSequence));
        return this;
    }

    /**
     * Appends a divider CharSequence to the builder. Will only be included in the final built text if it has something to divide on both sides.
     *
     * @param divider maybe be null or empty, will be ignored if so.
     * @return this DividedStringBuilder instance for chaining.
     */
    public DividedStringBuilder divider(CharSequence divider) {
        items.add(new DividerItem(divider));
        return this;
    }

    /**
     * Appends an object to the builder.
     *
     * @param object    the object to add to the final text. Will be formatted with the given provider. May be null, the entry will be ignored then.
     * @param formatter the formatter that tells how the given object should be converted to CharSequence. If null, .toString() will be called on the object.
     * @param <T>       The type of the object.
     * @return this DividedStringBuilder instance for chaining.
     */
    public <T> DividedStringBuilder append(T object, Formatter<T> formatter) {
        items.add(new ObjectItem(object, formatter));
        return this;
    }

    //Lists

    /**
     * Appends a list of CharSequences to the builer.
     *
     * @param charSequences may be null or empty, will be ignored then. May also contain null or empty items, they will be ignored.
     * @return this DividedStringBuilder instance for chaining.
     */
    public DividedStringBuilder appendStrings(List<? extends CharSequence> charSequences) {
        return appendObjects(charSequences, null, null);
    }

    /**
     * Appends a list of CharSequences to the builder with a divider. The divider will only be placed in between the items.
     *
     * @param charSequences may be null or empty, will be ignored then. May also contain null or empty items, they will be ignored.
     * @param divider       maybe be null or empty, will be ignored if so.
     * @return this DividedStringBuilder instance for chaining.
     */
    public DividedStringBuilder appendStrings(List<? extends CharSequence> charSequences, CharSequence divider) {
        return appendObjects(charSequences, divider, null);
    }

    /**
     * Appends a list of objects to the builder. Objects will be added by calling toString() on them.
     *
     * @param objects may be null or empty, will be ignored then. May also contain null or empty items, they will be ignored.
     * @return this DividedStringBuilder instance for chaining.
     */
    public DividedStringBuilder appendObjects(List<? extends Object> objects) {
        return appendObjects(objects, null, null);
    }

    /**
     * @param objects may be null or empty, will be ignored then. May also contain null or empty items, they will be ignored.
     * @param divider maybe be null or empty, will be ignored if so.
     * @return this DividedStringBuilder instance for chaining.
     */
    public DividedStringBuilder appendObjects(List<? extends Object> objects, CharSequence divider) {
        return appendObjects(objects, divider, null);
    }

    /**
     * @param objects   may be null or empty, will be ignored then. May also contain null or empty items, they will be ignored.
     * @param formatter the formatter that tells how the given object should be converted to CharSequence. If null, .toString() will be called on the object.
     * @param <T>       The type of the objects in the list.
     * @return this DividedStringBuilder instance for chaining.
     */
    public <T> DividedStringBuilder appendObjects(List<T> objects, Formatter<T> formatter) {
        return appendObjects(objects, null, formatter);
    }

    /**
     * @param objects   may be null or empty, will be ignored then. May also contain null or empty items, they will be ignored.
     * @param divider   maybe be null or empty, will be ignored if so.
     * @param formatter the formatter that tells how the given object should be converted to CharSequence. If null, .toString() will be called on the object.
     * @param <T>       The type of the objects in the list.
     * @return this DividedStringBuilder instance for chaining.
     */
    public <T> DividedStringBuilder appendObjects(List<T> objects, CharSequence divider, Formatter<T> formatter) {
        this.items.add(new ListItem(objects, divider, formatter));
        return this;
    }

    /**
     * @return the built CharSequence from the given parameters.
     */
    public CharSequence build() {
        SpannableStringBuilder result = new SpannableStringBuilder();

        Item lastAppendedItem = null;
        Item lastDivider = null;

        for (int i = 0; i < items.size(); i++) {
            Item thisItem = items.get(i);

            if (thisItem instanceof DividerItem) {
                if (!TextUtils.isEmpty(thisItem.build())) {
                    lastDivider = thisItem;
                }

            } else {
                CharSequence toAppend = thisItem.build();

                if (!TextUtils.isEmpty(toAppend)) {
                    if (lastDivider != null && !(lastAppendedItem instanceof DividerItem) && result.length() > 0) {
                        result.append(lastDivider.build());
                    }
                    lastDivider = null;
                    result.append(toAppend);
                    lastAppendedItem = thisItem;
                }
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return build().toString();
    }

    /**
     * Tells how instances of the given class should be converted to CharSequence.
     *
     * @param <T> the type of the Objects to format.
     */
    public static interface Formatter<T> {
        /**
         * @param item the object to convert to CharSequence. Will never be null.
         * @return the converted CharSequence. May be null or empty.
         */
        public CharSequence format(T item);
    }

    public static interface Item {
        /**
         * @return the exact CharSequence that we want to append to the final built text. May be null or empty.
         */
        public CharSequence build();
    }
}
