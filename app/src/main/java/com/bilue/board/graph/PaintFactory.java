package com.bilue.board.graph;

import com.bilue.board.constant.Engine;

/**
 * Created by bilue on 17/3/15.
 */

public class PaintFactory {

    public  BasePaint creatPaint(int paintStyle,float paintSize,int paintColor,String paintText){
        BasePaint paint = null;
        switch (paintStyle) {
            case Engine.PEN_TOOL: // 铅笔
                paint = new PenPaint(paintSize, paintColor);
                break;

            case Engine.CIRCLECT_TOOL:
                paint = new CirclectlPaint(paintSize, paintColor);
                break;
            case Engine.LINE_TOOL:
                paint = new LinePaint(paintSize,paintColor);
                break;
            case Engine.RECTU_TOOL:
                paint = new RectuPaint(paintSize,paintColor);
                break;
            case Engine.ERASER_TOOL:
                paint = new EraserPaint(paintSize);
                break;
            case Engine.ARROW_TOOL:
                paint = new ArrowPaint(paintSize,paintColor);
                break;
            case Engine.TEXT_TOOL:
                paint = new TextPaint(paintSize,paintColor,paintText);
                break;
            default:
                paint = new PenPaint(Engine.DEFAULT_SIZE, Engine.DEFAULT_COLOR); //最后的容错处理
                break;
        }
        return paint;
    }

}
