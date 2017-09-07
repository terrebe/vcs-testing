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

package com.augumenta.demo.truckster.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.augumenta.agapi.AugumentaManager;
import com.augumenta.agapi.HandPose;
import com.augumenta.agapi.HandPoseEvent;
import com.augumenta.agapi.HandPoseListener;
import com.augumenta.agapi.HandTransition;
import com.augumenta.agapi.HandTransitionEvent;
import com.augumenta.agapi.HandTransitionListener;
import com.augumenta.agapi.Poses;
import com.augumenta.demo.truckster.R;

/**
 * PoseLayout provides interaction capabilities for layouts child Views.
 *
 * Child views can be clicked with P001 -> P032 transitions if draggable mode is turned off.
 * If draggable mode is enabled Views like WebView can be scrolled using grab pose.
 *
 * PoseLayout uses PoseCursorLayout for showing pose cursor on the screen.
 */
public class PoseLayout extends PoseCursorLayout implements HandPoseListener {
	private static final String TAG = PoseLayout.class.getSimpleName();

	// Should child views be dragged or clicked
	private boolean mDraggable = true;

	// Poses used hover hovering and dragging/clicking
	public int POSE_HOVER = Poses.P001;
	public int POSE_TOUCH = Poses.P141;

	public PoseLayout(Context context) {
		this(context, null, -1);
	}

	public PoseLayout(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public PoseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PoseLayout, 0, 0);
		try {
			// read draggable parameter from layout xml
			mDraggable = a.getBoolean(R.styleable.PoseLayout_draggable, mDraggable);
		} finally {
			a.recycle();
		}
	}

	/**
	 * Register for poses used for interactions
	 * @param detman detection manager instance
	 */
	public void registerPoses(AugumentaManager detman) {
		detman.registerListener(touchDownListener, new HandPose(POSE_HOVER, HandPose.HandSide.ALL, 0, 90), new HandPose(POSE_TOUCH, HandPose.HandSide.ALL, 0, 90));
		detman.registerListener(this, POSE_HOVER, HandPose.HandSide.ALL, 0, 90);
		detman.registerListener(this, POSE_TOUCH, HandPose.HandSide.ALL, 0, 90);
	}

	/**
	 * Unregister pose listeners
	 * @param detman
	 */
	public void unregisterPose(AugumentaManager detman) {
		detman.unregisterListener(touchDownListener);
		detman.unregisterListener(this);
	}

	// previous pose, used for tracking pose changes
	private int mPreviousPose = 0;

	/**
	 * Move cursor view based on the pose event
	 * @param event pose event
	 */
	private void moveCursor(HandPoseEvent event) {
		// get event absolute position on the screen
		int x = (int) (margin.translateX(event.rect.centerX()) * getWidth());
		int y = (int) (margin.translateY(event.rect.centerY()) * getHeight());

		int pose = event.handpose.pose();

		// compensate for the shift in position between P001 and P032
		if (pose == POSE_TOUCH) {
			if (mPreviousPose == POSE_HOVER) {
				mCursorDeltaX = mCursorX - x;
				mCursorDeltaY = mCursorY - y;
			}
			x += mCursorDeltaX;
			y += mCursorDeltaY;
		}
		mPreviousPose = pose;
		mCursorX = x;
		mCursorY = y;
	}

	/**
	 * Dispatch click event in the cursor position
	 */
	private void dispatchClickEvent() {
		Log.d(TAG, "Dispatch click: " + mCursorX + ", " + mCursorY);
		long now = SystemClock.uptimeMillis();
		dispatchTouchEvent(MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, mCursorX, mCursorY, 0));
		dispatchTouchEvent(MotionEvent.obtain(now, now + 1, MotionEvent.ACTION_UP, mCursorX, mCursorY, 0));
	}

	// time when touch was started
	private long touch_event_time = -1;

	/**
	 * Dispatch touch down event in the cursor position
	 */
	private void touchDownEvent() {
		final long now = SystemClock.uptimeMillis();
		if (touch_event_time < 0) {
			touch_event_time = now;
			dispatchTouchEvent(MotionEvent.obtain(touch_event_time, now, MotionEvent.ACTION_DOWN, mCursorX, mCursorY, 0));
		} else {
			dispatchTouchEvent(MotionEvent.obtain(touch_event_time, now, MotionEvent.ACTION_MOVE, mCursorX, mCursorY, 0));
		}
	}

	/**
	 * Dispatch touch up event in the cursor position
	 */
	private void touchUpEvent() {
		final long now = SystemClock.uptimeMillis();
		dispatchTouchEvent(MotionEvent.obtain(touch_event_time, now, MotionEvent.ACTION_UP, mCursorX, mCursorY, 0));
		touch_event_time = -1;
	}

	/**
	 * Dispatch touch cancel event
	 */
	private void touchCancelEvent() {
		final long now = SystemClock.uptimeMillis();
		dispatchTouchEvent(MotionEvent.obtain(touch_event_time, now, MotionEvent.ACTION_CANCEL, mCursorX, mCursorY, 0));
		touch_event_time = 1;
	}

	/**
	 * Runnable task for canceling touch
	 */
	private Runnable mCancelTouchTask = new Runnable() {
		@Override
		public void run() {
			touchCancelEvent();
		}
	};

	// cursor position
	private int mCursorX = 0;
	private int mCursorY = 0;

	// offset to cursor position to compensate shift in position when pose changes
	private int mCursorDeltaX = 0;
	private int mCursorDeltaY = 0;

	@Override
	public void onDetected(final HandPoseEvent event, final boolean newdetected) {
		Log.d(TAG, "onDetected: " + event);
		// Call parents onDetected so that pose cursor is shown
		super.onDetected(event, newdetected);

		post(new Runnable() {
			@Override
			public void run() {
				if(newdetected) {
					if (touch_event_time == -1 && event.handpose.pose() == POSE_TOUCH)
						return;

					// if this is a end of the touch, trigger touchUpEvent
					if (event.handpose.pose() == POSE_HOVER && mPreviousPose == POSE_TOUCH) {
						if (mDraggable) {
							touchUpEvent();
						}
					}
					moveCursor(event);

					// if this is a start of the touch, trigger touchDownEvent
					if (event.handpose.pose() == POSE_TOUCH) {
						if (mDraggable) {
							touchDownEvent();
						}
					}

					// remove cancel touch timeout
					removeCallbacks(mCancelTouchTask);
				} else {
					moveCursor(event);

					// dispatch touch down (touch move) event
					if (touch_event_time != -1 && event.handpose.pose() == POSE_TOUCH) {
						if (mDraggable) {
							touchDownEvent();
						}
					}

					// remove cancel touch timeout
					removeCallbacks(mCancelTouchTask);
				}
			}
		});
	}

	@Override
	public void onLost(final HandPoseEvent event) {
		Log.d(TAG, "onLost: " + event);
		// Call parents onLost so that pose cursor is updated
		super.onLost(event);

		if (event.handpose.pose() == POSE_TOUCH) {
			// cancel touch after a delay if touching and pose is lost
			if (mDraggable) {
				postDelayed(mCancelTouchTask, 3000);
			}
		}
	}

	@Override
	public void onMotion(final HandPoseEvent event) {
	}

	public final HandTransitionListener touchDownListener = new HandTransitionListener() {
		@Override
		public void onTransition(final HandTransitionEvent event) {
			post(new Runnable() {
				@Override
				public void run() {
					if (mDraggable) {
						// start dragging
						touchDownEvent();
					} else {
						// dispatch a click
						dispatchClickEvent();
					}
				}
			});
		}
	};
}
