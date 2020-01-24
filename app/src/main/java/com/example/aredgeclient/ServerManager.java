package com.example.aredgeclient;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class ServerManager {
    public static final String SERVER_ENDPOINT = "http://152.3.52.145";
    public static final String GET_ALL_ROUTE="/getAllImage";
    public static final String GET_MODEL_PATH="/getRenderable";
    private JsonHttpResponseHandler httpResponseHandler;

    public ServerManager(JsonHttpResponseHandler handler){
        httpResponseHandler=handler;
    }

    public void getImages(){
        AsyncHttpClient client= new AsyncHttpClient();
        client.addHeader("clientId","5e1e1ace06caed56fac85178");//Heroku 5e1fad47c3b63200174fe1b9  OIT 5e1e1ace06caed56fac85178
        Log.d("DATA DOWNLOAD STARTED","Time: "+System.nanoTime());
        client.get(SERVER_ENDPOINT+GET_ALL_ROUTE,httpResponseHandler);
    }
    public void getModel(String id){
        AsyncHttpClient client= new AsyncHttpClient();
        client.addHeader("renderableId",id);
        client.get(SERVER_ENDPOINT+GET_MODEL_PATH,httpResponseHandler);

    }
}