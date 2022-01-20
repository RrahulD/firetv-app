/*
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.tools;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout;

public class ComponentTools {
    public static boolean isRtl(View view) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            return view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        } else {
            return isRtl(view.getContext());
        }
    }

    public static boolean isRtl(Context context) {
        Configuration config = context.getResources().getConfiguration();
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1 && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            return true;
        }
        return false;
    }

    public static void measureAndRun(final View viewToMeasure, final Runnable runnable) {
        if (viewToMeasure.getMeasuredHeight() != 0 || viewToMeasure.getMeasuredWidth() != 0) {
            runnable.run();
        } else {
            viewToMeasure.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    viewToMeasure.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    runnable.run();
                }
            });
        }
    }

    public static boolean isVisibleInParent(View parent, View child) {
        if (child == null || parent == null)
            return false;

        final Rect parentBounds = new Rect();
        parent.getHitRect(parentBounds);

        final int i[] = new int[2];
        child.getLocationOnScreen(i);

        int childX = i[0];
        int childY = i[1];

        return parentBounds.left < childX && childX < parentBounds.right && parentBounds.top < childY && childY < parentBounds.bottom;
    }

    public static interface OnSizeListener {
        public void onSize(View view, int width, int height);
    }

    public static void getMeasuredSize(final View view, final OnSizeListener onSizeListener) {
        if (view == null) {
            onSizeListener.onSize(view, 0, 0);
        } else if (view.getMeasuredWidth() != 0 || view.getMeasuredHeight() != 0) {
            onSizeListener.onSize(view, view.getMeasuredWidth(), view.getMeasuredHeight());
        } else {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    onSizeListener.onSize(view, view.getMeasuredWidth(), view.getMeasuredHeight());
                }
            });
        }
    }

    public static void getMeasuredSize(final View view, final OnSizeListener onSizeListener, final boolean waitForNotNull) {
        if (view == null) {
            onSizeListener.onSize(view, 0, 0);
        } else if (view.getMeasuredWidth() != 0 || view.getMeasuredHeight() != 0) {
            onSizeListener.onSize(view, view.getMeasuredWidth(), view.getMeasuredHeight());
        } else {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    if (!waitForNotNull || view.getMeasuredHeight() != 0) {
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        onSizeListener.onSize(view, view.getMeasuredWidth(), view.getMeasuredHeight());
                    }
                }
            });
        }
    }

    public static interface OnSizeAndVisibilityListener {
        public void onSizeAndVisibility(View parent, View view, int width, int height, boolean visible);
    }

    public static void getMeasuredSizeIfVisible(final View parent, final View view, final OnSizeAndVisibilityListener onSizeAndVisibilityListener) {
        if (view == null) {
            onSizeAndVisibilityListener.onSizeAndVisibility(parent, view, 0, 0, false);

        } else if (parent != null && !isVisibleInParent(parent, view)) {
            onSizeAndVisibilityListener.onSizeAndVisibility(parent, view, view.getMeasuredWidth(), view.getMeasuredHeight(), false);

        } else if (view.getMeasuredWidth() != 0 || view.getMeasuredHeight() != 0) {
            onSizeAndVisibilityListener.onSizeAndVisibility(parent, view, view.getMeasuredWidth(), view.getMeasuredHeight(), true);

        } else {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    onSizeAndVisibilityListener.onSizeAndVisibility(parent, view, view.getMeasuredWidth(), view.getMeasuredHeight(), true);
                }
            });
        }
    }

    public static Pair<Integer, Integer> getGridCellSize(Activity activity, float aspect, int numColumns, int gridWidth) {
        int grid_column_width = (int) Math.ceil((float) gridWidth / numColumns);
        int grid_row_height = (int) ((float) grid_column_width / aspect);

        return new Pair<Integer, Integer>(grid_column_width, grid_row_height);
    }

    public static Pair<Integer, Integer> getGridCellSize(Activity activity, float aspect, int numColumns) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getGridCellSize(activity, aspect, numColumns, size.x);
    }

    public static void setViewAndChildrenClickable(View view, boolean clickable) {
        // Recursive setClickable
        view.setClickable(clickable);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++)
                setViewAndChildrenClickable(viewGroup.getChildAt(i), clickable);
        }
    }

    public static Activity getActivityFromContext(Context context) {
        while (context != null && !(context instanceof Activity) && (context instanceof ContextWrapper))
            context = ((ContextWrapper) context).getBaseContext();

        if (context != null)
            return (Activity) context;
        else
            return null;
    }

    public static ViewGroup getParentView(View view) {
        if (view == null)
            return null;

        ViewParent parent = view.getParent();

        while (parent != null && !(parent instanceof ViewGroup))
            parent = parent.getParent();

        if (parent instanceof ViewGroup)
            return (ViewGroup) parent;

        return null;
    }

    public static void addRule(View view, int verb, int anchor) {
        RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) view.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT; // TODO necessary?
        params.addRule(verb, anchor);
        view.setLayoutParams(params);
    }

    public static void addRule(View view, int verb) {
        RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) view.getLayoutParams();
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT; // TODO necessary?
        params.addRule(verb);
        view.setLayoutParams(params);
    }

    public static void setOverscrollColor(Context context, int color) {
        try {
            int edgeDrawableId = context.getResources().getIdentifier("overscroll_edge", "drawable", "android");
            int glowDrawableId = context.getResources().getIdentifier("overscroll_glow", "drawable", "android");
            Drawable androidedge = context.getResources().getDrawable(edgeDrawableId);
            Drawable androidGlow = context.getResources().getDrawable(glowDrawableId);
            androidedge.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            androidGlow.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        } catch (Exception ignored) {
            // This is a dirty hack. Have to be careful
        }
    }
}
