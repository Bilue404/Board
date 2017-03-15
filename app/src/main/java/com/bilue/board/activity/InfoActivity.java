package com.bilue.board.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bilue.board.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoActivity extends BaseActivity {

    @BindView(R.id.toolbar_main) Toolbar mToolbar;
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
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        iv = (ImageView) findViewById(R.id.iv);
        Bitmap bm = BitmapFactory.decodeFile(path);
        iv.setImageBitmap(bm);

    }

}
