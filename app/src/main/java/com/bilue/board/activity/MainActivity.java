package com.bilue.board.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bilue.board.R;
import com.bilue.board.ui.SvgView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity{


    @BindView( R.id.btn_creat_room ) Button btnCreatRoom;
    @BindView( R.id.btn_join_room ) Button btnJoinRoom;
    @BindView( R.id.btn_creat_history ) Button btnCreatHistory;
    @BindView(R.id.btn_quite) Button btnQuite;
    @BindView(R.id.btn_history_room) Button btnHistoryRoom;
    private Toolbar mToolbar;
    @BindView(R.id.ll_logo_root) LinearLayout llLogoRoot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();

    }


    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);

//        llLogoRoot = (LinearLayout) findViewById(R.id.main_logo_root);
//        btnCreatRoom = (Button) findViewById(R.id.main_creatroom);
//        btnJoinRoom = (Button) findViewById(R.id.main_joinroom);
//        btnHistoryRoom  = (Button) findViewById(R.id.mymain_historyroom);
//        btnCreatHistory = (Button) findViewById(R.id.main_creathistory);
//        btnQuite = (Button) findViewById(R.id.main_quite);
        LayoutInflater inflater = getLayoutInflater();

        addSvgView(inflater, llLogoRoot);
//        btnCreatRoom.setOnClickListener(this);
//        btnJoinRoom.setOnClickListener(this);
//        btnCreatHistory.setOnClickListener(this);
//        btnHistoryRoom.setOnClickListener(this);
//        btnQuite.setOnClickListener(this);
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



    @OnClick(R.id.btn_creat_room)
    public void creatRoom(){
        Intent creatIntent = new Intent(MainActivity.this, CreatRoom.class);
        creatIntent.putExtra("path","");
        startActivity(creatIntent);
    }

    @OnClick(R.id.btn_join_room)
    public void joinRoom(){
        Intent joinIntent = new Intent(MainActivity.this, JoinRoom.class);
        startActivity(joinIntent);
    }

    @OnClick(R.id.btn_creat_history)
    public void creatHistory(){
        Intent historyIntent = new Intent(MainActivity.this, HistoryListActivity.class);
        startActivity(historyIntent);
    }
    @OnClick(R.id.btn_quite)
    public void quite(){
        System.exit(0);
    }


//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.main_creatroom:
//                Intent creatIntent = new Intent(MainActivity.this, CreatRoom.class);
//                creatIntent.putExtra("path","");
//                startActivity(creatIntent);
//                break;
//            case R.id.main_joinroom:
//                Intent joinIntent = new Intent(MainActivity.this, JoinRoom.class);
//                startActivity(joinIntent);
//                break;
//            case R.id.main_creathistory:
//                Intent historyIntent = new Intent(MainActivity.this, HistoryListActivity.class);
//                startActivity(historyIntent);
//                break;
//            case R.id.mymain_historyroom:
//                Intent is = new Intent(MainActivity.this, NewHistoryListActivity.class);
//                startActivity(is);
//                break;
//            case R.id.main_quite:
//                System.exit(0);
//                break;
//
//        }
//
//    }


}
