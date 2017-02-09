/*
   Copyright 2012 Harri Smatt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.android.ljm.comic.viewer;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Simple Activity for curl testing.
 * 
 * @author harism
 */
public class CurlActivity extends Activity {

	// Bitmap resources.
	private CurlView mCurlView;
	private static int index;
	private List<File> files;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		getActionBar().hide();
		
		initBitmapData();

		mCurlView = (CurlView) findViewById(R.id.curl);
		mCurlView.setPageProvider(new PageProvider());
		mCurlView.setSizeChangedObserver(new SizeChangedObserver());
		mCurlView.setCurrentIndex(index);
		mCurlView.setBackgroundColor(0xFF202830);

		// This is something somewhat experimental. Before uncommenting next
		// line, please see method comments in CurlView.
		// mCurlView.setEnableTouchPressure(true);
	}

	private void initBitmapData() {
		File file = (File) getIntent().getSerializableExtra("file");
		File path = file.getParentFile();
		File[] imgs = path.listFiles();
		Arrays.sort(imgs);
		files = Arrays.asList(imgs);
		index = files.indexOf(file);
	}

	@Override
	public void onPause() {
		super.onPause();
		mCurlView.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mCurlView.onResume();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return mCurlView.getCurrentIndex();
	}
	
	@Override
	protected void onStop() {
		Toast.makeText(this, files.get(index).getName(), Toast.LENGTH_LONG).show();
		super.onStop();
	}

	/**
	 * Bitmap provider.
	 */
	private class PageProvider implements CurlView.PageProvider {

		@Override
		public int getPageCount() {
			return files.size();
		}

		private Bitmap loadBitmap(int width, int height, int index) {
			Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
			b.eraseColor(0xFFFFFFFF);
			Canvas c = new Canvas(b);
			Drawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeFile(files.get(CurlActivity.index = index).getPath()));

			Rect r = new Rect(0, 0, width, height);

			int imageWidth = r.width();
			int imageHeight = imageWidth * d.getIntrinsicHeight() / d.getIntrinsicWidth();
			if (imageHeight > r.height()) {
				imageHeight = r.height();
				imageWidth = imageHeight * d.getIntrinsicWidth() / d.getIntrinsicHeight();
			}

			r.left += ((r.width() - imageWidth) / 2);
			r.right = r.left + imageWidth;
			r.top += ((r.height() - imageHeight) / 2);
			r.bottom = r.top + imageHeight;

			Paint p = new Paint();
			p.setColor(0xFFC0C0C0);
			c.drawRect(r, p);

			d.setBounds(r);
			d.draw(c);

			return b;
		}

		@Override
		public void updatePage(CurlPage page, int width, int height, int index) {
			Bitmap front = loadBitmap(width, height, index);
			page.setTexture(front, CurlPage.SIDE_FRONT);
		}

	}

	/**
	 * CurlView size changed observer.
	 */
	private class SizeChangedObserver implements CurlView.SizeChangedObserver {
		@Override
		public void onSizeChanged(int w, int h) {
			if (w > h) {
				mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
			} else {
				mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
			}
		}
	}

}