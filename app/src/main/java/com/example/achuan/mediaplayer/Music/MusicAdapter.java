package com.example.achuan.mediaplayer.Music;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.achuan.mediaplayer.R;

public class MusicAdapter extends BaseAdapter{	 
	List<Integer> musicTime;//歌曲的时间集合
	List<HashMap<String, String>> list;//歌曲的名称信息和路径信息
	LayoutInflater mInflater;//创建布局装载对象来获得相关控件（类似findViewById()）
	//1-创造构造方法
	public MusicAdapter(List musicTime,List list,Context context){
		this.musicTime=musicTime;
		this.list=list;
		//通过context来初始化mInflater动态加载器
	    mInflater=mInflater.from(context);
	}
	//2-适配器中数据集的个数
	public int getCount() {
		return list.size();
	}
    //3-获取数据集中与指定索引对应的数据项
	public Object getItem(int arg0) {
		return list.get(arg0);
	}
	//4-获得指定行对应的ID
	public long getItemId(int arg0) {
		return arg0;
	}
	/*获得每一个Item的显示内容*/
	/******5-主要编写该方法实现布局控件的设置*********/
	public View getView(int position, View Convertview, ViewGroup arg2) {
		ViewHolder viewHolder;//声明内部类
		if(Convertview==null){
			viewHolder= new ViewHolder();//新建实例
			//获得（父）布局，并且加载一个item时只会创建一次view
			Convertview=mInflater.inflate(R.layout.music_item, null);
			//通过viewHolder的对象来获得（子）控件，并保存到viewHolder对象中
			viewHolder.tv1=(TextView) Convertview.findViewById(R.id.songname);
			viewHolder.tv2 = (TextView) Convertview.findViewById(R.id.songtime);
			Convertview.setTag(viewHolder);//将viewHolder对象与convertView进行关联
		}else {
			viewHolder = (ViewHolder) Convertview.getTag();//获得关联的对象（包括缓冲的控件）
		}
		/*歌曲名称显示*/
		HashMap<String, String> hashMap = list.get(position);//获得歌曲信息集合
		String mString=hashMap.get("music");//获得歌曲名称信息
		viewHolder.tv1.setText(removeSuffixString(mString));//去掉名称的后缀
		/*时间显示*/
		int time=0,minute=0,second = 0;
		time=musicTime.get(position);//获得原始的时间信息
		//将原始的时间信息转换为:分/秒
		minute=time/60000;
		second=time%60000/1000;
		if(second<10)
		{
			viewHolder.tv2.setText(minute+":0"+second);
		}
		else{
			viewHolder.tv2.setText(minute+":"+second);
		}
		return Convertview;
	}
	//去掉名称的后缀的方法
	String removeSuffixString(String String)
	{
		String lastString = null;
		//逻辑判断，去掉相关的字符串
				 if(String.indexOf(".mp3")!=-1)//
				 {
					 lastString=String.replace(".mp3", "");
				 }
				 else if(String.indexOf(".m4a")!=-1)
				 {
					 lastString=String.replace(".m4a", "");
				 }
				 else if(String.indexOf(".ape")!=-1)
				 {
					 lastString=String.replace(".ape", "");
				 }
				 else if(String.indexOf(".flac")!=-1)
				 {
					 lastString=String.replace(".flac", "");
				 }
				 return lastString;
	}
	/*编写内部类：ViewHolder类,避免重复的findViewById(),节约资源*/
	private static class ViewHolder{
		public TextView tv1;
		public TextView tv2;
	}
}
