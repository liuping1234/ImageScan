package com.example.imagescan;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public abstract class BitmapCacheAdapter<T> extends BaseAdapter {
	protected List<T> list;
	protected LruCache<String, Bitmap> mMemoryCache;
	protected LayoutInflater mInflater;
	
	public BitmapCacheAdapter(Context context, List<T> list){
		this.list = list;
		mInflater = LayoutInflater.from(context);
		
		//��ȡӦ�ó��������ڴ�
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		//������ڴ��1/4���洢ͼƬ
		final int cacheSize = maxMemory / 4;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			
			//��ȡÿ��ͼƬ�Ĵ�С
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
			}
		};
	}
	
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	/**
	 * ����ͼƬ��ImageView��
	 * @param mImageView
	 * @param path
	 */
	public void setImageView(ImageView mImageView, String path, int viewWidth, int viewHeight){
		//�ȴ��ڴ��л�ȡBitmap
		Bitmap bitmap = getBitmapFromMemCache(path);
		if(bitmap == null){
			//û�оʹ�File�л�ȡ����ͼ
			bitmap = decodeThumbBitmapForFile(path, viewWidth, viewHeight);
			addBitmapToMemoryCache(path, bitmap);
		}
		
		//���õ�ImageView����
		mImageView.setImageBitmap(bitmap);
	}
	
	/**
	 * ���ڴ滺�������Bitmap
	 * 
	 * @param key
	 * @param bitmap
	 */
	protected void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null && bitmap != null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * ����key����ȡ�ڴ��е�ͼƬ
	 * @param key
	 * @return
	 */
	protected Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}
	
	/**
	 * ����View(��Ҫ��ImageView)�Ŀ�͸�����ȡͼƬ������ͼ
	 * @param path
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	protected Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight){
		BitmapFactory.Options options = new BitmapFactory.Options();
		//����Ϊtrue,��ʾ����Bitmap���󣬸ö���ռ�ڴ�
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		//�������ű���
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);
		
		//����Ϊfalse,����Bitmap������뵽�ڴ���
		options.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeFile(path, options);
	}
	
	
	/**
	 * ����View(��Ҫ��ImageView)�Ŀ�͸�������Bitmap���ű�����Ĭ�ϲ�����
	 * @param options
	 * @param width
	 * @param height
	 */
	protected int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight){
		int inSampleSize = 1;
		if(viewWidth == 0 || viewWidth == 0){
			return inSampleSize;
		}
		int bitmapWidth = options.outWidth;
		int bitmapHeight = options.outHeight;
		
		//����Bitmap�Ŀ�Ȼ�߶ȴ��������趨ͼƬ��View�Ŀ�ߣ���������ű���
		if(bitmapWidth > viewWidth || bitmapHeight > viewWidth){
			int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
			int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);
			
			//Ϊ�˱�֤ͼƬ�����ű��Σ�����ȡ��߱�����С���Ǹ�
			inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
		return inSampleSize;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent) ;

}
