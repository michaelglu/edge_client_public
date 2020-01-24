package com.example.aredgeclient;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileLoader extends AsyncTask<String, String, String> {
    private Context myContext;
    private MainActivity.DownloadListener myListener;
    private boolean isDone;
    public  FileLoader(Context context, MainActivity.DownloadListener listener){
        myContext=context;
        isDone=false;
        myListener=listener;
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;

        try{
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            int lenghtOfFile = conection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);

            // Output stream
            OutputStream output = new FileOutputStream(myContext.getFilesDir().toString()
                    + "/DOWNLOAD.glb");

            byte data[] = new byte[1024];

            long total = 0;
            Log.d("FILE LOADER","START "+System.nanoTime());
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();




        }catch(Exception e){ Log.e("FILE LOADER: ", e.getMessage());}

        File file = new File(myContext.getFilesDir().toString()
                + "/DOWNLOAD.sfb");

            Log.d("FILE LOADER","PATH  "+myContext.getFilesDir().toString()
                + "/DOWNLOAD.sfb");
        return myContext.getFilesDir().toString()
                + "/DOWNLOAD.sfb";
    }
    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
//        Log.d("FILE LOADER",""+Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
        Log.d("FILE LOADER","DONE "+System.nanoTime());
        isDone=true;
        myListener.onDownloadCompleted();

    }
}
