package com.bilue.board.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

public class WifiUtil {

	/*
	 * 1.WIFI_STATE_DISABLED : WIFI网卡不可用（1） 　　2.WIFI_STATE_DISABLING :
	 * WIFI网卡正在关闭（0） 　　3.WIFI_STATE_ENABLED : WIFI网卡可用（3）
	 * 　　4.WIFI_STATE_ENABLING : WIFI网正在打开（2） （WIFI启动需要一段时间）
	 * 　　5.WIFI_STATE_UNKNOWN : 未知网卡状态
	 */

	private WifiManager mWifiManager;
	private List<ScanResult> mWifiList ;
	private List<ScanResult> sWifiList;
	private List<WifiConfiguration> mWifiConfigurations;
	private WifiInfo mWifiInfo;
	private WifiLock mWifiLock;

	public WifiUtil(Context context) {
		// TODO Auto-generated constructor stub
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
	}

	public void openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	public void closeWifi() {

		mWifiManager.setWifiEnabled(false);

	}

	public int getState() {
		return mWifiManager.getWifiState();
	}

	// 解锁wifiLock
	public void releaseWifiLock() {
		// 判断是否锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个wifiLock
	public void createWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfigurations;
	}

	// 指定配置好的网络进行连接
	public void connetionConfiguration(int index) {
		if (index > mWifiConfigurations.size()) {
			return;
		}
		// 连接配置好指定ID的网络
		mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId,
				true);
	}

	public void startScan() {

		mWifiList = new ArrayList<ScanResult>();
		mWifiManager.startScan();
		// 得到扫描结果
		
		sWifiList = mWifiManager.getScanResults();
		//过滤结果
		for(int i=0 ; i<sWifiList.size(); i++){
			ScanResult sr = sWifiList.get(i);
			if(sr.SSID.endsWith("missing_room")||sr.SSID.endsWith("missing_roomL")){
				mWifiList.add(sr);
			}

		}
		
		// 得到配置好的网络连接
		mWifiConfigurations = mWifiManager.getConfiguredNetworks();
	}

	// 得到网络列表
	public List<ScanResult> getWifiList() {
		return mWifiList;
	}

	// 查看扫描结果
	public StringBuffer lookUpScan() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mWifiList.size(); i++) {
			sb.append("Index_" + new Integer(i + 1).toString() + ":");
			// 将ScanResult信息转换成一个字符串包
			// 其中把包括：BSSID、SSID、capabilities、frequency、level
			sb.append((mWifiList.get(i)).toString()).append("\n");
		}
		return sb;
	}

	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	public int getWifiApStateInt() {
		try {
			int i = ((Integer) mWifiManager.getClass()
					.getMethod("getWifiApState", new Class[0])
					.invoke(mWifiManager, new Object[0])).intValue();
			return i;
		} catch (Exception localException) {
		}
		return 4;
	}

	public int getIpAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// 得到连接的ID
	public int getNetWordId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	// 得到wifiInfo的所有信息
	public String getWifiInfo() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	// 不需要密码
	public int addWifiConfig(String ssid) {
		int wifiId = -1;

		WifiConfiguration wifiCong = new WifiConfiguration();

		wifiCong.SSID = "\"" + ssid + "\"";
		// wifiCong.wepKeys[0] = "";
		wifiCong.allowedKeyManagement.set(0);
		// wifiCong.wepTxKeyIndex = 0;

		wifiId = mWifiManager.addNetwork(wifiCong);
		if (wifiId != -1) {
			return wifiId;

		}
		return wifiId;
	}

	// 需要密码
	public int addWifiConfig(String ssid, String passwd) {
		int wifiId = -1;

		WifiConfiguration wifiCong = new WifiConfiguration();
		wifiCong.SSID = "\"" + ssid + "\"";
		wifiCong.preSharedKey = "\"" + passwd + "\"";
		wifiCong.hiddenSSID = false;
		wifiCong.status = WifiConfiguration.Status.ENABLED;
		wifiId = mWifiManager.addNetwork(wifiCong);
		if (wifiId != -1) {
			return wifiId;

		}
		return wifiId;
	}

	public boolean connect(int i) {
		return mWifiManager.enableNetwork(i, true);
	}

	// 添加一个网络并连接
	// public void addNetWork(WifiConfiguration configuration){
	// int wcgId=mWifiManager.addNetwork(configuration);
	// mWifiManager.enableNetwork(wcgId, true);
	// }
	// 断开指定ID的网络
	public void disConnectionWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}

	public WifiManager getmWifiManager() {
		return mWifiManager;
	}

	public boolean creatAp(String SSID, String PassWd,boolean x) {
		Method method = null;

		try {
			method = mWifiManager.getClass().getMethod("setWifiApEnabled",
					WifiConfiguration.class, boolean.class);
			WifiConfiguration wifiConfig = new WifiConfiguration();
			wifiConfig.SSID = SSID;
			wifiConfig.preSharedKey = PassWd;

			wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			wifiConfig.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			wifiConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			wifiConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			wifiConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			wifiConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);

			method.invoke(mWifiManager, wifiConfig, x);

			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
