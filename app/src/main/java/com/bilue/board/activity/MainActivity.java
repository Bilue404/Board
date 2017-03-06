package com.bilue.board.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bilue.board.R;
import com.bilue.board.ui.SvgView;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button creatRoom;
    private Button joinRoom;
    private Button main_creathistory;
    private Button quiteView;
    private Button history;
    private Toolbar mToolbar;
    private LinearLayout logoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }


    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);

        logoView = (LinearLayout) findViewById(R.id.main_logo_root);
        creatRoom = (Button) findViewById(R.id.main_creatroom);
        joinRoom = (Button) findViewById(R.id.main_joinroom);
        history  = (Button) findViewById(R.id.mymain_historyroom);
        main_creathistory = (Button) findViewById(R.id.main_creathistory);
        quiteView = (Button) findViewById(R.id.main_quite);
        LayoutInflater inflater = getLayoutInflater();

        addSvgView(inflater, logoView);
        creatRoom.setOnClickListener(this);
        joinRoom.setOnClickListener(this);
        main_creathistory.setOnClickListener(this);
        history.setOnClickListener(this);
        quiteView.setOnClickListener(this);
    }


    private void addSvgView(LayoutInflater inflater, LinearLayout container)
    {
        final View view = inflater.inflate(R.layout.item_svg, container, false);
        final SvgView svgView = (SvgView) view.findViewById(R.id.svg);

        svgView.setSvgResource(R.raw.board);
        //view.setBackgroundResource(R.color.wheat);
//        svgView.setmCallback(new SvgCompletedCallBack() {
//
//            @Override
//            public void onSvgCompleted() {
//                DoAgainBtn.setEnabled(true);
//            }
//        });

        container.addView(view);
       // svgView.startAnimation();
//        DoAgainBtn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                DoAgainBtn.setEnabled(false);
//                svgView.startAnimation();
//
//            }
//        });
//
        Handler handlerDelay = new Handler();
        handlerDelay.postDelayed(new Runnable(){
            public void run() {
                svgView.startAnimation();
            }}, 500);
    }








    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_creatroom:
                Intent creatIntent = new Intent(MainActivity.this, CreatRoom.class);
                creatIntent.putExtra("path","");
                startActivity(creatIntent);
                break;
            case R.id.main_joinroom:
                Intent joinIntent = new Intent(MainActivity.this, JoinRoom.class);
                startActivity(joinIntent);
                break;
            case R.id.main_creathistory:
                Intent historyIntent = new Intent(MainActivity.this, HistoryListActivity.class);
                startActivity(historyIntent);
                break;
            case R.id.mymain_historyroom:
                Intent is = new Intent(MainActivity.this, NewHistoryListActivity.class);
                startActivity(is);
                break;
            case R.id.main_quite:
                System.exit(0);
                break;

        }

    }


}
