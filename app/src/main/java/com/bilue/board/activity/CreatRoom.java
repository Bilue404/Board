package com.bilue.board.activity;

import com.bilue.board.util.Engine;
import com.bilue.board.util.WifiUtil;
import com.bilue.board.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.drm.ProcessedData;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class CreatRoom extends AppCompatActivity {
	private EditText missName;
	private EditText pwd;
	private TextView back;
	private TextView creat;
	private TextView titleView;
	private RadioGroup isneedpwd;
	private RadioButton needpwd;
	private RadioButton nopwd;
	private LinearLayout passwdll;
	private String name;
	private String passwd = "123456";
	private boolean isNeedPwd = false;
	private WifiUtil wifiUtil;
	private Handler myhandler;
	private Toolbar mToolbar;
	private String path;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creatroom);


		path=getIntent().getExtras().getString("path"); //历史数据

		//Toast.makeText(this, "dd" + path, Toast.LENGTH_SHORT).show();

		wifiUtil = new WifiUtil(this);
		findView();

		listener();
		myhandler = new Myhandler();
	}

	private void findView() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
		setSupportActionBar(mToolbar);
		missName = (EditText) findViewById(R.id.creat_room_missname);
		back = (TextView) findViewById(R.id.creat_room_back);
		creat = (TextView) findViewById(R.id.creat_room_creat);
		isneedpwd = (RadioGroup) findViewById(R.id.creat_room_isneedpasswd);
		needpwd = (RadioButton) findViewById(R.id.creat_room_needpasswd);
		nopwd = (RadioButton) findViewById(R.id.creat_room_nopasswd);
		passwdll = (LinearLayout) findViewById(R.id.creat_room_passwdedit);
		titleView = (TextView) findViewById(R.id.main_title);
		if(!path.equals("")){
			titleView.setText("创建历史会议");
		}
	}

	private void listener() {
		isneedpwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.creat_room_needpasswd) {
					pwd = new EditText(CreatRoom.this);
					pwd.setWidth(passwdll.getWidth());
					pwd.setHint("请输入密码");
					passwdll.addView(pwd);
					isNeedPwd = true;
				} else {
					if (pwd != null) {
						passwdll.removeAllViews();
						pwd = null;
					}
					isNeedPwd = false;
				}
			}
		});

		creat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				name = missName.getText().toString().trim();
				if (!name.equals("")) {
					
					if (isNeedPwd) {
						passwd = pwd.getText().toString().trim();
						if (!passwd.equals("")) {
							// CreatAp(passwd);
							Engine.missName = name.replace("_missing_roomL","");
							creatwifi(name + "_missing_roomL", passwd, true);

						} else {
							// sendErrorUi("请设置房间密码 或选择不加密");
						}
					} else {
						passwd = Engine.initPasswd;
						Engine.missName = name.replace("_missing_room","");
						creatwifi(name + "_missing_room", passwd, true);
						// CreatAp(passwd);
					}
				} else {

					Toast.makeText(CreatRoom.this, "会议名字为空", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// wifiUtil.creatAp("Thx God", "123456",false);

				finish();
			}
		});
	}

	private boolean checkState() {

		if (wifiUtil.getWifiApStateInt() == 4) {
			return false;
		}
		if (wifiUtil.getmWifiManager().isWifiEnabled()) {
			wifiUtil.closeWifi();
			//Toast.makeText(CreatRoom.this, "关闭当前wifi", Toast.LENGTH_SHORT)
			//		.show();
		}
		if ((wifiUtil.getWifiApStateInt() == 4)
				|| (wifiUtil.getWifiApStateInt() == 13)) {
			wifiUtil.closeWifi();
			wifiUtil.creatAp("close", "123456", false);
			//Toast.makeText(CreatRoom.this, "关闭当前热点", Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	private void creatwifi(final String ssid, final String pwd, final boolean s) {
		// TODO 创建wifi ap 在这里完成 各种可能出现的情况检测
		// 可能出现的情况1手机 不能开热点 2，wifi开着 得关闭 ； 3，已有热点 名字不同 4，已有热点 名字符合规则

		if (checkState()) {
			final ProgressDialog dialog = new ProgressDialog(CreatRoom.this);
			dialog.setTitle("创建会议");
			dialog.setMessage("正在创建会议 请稍候");
			dialog.show();
			Thread td = new Thread() {
				public void run() {

					wifiUtil.creatAp(ssid, pwd, true);
					Engine.isClient = false;
					//Engine.ServerIp = wifiUtil.getIpAddress()+"";
					try {
						Thread.sleep(2000); // 停顿  为了UI 好看
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
					Message msg = new Message();
					msg.what = 2;




					myhandler.sendMessage(msg);
					dialog.dismiss();

				};
			};
			td.start();	
		}	
	}
	
	class Myhandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==2){
				Intent serverboardIntent = new Intent(CreatRoom.this, Board.class) ;
				if(path.equals("")||path==null){
					serverboardIntent.putExtra("myPath","");
				}
				else{

					serverboardIntent.putExtra("myPath",path);
				}


				startActivity(serverboardIntent);
				
				finish();
			}
		}
	}
}
