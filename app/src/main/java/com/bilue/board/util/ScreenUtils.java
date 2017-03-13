package com.bilue.board.util;

import com.bilue.board.BApplication;

/**
 * Created by bilue on 17/3/13.
 */

public class ScreenUtils {
    public static final float getHeightInPx() {
        final float height = BApplication.getApp().getResources().getDisplayMetrics().heightPixels;
        return height;
    }

    public static final float getWidthInPx() {
        final float width = BApplication.getApp().getResources().getDisplayMetrics().widthPixels;
        return width;
    }

    public static final int getHeightInDp() {
        final float height = BApplication.getApp().getResources().getDisplayMetrics().heightPixels;
        int heightInDp = pxTodp(height);
        return heightInDp;
    }

    public static final int getWidthInDp() {
        final float height = BApplication.getApp().getResources().getDisplayMetrics().heightPixels;
        int widthInDp = pxTodp(height);
        return widthInDp;
    }
    /**
     * Convert Pixel to dp 将pixel转换为dp
     */
    public static int pxTodp(float px) {
        final float scale = BApplication.getApp().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}