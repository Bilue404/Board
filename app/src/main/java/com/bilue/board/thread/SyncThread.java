package com.bilue.board.thread;

import com.bilue.board.controller.ConfigController;

import org.json.JSONArray;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by bilue on 17/3/8.
 */

public class SyncThread implements Runnable {

    private List<Socket> userList;
    public SyncThread(List<Socket> userList){
        this.userList = userList;
    }

    @Override
    public void run() {
        if (userList.size() != 0) {
            // Log.i("server_test", "--服务端：开始发送所有数据");
            for (int i = 0; i < userList.size(); i++) {
                Socket socket =  userList.get(i);
                syncConfig(socket);
            }
        }
    }

    private void syncConfig(Socket socket) {
        try {
            OutputStream outputStram = socket.getOutputStream();
            ObjectOutputStream oop = new ObjectOutputStream(outputStram);
            JSONArray jsonArr = ConfigController.getConfigController().getCurrConfig();
            oop.writeObject(jsonArr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
