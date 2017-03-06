package com.bilue.board.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bilue.board.R;

public class InfoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private String path;
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        path = getIntent().getExtras().getString("path");
        init();;
    }


    private void init(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        iv = (ImageView) findViewById(R.id.iv);
        Bitmap bm = BitmapFactory.decodeFile(path);
        iv.setImageBitmap(bm);

    }

}
