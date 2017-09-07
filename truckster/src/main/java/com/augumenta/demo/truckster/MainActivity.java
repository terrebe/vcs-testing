/*
 * Copyright (c) 2012-2016 Augumenta Ltd. All rights reserved.
 *
 * This source code file is furnished under a limited license and may be used or
 * copied only in accordance with the terms of the license. Except as permitted
 * by the license, no part of this source code file may be  reproduced, stored in
 * a retrieval system, or transmitted, in any form or by  any means, electronic,
 * mechanical, recording, or otherwise, without the prior written permission of
 * Augumenta.
 *
 * This source code file contains proprietary information that is protected by
 * copyright. Certain parts of proprietary information is patent protected. The
 * content herein is furnished for informational use only, is subject to change
 * without notice, and should not be construed as a commitment by Augumenta.
 * Augumenta assumes no responsibility or liability for any errors or
 * inaccuracies that may appear in the informational content contained herein.
 * This source code file has not been thoroughly tested under all conditions.
 * Augumenta, therefore, does not guarantee or imply its reliability,
 * serviceability, or function.
 *
 */

package com.augumenta.demo.truckster;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.augumenta.agapi.AugumentaManager;
import com.augumenta.agapi.CameraFrameProvider;
import com.augumenta.agapi.Poses;
import com.augumenta.demo.truckster.fragments.BaseFragment;
import com.augumenta.demo.truckster.fragments.MenuFragment;

/**
 * MainActivity contains all the Fragments (windows) of the application as well as the logic to
 * initialize the camera and Augumenta's detection manager.
 */
public class MainActivity extends FragmentActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	// On Android 6.0 and later permissions are requested at runtime.
	// See more information about requesting permission at runtime at:
	// https://developer.android.com/training/permissions/requesting.html

	// Permission request code for CAMERA permission
	private static final int PERMISSION_REQUEST_CAMERA = 0;

	private static final String FRAGMENT_TAG = "current_fragment";

	private FragmentManager mFragmentManager;
	private BaseFragment mCurrentFragment;

	private SurfaceView mCameraPreview;

	private AugumentaManager mAugumentaManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d(TAG, "onCreate");

		mCameraPreview = (SurfaceView) findViewById(R.id.camera_preview);

		mFragmentManager = getSupportFragmentManager();

		if (mCurrentFragment == null) {
			popRootFragment();
			mCurrentFragment = new MenuFragment();
		}
		showFragment(mCurrentFragment, false);

		try {
			mAugumentaManager = AugumentaManager.getInstance(this.getApplicationContext());
		} catch (IllegalStateException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		mAugumentaManager.getFrameProvider().setFramesPerSecond(15); /* Fix for ODG camera */
		ServiceTracker.start();

		HandDetectionNotifier.addListener(handDetectionListener);
	}

	public void popFragment() {
		Log.d(TAG, "pop fragment");
		mFragmentManager.popBackStack();
	}

	public void popFragment(String tag) {
		Log.d(TAG, "pop fragment: " + tag);
		mFragmentManager.popBackStack(tag, 0);
	}

	public void popRootFragment() {
		Log.d(TAG, "pop root fragment");
		mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	public void showFragment(BaseFragment fragment, boolean push_to_stack) {
		String tag = fragment.getClass().getSimpleName();
		Log.d(TAG, "Show fragment: " + tag);
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.content, fragment, tag);
		if (push_to_stack) {
			ft.addToBackStack(tag);
		}
		ft.commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");

		// Check if the Camera permission is already available
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
		    != PackageManager.PERMISSION_GRANTED) {
			// Camera permission has not been granted
			requestCameraPermission();
		} else {
			// Camera permission is already available
			// Start detection when activity is resumed
			startAugumentaManager();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");

		mAugumentaManager.stop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

	private void startAugumentaManager() {
		// set camera preview after tutorial has finished
		((CameraFrameProvider) mAugumentaManager.getFrameProvider()).setCameraPreview(mCameraPreview);
		if (!mAugumentaManager.start()) {
			Toast.makeText(this, "Failed to start camera", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private void requestCameraPermission() {
		// Request CAMERA permission from user
		Log.d(TAG, "Requesting CAMERA permission");
		ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, PERMISSION_REQUEST_CAMERA);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == PERMISSION_REQUEST_CAMERA) {
			Log.d(TAG, "Received response for CAMERA permission");

			if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG, "CAMERA permission granted, starting AugumentaManager");
				startAugumentaManager();
			} else {
				Log.d(TAG, "CAMERA permission not granted, exiting..");
				Toast.makeText(this, "Camera permission was not granted.", Toast.LENGTH_LONG).show();
				finish();
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}


	private HandDetectionNotifier.Listener handDetectionListener = new HandDetectionNotifier.Listener() {
		private int preview_width;
		private int preview_height;

		private void setPreviewSize(int width, int height) {
			LayoutParams lp = mCameraPreview.getLayoutParams();
			lp.width = width;
			lp.height = height;
			mCameraPreview.setLayoutParams(lp);
		}

		@Override
		public void onDetection() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mCameraPreview.getWidth() == 1 || mCameraPreview.getHeight() == 1)
						return;

					Log.d(TAG, "HIDE PREVIEW");
					preview_width = mCameraPreview.getWidth();
					preview_height = mCameraPreview.getHeight();
					setPreviewSize(1, 1);
				}
			});
		}

		@Override
		public void onLost() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.d(TAG, "SHOW PREVIEW");
					setPreviewSize(preview_width, preview_height);
				}
			});
		}
	};
}
