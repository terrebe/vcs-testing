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

package com.augumenta.demo.truckster.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.augumenta.demo.truckster.R;
import com.augumenta.demo.truckster.ServiceTracker;
import com.augumenta.demo.truckster.ServiceTracker.TaskItem;
import com.augumenta.demo.truckster.views.PoseLayout;
import com.augumenta.agapi.AugumentaManager;
import com.augumenta.agapi.Poses;

/**
 * TaskFragment displays the list of tasks needed to be done by the mechanic for maintenance.
 */
public class TaskFragment extends BaseMenuFragment {
	private static final String TAG = TaskFragment.class.getSimpleName();

	private PoseLayout mPoseLayout;

	private TextView mTaskNameText;
	private GridView mTaskListView;
	private TaskListAdapter mTaskListAdapter;

	private ServiceTracker mServiceTracker;

	public TaskFragment() {
		super("tasklist");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mServiceTracker = ServiceTracker.current();
		mTaskListAdapter = new TaskListAdapter(getActivity(), mServiceTracker);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);

		mPoseLayout = (PoseLayout) rootView.findViewById(R.id.pose_layout);

		mTaskNameText = (TextView) rootView.findViewById(R.id.task_title);

		mTaskListView = (GridView) rootView.findViewById(R.id.task_list);
		mTaskListView.setAdapter(mTaskListAdapter);
		mTaskListView.setOnItemClickListener(mTaskClickListener);

		Log.d(TAG, "Task count: " + mTaskListAdapter.getCount());

		return rootView;
	}

	@Override
	public void onShown() {
		super.onShown();
		AugumentaManager detman = AugumentaManager.getInstance(this.getActivity());
		mPoseLayout.registerPoses(detman);

		detman.registerListener(mPoseLayout, Poses.P201);

		mTaskNameText.setText(getResources().getString(R.string.task_title_text));
	}

	@Override
	public void onHide() {
		super.onHide();
		AugumentaManager detman = AugumentaManager.getInstance(this.getActivity());
		mPoseLayout.unregisterPose(detman);

		detman.unregisterListener(mPoseLayout);
	}

	@Override
	public CharSequence getTitle() {
		return TAG;
	}

	private OnItemClickListener mTaskClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			TaskItem item = (TaskItem) mTaskListAdapter.getItem(position);
			if (item.status == TaskItem.STATUS_PENDING)
				item.status = TaskItem.STATUS_PROGRESS;
			showFragment(SubTaskPagerFragment.newInstance(item));
		}
	};

	private class TaskListAdapter extends BaseAdapter {
		private ServiceTracker mServiceTracker;
		private Context mContext;
		private LayoutInflater mLayoutInflater;

		public TaskListAdapter(Context context, ServiceTracker serviceTracker) {
			super();
			mServiceTracker = serviceTracker;
			mContext = context;
			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mServiceTracker.getTaskCount();
		}

		@Override
		public Object getItem(int position) {
			return mServiceTracker.getTask(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TaskItem item = (TaskItem) getItem(position);

			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.view_task_item, parent, false);
			}
			TextView titleText = (TextView) convertView;
			titleText.setText(item.title);

			switch (item.status) {
				case TaskItem.STATUS_COMPLETED:
					titleText.setBackgroundResource(R.drawable.task_background_completed);
					titleText.setTextColor(Color.BLACK);
					break;
				case TaskItem.STATUS_PROGRESS:
					titleText.setBackgroundResource(R.drawable.task_background_progress);
					titleText.setTextColor(Color.BLACK);
					break;
				default:
					titleText.setBackgroundResource(R.drawable.task_background_pending);
					titleText.setTextColor(Color.WHITE);
					break;
			}

			return convertView;
		}
	}
}
