package com.example.achuan.mediaplayer.Video;

import java.io.File;
import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.achuan.mediaplayer.R;

public class VideoPlayActivity extends AppCompatActivity {
	//1-声明相关变量
	/*主要控件相关*/
	private MediaPlayer mediaPlayer;//媒体播放组件
	private AudioManager mAudioManager = null;//视频播放组件
	private SurfaceView surfaceView;//视频播放进度条控件
	private File videofile;//视频文件名
	private Handler handler = null;//视图的操作者，主线程
	private int position;//视频播放的位置
	private int currentPosition;//视频当前的位置
	private boolean pause = false;//判断当前的播放状态
	private boolean firstFlag = false;
    /*进度条相关*/
	private LinearLayout linearLayout_seekbar;//进度条布局
	private TextView playtime = null;//播放时间显示文本
	private SeekBar seekbar = null;//播放进度条控件
	private TextView durationTime = null;//视频总时间显示文本
	/*视频控制按钮相关*/
	private LinearLayout linearLayout_iv;//播放控制布局
	private ImageButton playbutton;
	private ImageButton resetbutton;
	private ImageButton stopbutton;
	/*声音控制相关*/
	//private TextView sound;//声音大小文本显示
	//private SeekBar soundBar = null;//声音大小条控件
	//int MaxSound;//最大
	//int CurrentSound;//当前的声音
	private boolean isOpen=false;//其它控件是否显示标志

