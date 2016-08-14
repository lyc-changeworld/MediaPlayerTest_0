package com.example.achuan.mediaplayer.Video;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.achuan.mediaplayer.R;
import java.util.Vector;

/**
 * Created by achuan on 16-4-4.
 */
public class VideoActivity extends AppCompatActivity
{
    //1-变量初始化
    private ListView mListView;
    private VideoListAdapter mAdapter;
    private AlertDialog ad=null;
    private AlertDialog.Builder builder=null;
    //视频信息存储相关的变量
    public static Uri mUri;//存储视频文件信息
    private Vector<String> mVector;//存储视频文件信息容器
    String mVideoInfo;//存储视频文件信息
    private  int[] mVideoId;//存储视频文件信息编号
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_listview);//加载布局
        /*1-初始化控件*/
        mListView= (ListView) findViewById(R.id.id_video_lv);
        //2-从媒体库中读取视频信息并存入游标
        Cursor mCursor=this.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Video.Media.TITLE,//文件名
                        MediaStore.Video.Media.DURATION,//视频的时间
                        MediaStore.Video.Media._ID,//视频编号
                        MediaStore.Video.Media.DISPLAY_NAME,//视频的名字信息
                        MediaStore.Video.Media.DATA},null,null,null);
        //如果数据库为空，进行相关的提示
        if(mCursor==null||mCursor.getCount()==0)
        {
            builder=new AlertDialog.Builder(this);
            builder.setMessage("存储列表为空...")
                    .setPositiveButton("确定",null);
            ad=builder.create();
            ad.show();
        }
        //3 轮询媒体库，读取视频信息并存入容器
        mCursor.moveToFirst();//游标移动到第一项
        //变量对象化
        mVector =new Vector<String>();
        mVideoId=new int[mCursor.getCount()];
        for (int i = 0; i < mCursor.getCount(); i++) {
            mVideoId[i]=mCursor.getInt(3);//视频编号
            mVector.add(mCursor.getString(4) );//视频的名字信息存储到容器中
            mCursor.moveToNext();//游标继续向后移动
        }
        mAdapter=new VideoListAdapter(this,mCursor);//实例化适配器，引入数据流
        mListView.setAdapter(mAdapter);//为列表添加适配器
        //4-设置列表的单击响应函数
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent=new Intent(VideoActivity.this,VideoPlayActivity.class);
                mIntent.putExtra("mVideoId",mVideoId);
                mIntent.putExtra("postion",position);
                startActivity(mIntent);//启动意图跳转
                mVideoInfo=mVector.get(position);//拿到容器中的视频信息
                mUri=Uri.parse(mVideoInfo);//将视频名称信息生成一个链接流
            }
        });
    }
}
