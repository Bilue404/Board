package com.bilue.board.task;

import com.bilue.board.util.DrawAction;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by bilue on 17/3/14.
 */

public class SendActionTask implements Runnable {
    private Socket socket;
    public SendActionTask(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {

    }

    public void sendAction(DrawAction drawAction){
        ObjectOutputStream obs = null;
        try {
            obs = new ObjectOutputStream(socket.getOutputStream());
            obs.writeObject(drawAction);
            obs.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
