/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.typography;

import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import java.util.ArrayList;

/**
 * TextView Spannable builder, where you can alter the typeface, color, and size of just parts of a given text.
 * If you add 2 strings parts to the builder, and two colors, the first stringpart will get the first, the second part will get the second color.
 */
public class FormattedTextBuilder {
    private ArrayList<String> strings = new ArrayList<String>();
    private ArrayList<Typeface> typefaces = new ArrayList<Typeface>();
    private ArrayList<Integer> colors = new ArrayList<Integer>();
    private ArrayList<Float> relativesizes = new ArrayList<Float>();
    private String separator;
    private boolean isHtml = true;

    /**
     * @param strings the text parts in order, to add to the builder
     * @return this instance for method chaining
     */
    public FormattedTextBuilder addStrings(String... strings) {
        if (strings != null)
            for (int i = 0; i < strings.length; i++)
                this.strings.add(strings[i]);
        return this;
    }

    /**
     * @param typefaces the typefaces in order, to add to the builder
     * @return this instance for method chaining
     */
    public FormattedTextBuilder addTypefaces(Typeface... typefaces) {
        if (typefaces != null)
            for (int i = 0; i < typefaces.length; i++)
                this.typefaces.add(typefaces[i]);
        return this;
    }

    /**
     * @param colors the colors in order, to add to the builder
     * @return this instance for method chaining
     */
    public FormattedTextBuilder addColors(Integer... colors) {
        if (colors != null)
            for (int i = 0; i < colors.length; i++)
                this.colors.add(colors[i]);
        return this;
    }

    /**
     * Example usage: to have a smallLARGEsmall text, you'd need the following relative-sizes: 1f, 2f, .5f
     * Meaning: Normal size, Twice the size of the last block, Half the size of the last block (half the twice = normal size)
     *
     * @param relativesizes the sizes in float percentage, compared to the last part's size.
     * @return this instance for method chaining
     */
    public FormattedTextBuilder addRelativeSizes(Float... relativesizes) {
        if (relativesizes != null)
            for (int i = 0; i < relativesizes.length; i++)
                this.relativesizes.add(relativesizes[i]);
        return this;
    }

    /**
     * @param separator the separator the builder will put between all of the stringparts.
     * @return this instance for method chaining
     */
    public FormattedTextBuilder setSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    /**
     * @param isHtml if true, the builder will call Html.fromHtml(string) on each of the stringparts.
     * @return this instance for method chaining
     */
    public FormattedTextBuilder setHtml(boolean isHtml) {
        this.isHtml = isHtml;
        return this;
    }

    /**
     * @return the formatted text, ready to be placed inside a TextView
     */
    public SpannableStringBuilder getFormattedText() {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        float proportion = 1f;
        for (int i = 0; i < strings.size(); i++) {
            String string = strings.get(i);

            if (string == null)
                string = "";

            if (separator != null && i != strings.size() - 1)
                string += separator;

            int start = ssb.length();
            ssb.append(isHtml ? Html.fromHtml(string) : string);

            // Size
            if (relativesizes != null && i < relativesizes.size() && relativesizes.get(i) != null) {
                ssb.setSpan(new RelativeSizeSpan(relativesizes.get(i) / proportion), start, ssb.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                proportion = relativesizes.get(i);
            }

            // Typeface
            if (typefaces != null && i < typefaces.size() && typefaces.get(i) != null)
                ssb.setSpan(new CustomTypefaceSpan(typefaces.get(i)), start, ssb.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            // Color
            if (colors != null && i < colors.size() && colors.get(i) != null)
                ssb.setSpan(new ForegroundColorSpan(colors.get(i)), start, ssb.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return ssb;
    }
}
