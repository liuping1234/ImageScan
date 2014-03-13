package com.example.imagescan;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.imagescan.MainActivity.ImageBean;
import com.example.imagescan.MyImageView.OnMeasureListener;

public class GroupAdapter extends BitmapCacheAdapter<ImageBean>{
	private int mImageViewWidth;
	private int mImageViewHeight;
	
	public GroupAdapter(Context context, List<ImageBean> list){
		super(context, list);
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		ImageBean mImageBean = list.get(position);
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.grid_group_item, null);
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.group_image);
			viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.group_title);
			viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.group_count);
			
			viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {
				
				@Override
				public void onMeasureSize(int width, int height) {
					mImageViewWidth = width;
					mImageViewHeight = height;
				}
			});
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.mTextViewTitle.setText(mImageBean.getTitle());
		viewHolder.mTextViewCounts.setText(Integer.toString(mImageBean.getCounts()));
		setImageView(viewHolder.mImageView, mImageBean.getTopImagePath(), mImageViewWidth, mImageViewHeight);
		
		return convertView;
	}
	
	
	
	public static class ViewHolder{
		public MyImageView mImageView;
		public TextView mTextViewTitle;
		public TextView mTextViewCounts;
	} 

	
}
