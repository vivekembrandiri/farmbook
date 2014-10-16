package com.mypackage;

import com.utilitiespackage.CustomHttpClient;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageAdapter extends BaseAdapter {

	private Context context;
	private String list[];
	
	public ImageAdapter(Context c) {
		this.context = c;
	}
	
	public ImageAdapter(Context c, String list[]) {
		this.context = c;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		return list.length;
	}
	
	@Override
	public Object getItem(int position) {
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {  
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(60, 60));
			imageView.setScaleType(ScaleType.CENTER_INSIDE);
		} else {
			imageView = (ImageView) convertView;
		}
		
		imageView.setImageBitmap(CustomHttpClient.downloadImage(list[position]+"/profilepic.jpg"));
		return imageView;
	}


}
