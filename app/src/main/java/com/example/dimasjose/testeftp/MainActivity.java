package com.example.dimasjose.testeftp;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    MediaRecorder recorder;
    String OUTPUT_FILE;
    Button startBtn, finishBtn, playBtn, stopBtn,buttonUpload,buttonDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBtn = (Button) findViewById(R.id.buttonGravar);
        finishBtn = findViewById(R.id.buttonPararGravacao);
        playBtn = findViewById(R.id.buttonPlay);
        stopBtn = findViewById(R.id.buttonStop);
        buttonUpload = findViewById(R.id.buttonUpload);
        buttonDownload = findViewById(R.id.buttonDownload);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        OUTPUT_FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiorecorder.3gp";
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();

            }
        });
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    beginRecording();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecording();
                }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayback();
            }
        });

    }


    public void upload(){


        FTPClient con = null;

        try
        {
            con = new FTPClient();
            con.connect("192.168.2.17");

            if (con.login("acoustic", "acoustic2018"))
            {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE);
                String data = OUTPUT_FILE;

                FileInputStream in = new FileInputStream(new File(data));
                boolean result = con.storeFile("audiorecorder.3gp", in);
                in.close();
                if (result) Log.v("upload result", "succeeded");
                con.logout();
                con.disconnect();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        }
    public void download(){
        FTPClient con = null;

        try
        {
            con = new FTPClient();
            con.connect("192.168.2.17");

            if (con.login("acoustic", "acoustic2018"))
            {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE);
                String data = "/sdcard/downloadaudiorecorder.3gp";

                OutputStream out = new FileOutputStream(new File(data));
                boolean result = con.retrieveFile("audiorecorder.3gp", out);
                out.close();
                if (result) Log.v("download result", "succeeded");
                con.logout();
                con.disconnect();
            }
        }
        catch (Exception e)
        {
            Log.v("download result","failed");
            e.printStackTrace();
        }

        }
    private void playRecording()  {
        //   playBtn.setEnabled(false);
        // startBtn.setEnabled(false);
        //  finishBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(OUTPUT_FILE);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();

    }

    private void dichtMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void stopPlayback() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            playBtn.setEnabled(true);
            startBtn.setEnabled(true);
            finishBtn.setEnabled(false);
            stopBtn.setEnabled(false);
        }
    }

    private void stopRecording() {
        if (recorder != null)
        recorder.stop();
        // finishBtn.setEnabled(false);
        //          playBtn.setEnabled(true);
        //  startBtn.setEnabled(true);
//            stopBtn.setEnabled(false);


    }

    private void beginRecording() throws IOException {
        if (recorder != null)
            recorder.release();
        File outFile = new File(OUTPUT_FILE);
        if (outFile.exists())
            outFile.delete();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        recorder.setOutputFile(OUTPUT_FILE);
        recorder.prepare();
        recorder.start();

        //finishBtn.setEnabled(true);
        // startBtn.setEnabled(false);
        // playBtn.setEnabled(false);
        // startBtn.setEnabled(false);
    }

    private void dichtMediaRecorder() {
        if (recorder != null)
            recorder.release();
    }
}
