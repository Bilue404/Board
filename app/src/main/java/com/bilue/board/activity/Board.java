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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bilue.board.R;
import com.bilue.board.socket.ServerSock;
import com.bilue.board.ui.CustomSeekBar;
import com.bilue.board.util.BitmapUtil;
import com.bilue.board.util.Engine;
import com.bilue.board.view.ClientBgView;
import com.bilue.board.view.ClientFrontView;
import com.fourmob.colorpicker.ColorPickerDialog;
import com.fourmob.colorpicker.ColorPickerSwatch.OnColorSelectedListener;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Board extends AppCompatActivity implements OnClickListener {

    private int Tag = 0; // 用于标志是否第一次 用于开启服务
    @BindView(R.id.fl_draw_body) FrameLayout flDrawbody;
    private ClientBgView sbv;
    public static ServerBoardHandler serverBoardHandler;
    private ProgressDialog pd;

    private TextView moreView;
    private ClientFrontView cfv = null;
    private Button colorPicketView;
    private GradientDrawable myGrad;// 顶端小圆点
    private ColorPickerDialog colorPickerDialog;
    private CustomSeekBar myseekView;

    @BindView(R.id.iv_menu_linepath) ImageView ivMenuLinepath;
    @BindView(R.id.iv_menu_line) ImageView ivMenuLine;
    @BindView(R.id.iv_menu_square) ImageView ivMenuSquare;
    @BindView(R.id.iv_menu_circular) ImageView ivMenuCircular;
    @BindView(R.id.iv_menu_arrow) ImageView ivMenuArrow;
    @BindView(R.id.iv_menu_text) ImageView ivMenuText;
    @BindView(R.id.iv_menu_eraser) ImageView ivMenuEraser;
    @BindView(R.id.iv_menu_save) ImageView ivMenuSave;
    @BindView(R.id.iv_menu_clean) ImageView ivMmenuClean;
    @BindView(R.id.iv_menu_undo) ImageView ivMenuUndo;
    @BindView(R.id.iv_menu_redo) ImageView ivMenuRedo;
    @BindView(R.id.iv_menu_quite) ImageView ivMenuQuite;
    @BindView(R.id.iv_menu_new) ImageView ivMenuNew;
    @BindView(R.id.iv_menu_delete) ImageView ivMenuDelete;
    @BindView(R.id.iv_menu_last) ImageView ivMenuLast;
    @BindView(R.id.iv_menu_next) ImageView ivMenuNext;

    private Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ServerSock ss;//socket通讯 redo undo clean 等操作

    private String myPath;
    private TextView positionView;
    public static int position=1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        myPath=getIntent().getExtras().getString("myPath"); //历史数据


        initView();
        listener();

        sbv = new ClientBgView(this, null);

        flDrawbody.addView(sbv);

        positionView = new TextView(this);
        positionView.setText(position+"/"+position);
        positionView.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        flDrawbody.addView(positionView);

    }

    private void initView() {

        ButterKnife.bind(this);
        // 颜色选择器
        colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(R.string.board_pick_color, new int[]{
                        Color.CYAN, Color.LTGRAY, Color.BLACK, Color.BLUE, Color.GREEN,
                        Color.MAGENTA, Color.RED, Color.GRAY, Color.YELLOW},
                Color.BLACK, 3, 2);

//        drawBody = (FrameLayout) findViewById(R.id.board_drawbody);
        colorPicketView = (Button) findViewById(R.id.board_color_picker);
        myseekView = (CustomSeekBar) findViewById(R.id.board_myseek);
        moreView = (TextView) findViewById(R.id.board_more);
        myGrad = (GradientDrawable) colorPicketView.getBackground();
        myGrad.setColor(Color.BLACK);

//        squareView = (ImageView) findViewById(R.id.left_menu_square);
//        circularView = (ImageView) findViewById(R.id.left_menu_circular);
//        arrowView = (ImageView) findViewById(R.id.left_menu_arrow);
//        textView = (ImageView) findViewById(R.id.left_menu_text);
//        lineView = (ImageView) findViewById(R.id.left_menu_line);
//        linePathView = (ImageView) findViewById(R.id.left_menu_linepath);

//        eraserView = (ImageView) findViewById(R.id.left_menu_eraser);
//        saveView = (ImageView) findViewById(R.id.left_menu_save);
//        cleanView = (ImageView) findViewById(R.id.left_menu_clean);
//        redoView = (ImageView) findViewById(R.id.left_menu_redo);
//        undoView = (ImageView) findViewById(R.id.left_menu_undo);
//        quiteView = (ImageView) findViewById(R.id.left_menu_quite);
//        newView= (ImageView) findViewById(R.id.left_menu_new);
//        delectView= (ImageView) findViewById(R.id.left_menu_delect);
//        lastView= (ImageView) findViewById(R.id.left_menu_last);
//        nextView= (ImageView) findViewById(R.id.left_menu_next);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        //显示菜单
        mDrawerToggle.syncState();
        //显示动画
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        colorPicketView.setOnClickListener(this);
//        textView.setOnClickListener(this);
//        ivMenuLinepath.setOnClickListener(this);
//        lineView.setOnClickListener(this);
//        circularView.setOnClickListener(this);
//        squareView.setOnClickListener(this);
//        arrowView.setOnClickListener(this);
//        eraserView.setOnClickListener(this);
//        saveView.setOnClickListener(this);
//        cleanView.setOnClickListener(this);
//        undoView.setOnClickListener(this);
//        redoView.setOnClickListener(this);
//        quiteView.setOnClickListener(this);
//        newView.setOnClickListener(this);
//        delectView.setOnClickListener(this);
//        lastView.setOnClickListener(this);
//        nextView.setOnClickListener(this);
        serverBoardHandler = new ServerBoardHandler();

    }

    @OnClick(R.id.iv_menu_linepath)
    public void linePath(){
        Engine.DRAW_PEN_STYLE = Engine.penTool;
        cfv.updatePaint();
        ivMenuText.setImageResource(R.drawable.text);
        ivMenuArrow.setImageResource(R.drawable.arrow);
        ivMenuLinepath.setImageResource(R.drawable.linepathed);
        ivMenuLine.setImageResource(R.drawable.line);
        ivMenuSquare.setImageResource(R.drawable.square);
        ivMenuCircular.setImageResource(R.drawable.circular);
        ivMmenuClean.setImageResource(R.drawable.eraser);
    }

    @OnClick(R.id.iv_menu_line)
    public void Line(){
        Engine.DRAW_PEN_STYLE = Engine.lineTool;
        cfv.updatePaint();
        ivMenuText.setImageResource(R.drawable.text);
        ivMenuArrow.setImageResource(R.drawable.arrow);
        ivMenuLinepath.setImageResource(R.drawable.linepath);
        ivMenuLine.setImageResource(R.drawable.lined);
        ivMenuSquare.setImageResource(R.drawable.square);
        ivMenuCircular.setImageResource(R.drawable.circular);
        ivMmenuClean.setImageResource(R.drawable.eraser);
    }

    @OnClick(R.id.iv_menu_arrow)
    public void arrow(){
        Engine.DRAW_PEN_STYLE = Engine.arrowTool;
        cfv.updatePaint();
        ivMenuText.setImageResource(R.drawable.text);
        ivMenuArrow.setImageResource(R.drawable.arrowed);
        ivMenuLinepath.setImageResource(R.drawable.linepath);
        ivMenuLine.setImageResource(R.drawable.line);
        ivMenuSquare.setImageResource(R.drawable.square);
        ivMenuCircular.setImageResource(R.drawable.circular);
        ivMmenuClean.setImageResource(R.drawable.eraser);
    }

    @OnClick(R.id.iv_menu_text)
    public void text(){
        final EditText textip = new EditText(Board.this);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("请输入文字").setIcon(android.R.drawable.ic_dialog_info).setView(textip)
                .setNegativeButton("取消", null);
        builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String textString = textip.getText().toString().trim();
                if (textString.equals("")) {
                    Toast.makeText(Board.this, "不能为空", Toast.LENGTH_LONG).show();

                } else {
                    Engine.DRAW_PEN_STYLE = Engine.textTool;
                    Engine.paintText = textString;
                    cfv.updatePaint();

                    ivMenuText.setImageResource(R.drawable.texted);
                    ivMenuArrow.setImageResource(R.drawable.arrow);
                    ivMenuLinepath.setImageResource(R.drawable.linepath);
                    ivMenuLine.setImageResource(R.drawable.line);
                    ivMenuSquare.setImageResource(R.drawable.square);
                    ivMenuCircular.setImageResource(R.drawable.circular);
                    ivMmenuClean.setImageResource(R.drawable.eraser);

                }

            }
        });
        builder1.show();
    }

    @OnClick(R.id.iv_menu_square)
    public void square(){
        Engine.DRAW_PEN_STYLE = Engine.rectuTool;
        cfv.updatePaint();
        ivMenuText.setImageResource(R.drawable.text);
        ivMenuArrow.setImageResource(R.drawable.arrow);
        ivMenuLinepath.setImageResource(R.drawable.linepath);
        ivMenuLine.setImageResource(R.drawable.line);
        ivMenuSquare.setImageResource(R.drawable.squared);
        ivMenuCircular.setImageResource(R.drawable.circular);
        ivMmenuClean.setImageResource(R.drawable.eraser);
    }
    @OnClick(R.id.iv_menu_circular)
    public void circular(){
        Engine.DRAW_PEN_STYLE = Engine.circlectTool;
        cfv.updatePaint();
        ivMenuText.setImageResource(R.drawable.text);
        ivMenuArrow.setImageResource(R.drawable.arrow);
        ivMenuLinepath.setImageResource(R.drawable.linepath);
        ivMenuLine.setImageResource(R.drawable.line);
        ivMenuSquare.setImageResource(R.drawable.square);
        ivMenuCircular.setImageResource(R.drawable.circulared);
        ivMmenuClean.setImageResource(R.drawable.eraser);
    }


    @OnClick(R.id.iv_menu_undo)
    public void undo(){
        if(Engine.isClient){
            Toast.makeText(Board.this,"客户端没有撤销权限",Toast.LENGTH_SHORT).show();
        }
        else{
            ss.undo();
        }
    }

    @OnClick(R.id.iv_menu_redo)
    public void redo(){
        if(Engine.isClient){
            Toast.makeText(Board.this,"客户端没有重做权限",Toast.LENGTH_SHORT).show();
        }
        else{
            ss.redo();
        }
    }

    @OnClick(R.id.iv_menu_eraser)
    public void eraser(){
        Engine.DRAW_PEN_STYLE = Engine.eraserTool;
        cfv.updatePaint();
        ivMenuText.setImageResource(R.drawable.text);
        ivMenuArrow.setImageResource(R.drawable.arrow);
        ivMenuLinepath.setImageResource(R.drawable.linepath);
        ivMenuLine.setImageResource(R.drawable.line);
        ivMenuSquare.setImageResource(R.drawable.square);
        ivMenuCircular.setImageResource(R.drawable.circular);
        ivMenuEraser.setImageResource(R.drawable.erasered);
        ivMenuSave.setImageResource(R.drawable.save);
        ivMmenuClean.setImageResource(R.drawable.clear);
    }

    @OnClick(R.id.iv_menu_clean)
    public void clean(){
        if(Engine.isClient){
            Toast.makeText(Board.this,"客户端没有清空权限",Toast.LENGTH_SHORT).show();
        }
        else{
            ss.cleanBitmap();
            ivMenuText.setImageResource(R.drawable.text);
            ivMenuArrow.setImageResource(R.drawable.arrow);
            ivMmenuClean.setImageResource(R.drawable.cleared);
            ivMenuLinepath.setImageResource(R.drawable.linepath);
            ivMenuLine.setImageResource(R.drawable.line);
            ivMenuSquare.setImageResource(R.drawable.square);
            ivMenuCircular.setImageResource(R.drawable.circular);
            ivMenuEraser.setImageResource(R.drawable.eraser);
            ivMenuSave.setImageResource(R.drawable.save);
        }
    }

    @OnClick(R.id.iv_menu_last)
    public void menuLast(){
        if(Engine.isClient){
            Toast.makeText(Board.this,"客户端没有切换页权限",Toast.LENGTH_SHORT).show();
        }
        else {
            last();
        }
    }


    @OnClick(R.id.iv_menu_next)
    public void menuNext(){
        if(Engine.isClient){
            Toast.makeText(Board.this,"客户端没有切换页权限",Toast.LENGTH_SHORT).show();
        }
        else {
            next();
        }
    }

    @OnClick(R.id.iv_menu_new)
    public void menuNew(){
        if(Engine.isClient){
            Toast.makeText(Board.this,"客户端没有新建页权限",Toast.LENGTH_SHORT).show();
        }
        else{
            newBit();
        }
    }


    @OnClick(R.id.iv_menu_delete)
    public void menuDelete(){
        if(Engine.isClient){
            Toast.makeText(Board.this,"客户端没有删除页权限",Toast.LENGTH_SHORT).show();
        }
        else {
            delBit();
        }
    }

    @OnClick(R.id.iv_menu_save)
    public void menuSave(){
        ivMenuText.setImageResource(R.drawable.text);
        ivMenuArrow.setImageResource(R.drawable.arrow);
        ivMenuLinepath.setImageResource(R.drawable.linepath);
        ivMenuLine.setImageResource(R.drawable.line);
        ivMenuSave.setImageResource(R.drawable.square);
        ivMenuCircular.setImageResource(R.drawable.circular);
        ivMenuEraser.setImageResource(R.drawable.eraser);
        ivMenuSave.setImageResource(R.drawable.saved);


        save();

        ivMenuSave.setImageResource(R.drawable.save);
        ivMenuLinepath.setImageResource(R.drawable.linepathed);
    }

    @OnClick(R.id.iv_menu_quite)
    public void menuQuite(){
        quite();
    }

    @Override
    public void onClick(View v) {
        //关闭抽屉
        mDrawerLayout.closeDrawers();
        switch (v.getId()) {

            case R.id.board_color_picker:
                colorPickerDialog.show(getSupportFragmentManager(), "colorpicker");
                break;


//            case R.id.left_menu_linepath:
//
//                Engine.DRAW_PEN_STYLE = Engine.penTool;
//                cfv.updatePaint();
//                textView.setImageResource(R.drawable.text);
//                arrowView.setImageResource(R.drawable.arrow);
//                ivMenuLinepath.setImageResource(R.drawable.linepathed);
//                lineView.setImageResource(R.drawable.line);
//                squareView.setImageResource(R.drawable.square);
//                circularView.setImageResource(R.drawable.circular);
//                eraserView.setImageResource(R.drawable.eraser);
//                break;
//
//            case R.id.left_menu_line:
//
//                Engine.DRAW_PEN_STYLE = Engine.lineTool;
//                cfv.updatePaint();
//                textView.setImageResource(R.drawable.text);
//                arrowView.setImageResource(R.drawable.arrow);
//                ivMenuLinepath.setImageResource(R.drawable.linepath);
//                lineView.setImageResource(R.drawable.lined);
//                squareView.setImageResource(R.drawable.square);
//                circularView.setImageResource(R.drawable.circular);
//                eraserView.setImageResource(R.drawable.eraser);
//
//                break;
//            case R.id.left_menu_square:
//                Engine.DRAW_PEN_STYLE = Engine.rectuTool;
//                cfv.updatePaint();
//                textView.setImageResource(R.drawable.text);
//                arrowView.setImageResource(R.drawable.arrow);
//                ivMenuLinepath.setImageResource(R.drawable.linepath);
//                lineView.setImageResource(R.drawable.line);
//                squareView.setImageResource(R.drawable.squared);
//                circularView.setImageResource(R.drawable.circular);
//                eraserView.setImageResource(R.drawable.eraser);
//                break;

//            case R.id.left_menu_circular:
//                Engine.DRAW_PEN_STYLE = Engine.circlectTool;
//                cfv.updatePaint();
//                textView.setImageResource(R.drawable.text);
//                arrowView.setImageResource(R.drawable.arrow);
//                ivMenuLinepath.setImageResource(R.drawable.linepath);
//                lineView.setImageResource(R.drawable.line);
//                squareView.setImageResource(R.drawable.square);
//                circularView.setImageResource(R.drawable.circulared);
//                eraserView.setImageResource(R.drawable.eraser);
//                break;

//            case R.id.left_menu_arrow:
//                Engine.DRAW_PEN_STYLE = Engine.arrowTool;
//                cfv.updatePaint();
//                textView.setImageResource(R.drawable.text);
//                arrowView.setImageResource(R.drawable.arrowed);
//                ivMenuLinepath.setImageResource(R.drawable.linepath);
//                lineView.setImageResource(R.drawable.line);
//                squareView.setImageResource(R.drawable.square);
//                circularView.setImageResource(R.drawable.circular);
//                eraserView.setImageResource(R.drawable.eraser);
//                break;
//
//            case R.id.left_menu_text:
//
//
//
//
//                final EditText textip = new EditText(Board.this);
//                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
//                builder1.setTitle("请输入文字").setIcon(android.R.drawable.ic_dialog_info).setView(textip)
//                        .setNegativeButton("取消", null);
//                builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        String textString = textip.getText().toString().trim();
//                        if (textString.equals("")) {
//                            Toast.makeText(Board.this, "不能为空", Toast.LENGTH_LONG).show();
//
//                        } else {
//                            Engine.DRAW_PEN_STYLE = Engine.textTool;
//                            Engine.paintText = textString;
//                            cfv.updatePaint();
//
//                            textView.setImageResource(R.drawable.texted);
//                            arrowView.setImageResource(R.drawable.arrow);
//                            ivMenuLinepath.setImageResource(R.drawable.linepath);
//                            lineView.setImageResource(R.drawable.line);
//                            squareView.setImageResource(R.drawable.square);
//                            circularView.setImageResource(R.drawable.circular);
//                            eraserView.setImageResource(R.drawable.eraser);
//
//                        }
//
//                    }
//                });
//                builder1.show();
//
//                break;


//            case R.id.left_menu_undo:
//                if(Engine.isClient){
//                    Toast.makeText(Board.this,"客户端没有撤销权限",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    ss.undo();
//                }
//
//
//                break;
//            case R.id.left_menu_redo:
//                if(Engine.isClient){
//                    Toast.makeText(Board.this,"客户端没有重做权限",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    ss.redo();
//                }
//
//                break;

//            case R.id.left_menu_eraser:
//
//                Engine.DRAW_PEN_STYLE = Engine.eraserTool;
//                cfv.updatePaint();
//                textView.setImageResource(R.drawable.text);
//                arrowView.setImageResource(R.drawable.arrow);
//                ivMenuLinepath.setImageResource(R.drawable.linepath);
//                lineView.setImageResource(R.drawable.line);
//                squareView.setImageResource(R.drawable.square);
//                circularView.setImageResource(R.drawable.circular);
//                eraserView.setImageResource(R.drawable.erasered);
//                saveView.setImageResource(R.drawable.save);
//                cleanView.setImageResource(R.drawable.clear);
//                break;
//            case R.id.left_menu_clean:
//                if(Engine.isClient){
//                    Toast.makeText(Board.this,"客户端没有清空权限",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    ss.cleanBitmap();
//                    textView.setImageResource(R.drawable.text);
//                    arrowView.setImageResource(R.drawable.arrow);
//                    cleanView.setImageResource(R.drawable.cleared);
//                    ivMenuLinepath.setImageResource(R.drawable.linepath);
//                    lineView.setImageResource(R.drawable.line);
//                    squareView.setImageResource(R.drawable.square);
//                    circularView.setImageResource(R.drawable.circular);
//                    eraserView.setImageResource(R.drawable.eraser);
//                    saveView.setImageResource(R.drawable.save);
//                }
//                break;
//            case R.id.left_menu_save:
//
//                textView.setImageResource(R.drawable.text);
//                arrowView.setImageResource(R.drawable.arrow);
//                ivMenuLinepath.setImageResource(R.drawable.linepath);
//                lineView.setImageResource(R.drawable.line);
//                squareView.setImageResource(R.drawable.square);
//                circularView.setImageResource(R.drawable.circular);
//                eraserView.setImageResource(R.drawable.eraser);
//                saveView.setImageResource(R.drawable.saved);
//
//
//                save();
//
//                saveView.setImageResource(R.drawable.save);
//                ivMenuLinepath.setImageResource(R.drawable.linepathed);
//                break;
//            case R.id.left_menu_quite:
//                quite();
//                break;
//            case R.id.left_menu_new:
//                if(Engine.isClient){
//                    Toast.makeText(Board.this,"客户端没有新建页权限",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    newBit();
//                }
//
//                break;
//            case R.id.left_menu_delect:
//                if(Engine.isClient){
//                    Toast.makeText(Board.this,"客户端没有删除页权限",Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    delBit();
//                }
//                break;
//            case R.id.left_menu_last:
//                if(Engine.isClient){
//                    Toast.makeText(Board.this,"客户端没有切换页权限",Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    last();
//                }
//                break;
//            case R.id.left_menu_next:
//                if(Engine.isClient){
//                    Toast.makeText(Board.this,"客户端没有切换页权限",Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    next();
//                }
//                break;
//            default:
//                break;
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
                        cfv.updatePaint();
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

        if (Tag == 0) {
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

            Tag = 1;
        }
    }

    public void initBgView() {
        // 初始化连接 获取背景
        if (!Engine.isClient) {
            if(myPath==null||myPath.equals("")){
                ss = new ServerSock(sbv);
            }
            else {

                ss = new ServerSock(sbv,myPath);
            }

        }

        sbv.connectServer();
    }

    // 加载drawview 需要先先加载bgview 才能得到socket
    public void initDrawView() {

        cfv = new ClientFrontView(Board.this, null);
        myseekView.sendClient(cfv);
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
                                        if (sbv.save(fileName)) {
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
                        if (sbv.save(fileName)) {
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
        positionView.setText(position+"/"+ ss.getCount());

    }

    private void delBit(){
        if(position==1) {
            return;
        }
        ss.del(position);
        position =ss.getCount();
        positionView.setText(position+"/"+ss.getCount());
    }

    private void last(){
        if(position==1){
            return;
        }
        ss.last(position);
        position -= 1;
        positionView.setText(position+"/"+ss.getCount());
    }

    private void next(){
        if(position==ss.getCount()){
            return;
        }
        ss.next(position);
        position += 1;
        positionView.setText(position+"/"+ss.getCount());
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
                        positionView.setText(ss.getCount()+"/"+ss.getCount());
                    }



                    break;
            }
        }
    }


}
