package com.example.achuan.mediaplayer.Music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.achuan.mediaplayer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by achuan on 16-3-29.
 */
public class MusicActivity extends AppCompatActivity
{
    // 1变量定义
    public static List<HashMap<String,String>> list;// 歌曲信息集合变量
    public static List<Integer> musicTime;
    private ListView mListView;
    private ImageButton play, next, last;// 控制按钮
    private TextView mTextView;//显示当前播放的歌曲的名称信息
    private SeekBar seekBar;// 进度条
    private TextView antiontime, totaltime;// 音乐播放时间和总时间
    private MediaPlayer mediaPlayer = null;// 音乐播放控制器
    boolean playSwitch = false;// 判断是否处于播放
    private int currentPosition=0; // 音乐当前位置
    private MusicBroadcastReciver musicBroadcastReciver;//进度条广播变量
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉title
        setContentView(R.layout.activity_main);
        // 1 变量初始化
        initview();
        // 2 读取音频文件信息并在列表中显示
        if (MusicActivity.this != null) {
            // 从媒体库中读取音频信息并存入游标
            Cursor cursor = MusicActivity.this.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                // 轮询游标并把音频信息加载到集合变量list中
                while (cursor.moveToNext()) {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put(
                            "music",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                    hashMap.put(
                            "musicpath",
                            cursor.getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                    list.add(hashMap);
                    //获得歌曲的时间
                    int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    musicTime.add(duration);
                }
            }
        }
        // 将mListView与Adapter绑定
        MusicAdapter musicAdapter = new MusicAdapter(musicTime,list, MusicActivity.this);
        mListView.setAdapter(musicAdapter);
        // 3 开启播放器服务
        startService(new Intent(MusicActivity.this, MusicServer.class));
        // 单击mListView中的条项后发送播放消息给广播接收器
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                String str_musicpathString = list.get(arg2).get("musicpath");
                Intent intent = new Intent();
                intent.setAction("receiver_music");
                intent.putExtra("musicPath", str_musicpathString);
                sendBroadcast(intent);
                playSwitch = true;//点击后状态显示为播放
                play.setBackgroundResource(R.drawable.ic_pause_black_36dp);//切换图片
                currentPosition = arg2;//更新歌曲的位置
                updateTextView();//更新当前歌曲的名称消息和显示颜色
                //Log.v("achuan", "当前位置为:"+currentPosition);
            }
        });
        // 4 控制按钮添加单击响应函数
        // 播放按钮添加单击响应函数
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (playSwitch) //如果当前是播放，点击后就让其暂停
                {
                    play.setBackgroundResource(R.drawable.ic_play_arrow_black_36dp);
                    Intent intent = new Intent();
                    intent.setAction("pause_music");
                    sendBroadcast(intent);//发送停止歌曲的广播
                    playSwitch = false;
                } else {
                    play.setBackgroundResource(R.drawable.ic_pause_black_36dp);
                    Intent intent = new Intent();
                    intent.setAction("continue_music");
                    sendBroadcast(intent);//发送继续播放歌曲的广播
                    playSwitch = true;
                }
                updateTextView();
            }
        });
        // 下一首按钮添加单击响应函数
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (currentPosition + 1 < list.size())
                {
                    play.setBackgroundResource(R.drawable.ic_pause_black_36dp);
                    String str_musicpathString = list.get(currentPosition + 1)
                            .get("musicpath");//拿到下首歌曲的路径消息
                    Intent intent = new Intent();
                    intent.setAction("receiver_music");
                    intent.putExtra("musicPath", str_musicpathString);
                    sendBroadcast(intent);//发送开始播放的歌曲的广播
                    playSwitch = true;//当前状态变为启动
                    currentPosition += 1;//更新当前播放歌曲的位置消息
                    updateTextView();
                }
                else {//当前为最后一首歌时，currentPosition:0～list.size()-1
                    Toast.makeText(MusicActivity.this,//显示的位置
                            "已经是最后一首了",//显示的消息
                            Toast.LENGTH_SHORT)//显示消息的类型
                            .show();
                }
            }
        });
        // 后一首按钮添加单击响应函数
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (currentPosition - 1 >= 0) {
                    play.setBackgroundResource(R.drawable.ic_pause_black_36dp);
                    String str_musicpathString = list.get(currentPosition - 1)
                            .get("musicpath");//拿到上首歌曲的路径消息
                    Intent intent = new Intent();
                    intent.setAction("receiver_music");
                    intent.putExtra("musicPath", str_musicpathString);
                    sendBroadcast(intent);//发送开始播放的歌曲的广播
                    playSwitch = true;//当前状态变为启动
                    currentPosition -= 1;
                    updateTextView();
                } else {
                    Toast.makeText(MusicActivity.this,//显示的位置
                            "已经是第一首了",//显示的消息
                            Toast.LENGTH_SHORT)//显示消息的类型
                            .show();
                }
            }
        });
        // 进度条控制响应功能
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // 进度条停止拖动
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                int progress = seekBar.getProgress();
                Intent intent = new Intent();
                intent.setAction("seekbar");
                intent.putExtra("Progress", progress);
                sendBroadcast(intent);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
            }
        });
        //5 进度条进度广播注册
        IntentFilter intentFilter = new IntentFilter();
        musicBroadcastReciver = new MusicBroadcastReciver();
        intentFilter.addAction("currentProgress");
        registerReceiver(musicBroadcastReciver, intentFilter);
        updateTextView();//初始化显示第一首歌曲的名称
    }
    //6 更新显示的歌曲名称
    void updateTextView()
    {
        //去掉歌曲名称的后缀格式（常见格式）
        String mString=list.get(currentPosition).get("music");
        if(mString.indexOf(".mp3")!=-1)//包含相关字符就执行操作
        {
            mTextView.setText(mString.replace(".mp3", ""));
        }
        else if(mString.indexOf(".m4a")!=-1)
        {
            mTextView.setText(mString.replace(".m4a", ""));
        }
        else if(mString.indexOf(".ape")!=-1)
        {
            mTextView.setText(mString.replace(".ape", ""));
        }
        else if(mString.indexOf(".flac")!=-1)
        {
            mTextView.setText(mString.replace(".flac", ""));
        }
        //mTextView.setText(list.get(currentPosition).get("music").replace(".", ""));
        if(playSwitch)//播放歌曲时为绿色
        {
            mTextView.setTextColor(Color.parseColor("#228b22"));//绿色
        }
        else {//停止播放时为红色
            mTextView.setTextColor(Color.parseColor("#b22222"));//红色
        }
    }
    // 1 变量初始化
    void initview() {
        mListView = (ListView) findViewById(R.id.id_listView);
        list = new ArrayList<HashMap<String, String>>();
        musicTime=new ArrayList<Integer>();
        play = (ImageButton) findViewById(R.id.play);
        last = (ImageButton) findViewById(R.id.last);
        next = (ImageButton) findViewById(R.id.next);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        mTextView=(TextView) findViewById(R.id.id_tv_0);
    }
    //5 设置进度条显示进度
    void setSeekbarProgerss(int int_musicProgress){
        seekBar.setProgress(int_musicProgress);
    }
    //5 进度条进度广播
     class MusicBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            int int_musicProgress = intent.getIntExtra("currentProgress", 0);
            setSeekbarProgerss(int_musicProgress);
        }
    }
}
