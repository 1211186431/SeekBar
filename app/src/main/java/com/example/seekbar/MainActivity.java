package com.example.seekbar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    String path = "/storage/emulated/0/lujing/Carly Rae Jepsen - I Really Like You.mp3";
    SeekBar seekBar;
    int istouch = 1;

    //处理进度条更新
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //更新进度
                    int position = mediaPlayer.getCurrentPosition();

                    int time = mediaPlayer.getDuration();
                    int max = seekBar.getMax();
                    if (istouch == 1) {

                        seekBar.setProgress(position * max / time);

                        double n = ((double) position) / 1000;
                        String n2 = String.format("%.2f", n);
                        String time2 = String.format("%.2f", ((double) time) / 1000);
                        TextView t = (TextView) findViewById(R.id.time2);
                        t.setText(n2 + "s / " + time2 + "s");
                    }
                    if (!mediaPlayer.isPlaying()) {
                        if (position >= time) {
                            Log.v("Tag", "over");
                            changeSong();
                        }

                    }
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button buttonup = (Button) findViewById(R.id.buttonup);
        final Button buttonPause = (Button) findViewById(R.id.buttonPause);
        final Button buttondown = (Button) findViewById(R.id.buttondown);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();
        init();

        //暂停播放
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    buttonPause.setText("Play");
                    mediaPlayer.pause();
                } else {
                    buttonPause.setText("Pause");
                    mediaPlayer.start();
                }

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                //;
                double dest = seekBar.getProgress();
                double time = mediaPlayer.getDuration();
                double max = seekBar.getMax();
                double n = (time * dest / max) / 1000;
                String n2 = String.format("%.2f", n);
                String time2 = String.format("%.2f", time / 1000);
                TextView t = (TextView) findViewById(R.id.time2);
                t.setText(n2 + "s / " + time2 + "s");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "开始滑动！");
                istouch = -1;     //不让线程走
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { //改到这里
                int dest = seekBar.getProgress();
                int time = mediaPlayer.getDuration();
                int max = seekBar.getMax();
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(time * dest / max);
                }
                Log.e("------------", "停止滑动！");
                istouch = 1;
            }
        });
        init();


    }

    public void init() {
        //进入Idle
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //后台线程发送消息进行更新进度条
            final int milliseconds = 200;
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        mHandler.sendEmptyMessage(0);
                        try {
                            sleep(milliseconds);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    }
                }
            }.start();


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void changeSong(){   //结束时换歌
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource("/storage/emulated/0/Topic+09.mp3");
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}