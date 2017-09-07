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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.augumenta.demo.truckster.R;
import com.augumenta.demo.truckster.ServiceTracker.SubTaskItem;
import com.augumenta.demo.truckster.ServiceTracker.TaskItem;
import com.augumenta.demo.truckster.views.PoseCursorLayout;
import com.augumenta.demo.truckster.views.TaskBalloonView;
import com.augumenta.agapi.AugumentaManager;
import com.augumenta.agapi.Poses;
import com.augumenta.agapi.HandPose;
import com.augumenta.agapi.HandTransition;
import com.augumenta.agapi.HandTransitionEvent;
import com.augumenta.agapi.HandTransitionListener;

/**
 * SubTaskPagerFragment displays the subtasks content as a full page.
 */
public class SubTaskPagerFragment extends BaseMenuFragment {
	private static final String TAG = SubTaskPagerFragment.class.getSimpleName();

	private ViewPager mSubTaskPager;
	private SubTaskPagerAdapter mSubTaskAdapter;

	private PoseCursorLayout mPoseCursorLayout;

	private TextView mTaskTitleText;
	private TaskBalloonView mTaskBalloonView;

	private TaskItem mTaskItem;

	public static SubTaskPagerFragment newInstance(TaskItem taskItem) {
		SubTaskPagerFragment fragment = new SubTaskPagerFragment();
		fragment.mTaskItem = taskItem;
		return fragment;
	}

	public SubTaskPagerFragment() {
		super("tasks");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSubTaskAdapter = new SubTaskPagerAdapter(getChildFragmentManager(), mTaskItem);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_subtasks, container, false);

		mPoseCursorLayout = (PoseCursorLayout) rootView.findViewById(R.id.cursor_layout);

		mSubTaskPager = (ViewPager) rootView.findViewById(R.id.task_pager);
		mSubTaskPager.setAdapter(mSubTaskAdapter);

		mTaskTitleText = (TextView) rootView.findViewById(R.id.title);
		mTaskTitleText.setText(mTaskItem.title);

		mTaskBalloonView = (TaskBalloonView) rootView.findViewById(R.id.balloon_view);
		mTaskBalloonView.setTaskItem(mTaskItem);

		return rootView;
	}

	@Override
	public void onShown() {
		super.onShown();

		mSubTaskPager.setCurrentItem(mTaskItem.getNextSubTask());
		mTaskBalloonView.update();

		AugumentaManager detman = AugumentaManager.getInstance(this.getActivity());
		detman.registerListener(mNextPoseListener, new HandPose(Poses.P201), new HandPose(Poses.P141, HandPose.HandSide.ALL, 0, 90));
		detman.registerListener(mPoseCursorLayout, Poses.P201);
	}

	@Override
	public void onHide() {
		super.onHide();

		AugumentaManager detman = AugumentaManager.getInstance(this.getActivity());
		detman.unregisterListener(mNextPoseListener);
		detman.unregisterListener(mPoseCursorLayout);
	}

	@Override
	public CharSequence getTitle() {
		return TAG;
	}

	private HandTransitionListener mNextPoseListener = new HandTransitionListener() {
		@Override
		public void onTransition(HandTransitionEvent event) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					int current_page = mSubTaskPager.getCurrentItem();
					mSubTaskAdapter.getSubTask(current_page).done = true;
					mTaskBalloonView.setBalloonAt(current_page, true);
					current_page++;
					if (current_page < mSubTaskAdapter.getCount()) {
						mSubTaskPager.setCurrentItem(current_page);
					} else {
						Log.d(TAG, "Task done: " + mTaskItem.title);
						Toast.makeText(getActivity(), "Task done: " + mTaskItem.title, Toast.LENGTH_SHORT).show();
						mTaskItem.status = TaskItem.STATUS_COMPLETED;
						pop();
					}
				}
			});
		}
	};

	private class SubTaskPagerAdapter extends FragmentPagerAdapter {
		private TaskItem mTaskItem;

		public SubTaskPagerAdapter(FragmentManager fm, TaskItem taskItem) {
			super(fm);
			mTaskItem = taskItem;
		}

		public SubTaskItem getSubTask(int position) {
			return mTaskItem.sub_tasks[position];
		}

		@Override
		public Fragment getItem(int position) {
			return SubTaskFragment.newInstance(mTaskItem.sub_tasks[position]);
		}

		@Override
		public int getCount() {
			return mTaskItem.sub_tasks.length;
		}
	}

	public static class SubTaskFragment extends Fragment {
		private SubTaskItem mSubTaskItem;
		
		public static SubTaskFragment newInstance(SubTaskItem subTaskItem) {
			SubTaskFragment fragment = new SubTaskFragment();
			fragment.mSubTaskItem = subTaskItem;
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_subtask_item, container, false);

			TextView titleText = (TextView) rootView.findViewById(R.id.title);
			titleText.setText(mSubTaskItem.title);

			TextView descriptionText = (TextView) rootView.findViewById(R.id.description);
			if (mSubTaskItem.description != null) {
				descriptionText.setText(mSubTaskItem.description);
			} else {
				descriptionText.setVisibility(View.GONE);
			}

			ImageView image = (ImageView) rootView.findViewById(R.id.image);
			if (mSubTaskItem.image != -1) {
				image.setImageResource(mSubTaskItem.image);
			} else {
				image.setVisibility(View.GONE);
			}

			return rootView;
		}
	}
}
