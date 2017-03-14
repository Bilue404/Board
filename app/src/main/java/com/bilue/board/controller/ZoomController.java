package com.bilue.board.controller;

import com.bilue.board.util.ScreenUtils;

/**
 * Created by bilue on 17/3/13.
 */

public class ZoomController {
    //宽度基准
    public static final float NORM_WIDGET = 720f;
    //高度基准
    public static final float NORM_HEIGHT = 1080f;

    private static float scale = -1;
    

    public static float getScale(){
        if (scale == -1 ){
            float hScale = ScreenUtils.getHeightInPx()/NORM_HEIGHT;
            float wScale = ScreenUtils.getWidthInPx()/NORM_WIDGET;
            scale = hScale>wScale?wScale:hScale;
        }

        return scale;
    }
}
