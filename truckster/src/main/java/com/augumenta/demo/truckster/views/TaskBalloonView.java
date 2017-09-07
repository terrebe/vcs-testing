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
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.augumenta.demo.truckster.R;
import com.augumenta.demo.truckster.ServiceTracker.SubTaskItem;
import com.augumenta.demo.truckster.ServiceTracker.TaskItem;

/**
 * Progress indicator for showing tasks progress
 */
public class TaskBalloonView extends LinearLayout {

	private static final int DRAWABLE_DONE = R.drawable.pointer_spot_touch;
	private static final int DRAWABLE_NOT_DONE = R.drawable.pointer_spot_hover;

	private static final int BALLOON_SIZE = 20;

	private TaskItem mTaskItem;

	public TaskBalloonView(Context context) {
		super(context);
	}

	public TaskBalloonView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TaskBalloonView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setTaskItem(TaskItem item) {
		mTaskItem = item;
		update();
	}

	public void update() {
		if (mTaskItem == null)
			return;

		int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BALLOON_SIZE, getResources().getDisplayMetrics());

		removeAllViews();
		for (SubTaskItem sub_item : mTaskItem.sub_tasks) {
			View balloon = new View(getContext());
			balloon.setBackgroundResource(sub_item.done ? DRAWABLE_DONE : DRAWABLE_NOT_DONE);
			balloon.setLayoutParams(new LayoutParams(size, size));
			addView(balloon);
		}
	}

	public void setBalloonAt(int position, boolean done) {
		View view = getChildAt(position);
		view.setBackgroundResource(mTaskItem.sub_tasks[position].done ? DRAWABLE_DONE : DRAWABLE_NOT_DONE);
	}
}
