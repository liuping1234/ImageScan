package com.example.imagescan;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
	private List<ImageBean> list = new ArrayList<MainActivity.ImageBean>();
	private final static int SCAN_OK = 1;
	private ProgressDialog mProgressDialog;
	private GroupAdapter adapter;
	private GridView mGroupGridView;
	
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:
				//�رս�����
				mProgressDialog.dismiss();
				
				adapter = new GroupAdapter(MainActivity.this, list = subGroupOfImage(mGruopMap));
				mGroupGridView.setAdapter(adapter);
				break;
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mGroupGridView = (GridView) findViewById(R.id.main_grid);
		
		getImages();
		
		mGroupGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				List<String> childList = mGruopMap.get(list.get(position).getTitle());
				
				Intent mIntent = new Intent(MainActivity.this, ShowImageActivity.class);
				mIntent.putStringArrayListExtra("data", (ArrayList<String>)childList);
				startActivity(mIntent);
				
			}
		});
		
	}


	/**
	 * ����ContentProviderɨ���ֻ��е�ͼƬ���˷��������������߳���
	 */
	private void getImages() {
		//��ʾ������
		mProgressDialog = ProgressDialog.show(this, null, "���ڼ���...");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = MainActivity.this.getContentResolver();

				//ֻ��ѯjpeg��png��ͼƬ
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_TAKEN);
				
				if(mCursor == null){
					return;
				}
				
				while (mCursor.moveToNext()) {
					//��ȡͼƬ��·��
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					
					//��ȡ��ͼƬ�ĸ�·����
					String parentName = new File(path).getParentFile().getName();

					
					//���ݸ�·������ͼƬ���뵽mGruopMap��
					if (!mGruopMap.containsKey(parentName)) {
						List<String> chileList = new ArrayList<String>();
						chileList.add(path);
						mGruopMap.put(parentName, chileList);
					} else {
						mGruopMap.get(parentName).add(path);
					}
				}
				
				//֪ͨHandlerɨ��ͼƬ���
				mHandler.sendEmptyMessage(SCAN_OK);
				
			}
		}).start();
		
	}
	
	
	/**
	 * ��װ�������GridView������Դ����Ϊ����ɨ���ֻ���ʱ��ͼƬ��Ϣ����HashMap��
	 * ������Ҫ����HashMap��������װ��List
	 * 
	 * @param mGruopMap
	 * @return
	 */
	private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap){
		if(mGruopMap.size() == 0){
			return null;
		}
		List<ImageBean> list = new ArrayList<MainActivity.ImageBean>();
		
		Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();
			
			mImageBean.setTitle(key);
			mImageBean.setCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));//��ȡ����ĵ�һ��ͼƬ
			
			list.add(mImageBean);
		}
		
		return list;
		
	}

	
	/**
	 * GridView��ÿ��item�����ݶ���
	 * 
	 * @author len
	 *
	 */
	public static class ImageBean{
		private String topImagePath;//�ķ����еĶ���ͼƬ·��
		private String title; //�÷���ı���
		private int counts; //�÷�����ͼƬ�ĸ���
		
		public String getTopImagePath() {
			return topImagePath;
		}
		public void setTopImagePath(String topImagePath) {
			this.topImagePath = topImagePath;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getCounts() {
			return counts;
		}
		public void setCounts(int counts) {
			this.counts = counts;
		}
		
	}

}
