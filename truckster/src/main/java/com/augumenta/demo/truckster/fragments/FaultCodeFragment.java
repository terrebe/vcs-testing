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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.augumenta.demo.truckster.FaultCodes;
import com.augumenta.demo.truckster.FaultCodes.FaultCode;
import com.augumenta.demo.truckster.R;
import com.augumenta.demo.truckster.views.PoseLayout;
import com.augumenta.agapi.AugumentaManager;
import com.augumenta.agapi.Poses;

/**
 * FaultCodeFragment display the buttons for the FaultCodes
 */
public class FaultCodeFragment extends BaseMenuFragment {
	private static final String TAG = FaultCodeFragment.class.getSimpleName();

	// pose layout handles pose insteractions on layout
	private PoseLayout mPoseLayout;

	private ListView mListView;
	private FaultCodeAdapter mFaultCodeAdapter;

	public FaultCodeFragment() {
		super("fault_code");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mFaultCodeAdapter = new FaultCodeAdapter(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fault_code, container, false);

		mPoseLayout = (PoseLayout) rootView.findViewById(R.id.pose_layout);

		mListView = (ListView) rootView.findViewById(R.id.listview);
		mListView.setAdapter(mFaultCodeAdapter);
		mListView.setOnItemClickListener(mFaultCodeClickListener);

		return rootView;
	}

	@Override
	public void onShown() {
		super.onShown();

		// Register PoseLayout poses
		AugumentaManager detman = AugumentaManager.getInstance(this.getActivity());
		mPoseLayout.registerPoses(detman);

		// use PoseLayout to show P201 cursor
		detman.registerListener(mPoseLayout, Poses.P201);
	}

	@Override
	public void onHide() {
		super.onHide();

		// Unregister PoseLayout poses
		AugumentaManager detman = AugumentaManager.getInstance(this.getActivity());
		mPoseLayout.unregisterPose(detman);

		detman.unregisterListener(mPoseLayout);
	}

	/**
	 * Click listener for fault code list items
	 */
	private OnItemClickListener mFaultCodeClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			FaultCode faultCode = (FaultCode) mFaultCodeAdapter.getItem(position);
			showFragment(FaultCodeDetailFragment.newInstance(faultCode.uri));
		}
	};

	@Override
	public CharSequence getTitle() {
		return TAG;
	}

	/**
	 * Adapter used to populate the fault code list view
	 */
	private class FaultCodeAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mLayoutInflater;

		public FaultCodeAdapter(Context context) {
			super();
			mContext = context;
			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return FaultCodes.codes.length;
		}

		@Override
		public Object getItem(int position) {
			return FaultCodes.codes[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FaultCode faultCode = FaultCodes.codes[position];

			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.view_fault_code_list_item, parent, false);
			}
			TextView codeText = (TextView) convertView.findViewById(R.id.code);
			codeText.setText(faultCode.code);

			TextView descriptionText = (TextView) convertView.findViewById(R.id.description);
			descriptionText.setText(faultCode.description);

			return convertView;
		}
	}
}
