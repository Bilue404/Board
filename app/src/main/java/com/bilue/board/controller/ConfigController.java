package com.bilue.board.controller;

import android.text.TextUtils;
import android.util.Log;

import com.bilue.board.bean.DrawAction;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bilue on 17/3/8.
 */

public class ConfigController {
    private static final String IS_NEED_SHOW = "isNeedShow";
    private static final String ITEM = "item";
    private static ConfigController controller;
    private static JSONArray config;
    private JSONObject currObj;
    private ConfigController(){}

    public static  ConfigController getConfigController(){
        if (controller == null) {
            controller = new ConfigController();
            config = new JSONArray();
        }
        return controller;
    }

    public void setConfig(JSONArray config){
        if (config != null) {
            this.config = config;
        }
    }


    public JSONArray getCurrConfig(){
        return config;
    }

    public void putAction(DrawAction drawAction){
        if (drawAction == null) {
            return ;
        }
        try {
            putAction2JsonArray(drawAction);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("putAction","the config is "+config.toString());
    }

    private void putAction2JsonArray(DrawAction drawAction) throws JSONException {
        JSONArray jsonArr ;
        if (TextUtils.equals(DrawAction.ACTION_DOWM , drawAction.getAction())){
            currObj = new JSONObject();
            jsonArr = new JSONArray();
            currObj.put(IS_NEED_SHOW,true);
            currObj.put(ITEM,jsonArr);
            JSONArray jsonArray = getCurrConfig();
            jsonArray.put(currObj);
        }
        jsonArr = currObj.getJSONArray(ITEM);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(drawAction);
        JSONObject jsonObj = new JSONObject(jsonStr);
        jsonArr.put(jsonObj);
    }
}
