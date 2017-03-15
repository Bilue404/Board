package com.bilue.board.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by bilue on 17/3/14.
 */

public class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mContext = this;

    }



}
