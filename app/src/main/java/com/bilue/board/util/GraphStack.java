package com.bilue.board.util;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/17.
 */
public class GraphStack {
    private ArrayList<Bitmap> bitmaps;

    public GraphStack(){
        bitmaps = new ArrayList<>();
    }

    public GraphStack(ArrayList<Bitmap> bitmaps){
        this.bitmaps = bitmaps;
    }

    public void refresh(Bitmap bitmap,int position){
        Bitmap bt = bitmap.copy(Bitmap.Config.ARGB_8888,true);

        bitmaps.set(position-1,bt);

    }

    public void push(Bitmap bitmap,int position){
        Bitmap bt = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        bitmaps.add(position, bt);
    }

    public Bitmap last(int position){
        if(bitmaps.size()>2){
            return bitmaps.get(position-2);

        }
        else{
            return bitmaps.get(0);
        }

    }

    public Bitmap next(int position){
        if(position<bitmaps.size()-1){
            return bitmaps.get(position);

        }
        else{
            return bitmaps.get(bitmaps.size()-1);
        }

    }


    public Bitmap delect(int position){
        if(bitmaps.size()>1){
            bitmaps.remove(position);

        }
        return bitmaps.get(bitmaps.size()-1);
    }


    public int getCount(){
        return bitmaps.size();
    }


    public ArrayList<Bitmap> getBitmaps(){
        return bitmaps;
    }

}
