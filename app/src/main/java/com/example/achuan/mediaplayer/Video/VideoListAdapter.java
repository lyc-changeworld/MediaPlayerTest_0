package com.example.achuan.mediaplayer.Video;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.achuan.mediaplayer.R;

/**
 * Created by achuan on 16-4-3.
 */
public class VideoListAdapter extends BaseAdapter
{
    private Cursor mCursor;
    private LayoutInflater mInflater;
    //4-构造器
    public VideoListAdapter(Context mContext, Cursor mCursor) {
        this.mCursor = mCursor;
        mInflater=LayoutInflater.from(mContext);
    }
    //1-需要加载的item的个数
    public int getCount() {
        return mCursor.getCount();
    }
    //2-item对象
    public Object getItem(int position) {
        return mCursor.getPosition();
    }
    //3-item的标号
    public long getItemId(int position) {
        return position;
    }
    //5-获取每一个Item的显示内容
    /***********主要编写该方法实现布局控件的设置************/
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if(convertView==null)
        {
            mViewHolder=new ViewHolder();
            convertView=mInflater.inflate(R.layout.video_item,null);
            //通过viewHolder的对象来获得（子）控件，并保存到viewHolder对象中
            mViewHolder.mTv_name= (TextView) convertView.findViewById(R.id.id_tv_name);
            mViewHolder.mTv_time= (TextView) convertView.findViewById(R.id.id_tv_time);
            convertView.setTag(mViewHolder);//将viewHolder对象与convertView进行关联
        }
        else {
            mViewHolder= (ViewHolder) convertView.getTag();//获得关联的对象（包括缓冲的控件）
        }
        //根据列表项的位置获取媒体库游标的内容
        mCursor.moveToPosition(position);
        mViewHolder.mTv_name.setText(mCursor.getString(0));//拿到视频文件名
        mViewHolder.mTv_time.setText(toTime(mCursor.getInt(1)));//拿到视频的时间
        return convertView;
    }
    /*6-编写内部类：ViewHolder类,避免重复的findViewById(),节约资源*/
    private static class ViewHolder
    {
         public TextView mTv_name;
         public TextView mTv_time;
    }
    //7-将媒体库的时间转换成常规时间
    public String toTime(int time)
    {
        time/=1000;//先转换为秒
        int minute=time/60;//分
        int second=time%60;//秒

        //int hour=minute%60;//时
       /* if(hour!=0)
        {

        }*/
        return String.format("%02d:%02d",minute,second);
    }
}
