package com.android.ljm.comic.viewer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
public class MainActivity extends ActionBarActivity implements OnClickListener, OnItemClickListener {
	
	private Button btn;
	private ListView listView;
	private SimpleAdapter adapter;
	private File currentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        currentPath = Environment.getExternalStorageDirectory();
        
        initUI();
    }

	private void initUI() {
		btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(this);
		listView = (ListView) findViewById(R.id.listView);
		refreshFilelist(currentPath);
		listView.setOnItemClickListener(this);
		initHeadView();
	}

	private void initHeadView() {
		LinearLayout headLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.list_file, listView, false);
		ImageView img = (ImageView) headLayout.findViewById(R.id.img_file_type);
		TextView tv = (TextView) headLayout.findViewById(R.id.tv_file_name);
		img.setImageResource(R.drawable.folder);
		tv.setText("..");
		headLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refreshFilelist(currentPath.getParentFile());
				currentPath = currentPath.getParentFile();
			}
		});
		listView.addHeaderView(headLayout);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			startActivityForResult(intent, 0);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			Uri uri = data.getData();
			
			File path = new File(uri.getPath()).getParentFile();
			Toast.makeText(this, uri.getPath(), Toast.LENGTH_LONG).show();
			refreshFilelist(path);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void refreshFilelist(File path) {
//		getActionBar().setTitle(path.getName());
		if (path.isDirectory()) {
			path.listFiles();
			File[] array = path.listFiles();
			List<File> folders = new ArrayList<File>();
			List<File> files = new ArrayList<File>();
			for (int i = 0; i < array.length; i++) {
				if (array[i].isDirectory()) {
					folders.add(array[i]);
				} else {
					files.add(array[i]);
				}
			}
			Collections.sort(folders);
			Collections.sort(files);
			List<File> fileList = new ArrayList<File>(folders);
			fileList.addAll(files);
			
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			for (int i = 0; i < array.length; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				if (fileList.get(i).isDirectory()) {
					map.put("type", R.drawable.folder);
				} else {
					map.put("type", R.drawable.file);
				}
				map.put("name", fileList.get(i).getName());
				map.put("file", fileList.get(i));
				list.add(map);
			}
			adapter = new SimpleAdapter(this, list, R.layout.list_file, new String[]{"type", "name"}, new int[]{R.id.img_file_type, R.id.tv_file_name});
			listView.setAdapter(adapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
		File file = (File) map.get("file");
		if (file.isFile()) {
			Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, CurlActivity.class);
			intent.putExtra("file", file);
			startActivity(intent);
		} else {
			refreshFilelist(file);
			currentPath = file;
		}
	}

}
