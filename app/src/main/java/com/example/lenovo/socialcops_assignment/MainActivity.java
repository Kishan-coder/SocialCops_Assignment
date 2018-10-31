package com.example.lenovo.socialcops_assignment;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    public static final String video_url="https://socialcops.com/images/old/spec/home/header-img-background_video-1920-480.mp4";
    VideoView videoView;
    Button btn;
    MediaController media;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        media = new MediaController(this);
    }

    public void playvideo(View view) {
        videoView = (VideoView) findViewById(R.id.videoView);
        String file_name=initializePlayer();
            if(new File(getFilesDir(), file_name).exists()){
              File file=new File(getFilesDir(), file_name);
              file.setReadable(true, false);
                Uri videoUri = Uri.parse(file.getAbsolutePath());
                    videoView.setVideoURI(videoUri);
                    videoView.start();
                    Toast.makeText(this, "Playing offline video.", Toast.LENGTH_SHORT).show();

            }
        if(!isNetworkAvailable()){
                Toast.makeText(this, "Not connected!", Toast.LENGTH_SHORT).show();
            }



        videoView.start();

        DownLoadTask downLoadTask=new DownLoadTask();
        downLoadTask.execute(video_url,file_name);
    }
    private Uri getMedia(String mediaName) {
        return Uri.parse(mediaName);
    }

    private String initializePlayer() {
        Uri videoUri = getMedia(video_url);
        videoView.setVideoURI(videoUri);
        return videoUri.getLastPathSegment();
    }

    private void releasePlayer() {
        videoView.stopPlayback();
    }

    @Override
    protected void onStop() {
        super.onStop();

        releasePlayer();
    }

    private class DownLoadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String file_name=strings[1];
            if(!isNetworkAvailable()|| new File(getFilesDir(), file_name).exists()){
                return "";
            }
            String path=strings[0];

            int fileLength;
            try {

                URL url=new URL(path);
                URLConnection urlConnection=url.openConnection();
                urlConnection.connect();
                fileLength= urlConnection.getContentLength();
                File input_file = new File(getFilesDir(), file_name);

                InputStream inputStream= new BufferedInputStream(url.openStream(), 8192 );
                byte [] data= new byte[1024];
                int count= 0;

                OutputStream outputStream= new FileOutputStream(input_file);
                while ((count=inputStream.read(data))!=-1){
                    outputStream.write(data, 0, count);
                }

                inputStream.close();
                outputStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Download completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result=="")
                return;
            Toast.makeText(getApplication(), result, Toast.LENGTH_LONG ).show();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
