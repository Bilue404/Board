package com.bilue.board.util;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

/**
 * 操作栈 可撤销 可重做
 * Created by Administrator on 2015/8/8.
 */
 public class ControlStack {
    private ArrayList<Bitmap> redoStack;
    private ArrayList<Bitmap> undoStack;
    private final int STACK_SIZE = 11;  //操作栈大小 此处11实际为10  以为最低部一块没用到 撤销时拿倒数第二步出来 必有底 才能保证不出错
    private Bitmap bitmap ; //用于栈中转移数据用
    public ControlStack(){
        redoStack = new ArrayList<>();
        undoStack = new ArrayList<>();
    }

    public void push(Bitmap bitmap){
        Bitmap bt = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        if(undoStack.size()>STACK_SIZE){
            undoStack.remove(0);
        }
        undoStack.add(bt);

    }

    private void push2undo(Bitmap bitmap){

        if(undoStack.size()>STACK_SIZE){
            undoStack.remove(0);
        }
        undoStack.add(bitmap);
    }


    private void pust2redo(Bitmap bitmap){

        if(redoStack.size()>STACK_SIZE){
            redoStack.remove(0);
        }
        redoStack.add(bitmap);
    }



    public Bitmap redo(){
        if(isCanRedo()){
            bitmap = redoStack.get(redoStack.size()-1);
            push2undo(bitmap);
            redoStack.remove(redoStack.size()-1);
            return bitmap;
        }

        return null;
    }


    public Bitmap undo(){
        if (isCanUndo()){
            bitmap = undoStack.get(undoStack.size()-2);
            pust2redo(undoStack.get(undoStack.size()-1));

            undoStack.remove(undoStack.size() - 1);

            return bitmap;
        }
        return null;
    }



    private boolean isCanRedo(){
        return (redoStack.size()>0);
    }

    private boolean isCanUndo(){
        return (undoStack.size()>1);
    }





}
