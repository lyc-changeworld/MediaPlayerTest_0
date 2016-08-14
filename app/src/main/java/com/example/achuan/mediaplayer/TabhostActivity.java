package com.example.achuan.mediaplayer;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TabHost;
import com.example.achuan.mediaplayer.Music.MusicActivity;
import com.example.achuan.mediaplayer.Video.VideoActivity;

import java.lang.ref.WeakReference;

public class TabhostActivity extends TabActivity{
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabhost);
        //获取TabHost布局
        TabHost tabHost=this.getTabHost();
        TabHost.TabSpec spec;//选项卡指示符
        Intent intent;//声明意图
        /*加载音乐播放器*/
        intent=new Intent().setClass(TabhostActivity.this,MusicActivity.class);//创建意图
        spec=tabHost.newTabSpec("Music").//实例化一个分页
                setIndicator("Music").//设置此分页显示的标题
                setContent(intent);//指定一个加载activity的Intent对象作为选项卡内容
        tabHost.addTab(spec);//菜单中添加spec分页
        /*加载视频播放器*/
        intent=new Intent().setClass(TabhostActivity.this,VideoActivity.class);//创建意图
        spec=tabHost.newTabSpec("Video").//实例化一个分页
                setIndicator("Video").//设置此分页显示的标题
                setContent(intent);//指定一个加载activity的Intent对象作为选项卡内容
        tabHost.addTab(spec);//菜单中添加spec分页
        //设置当前的显示页面卡
        tabHost.setCurrentTab(0);
    }

}
