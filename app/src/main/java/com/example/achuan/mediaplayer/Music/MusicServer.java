package com.example.achuan.mediaplayer.Music;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

public class MusicServer extends Service {
	// 3 初始化变量
	int mediaDuration;//音频的持续时间
	int mediaPercent;// 音频的播放进度（xx%）
	private MediaPlayer mediaPlayer = null;//音频播放组件变量
	private MusicBroadcastReciver musicBroadcastReciver;//开始广播接收器
	private MusicPauseBroadcastReciver musicPauseBroadcastReciver;//暂停广播接收器
	private MusicContinueBroadcastReciver musicContinueBroadcastReciver;//继续广播接收器
	private MusicSeekBarBroadcastReciver musicSeekBarBroadcastReciver;//进度条控制广播接收器
	@Override
	public void onCreate() {
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	// 4 Service的启动服务中完成广播注册
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		// 注册开始播放广播"receiver_music"
		IntentFilter intentFilter0 = new IntentFilter();
		musicBroadcastReciver = new MusicBroadcastReciver();
		intentFilter0.addAction("receiver_music");
		registerReceiver(musicBroadcastReciver, intentFilter0);
		//注册暂停广播"pause_music"
		IntentFilter intentFilter1 = new IntentFilter();
		musicPauseBroadcastReciver = new MusicPauseBroadcastReciver();
		intentFilter1.addAction("pause_music");
		registerReceiver(musicPauseBroadcastReciver, intentFilter1);
		//Log.i("调用", "调用pause");
		//注册继续播放广播"continue_music"
		IntentFilter intentFilter2 = new IntentFilter();
		musicContinueBroadcastReciver = new MusicContinueBroadcastReciver();
		intentFilter2.addAction("continue_music");
		registerReceiver(musicContinueBroadcastReciver, intentFilter2);
		//注册进度条控制播放广播"seekbar"
		IntentFilter intentFilter3 = new IntentFilter();
		musicSeekBarBroadcastReciver = new MusicSeekBarBroadcastReciver();
		intentFilter3.addAction("seekbar");
		registerReceiver(musicSeekBarBroadcastReciver, intentFilter3);
		// 5以及完成进度条显示广播的发送
		//每隔1秒更新进度条
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				sendProgress();
			}
		}, 0, 1000);//延时0秒后重复的执行new TimerTask()，周期是1000毫秒

	}
	// 5 发送广播消息给MusicActivity视图中的进度条广播接收器
	private void sendProgress() {
		if (mediaPlayer != null) {
			int currentProgress = (int) ((float) mediaPlayer
					.getCurrentPosition() / (float) mediaDuration * 100f);
			Intent intent = new Intent();
			intent.setAction("currentProgress");
			intent.putExtra("currentProgress", currentProgress);
			sendBroadcast(intent);
		}
	}
	/*
	 * 1 完成音乐播放器的五大功能:a开始播放 b停止播放 c暂停播放 d继续播放 e进度条播放
	 */
	// a 开始播放
	public void play(String path) {
		stop();
		File file = new File(path);
		Uri uri = Uri.fromFile(file);
		mediaPlayer = null;
		//Log.i("shoudao", "shoudao");
		if (mediaPlayer == null) {
			mediaPlayer = MediaPlayer.create(MusicServer.this, uri);
			mediaPlayer.setLooping(false);
			mediaPlayer.seekTo(0);//进度置为0
			mediaPlayer.start();//开始播放
			//获取长度
			mediaDuration = mediaPlayer.getDuration();//当前播放的长度
			mediaPercent = mediaDuration / 100;//转换为xx%
		}
	}
	// b 停止播放
	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			System.gc();//呼叫java虚拟机的垃圾回收器运行 回收内存的垃圾
		}
	}
	// c 暂停播放
	public void Musicpause() {
		//Log.i("调用pause", "调用pause");
		if (mediaPlayer != null) {
			mediaPlayer.pause();
		}
	}
	// d 继续播放
	public void MusicContinue() {
		if (mediaPlayer != null) {
			mediaPlayer.start();
		}
	}
	// e 进度条播放
	void musicProgress(int progress) {
		if (mediaPlayer != null) {
			mediaPlayer.seekTo(progress * mediaPercent);
		}
	}
	/*
	 * 2 完成音乐播放器的五大（广播接收器）功能:a开始播放 b停止播放 c暂停播放 d继续播放 e进度条播放
	 */
	//a开始播放
	class MusicBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String str_musicpathString = intent.getStringExtra("musicPath");//拿到歌曲的路径消息
			play(str_musicpathString);//对目标路径文件进行数据流读取
		}
	}
	//b停止播放
	class MusicPauseBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Musicpause();
		}
	}
    //d继续播放
	class MusicContinueBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			MusicContinue();
		}
	}
	//e进度条播放
	class MusicSeekBarBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int progress = intent.getIntExtra("Progress", 0);
			musicProgress(progress);
		}
	}
}
