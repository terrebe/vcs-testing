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
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.augumenta.demo.truckster.HandDetectionNotifier;
import com.augumenta.demo.truckster.R;
import com.augumenta.demo.truckster.utils.RelativeMargin;
import com.augumenta.agapi.Poses;
import com.augumenta.agapi.HandPose.HandSide;
import com.augumenta.agapi.HandPoseEvent;
import com.augumenta.agapi.HandPoseListener;

/**
 * PoseCursorLayout is a layout to show pose images on the screen.
 */
public class PoseCursorLayout extends RelativeLayout implements HandPoseListener {

	public static final float MARGIN_TOP = -0.2f;
	public static final float MARGIN_BOTTOM = -0.2f;
	public static final float MARGIN_LEFT = -0.2f;
	public static final float MARGIN_RIGHT = -0.2f;

	// RelativeMargin maps the camera view so that it's possible to interact with views near
	// the edges
	public final RelativeMargin margin = new RelativeMargin(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM);

	private static final SparseArray<Integer> POSE_CURSORS = new SparseArray<>();
	static {
		POSE_CURSORS.put(Poses.P001, R.drawable.p001);
		POSE_CURSORS.put(Poses.P008, R.drawable.p008);
		POSE_CURSORS.put(Poses.P032, R.drawable.p032);
		POSE_CURSORS.put(Poses.P141, R.drawable.p032);
		POSE_CURSORS.put(Poses.P201, R.drawable.p201);
	}

	// Views showning the poses, left and right
	private ImageView mCursorViewRight;
	private ImageView mCursorViewLeft;

	public PoseCursorLayout(Context context) {
		this(context, null, -1);
	}

	public PoseCursorLayout(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public PoseCursorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		// create cursor views for both left and right hands
		mCursorViewLeft = createCursorView(context);
		mCursorViewRight = createCursorView(context);
		// mirror right hand cursor view
		mCursorViewRight.setRotationY(180);

		addView(mCursorViewLeft);
		addView(mCursorViewRight);
	}

	/**
	 * Create default pose cursor view
	 * @param context context
	 * @return cursor view
	 */
	private ImageView createCursorView(Context context) {
		ImageView cursor = new ImageView(context);
		cursor.setVisibility(View.GONE);

		int size = getResources().getDimensionPixelSize(R.dimen.pose_cursor_size);
		LayoutParams param = new LayoutParams(size, size);
		cursor.setLayoutParams(param);

		return cursor;
	}

	/**
	 * Show/hide cursor views based on handside
	 * @param handside left/right handside
	 * @param visible view visible
	 */
	public void setCursorVisible(HandSide handside, boolean visible) {
		if (handside == HandSide.LEFT && mCursorViewLeft != null) {
			mCursorViewLeft.setVisibility(visible ? View.VISIBLE : View.GONE);
		}

		if (handside == HandSide.RIGHT && mCursorViewRight != null) {
			mCursorViewRight.setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		// ensure that cursor views are in front
		if (mCursorViewLeft != null) {
			mCursorViewLeft.bringToFront();
		}
		if (mCursorViewRight != null) {
			mCursorViewRight.bringToFront();
		}
	}

	/**
	 * Update cursor views based on the event
	 * Positions the cursor on the screen and selects the view to use
	 * based on the handside.
	 * @param event pose event
	 */
	private void updateCursor(HandPoseEvent event) {
		ImageView view = null;
		if (event.handpose.handside() == HandSide.LEFT) {
			view = mCursorViewLeft;
		}
		if (event.handpose.handside() == HandSide.RIGHT) {
			view = mCursorViewRight;
		}

		if (view != null) {
			int x = (int) (margin.translateX(event.rect.centerX()) * getWidth());
			int y = (int) (margin.translateY(event.rect.centerY()) * getHeight());
			int pose = event.handpose.pose();

			MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
			params.leftMargin = x - (view.getWidth() / 2);
			params.topMargin = y - (view.getHeight() / 2);
			view.setLayoutParams(params);

			view.setImageResource(POSE_CURSORS.get(pose, R.drawable.fallback_cursor));
		}
	}

	@Override
	public void onDetected(final HandPoseEvent event, final boolean newdetection) {
		if(newdetection) {
			HandDetectionNotifier.notifyDetected();
		}

		post(new Runnable() {
			@Override
			public void run() {
				// new pose, show cursor
				updateCursor(event);
				if(newdetection) {
					setCursorVisible(event.handpose.handside(), true);
				}
			}
		});
	}

	@Override
	public void onLost(final HandPoseEvent event) {
		HandDetectionNotifier.notifyLost();

		post(new Runnable() {
			@Override
			public void run() {
				// pose lost, hide cursor
				setCursorVisible(event.handpose.handside(), false);
			}
		});

	}

	@Override
	public void onMotion(final HandPoseEvent event) {
	}
}
