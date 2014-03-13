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
		
		//获取应用程序的最大内存
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		//用最大内存的1/4来存储图片
		final int cacheSize = maxMemory / 4;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			
			//获取每张图片的大小
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
	 * 设置图片到ImageView中
	 * @param mImageView
	 * @param path
	 */
	public void setImageView(ImageView mImageView, String path, int viewWidth, int viewHeight){
		//先从内存中获取Bitmap
		Bitmap bitmap = getBitmapFromMemCache(path);
		if(bitmap == null){
			//没有就从File中获取缩略图
			bitmap = decodeThumbBitmapForFile(path, viewWidth, viewHeight);
			addBitmapToMemoryCache(path, bitmap);
		}
		
		//设置到ImageView上面
		mImageView.setImageBitmap(bitmap);
	}
	
	/**
	 * 往内存缓存中添加Bitmap
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
	 * 根据key来获取内存中的图片
	 * @param key
	 * @return
	 */
	protected Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}
	
	/**
	 * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
	 * @param path
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	protected Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight){
		BitmapFactory.Options options = new BitmapFactory.Options();
		//设置为true,表示解析Bitmap对象，该对象不占内存
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		//设置缩放比例
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);
		
		//设置为false,解析Bitmap对象加入到内存中
		options.inJustDecodeBounds = false;
		
		return BitmapFactory.decodeFile(path, options);
	}
	
	
	/**
	 * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
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
		
		//假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
		if(bitmapWidth > viewWidth || bitmapHeight > viewWidth){
			int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
			int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);
			
			//为了保证图片不缩放变形，我们取宽高比例最小的那个
			inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
		return inSampleSize;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent) ;

}
