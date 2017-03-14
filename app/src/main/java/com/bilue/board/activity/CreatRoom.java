package com.bilue.board.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bilue.board.R;
import com.bilue.board.constant.IntentExtraConstant;
import com.bilue.board.util.Engine;
import com.bilue.board.util.WifiUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreatRoom extends AppCompatActivity {
	private static final String TAG_NEED_PASSWD = "_missing_roomL";
	private static final String TAG_NO_PASSWD = "_missing_room";
	@BindView(R.id.et_root_name) EditText etRootName;
	@BindView(R.id.et_passwd) EditText etPasswd;
	@BindView(R.id.tv_back) TextView tv_back;
	@BindView(R.id.tv_create) TextView tv_create;
	@BindView(R.id.tv_title) TextView tvTitle;
	@BindView(R.id.rg_is_need_passwd) RadioGroup rgIsNeedPasswd;
	@BindView(R.id.rb_need_passwd) RadioButton rbNeedPasswd;
	@BindView(R.id.rb_no_passwd) RadioButton rbNoPasswd;
	@BindView(R.id.ll_passwd_edit_root) LinearLayout llPasswdEditRoot;
	@BindView(R.id.toolbar_main) Toolbar mToolbar;

	private String path; //历史数据地址
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creatroom);


		path=getIntent().getExtras().getString(IntentExtraConstant.PATH); //历史数据

		initView();

	}

	private void initView() {
		ButterKnife.bind(this);
		setSupportActionBar(mToolbar);
		if(!path.equals("")){
			tvTitle.setText("创建历史会议");
		}
	}


	@OnClick({R.id.rb_need_passwd,R.id.rb_no_passwd})
	public void isNeedPassWd(RadioButton radioButton){
		boolean isCheck = radioButton.isChecked();
		switch (radioButton.getId()){
			case R.id.rb_need_passwd:
				if (isCheck){
					etPasswd.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.rb_no_passwd:
				if (isCheck){
					etPasswd.setVisibility(View.GONE);
				}
				break;
		}
	}

	@OnClick(R.id.tv_back)
	public void back(){
		finish();
	}

	@OnClick(R.id.tv_create)
	public void create(){
		String name = etRootName.getText().toString().trim();
		String passwd = Engine.DEFAULT_PASSWD;
		String wifiName = "";
		if (!TextUtils.isEmpty(name)) {
			if (etPasswd.getVisibility()==View.VISIBLE){
				passwd = etPasswd.getText().toString().trim();
				if (!TextUtils.isEmpty(passwd)) {
					wifiName = name + TAG_NEED_PASSWD;
				} else {
					Toast.makeText(CreatRoom.this, "请设置房间密码 或选择不加密", Toast.LENGTH_SHORT).show();
				}
			}
			else{
				passwd = Engine.DEFAULT_PASSWD;
				wifiName = name + TAG_NO_PASSWD;
			}
			creatwifi(wifiName, passwd);

		} else {
			Toast.makeText(CreatRoom.this, "会议名字为空", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private boolean checkState(WifiUtil wifiUtil) {

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

	private void creatwifi(final String ssid, final String pwd) {
		// TODO 创建wifi ap 在这里完成 各种可能出现的情况检测
		// 可能出现的情况1手机 不能开热点 2，wifi开着 得关闭 ； 3，已有热点 名字不同 4，已有热点 名字符合规则
		final Myhandler myhandler = new Myhandler();
		final WifiUtil wifiUtil = new WifiUtil(this);
		if (checkState(wifiUtil)) {
			final ProgressDialog dialog = new ProgressDialog(CreatRoom.this);
			dialog.setTitle("创建会议");
			dialog.setMessage("正在创建会议 请稍候");
			dialog.show();
			Thread td = new Thread() {
				public void run() {

					wifiUtil.creatAp(ssid, pwd, true);
					Engine.isClient = false;
					//Engine.SERVER_IP = wifiUtil.getIpAddress()+"";
//					try {
//						Thread.sleep(2000); // 停顿  为了UI 好看
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
						
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
					serverboardIntent.putExtra(IntentExtraConstant.PATH,"");
				}
				else{

					serverboardIntent.putExtra(IntentExtraConstant.PATH,path);
				}


				startActivity(serverboardIntent);
				
				finish();
			}
		}
	}
}
