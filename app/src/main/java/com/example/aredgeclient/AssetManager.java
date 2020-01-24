package com.example.aredgeclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Session;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private Map<String, Bitmap> imageMap;
    private Map<String, String> renderableIdMap;
    private Map<String,String>renderablePathMap;
    private Map<String,ModelRenderable>renderableMap;
    private MainActivity.DownloadListener downloadListener;

    public AssetManager(MainActivity.DownloadListener listener) {
        imageMap = new HashMap<>();
        renderableIdMap = new HashMap<>();
        renderablePathMap= new HashMap<>();
        renderableMap= new HashMap<>();
        downloadListener=listener;
    }

    public AssetManager(HashMap images, HashMap renderableIds,HashMap renderablePaths) {
        imageMap = images;
        renderableIdMap = renderableIds;
        renderablePathMap=renderablePaths;
    }

    public void addImage(String key, String url, String renderableId, Context context) {

        renderableIdMap.put(key, renderableId);
        ImageLoader imageLoader= ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        imageLoader.loadImage(url,new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage){
                imageMap.put(key,loadedImage);
                downloadListener.onDownloadCompleted();
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason){
                Log.d("IMAGE LOADER","FAILED TO LOAD IMAGE "+failReason.getCause().getMessage());

            }
        });
    }

    public void addRenderable(String id, String filePath){
        renderablePathMap.put(id,filePath);
    }
    public AugmentedImageDatabase buildDB(Session session) {
        AugmentedImageDatabase db = new AugmentedImageDatabase(session);
        for (String key : imageMap.keySet()) {
            db.addImage(key, imageMap.get(key));
        }
        return db;
    }

    public int getImageMapSize(){return imageMap.keySet().size();}
    public int getRenderableMapSize(){return  renderablePathMap.keySet().size();}
    public int getRenderableModelMapSize(){return renderableMap.keySet().size();}
    public HashMap getImageMap(){return (HashMap) imageMap;}
    public HashMap getRenderbleIdMap(){return (HashMap) renderableIdMap;}
    public HashMap getRenderablePathMap(){return (HashMap) renderablePathMap;}
    public void addModelRenderable(String key, ModelRenderable modelRenderable){
        renderableMap.put(key,modelRenderable);
    }


}
