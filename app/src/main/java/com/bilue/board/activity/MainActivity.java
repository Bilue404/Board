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
import com.bilue.board.constant.IntentExtraConstant;
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
    @BindView(R.id.toolbar_main) Toolbar mToolbar;
    @BindView(R.id.ll_logo_root) LinearLayout llLogoRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }


    private void initView() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        LayoutInflater inflater = getLayoutInflater();
        addSvgView(inflater, llLogoRoot);
    }


    private void addSvgView(LayoutInflater inflater, LinearLayout container)
    {
        final View view = inflater.inflate(R.layout.item_svg, container, false);
        final SvgView svgView = (SvgView) view.findViewById(R.id.svg);

        svgView.setSvgResource(R.raw.board);

        container.addView(view);
        Handler handlerDelay = new Handler();
        handlerDelay.postDelayed(new Runnable(){
            public void run() {
                svgView.startAnimation();
            }}, 500);
    }



    @OnClick(R.id.btn_creat_room)
    public void creatRoom(){
        Intent creatIntent = new Intent(MainActivity.this, CreatRoom.class);
        creatIntent.putExtra(IntentExtraConstant.PATH,"");
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



}
