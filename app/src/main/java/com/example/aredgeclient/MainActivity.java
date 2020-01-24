package com.example.aredgeclient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private AssetManager assetManager;
    private ServerManager serverManager;
    private JsonHttpResponseHandler responseHandler;
    private DownloadListener downloadListener,fileLoaderListener;
    private int downloadSize;
    private static final String GLTF_ASSET =
            "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent= new Intent(this, ArActivity.class);
        downloadSize=0;
        downloadListener=new DownloadListener() {
            @Override
            public void onDownloadCompleted() {
                //LAUNCH A NEW ACTIVITY
                Log.d("Download Listener","IMAGE LOADED");
                try{Thread.sleep(1000);} catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(assetManager.getImageMapSize()==downloadSize&&assetManager.getRenderableMapSize()==downloadSize){
                    Log.d("DATA DOWNLOAD ENDED","Time: "+System.nanoTime());
                    Log.d("Download Listener","ALL ASSETS LOADED");

                    ImageStorage.setImageMap(assetManager.getImageMap());
                    intent.putExtra("renderableIdMap",assetManager.getRenderbleIdMap());
                    intent.putExtra("renderablePathMap",assetManager.getRenderablePathMap());
                    startActivity(intent);


                }
            }
        };
        fileLoaderListener=new DownloadListener() {
            @Override
            public void onDownloadCompleted() {
//                serverManager.getImages();
            }
        };


        assetManager = new AssetManager(downloadListener);
        responseHandler=setUpResponseHandler(this);
        serverManager= new ServerManager(responseHandler);
        Button button= findViewById(R.id.downloadButton);// http://edge-storage.herokuapp.com/3dmodels/model_1579471458154_Cybertruck.sfb     http://edge-storage.herokuapp.com/3dmodels/model_1579471494254_Cybertruck.glb
        button.setOnClickListener((View view)->{//http://152.3.52.145/3dmodels/model_1579455861820_Cybertruck.sfb     http://152.3.52.145/3dmodels/model_1579454160713_Cybertruck.glb

//http://edge-storage.herokuapp.com/3dmodels/model_1579796180678_tower.sfb   http://edge-storage.herokuapp.com/3dmodels/model_1579796531484_tower.glb
//http://152.3.52.145/3dmodels/model_1579473817752_tower.glb     http://152.3.52.145/3dmodels/model_1579477296627_tower.sfb

//            serverManager.getImages();//http://152.3.52.145/3dmodels/model_1578690423562_Duck.glb     http://152.3.52.145/3dmodels/model_1579031350087_Duck.sfb
            new FileLoader(MainActivity.this,fileLoaderListener).execute("http://edge-storage.herokuapp.com/3dmodels/model_1579796531484_tower.glb");//http://edge-storage.herokuapp.com/3dmodels/model_1579279788498_Duck.glb
    });
    }

    public JsonHttpResponseHandler setUpResponseHandler(Context context){
        JsonHttpResponseHandler handler= new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("SERVER","Success:"+response.toString());
                try{
                    assetManager.addRenderable(response.getString("id"),response.getString("filePath"));
                    // downloadRenderable(response.getString("id"));
                } catch (JSONException e) {
                    Log.e("JSON","ERROR PARSING JSON BODY: "+e.getMessage());
                }

            }
            @Override
            public void onSuccess(int statusCode,Header[]headers,JSONArray array){
                Log.d("SERVER","Success:"+array.toString());
                try{
                    downloadSize=array.length();
                    for(int i=0;i<array.length();i+=1){
                        JSONObject image= array.getJSONObject(i);
                        assetManager.addImage(image.getString("_id"),image.getString("filePath"),image.getString("renderableId"),context);
                        serverManager.getModel(image.getString("renderableId"));
                    }

                }catch (JSONException e){}


            }
            @Override
            public void onFailure(int code, Header []headers,String message,Throwable throwable){
                Log.d("SERVER","ERRPR: "+message+"\n"+throwable.getMessage());
            }
        };
        return  handler;
    }
    private void downloadRenderable(String key){
        ModelRenderable.builder()
                .setSource(this, RenderableSource.builder().setSource(
                        this,
                        Uri.parse(GLTF_ASSET),
                        RenderableSource.SourceType.GLTF2)
                        .setScale(0.2f)  // Scale the original model to 50%.
                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                        .build())
                .setRegistryId(GLTF_ASSET)
                .build()
                .thenAccept(renderable -> {assetManager.addModelRenderable(key,renderable);})
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load renderable " +
                                            GLTF_ASSET, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
    }

    public interface DownloadListener{
        public void onDownloadCompleted();
    }
}