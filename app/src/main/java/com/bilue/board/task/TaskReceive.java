package com.bilue.board.task;

import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;

import com.bilue.board.activity.BoardActivity;
import com.bilue.board.util.BitmapUtil;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by bilue on 17/3/14.
 * 接收任务， 将接收的数据解析成bitmap 然后更新
 */

public class TaskReceive implements Runnable {
    private Socket socket;
    private BoardActivity.ReceiveHandler receiveHandler;
    private boolean scaning = false;
    private InputStream is = null;
    private DataInputStream dis = null;
    public TaskReceive(Socket socket, BoardActivity.ReceiveHandler receiveHandler){
        this.socket = socket;
        this.receiveHandler = receiveHandler;
        if (socket!=null && receiveHandler!=null){
            scaning = true;
        }
    }
    @Override
    public void run() {
        if (socket != null && receiveHandler!=null) {
            while (scaning) {
                // 做监听处理getBaseBitmap(s);
                // 连进来先接受

                Bitmap bitmap = getBaseBitmap(socket);
                Message msg = new Message();
                msg.what = BoardActivity.ReceiveHandler.UPDATE_VIEW;
                msg.obj = bitmap;
                receiveHandler.sendMessage(msg);
            }
        }
    }

    // 获取首次bitmap
    private Bitmap getBaseBitmap(Socket s) {

        try {
            is = s.getInputStream();
            dis = new DataInputStream(is);
            int i = dis.readInt();
            byte[] bitByte = new byte[i];
            dis.readFully(bitByte);
            Bitmap bitmap = BitmapUtil.Bytes2Bitmap(bitByte);
            return bitmap;

        } catch (Exception e) {
            Log.i("client_test", "读取图片文件出错");
            e.printStackTrace();
        }

        return null;

    }
}
