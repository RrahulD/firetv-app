/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ColorTools {
    public static Bitmap invertColors(Bitmap bitmap) {
        if (bitmap == null)
            return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i++)
            pixels[i] = invertColor(pixels[i]);

        Bitmap dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        dest.setPixels(pixels, 0, width, 0, 0, width, height);
        return dest;
    }

    public static Bitmap adjustColor(Bitmap bitmap, int color) {
        if (bitmap == null)
            return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i++)
            pixels[i] = ColorTools.multiplyColors(pixels[i], color);

        Bitmap dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        dest.setPixels(pixels, 0, width, 0, 0, width, height);
        return dest;
    }

    public static int invertColor(int src) {
        int r1 = Color.red(src);
        int g1 = Color.green(src);
        int b1 = Color.blue(src);

        int r2 = 255 - r1;
        int g2 = 255 - g1;
        int b2 = 255 - b1;

        return Color.argb(Color.alpha(src), r2, g2, b2);
    }

    public static int multiplyColors(int src, int dst) {
        int r1, g1, b1, r2, g2, b2;

        r1 = Color.red(src);
        g1 = Color.green(src);
        b1 = Color.blue(src);

        r2 = Color.red(dst);
        g2 = Color.green(dst);
        b2 = Color.blue(dst);

        int r3 = (r1 * r2) / 255;
        int g3 = (g1 * g2) / 255;
        int b3 = (b1 * b2) / 255;

        return Color.argb(Color.alpha(src), r3, g3, b3);
    }

    public static int blendColors(int color1, int color2) {
        int a1, r1, g1, b1, a2, r2, g2, b2;

        a1 = Color.alpha(color1);
        r1 = Color.red(color1);
        g1 = Color.green(color1);
        b1 = Color.blue(color1);

        a2 = Color.alpha(color2);
        r2 = Color.red(color2);
        g2 = Color.green(color2);
        b2 = Color.blue(color2);

        int rOut = (r1 * a1 / 255) + (r2 * a2 * (255 - a1) / (255 * 255));
        int gOut = (g1 * a1 / 255) + (g2 * a2 * (255 - a1) / (255 * 255));
        int bOut = (b1 * a1 / 255) + (b2 * a2 * (255 - a1) / (255 * 255));
        int aOut = a1 + (a2 * (255 - a1) / 255);

        return Color.argb(aOut, rOut, gOut, bOut);
    }

    public static int darken(int color, float ratio) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= ratio; // value component
        if (hsv[2] > 1)
            hsv[2] = 1;
        return Color.HSVToColor(hsv);
    }
}
