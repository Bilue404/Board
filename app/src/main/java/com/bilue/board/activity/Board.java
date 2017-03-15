package com.bilue.board.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bilue.board.R;
import com.bilue.board.constant.IntentExtraConstant;
import com.bilue.board.controller.ZoomController;
import com.bilue.board.socket.ServerSock;
import com.bilue.board.task.ReceiveTask;
import com.bilue.board.task.SendActionTask;
import com.bilue.board.ui.CustomSeekBar;
import com.bilue.board.util.BitmapUtil;
import com.bilue.board.util.DrawAction;
import com.bilue.board.util.Engine;
import com.bilue.board.view.ClientBgView;
import com.bilue.board.view.ClientFrontView;
import com.fourmob.colorpicker.ColorPickerDialog;
import com.fourmob.colorpicker.ColorPickerSwatch.OnColorSelectedListener;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Board extends BaseActivity{

    @BindView(R.id.btn_color_picker) Button btnColorPicker;
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
    @BindView(R.id.cfv_draw_view) ClientFrontView cfvDrawView;
    private ServerSock ss;//socket通讯 redo undo clean 等操作
    private String myPath;
    public static int position=1;

    private GradientDrawable myGrad;// 顶端小圆点
    private ColorPickerDialog colorPickerDialog;
    private SendActionTask sendActionTask;
    private boolean isClient = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        myPath=getIntent().getExtras().getString(IntentExtraConstant.PATH); //历史数据;
        isClient = getIntent().getExtras().getBoolean(IntentExtraConstant.IS_CLIENT);//客户端 或者服务端
        initView();
        startRendererServer();
        setListener();

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
        //初始化选择状态
        reSetIconState(R.id.iv_menu_linepath);
        //将渲染view 根据比例缩放
        Animation animation = new ScaleAnimation(1, ZoomController.getScale(),1,ZoomController.getScale());
        animation.setFillBefore(false);
        animation.setFillAfter(true);
        cbv_Bg.startAnimation(animation);


    }

    @OnClick(R.id.iv_menu_linepath)
    public void linePath(){
        Engine.DRAW_PEN_STYLE = Engine.PEN_TOOL;
        setPaint(Engine.PEN_TOOL);
        reSetIconState(R.id.iv_menu_linepath);

    }

    @OnClick(R.id.iv_menu_line)
    public void Line(){
        Engine.DRAW_PEN_STYLE = Engine.LINE_TOOL;
        setPaint(Engine.LINE_TOOL);
        reSetIconState(R.id.iv_menu_line);

    }

    @OnClick(R.id.iv_menu_arrow)
    public void arrow(){
        Engine.DRAW_PEN_STYLE = Engine.ARROW_TOOL;
        setPaint(Engine.ARROW_TOOL);

        reSetIconState(R.id.iv_menu_arrow);

    }

    @OnClick(R.id.iv_menu_text)
    public void text(){
        final EditText textip = new EditText(Board.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入文字").setIcon(android.R.drawable.ic_dialog_info).setView(textip)
                .setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String textString = textip.getText().toString().trim();
                if (textString.equals("")) {
                    Toast.makeText(Board.this, R.string.board_not_empty, Toast.LENGTH_LONG).show();

                } else {
                    Engine.DRAW_PEN_STYLE = Engine.TEXT_TOOL;
                    setPaint(Engine.TEXT_TOOL);
                    reSetIconState(R.id.iv_menu_text);
                }

            }
        });
        builder.show();
    }

    @OnClick(R.id.iv_menu_square)
    public void square(){
        Engine.DRAW_PEN_STYLE = Engine.RECTU_TOOL;
        setPaint(Engine.RECTU_TOOL);


        reSetIconState(R.id.iv_menu_square);
    }
    @OnClick(R.id.iv_menu_circular)
    public void circular(){
        Engine.DRAW_PEN_STYLE = Engine.CIRCLECT_TOOL;
        setPaint(Engine.CIRCLECT_TOOL);
        reSetIconState(R.id.iv_menu_circular);
    }


    @OnClick(R.id.iv_menu_undo)
    public void undo(){
        if(isClient){
            Toast.makeText(Board.this,"客户端没有撤销权限",Toast.LENGTH_SHORT).show();
        }
        else{
            ss.undo();
        }
        reSetIconState(R.id.iv_menu_undo);

    }

    @OnClick(R.id.iv_menu_redo)
    public void redo(){
        if(isClient){
            Toast.makeText(Board.this,"客户端没有重做权限",Toast.LENGTH_SHORT).show();
        }
        else{
            ss.redo();
        }

        reSetIconState(R.id.iv_menu_redo);

    }

    @OnClick(R.id.iv_menu_eraser)
    public void eraser(){
        Engine.DRAW_PEN_STYLE = Engine.ERASER_TOOL;
        setPaint(Engine.ERASER_TOOL);
        reSetIconState(R.id.iv_menu_eraser);

    }

    @OnClick(R.id.iv_menu_clean)
    public void clean(){
        if(isClient){
            Toast.makeText(Board.this,"客户端没有清空权限",Toast.LENGTH_SHORT).show();
        }
        else{
            ss.cleanBitmap();
            reSetIconState(R.id.iv_menu_clean);
        }
    }

    @OnClick(R.id.iv_menu_last)
    public void menuLast(){
        if(isClient){
            Toast.makeText(Board.this,"客户端没有切换页权限",Toast.LENGTH_SHORT).show();
        }
        else {
            last();
            reSetIconState(R.id.iv_menu_last);

        }
    }


    @OnClick(R.id.iv_menu_next)
    public void menuNext(){
        if(isClient){
            Toast.makeText(Board.this,"客户端没有切换页权限",Toast.LENGTH_SHORT).show();
        }
        else {
            next();
            reSetIconState(R.id.iv_menu_next);

        }
    }

    @OnClick(R.id.iv_menu_new)
    public void menuNew(){
        if(isClient){
            Toast.makeText(Board.this,"客户端没有新建页权限",Toast.LENGTH_SHORT).show();
        }
        else{
            newBit();
            reSetIconState(R.id.iv_menu_new);

        }
    }


    @OnClick(R.id.iv_menu_delete)
    public void menuDelete(){
        if(isClient){
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
        cfvDrawView.setPaintStyle(paintStyle);
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

    private void setListener() {


        colorPickerDialog
                .setOnColorSelectedListener(new OnColorSelectedListener() {

                    @Override
                    public void onColorSelected(int color) {
//                        Toast.makeText(Board.this, "selectedColor : " + color,
//                                Toast.LENGTH_SHORT).show();
//                        Engine.paintColor = color;
                        myGrad.setColor(color);
                        seekbarPaintSize.setSeekBarColor(color);
                        cfvDrawView.setPaintColor(color);
                    }
                });


        seekbarPaintSize.setOnSizeChangeListener(new CustomSeekBar.OnSizeChangeListener() {
            @Override
            public void onSizeChangeListener(int size) {

                cfvDrawView.setPaintSize(size);
            }
        });


        cfvDrawView.setOnActionChangeListener(new ClientFrontView.OnActionChangeListener() {
            @Override
            public void onActionChange(String drawPenTAG, int drawPenStyle , String action, float x, float y, float paintSize, int paintColor,String paintText){
                DrawAction da = new DrawAction(drawPenTAG, action, x, y, drawPenStyle, paintSize, paintColor,Engine.paintText);
                if (da!=null && sendActionTask!=null){
                    sendActionTask.sendAction(da);
                }
            }
        });
    }


    private void startRendererServer(){

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle(R.string.board_waiting);
            pd.setMessage(getText(R.string.board_initing_miss_room));
            pd.show();

            Thread td = new Thread() {
                @Override
                public void run() {
                    if (!isClient) {
                        //建立服务端
                        if(myPath==null||myPath.equals("")){
                            ss = new ServerSock();
                        }
                        else {
                            ss = new ServerSock(myPath);
                        }


                        //渲染进程开启
                        try {
                            Thread.sleep(3000);// 暂停5S 再开始连接 防止服务器还在开启 导致连入失败
                            final Socket s = new Socket(Engine.SERVER_IP, Engine.Port);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pd.dismiss();

                                    if(!myPath.equals("")){
                                        position = ss.getCount();
                                        tvPosition.setText(ss.getCount()+"/"+ss.getCount());
                                    }

                                    startReceive(s);
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            td.start();
    }


    private void startReceive(Socket socket){
        if (socket != null) {
            ReceiveHandler handler = new ReceiveHandler();
            Thread td = new Thread(new ReceiveTask(socket,handler));
            td.start();
        }


        startSendActionServer(socket);

    }

    private void startSendActionServer(Socket socket){
        if (socket!=null){
            sendActionTask = new SendActionTask(socket);
            Thread sendActionThread = new Thread(sendActionTask);
            sendActionThread.start();
        }

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
                .setTitle(R.string.board_if_quite_miss)
                .setNegativeButton(R.string.board_save_and_quite, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
//                        if(!Engine.isClient){
//
//                            final EditText myInput = new EditText(Board.this);
//                            AlertDialog.Builder builder = new AlertDialog.Builder(Board.this);
//                            builder.setTitle(R.string.board_enter_name).setIcon(android.R.drawable.ic_dialog_info).setView(myInput)
//                                    .setNegativeButton(R.string.cancel, null);
//                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//
//                                public void onClick(DialogInterface dialog, int which) {
//
//
//
//
//                                    String fileName = myInput.getText().toString().trim();
//                                    if (fileName.equals("")) {
//                                        Toast.makeText(Board.this, R.string.board_not_empty, Toast.LENGTH_LONG).show();
//
//                                    } else {
//
//
//                                        String path = Environment.getExternalStorageDirectory().toString();
//
//                                        File file ;
//                                        String myDir = path+"/board/miss/"+fileName+"/";
//                                        file = new File(myDir);
//
//                                        if(!file.exists()){
//                                            file.mkdirs();
//                                        }
//
//                                        if (BitmapUtil.saveBitmaps2file(ss.getBitmaps(),fileName)) {
//                                            Toast.makeText(Board.this, R.string.board_save_success, Toast.LENGTH_LONG).show();
//                                            System.exit(0);
//                                            finish();
//                                        } else {
//                                            Toast.makeText(Board.this, R.string.board_save_faild, Toast.LENGTH_LONG).show();
//                                        }
//                                    }
//
//
//                                }
//                            });
//                            builder.show();
//
//
//                        }
//
//                        else{
//                            final EditText myInput = new EditText(Board.this);
//                            AlertDialog.Builder builder = new AlertDialog.Builder(Board.this);
//                            builder.setTitle(R.string.board_save_name).setIcon(android.R.drawable.ic_dialog_info).setView(myInput)
//                                    .setNegativeButton(R.string.cancel, null);
//                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//
//                                public void onClick(DialogInterface dialog, int which) {
//                                    String fileName = myInput.getText().toString().trim();
//                                    if(fileName.equals("")){
//                                        Toast.makeText(Board.this, R.string.board_not_empty, Toast.LENGTH_LONG).show();
//
//                                    }
//                                    else{
//                                        if (cbv_Bg.save(fileName)) {
//                                            Toast.makeText(Board.this, R.string.board_save_success, Toast.LENGTH_LONG).show();
//                                            System.exit(0);
//                                            finish();
//                                        } else {
//                                            Toast.makeText(Board.this,R.string.board_save_faild, Toast.LENGTH_LONG).show();
//                                        }
//                                    }
//
//
//                                }
//                            });
//                            builder.show();
//                        }


                        save();
                        finish();
                    }
                })
                .setNeutralButton(R.string.cancel,null)
                .setPositiveButton(R.string.board_quite, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {


                        System.exit(0);
                        finish();
                    }
                }).show();
    }


    private void save(){

        if(!isClient){

            final EditText myInput = new EditText(Board.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.board_enter_name).setIcon(android.R.drawable.ic_dialog_info).setView(myInput)
                    .setNegativeButton(R.string.cancel, null);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {




                    String fileName = myInput.getText().toString().trim();
                    if (fileName.equals("")) {
                        Toast.makeText(Board.this, R.string.board_not_empty, Toast.LENGTH_LONG).show();

                    } else {


                        String path = Environment.getExternalStorageDirectory().toString();

                        File file ;
                        String myDir = path+"/board/miss/"+fileName+"/";
                        file = new File(myDir);

                        if(!file.exists()){
                            file.mkdirs();
                        }

                        if (BitmapUtil.saveBitmaps2file(ss.getBitmaps(),fileName)) {
                            Toast.makeText(Board.this, R.string.board_save_success, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Board.this, R.string.board_save_faild, Toast.LENGTH_LONG).show();
                        }
                    }


                }
            });
            builder.show();


        }

        else{
            final EditText myInput = new EditText(Board.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.board_enter_name).setIcon(android.R.drawable.ic_dialog_info).setView(myInput)
                    .setNegativeButton(R.string.cancel, null);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    String fileName = myInput.getText().toString().trim();
                    if(fileName.equals("")){
                        Toast.makeText(Board.this, R.string.board_not_empty, Toast.LENGTH_LONG).show();

                    }
                    else{
                        if (cbv_Bg.save(fileName)) {
                            Toast.makeText(Board.this, R.string.board_save_success, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Board.this, R.string.board_save_faild, Toast.LENGTH_LONG).show();
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


    public class ReceiveHandler extends Handler{
        public static final int UPDATE_VIEW = 111;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATE_VIEW){
                Bitmap bitmap = (Bitmap) msg.obj;
                cbv_Bg.setBitmap(bitmap);
            }
        }
    }

}
