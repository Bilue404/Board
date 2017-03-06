package com.bilue.board.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilue.board.R;
import com.bilue.board.util.History;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/16.
 */
public class NewMyHistoryAdapter extends BaseAdapter{

    private  ArrayList<History> histories;
    private Context context;
    private LayoutInflater inflater;
    public NewMyHistoryAdapter(Context context, ArrayList<History> histories) {
        this.histories = histories;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return histories.size();
    }

    @Override
    public Object getItem(int position) {
        return histories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        History history = histories.get(position);

        ViewHodler viewHodler;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.activity_newhistorylist,null);
            viewHodler = new ViewHodler();

            viewHodler.infoView = (TextView) convertView.findViewById(R.id.newlistview);

            convertView.setTag(viewHodler);
        }

        else{
            viewHodler = (ViewHodler) convertView.getTag();
        }


        viewHodler.infoView.setText(history.getInfo());

        return convertView;
    }

    class ViewHodler{

        public TextView infoView;
    }



}
