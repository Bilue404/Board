package com.bilue.board.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bilue.board.R;
import com.bilue.board.constant.IntentExtraConstant;
import com.bilue.board.socket.ServerSock;
import com.bilue.board.ui.CustomSeekBar;
import com.bilue.board.util.BitmapUtil;
import com.bilue.board.util.Engine;
import com.bilue.board.view.ClientBgView;
import com.bilue.board.view.ClientFrontView;
import com.fourmob.colorpicker.ColorPickerDialog;
import com.fourmob.colorpicker.ColorPickerSwatch.OnColorSelectedListener;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Board extends AppCompatActivity{

    private boolean isFirstLoad = true; // 用于标志是否第一次 用于开启服务

    public static ServerBoardHandler serverBoardHandler;
    private ProgressDialog pd;
    private ClientFrontView cfv = null;
    @BindView(R.id.btn_color_picker) Button btnColorPicker;
    private GradientDrawable myGrad;// 顶端小圆点
    private ColorPickerDialog colorPickerDialog;
    @BindView(R.id.seekbar_paint_size) CustomSeekBar seekbarPaintSize;
    @BindViews({R.id.iv_menu_linepath,R.id.iv_menu_line,R.id.iv_menu_square,R.id.iv_menu_circular,
            R.id.iv_menu_arrow,R.id.iv_menu_text,R.id.iv_menu_eraser, R.id.iv_menu_save,
            R.id.iv_menu_clean,R.id.iv_menu_undo,R.id.iv_menu_redo,R.id.iv_menu_quite,
            R.id.iv_menu_new,R.id.iv_menu_delete,R.id.iv_menu_last, R.id.iv_menu_next})
    List<ImageView> ivIcons;
    @BindView(R.id.fl_draw_body) FrameLayout flDrawbody;
    @BindView(R.id.cbv_bg) ClientBgView cbv_Bg;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.tv_position) TextView tvPosition;

    private ServerSock ss;//socket通讯 redo undo clean 等操作

    private String myPath;
    public static int position=1;

    private int paintType = Engine.penTool;
    private int paintSize = 5;
    private int paintColor = Color.BLACK;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        myPath=getIntent().getExtras().getString(IntentExtraConstant.PATH); //历史数据


        initView();
        listener();

    }

    private void initView() {

        ButterKnife.bind(this);

        tvPosition.setText(position+"/"+position);

        // 颜色选择器
        colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(R.string.board_pick_color, new int[]{
                        Color.CYAN, Color.LTGRAY, Color.BLACK, Color.BLUE, Color.GREEN,
                        Color.MAGENTA, Color.RED, Color.GRAY, Color.YELLOW},
                Color.BLACK, 3, 2);

        myGrad = (GradientDrawable) btnColorPicker.getBackground();
        myGrad.setColor(Color.BLACK);



        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        //显示菜单
        mDrawerToggle.syncState();
        //显示动画
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        serverBoardHandler = new ServerBoardHandler();
        //初始化选择状态
        reSetIconState(R.id.iv_menu_linepath);

    }

    @OnClick(R.id.iv_menu_linepath)
    public void linePath(){
        Engine.DRAW_PEN_STYLE = Engine.penTool;
        paintType = Engine.penTool;
        setPaint(Engine.penTool);
        reSetIconState(R.id.iv_menu_linepath);

    }

    @OnClick(R.id.iv_menu_line)
    public void Line(){
        Engine.DRAW_PEN_STYLE = Engine.lineTool;
        paintType = Engine.lineTool;
        setPaint(Engine.lineTool);
        reSetIconState(R.id.iv_menu_line);

    }

    @OnClick(R.id.iv_menu_arrow)
    public void arrow(){
        Engine.DRAW_PEN_STYLE = Engine.arrowTool;
        paintType = Engine.arrowTool;
        setPaint(Engine.arrowTool);

        reSetIconState(R.id.iv_menu_arrow);

    }

    @OnClick(R.id.iv_menu_text)
    public void text(){
        final EditText textip = new EditText(Board.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入文字").setIcon(android.R.drawable.ic_dialog_info).setView(textip)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String textString = textip.getText().toString().trim();
                if (textString.equals("")) {
                    Toast.makeText(Board.this, "不能为空", Toast.LENGTH_LONG).show();

                } else {
                    Engine.DRAW_PEN_STYLE = Engine.textTool;
                    paintType = Engine.textTool;
                    setPaint(Engine.textTool);
                    reSetIconState(R.id.iv_menu_text);
                }

            }
        });
        builder.show();
    }

    @OnClick(R.id.iv_menu_square)
    public void square(){
        Engine.DRAW_PEN_STYLE = Engine.rectuTool;
        paintType = Engine.rectuTool;
        setPaint(Engine.rectuTool);


        reSetIconState(R.id.iv_menu_square);
    }
    @OnClick(R.id.iv_menu_circular)
    public void circular(){
        Engine.DRAW_PEN_STYLE = Engine.circlectTool;
        paintType = Engine.circlectTool;
        setPaint(Engine.circlectTool);
        reSetIconState(R.id.iv_menu_circular);
    }


    @OnClick(R.id.iv_menu_undo)
    public void undo(){
        if(Engine.isClient){
            Toast.makeText(Board.this,"客户端没有撤销权限",Toast.LENGTH_SHORT).show();
        }
        else{
            ss.undo();
        }
        reSetIconState(R.id.iv_menu_undo);

    }

    @OnClick(R.id.iv_menu_redo)
    public void redo(){
        if(Engine.isClient){
            Toast.makeText(Board.this,"客户端没有重做权限",Toast.LENGTH_SHORT).show();
        }
        else{
            ss.redo();
        }

        reSetIconState(R.id.iv_menu_redo);

    }

    @OnClick(R.id.iv_menu_eraser)
    public void eraser(){
        Engine.DRAW_PEN_STYLE = Engine.eraserTool;
        paintType = Engine.eraserTool;
        setPaint(Engine.eraserTool);
        reSetIconState(R.id.iv_menu_eraser);

    }

    @OnClick(R.id.iv_menu_clean)
    public void clean(){
        if(Engine.isClient){
            Toast.makeText(Board.this,"客户端没有清空权限",Toast.LENGTH_SHORT).show();
        }
        else{
            ss.cleanBitmap();
            reSetIconState(R.id.iv_menu_clean);
        }
    }

    @OnClick(R.id.iv_menu_last)
    public void menuLast(){
        if(Engine.isClient){
            Toast.makeText(Board.this,"客户端没有切换页权限",Toast.LENGTH_SHORT).show();
        }
        else {
            last();
            reSetIconState(R.id.iv_menu_last);

        }
    }


    @OnClick(R.id.iv_menu_next)
    public void menuNext(){
        if(Engine.isClient){
            Toast.makeText(Board.this,"客户端没有切换页权限",Toast.LENGTH_SHORT).show();
        }
        else {
            next();
            reSetIconState(R.id.iv_menu_next);

        }
    }

    @OnClick(R.id.iv_menu_new)
    public void menuNew(){
        if(Engine.isClient){
            Toast.makeText(Board.this,"客户端没有新建页权限",Toast.LENGTH_SHORT).show();
        }
        else{
            newBit();
            reSetIconState(R.id.iv_menu_new);

        }
    }


    @OnClick(R.id.iv_menu_delete)
    public void menuDelete(){
        if(Engine.isClient){
            Toast.makeText(Board.this,"客户端没有删除页权限",Toast.LENGTH_SHORT).show();
        }
        else {
            delBit();
            reSetIconState(R.id.iv_menu_new);

        }
    }

    @OnClick(R.id.iv_menu_save)
    public void menuSave(){
        save();
        reSetIconState(R.id.iv_menu_save);

    }

    @OnClick(R.id.iv_menu_quite)
    public void menuQuite(){
        quite();

    }

    @OnClick(R.id.btn_color_picker)
    public void pickColor(){
//        //关闭抽屉
//        mDrawerLayout.closeDrawers();
        colorPickerDialog.show(getSupportFragmentManager(), "colorpicker");
    }


    private void setPaint(int paintStyle){
        cfv.setPaintStyle(paintStyle);
    }

    private void reSetIconState(int id ){
        for (ImageView ivIcon : ivIcons) {
            if (ivIcon.getId() == id){
                ivIcon.setSelected(true);
            }
            else if (ivIcon.getId()!=id & ivIcon.isSelected()){
                ivIcon.setSelected(false);
            }
        }
    }

    private void listener() {


        colorPickerDialog
                .setOnColorSelectedListener(new OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
//                        Toast.makeText(Board.this, "selectedColor : " + color,
//                                Toast.LENGTH_SHORT).show();
                        Engine.paintColor = color;
                        myGrad.setColor(color);
                        seekbarPaintSize.setSeekBarColor(color);
                        paintColor = color;
                        cfv.setPaintColor(color);
                    }
                });


        seekbarPaintSize.setOnSizeChangeListener(new CustomSeekBar.OnSizeChangeListener() {
            @Override
            public void onSizeChangeListener(int size) {
                paintSize = size;
                cfv.setPaintSize(size);
            }
        });

    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();


    }

    // 放在这里的原因是 服务器需要获得view 的大小 来构造画布大小
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);

        if (isFirstLoad) {
            pd = new ProgressDialog(this);
            pd.setTitle("请稍候");
            pd.setMessage("初始化会议室中 请不要进行操作");
            pd.show();
            Thread td = new Thread() {
                @Override
                public void run() {
                    super.run();
                    initBgView();
                }
            };
            td.start();

            isFirstLoad = false;
        }
    }

    public void initBgView() {
        // 初始化连接 获取背景
        if (!Engine.isClient) {
            if(myPath==null||myPath.equals("")){
                ss = new ServerSock(cbv_Bg);
            }
            else {

                ss = new ServerSock(cbv_Bg,myPath);
            }

        }

        cbv_Bg.connectServer();
    }

    // 加载drawview 需要先先加载bgview 才能得到socket
    public void initDrawView() {

        cfv = new ClientFrontView(Board.this, null);
        seekbarPaintSize.sendClient(cfv);
        flDrawbody.addView(cfv);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            quite();
        }
        return false;
    }


    private  void quite(){
        new AlertDialog.Builder(this)
                .setTitle("是否退出本次会议")
                .setNegativeButton("保存并退出", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(!Engine.isClient){

                            final EditText myInput = new EditText(Board.this);
                            AlertDialog.Builder builder = new AlertDialog.Builder(Board.this);
                            builder.setTitle("请输入保存会议的名称").setIcon(android.R.drawable.ic_dialog_info).setView(myInput)
                                    .setNegativeButton("取消", null);
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {




                                    String fileName = myInput.getText().toString().trim();
                                    if (fileName.equals("")) {
                                        Toast.makeText(Board.this, "不能为空", Toast.LENGTH_LONG).show();

                                    } else {


                                        String path = Environment.getExternalStorageDirectory().toString();

                                        File file ;
                                        String myDir = path+"/board/miss/"+fileName+"/";
                                        file = new File(myDir);

                                        if(!file.exists()){
                                            file.mkdirs();
                                        }

                                        if (BitmapUtil.saveBitmaps2file(ss.getBitmaps(),fileName)) {
                                            Toast.makeText(Board.this, "保存成功", Toast.LENGTH_LONG).show();
                                            System.exit(0);
                                            finish();
                                        } else {
                                            Toast.makeText(Board.this, "保存失败", Toast.LENGTH_LONG).show();
                                        }
                                    }


                                }
                            });
                            builder.show();


                        }

                        else{
                            final EditText myInput = new EditText(Board.this);
                            AlertDialog.Builder builder = new AlertDialog.Builder(Board.this);
                            builder.setTitle("请输入保存的名称").setIcon(android.R.drawable.ic_dialog_info).setView(myInput)
                                    .setNegativeButton("取消", null);
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    String fileName = myInput.getText().toString().trim();
                                    if(fileName.equals("")){
                                        Toast.makeText(Board.this, "不能为空", Toast.LENGTH_LONG).show();

                                    }
                                    else{
                                        if (cbv_Bg.save(fileName)) {
                                            Toast.makeText(Board.this, "保存成功", Toast.LENGTH_LONG).show();
                                            System.exit(0);
                                            finish();
                                        } else {
                                            Toast.makeText(Board.this, "保存失败", Toast.LENGTH_LONG).show();
                                        }
                                    }


                                }
                            });
                            builder.show();
                        }

                    }
                })
                .setNeutralButton("取消",null)
                .setPositiveButton("直接退出", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {


                        System.exit(0);
                        finish();
                    }
                }).show();
    }


    private void save(){

        if(!Engine.isClient){

            final EditText myInput = new EditText(Board.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请输入保存会议的名称").setIcon(android.R.drawable.ic_dialog_info).setView(myInput)
                    .setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {




                    String fileName = myInput.getText().toString().trim();
                    if (fileName.equals("")) {
                        Toast.makeText(Board.this, "不能为空", Toast.LENGTH_LONG).show();

                    } else {


                        String path = Environment.getExternalStorageDirectory().toString();

                        File file ;
                        String myDir = path+"/board/miss/"+fileName+"/";
                        file = new File(myDir);

                        if(!file.exists()){
                            file.mkdirs();
                        }

                        if (BitmapUtil.saveBitmaps2file(ss.getBitmaps(),fileName)) {
                            Toast.makeText(Board.this, "保存成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Board.this, "保存失败", Toast.LENGTH_LONG).show();
                        }
                    }


                }
            });
            builder.show();


        }

        else{
            final EditText myInput = new EditText(Board.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请输入保存的名称").setIcon(android.R.drawable.ic_dialog_info).setView(myInput)
                    .setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    String fileName = myInput.getText().toString().trim();
                    if(fileName.equals("")){
                        Toast.makeText(Board.this, "不能为空", Toast.LENGTH_LONG).show();

                    }
                    else{
                        if (cbv_Bg.save(fileName)) {
                            Toast.makeText(Board.this, "保存成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Board.this, "保存失败", Toast.LENGTH_LONG).show();
                        }
                    }


                }
            });
            builder.show();
        }


    }


    private void newBit(){
        ss.add(position);
        position +=1;
        tvPosition.setText(position+"/"+ ss.getCount());

    }

    private void delBit(){
        if(position==1) {
            return;
        }
        ss.del(position);
        position =ss.getCount();
        tvPosition.setText(position+"/"+ss.getCount());
    }

    private void last(){
        if(position==1){
            return;
        }
        ss.last(position);
        position -= 1;
        tvPosition.setText(position+"/"+ss.getCount());
    }

    private void next(){
        if(position==ss.getCount()){
            return;
        }
        ss.next(position);
        position += 1;
        tvPosition.setText(position+"/"+ss.getCount());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    public class ServerBoardHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 6565:// 连接成功 圈圈消失
                    initDrawView();
                    pd.dismiss();

                    if(!myPath.equals("")){
                        position = ss.getCount();
                        tvPosition.setText(ss.getCount()+"/"+ss.getCount());
                    }



                    break;
            }
        }
    }


}
