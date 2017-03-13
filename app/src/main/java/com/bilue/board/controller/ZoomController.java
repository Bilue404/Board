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

    private static float wScale = -1;
    private static float hScale = -1;

    public static float getWidthScale(){
        if (wScale == -1 ){
            wScale = ScreenUtils.getWidthInPx()/NORM_WIDGET;
        }
        return wScale;
    }

    public static float getHeightScale(){
        if (hScale == -1){
            hScale = ScreenUtils.getHeightInPx()/NORM_HEIGHT;
        }
        return hScale;
    }


}
