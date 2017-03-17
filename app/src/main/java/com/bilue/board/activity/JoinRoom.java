package com.bilue.board.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bilue.board.R;
import com.bilue.board.adapter.WifiListAdapter;
import com.bilue.board.constant.IntentExtraConstant;
import com.bilue.board.constant.Engine;
import com.bilue.board.util.WifiUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JoinRoom extends BaseActivity {

	private static final int WIFI_UPDATED = 2;
	@BindView(R.id.btn_back) Button back;
	@BindView(R.id.lv_wifilist) ListView wifiListView;
	private List<ScanResult> wifiList;
	private WifiUtil wa;
	private MyHandler handler;
	private SearchWifiThread searchWifiThread;
	private WifiListAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_joinroom);
		ButterKnife.bind(this);
		wa = new WifiUtil(this);
		handler = new MyHandler();
		searchWifiThread = new SearchWifiThread();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		init();
		loadScanResult();

		searchWifiThread.start();

	}

	// 初始化 扫描列表等
	private void init() {
		//

		if (!wa.getmWifiManager().isWifiEnabled()) {
			wa.getmWifiManager().setWifiEnabled(true);
		}
		wa.startScan();
		wifiList = wa.getWifiList();

		myAdapter = new WifiListAdapter(this);
		listener();

	}

	// 加载扫描列表
	private void loadScanResult() {

		if (!wa.getmWifiManager().isWifiEnabled()) {
			wa.getmWifiManager().setWifiEnabled(true);
		}

		if (wifiList !=null & wifiList.size()>0) {

			//Toast.makeText(JoinRoom.this,"不空"+wifiList.get(0).SSID,Toast.LENGTH_SHORT).show();


			myAdapter.setWifiList(wifiList);
			wifiListView.setAdapter(myAdapter);

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		searchWifiThread.stop();
		searchWifiThread = null;
		wifiList = null;
	}


	private void listener() {
		wifiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				ScanResult sresult = wifiList.get(position);

				connect(sresult);

			}
		});

		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	// 连接
	private void connect(final ScanResult s) {

		if (s.SSID.endsWith("missing_roomL")){

			final EditText ed = new EditText(this);
			AlertDialog.Builder ad = new AlertDialog.Builder(this)
					.setTitle("输入密码").setView(ed)
					.setNegativeButton("确定", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							String pwd = ed.getText().toString().trim();
							int id = wa.addWifiConfig(s.SSID, pwd);

							if (id != -1) {
								if (wa.connect(id)) {
//									Toast.makeText(getApplicationContext(),
//											"Success", Toast.LENGTH_SHORT)
//											.show();
									join();

								}
							}

						}
					});
			ad.show();
		}

		else {
			// 不需要密码

			int id = wa.addWifiConfig(s.SSID, Engine.DEFAULT_PASSWD);

			if (id != -1) {
				if (wa.connect(id)) {

					Toast.makeText(getApplicationContext(), "Success",
							Toast.LENGTH_SHORT).show();
					join();
				} else {

					// showErrorMsg(WIFI_CONNECT_ERROR);
				}
			}

			else {
				// showErrorMsg(WIFI_CONNECT_ERROR);
			}

		}
	}

	private void join() {
		final ProgressDialog dialog = new ProgressDialog(JoinRoom.this);
		dialog.setTitle("加入房间");
		dialog.setMessage("加入房间中...");
		dialog.show();

		Thread td = new Thread() {
			public void run() {
//				Engine.isClient = true;
				//增加停顿 防止wifi 没开启就直接连接
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				Intent it = new Intent(JoinRoom.this, BoardActivity.class);
				it.putExtra(IntentExtraConstant.PATH,"");
				it.putExtra(IntentExtraConstant.IS_CLIENT,true);
				startActivity(it);

				finish();

				dialog.dismiss();
			}
		};

		td.start();

	}

	class SearchWifiThread implements Runnable {

		private boolean running = false; // 检测线程是否进行中
		private Thread thread;

		public SearchWifiThread() {
		}

		@Override
		public void run() {
			while (true) {
				if (!running) {
					return;
				}
				wa.startScan();
				List<ScanResult> list = wa.getWifiList();

				Message message = new Message();
				message.what = WIFI_UPDATED;
				message.obj = list;
				handler.sendMessage(message);
				try {
					Thread.sleep(3000);// 鍋�S 鍐嶆壂鎻�
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		public void start() {
			// Log.i("test", "OK");
			this.thread = new Thread(this);
			this.running = true;
			this.thread.start();
		}

		public void stop() {
			this.running = false;
			this.thread = null;
		}

	}

	class MyHandler extends Handler {

		public MyHandler() {
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case WIFI_UPDATED:
					wifiList = (List<ScanResult>) msg.obj;

					loadScanResult();
					break;

				default:
					break;
			}
		}

	}

}
