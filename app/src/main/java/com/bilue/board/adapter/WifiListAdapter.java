package com.bilue.board.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bilue.board.R;

import java.util.List;

public class WifiListAdapter extends BaseAdapter{

	private List<ScanResult> wifiList;
	private Context context;
	private LayoutInflater mInflater;
	private MyHolder holder;
	
	public WifiListAdapter(Context context) {
		
		this.mInflater = LayoutInflater.from(context);
		
	}
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return wifiList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ScanResult sr = wifiList.get(position);
		
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.adapter_wifilist, null);
			holder = new MyHolder();
			holder.missName = (TextView)convertView.findViewById(R.id.adapter_wifilist_missname);
			holder.join = (TextView)convertView.findViewById(R.id.adapter_wifilist_join);
			convertView.setTag(holder);
		}
		else{
			holder = (MyHolder)convertView.getTag();
		}
		
		String a = "_missing_room";
		String b = "_missing_roomL";
		String c = "";
		if(sr.SSID.endsWith(a)){
			c = sr.SSID.replace(a, "");
		}
		if(sr.SSID.endsWith(b)){
			c = sr.SSID.replace(b, "");
		}
		
		
		holder.missName.setText(c);
		
		//Log.i("test", sr.SSID+"-"+sr.capabilities);  
		return convertView;
	}
	class MyHolder {
		public TextView missName;
		public TextView join;
	}
	public void setWifiList(List<ScanResult> wifiList) {
		this.wifiList = wifiList;
	}
	

}
