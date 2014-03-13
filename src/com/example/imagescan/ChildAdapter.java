package com.example.imagescan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.imagescan.MyImageView.OnMeasureListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class ChildAdapter extends BitmapCacheAdapter<String> {
	private int mImageViewWidth;
	private int mImageViewHeight;
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();

	public ChildAdapter(Context context, List<String> list) {
		super(context, list);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		String path = list.get(position);
		
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.grid_child_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
			viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);
			
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
		
		viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!mSelectMap.containsKey(position) || !mSelectMap.get(position)){
					addAnimation(viewHolder.mCheckBox);
				}
				
				System.out.println("position = " + position + "   isChecked  = " + isChecked);
					
				mSelectMap.put(position, isChecked);
			}
		});
		
		setImageView(viewHolder.mImageView, path, mImageViewWidth, mImageViewHeight);
		viewHolder.mCheckBox.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);
		
		return convertView;
	}
	
	/**
	 * 给CheckBox加点击动画
	 * @param view
	 */
	private void addAnimation(View view){
		float [] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules), 
				ObjectAnimator.ofFloat(view, "scaleY", vaules));
				set.setDuration(150);
		set.start();
	}
	
	
	/**
	 * 获取选中的Item的position
	 * @return
	 */
	public List<Integer> getSelectItems(){
		List<Integer> list = new ArrayList<Integer>();
		for(Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();){
			Map.Entry<Integer, Boolean> entry = it.next();
			if(entry.getValue()){
				list.add(entry.getKey());
			}
		}
		
		return list;
	}
	
	
	public static class ViewHolder{
		public MyImageView mImageView;
		public CheckBox mCheckBox;
	}

}
