package com.example.aredgeclient;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class ImageStorage {
    private static Map<String,Bitmap> imageMap;
    public ImageStorage(){}
    public static void setImageMap(HashMap<String,Bitmap>map){imageMap=map;}
    public static HashMap<String,Bitmap>getImageMap(){return new HashMap<>(imageMap);}


}