	@TargetApi(Build.VERSION_CODES.M)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoplay);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
		InitView();//1-初始化布局控件
		SetListener();//2-设置组件的监听事件

	}
    /******2-设置组件的监听事件******/
	private void SetListener() {
		//为视频显示界面设置点击事件
		surfaceView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OtherViewChangState();//改变其它控件的显示状态
			}
		});
		//设置控制按钮的点击响应事件
		ButtonClickListener listener = new ButtonClickListener();
		playbutton.setOnClickListener(listener);
		resetbutton.setOnClickListener(listener);
		stopbutton.setOnClickListener(listener);
		//设置播放进度条的控制函数
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
				if (fromUser)
					mediaPlayer.seekTo(progress);
			}
		});
		//设置声音进度条控制函数
		/*soundBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					int ScurrentPosition = soundBar.getProgress();
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							ScurrentPosition, 0);
				}
			}
		});*/

	}
	/*****1-初始化布局******/
	private void InitView() {
		mediaPlayer = new MediaPlayer();// 新建媒体播放组件
		/*视频显示区域*/
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceView.getHolder().setFixedSize(320, 240);
        //1-播放进度条相关
		linearLayout_seekbar= (LinearLayout) findViewById(R.id.id_seekbar_ll);
		playtime = (TextView) findViewById(R.id.playtime);//当前播放时间文本框
		seekbar = (SeekBar) findViewById(R.id.seekbar);//播放进度条
		durationTime = (TextView) findViewById(R.id.duration);//总的播放时间文本框
        //2-播放控制相关
		linearLayout_iv= (LinearLayout) findViewById(R.id.id_btn_ll);
		playbutton = (ImageButton) this.findViewById(R.id.playBtn);
		resetbutton = (ImageButton) this.findViewById(R.id.resetBtn);
		stopbutton = (ImageButton) this.findViewById(R.id.stopBtn);
		/*//3-声音控制相关
		sound = (TextView) findViewById(R.id.soundsize);
		soundBar = (SeekBar) findViewById(R.id.soundBar);*/
	}
	/*时间显示格式化*/
	public String toTime(int time) {
		time /= 1000;
		int minute = time / 60;
		int second = time % 60;
		return String.format("%02d:%02d", minute, second);
	}
	/*
	 * 1-特殊情况下，系统会自动销毁activity（调用该方法），如果用户重新打开该activity，该方法将不会被调用
	 */
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		position = savedInstanceState.getInt("position");
		String path = savedInstanceState.getString("path");
		if (path != null && !"".equals(path)) {
			videofile = new File(path);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}
	/*
	 * 2-系统在特殊情况下自动销毁activity时会调用该方法，用户主动销毁activity时，该方法不会被调用
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("position", position);
		if (videofile != null)
			outState.putString("path", videofile.getAbsolutePath());
		super.onSaveInstanceState(outState);
	}
	/******activity的线程控制方法******/
	//1-失去焦点时调用该方法
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 用户切换视图的过程
		//判断是否正在播放，如果播放需要暂停
		mediaPlayer.pause();
		currentPosition = mediaPlayer.getCurrentPosition();
		playbutton.setBackgroundResource(R.drawable.ic_play_arrow_white_36dp);
		pause = false;
	}
	/*
	 *2-重新得到焦点时调用该方法
	 */
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 用户切换视图回来的时候，无论切换前的状态如何，都保持暂停状态
	}
	/******按钮的点击触发事件******/
	private final class ButtonClickListener implements View.OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			//播放按钮
			case R.id.playBtn:
				if (pause)//之前如果是播放状态
				{
					mediaPlayer.pause();//关闭媒体
					currentPosition = mediaPlayer.getCurrentPosition();
					playbutton.setBackgroundResource(R.drawable.ic_play_arrow_white_36dp);
					pause = false;
				} else
				{
					if (!firstFlag) {
						String filename = VideoActivity.mUri.toString();
						videofile = new File(filename);
						play();
						firstFlag = true;
					} else {
						mediaPlayer.seekTo(currentPosition);
						mediaPlayer.start();//
					}
					playbutton.setBackgroundResource(R.drawable.ic_pause_white_36dp);
					pause = true;
				}
				break;
			//重新启动按钮
			case R.id.resetBtn:
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.seekTo(0);
				} else {
					play();
				}
				break;
			//停止播放按钮
			case R.id.stopBtn:
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
					mediaPlayer.release();
					mediaPlayer = null;
				}
				break;
			default:
				break;
			}
		}
	}
	/*视频播放功能*/
	private void play() {
		try {
			// 重启播放器
			mediaPlayer.reset();
			// 设置播放器的音频，视频和文件路径
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setDisplay(surfaceView.getHolder());
			mediaPlayer.setDataSource(VideoActivity.mUri.toString());
			setup();//播放器的初始化
			mediaPlayer.start();//开启播放器
		} catch (Exception e) {
			System.out.println("play is wrong");
		}
	}
	public String toFotmat(int num) {
		return String.format("%02d", num);
	}
	/*初始化设置*/
	private void setup() {
		init();
		try {
			mediaPlayer.prepare();
			/*
			 * 注册一个回调函数，在视频预处理完成后调用。在视频预处理完成后被调用。此时视频的宽度、高度、
			 * 宽高比信息已经获取到，此时可调用seekTo让视频从指定位置开始播放。
			 */
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(final MediaPlayer mp) {
					seekbar.setMax(mp.getDuration());
					handler.sendEmptyMessage(1);
					playtime.setText(toTime(mp.getCurrentPosition()));
					durationTime.setText(toTime(mp.getDuration()));
					mp.seekTo(currentPosition);
					handler.sendEmptyMessage(2);
					/*sound.setText(toFotmat(CurrentSound) + "/"
							+ toFotmat(MaxSound));*/
				}
			});
		} catch (Exception e) {
			System.out.println("wrong");
		}
	}
	/* 初始化建立消息处理函数 */
	private void init() {
		// 新建一个主线程的handler，它负责监听所有的消息（Message）
		handler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				// 接收初始化的消息后循环更新已播放的时间和进度
				switch (msg.what) {
				case 1:
					//获得当前的播放进度
					if (mediaPlayer != null)
						currentPosition = mediaPlayer.getCurrentPosition();
					//根据播放进度更新UI组件
					seekbar.setProgress(currentPosition);
					playtime.setText(toTime(currentPosition));
					//循环发生消息
					handler.sendEmptyMessage(1);
					break;
					// 接收初始化的消息后循环更新播放音频大小
				case 2:
					mAudioManager = (AudioManager) VideoPlayActivity.this
							.getSystemService(VideoPlayActivity.AUDIO_SERVICE);
					/*MaxSound = mAudioManager
							.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
					CurrentSound = mAudioManager
							.getStreamVolume(AudioManager.STREAM_MUSIC);
					// 根据播放声音大小更新UI组件
					sound.setText(toFotmat(CurrentSound) + "/"
							+ toFotmat(MaxSound));*/
					handler.sendEmptyMessage(2);
					break;
				default:
					break;
				}
			}
		};
	}

	//点击sufaceview,切换控件的显示状态
	public void openView()
	{
		if(isOpen) return;
        linearLayout_iv.setVisibility(View.VISIBLE);
		linearLayout_seekbar.setVisibility(View.VISIBLE);
		isOpen=true;
	}
	public void closeView()
	{
		if(!isOpen) return;
		linearLayout_iv.setVisibility(View.GONE);
		linearLayout_seekbar.setVisibility(View.GONE);
		isOpen=false;
	}
	private void OtherViewChangState() {
		if(isOpen)
		{
			closeView();
		}
		else
		{
			openView();
		}
	}
}