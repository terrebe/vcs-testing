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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ServiceTracker contains all the logic behind the tasks for maintenance service. It is written
 * to show how TaskItems and SubTasks can be parsed and displayed sequentially.
 */
public class ServiceTracker {
	private static final String TAG = ServiceTracker.class.getSimpleName();

	private static ServiceTracker mCurrent = null;
	private static ServiceTracker mPrevious = null;

	private long mStartTime;
	private long mEndTime = -1;

	private int mCurrentTask = 0;
	private List<TaskItem> mServiceTasks = new ArrayList<TaskItem>();

	public interface OnServiceChanged {
		void onStart(ServiceTracker serviceTracker);
		void onFinish(ServiceTracker serviceTracker);
		void onTaskChanged(ServiceTracker serviceTracker, TaskItem task);
	}

	public static OnServiceChanged mOnServiceChangedListener;
	public static void setOnServiceChangedListener(OnServiceChanged listener) {
		mOnServiceChangedListener = listener;
	}

	public static ServiceTracker current() {
		return mCurrent;
	}

	public static ServiceTracker previous() {
		return mPrevious;
	}

	public static ServiceTracker start() {
		mCurrent = new ServiceTracker();
		if (mOnServiceChangedListener != null) {
			mOnServiceChangedListener.onStart(mCurrent);
		}
		return mCurrent;
	}

	public static void stop() {
		if (mCurrent != null) {
			mCurrent.mEndTime = System.currentTimeMillis();
			mPrevious = mCurrent;
			mCurrent = null;

			if (mOnServiceChangedListener != null) {
				mOnServiceChangedListener.onFinish(mPrevious);
			}
		}
	}

	private ServiceTracker() {
		mStartTime = System.currentTimeMillis();
		mServiceTasks.addAll(Arrays.asList(TASKS_LT_50K));
	}

	public long getStartTime() {
		return mStartTime;
	}

	public long getEndTime() {
		return mEndTime;
	}

	public long getTimeSpent() {
		return mEndTime - mStartTime;
	}

	public int getCurrentTask() {
		return mCurrentTask;
	}

	public void setCurrentTask(int current_task) {
		mCurrentTask = current_task;
	}

	public TaskItem getTask(int position) {
		return mServiceTasks.get(position);
	}

	public int getTaskCount() {
		return mServiceTasks.size();
	}

	private static final TaskItem[] TASKS_LT_50K = new TaskItem[] {
		new TaskItem("Brake Components", new SubTaskItem[] {
                new SubTaskItem("Inspection target", "Verify that brakes are in acceptable condition",-1),
				new SubTaskItem("Drum brake diagram", "", R.drawable.fig_drum_brake),
				new SubTaskItem("Disc brake diagram", "", R.drawable.fig_disc_brake),
                new SubTaskItem("Checkpoint", "Verify that brake mountings, cables and pivots are not missing, loose, excessively worn or binding.",-1),
                new SubTaskItem("Checkpoint", "Verify that brake pipes don't have visible signs of leaking, swelling or bulging.",-1),
                new SubTaskItem("Checkpoint", "Verify that brake discs or drums don't have missing pieces or cracks.",-1),
                new SubTaskItem("Checkpoint", "Verify that brake discs are not worn beyond manufacturer specifications.",-1),
        }),
		new TaskItem("Tow Devices", new SubTaskItem[] {
                new SubTaskItem("Inspection target", "Verify that tow devices are in acceptable condition",-1),
				new SubTaskItem("Drawbar diagram", "Wear surfaces of a drawbar", R.drawable.fig_drawbar),
				new SubTaskItem("Pintle diagram", "Wear surfaces of a pintle", R.drawable.fig_pintle),
				new SubTaskItem("Towing eye diagram", "Wear surfaces of a towing eye", R.drawable.fig_towing),
                new SubTaskItem("Checkpoint", "Verify that tow device wear surfaces are not worn more than 10% of original diameter",-1),
                new SubTaskItem("Checkpoint", "Verify that tow device does not have cracks",-1),
                new SubTaskItem("Checkpoint", "Verify that tow device does not have advanced corrosion",-1),

        }),
            new TaskItem("Suspension", new SubTaskItem[]{
                    new SubTaskItem("Inspection target", "Verify that car suspension is in acceptable condition", -1),
                    new SubTaskItem("Suspension diagram", "", R.drawable.fig_suspension),
                    new SubTaskItem("Checkpoint", "Verify that springs are not broken and don't have cracks.", -1),
                    new SubTaskItem("Checkpoint", "Verify that shock absorbers are not loose or leaking.", -1),
                    new SubTaskItem("Checkpoint", "Verify that all suspension components are correctly aligned.", -1),
                    new SubTaskItem("Checkpoint", "Verify that no suspension components have been fixed by welding.", -1),

            }),
	};
	public static class TaskItem {
		public static final int STATUS_PENDING = 1;
		public static final int STATUS_PROGRESS = 2;
		public static final int STATUS_COMPLETED = 3;

		public String title;
		public int status = STATUS_PENDING;
		public SubTaskItem[] sub_tasks;

		public TaskItem(String title, SubTaskItem[] sub_tasks) {
			this.title = title;
			this.sub_tasks = sub_tasks;
		}

		public int getSubTaskCount() {
			return sub_tasks.length;
		}

		public int getSubTaskDoneCount() {
			int done_count = 0;
			for (SubTaskItem item : sub_tasks) {
				if (item.done)
					done_count++;
			}
			return done_count;
		}

		public int getNextSubTask() {
			for (int i = 0; i < sub_tasks.length; i++) {
				if (!sub_tasks[i].done)
					return i;
			}
			return -1;
		}

		public void reset() {
			status = STATUS_PENDING;
			for (SubTaskItem item : sub_tasks) {
				item.done = false;
			}
		}
	}

	public static class SubTaskItem {
		public String title;
		public String description;
		public int image = -1;
		public boolean done;

		public SubTaskItem(String title, String description, int image) {
			this.title = title;
			this.description = description;
			this.image = image;
		}
	}
}
