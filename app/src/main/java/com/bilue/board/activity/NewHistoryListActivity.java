package com.bilue.board.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bilue.board.R;
import com.bilue.board.adapter.MyHistoryAdapter;
import com.bilue.board.bean.History;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/8/15.
 */
public class NewHistoryListActivity extends BaseActivity {

    private ArrayList<History> items;
    private SwipeMenuListView mListView;
    private MyHistoryAdapter mAdapter;
    @BindView(R.id.toolbar_main) Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newhistorylist);
        init();
    }

    private void init() {

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().toString();
            File file;
            String myDir = path + "/board/miss/";

            File mfile = new File(myDir);

            if(!mfile.exists()){
                return;
            }


            File[] files = mfile.listFiles();

            items = new ArrayList<>();

            if(files.length>0){
                for (int i = 0; i < files.length; i++) {
                    History history = new History();
                    File fi = files[i];
                    history.setInfo(fi.getName());
                    history.setPath(fi.getAbsolutePath());
                    items.add(history);
                }
            }


            mAdapter = new MyHistoryAdapter(NewHistoryListActivity.this, items);

            mListView = (SwipeMenuListView) findViewById(R.id.newlistview);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startHistory(items.get(position));
                }
            });


            // step 1. create a MenuCreator
            SwipeMenuCreator creator = new SwipeMenuCreator() {

                @Override
                public void create(SwipeMenu menu) {

                    SwipeMenuItem deleteItem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                    deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                            0x3F, 0x25)));
                    // set item width
                    deleteItem.setWidth(dp2px(90));
                    // set a icon
                    deleteItem.setIcon(R.drawable.ic_delete);
                    // add to menu
                    menu.addMenuItem(deleteItem);
                }
            };
            // set creator
            mListView.setMenuCreator(creator);

            // step 2. listener item click event
            mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {

                    switch (index) {
                        case 0:

                            //Toast.makeText(HistoryListActivity.this, "item is" + position, Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(NewHistoryListActivity.this);
                            builder.setMessage("是否删除次会议")
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    delect(items.get(position).getPath(),position);
                                }
                            })
                            .setPositiveButton("取消", null).show();



                            break;

                    }
                    return false;
                }
            });


        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }



    private void startHistory(History history){
        Intent creatIntent = new Intent(NewHistoryListActivity.this, CreatRoomActivity.class);
        creatIntent.putExtra("path",history.getPath());
        startActivity(creatIntent);
    }


    private void delect (String path,int position){
        File file = new File(path);
        RecursionDeleteFile(file);

        items.remove(position);
        mAdapter.notifyDataSetChanged();

    }


    public static void RecursionDeleteFile(File file){
        if(file.isFile()){
            file.delete();
            return;
        }
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }


}
