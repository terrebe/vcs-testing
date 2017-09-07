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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.augumenta.demo.truckster.R;
import com.augumenta.demo.truckster.views.CompoundButton;
import com.augumenta.demo.truckster.views.PoseLayout;
import com.augumenta.agapi.AugumentaManager;

/**
 * MenuFragment shown pose selectable buttons
 */
public class MenuFragment extends BaseFragment {
	private static final String TAG = MenuFragment.class.getSimpleName();

	// pose layout handles the interaction with poses in layout
	private PoseLayout mPoseLayout;

	public MenuFragment() {
		super("menu");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

		mPoseLayout = (PoseLayout) rootView.findViewById(R.id.pose_layout);

		// initialize menu buttons
		CompoundButton tasksButton = (CompoundButton) rootView.findViewById(R.id.menu_button_tasks);
		tasksButton.setOnClickListener(mMenuButtonClickListener);

		CompoundButton faultCodesButton = (CompoundButton) rootView.findViewById(R.id.menu_button_fault_codes);
		faultCodesButton.setOnClickListener(mMenuButtonClickListener);

		CompoundButton aboutAugumentaButton = (CompoundButton) rootView.findViewById(R.id.menu_button_about_augumenta);
		aboutAugumentaButton.setOnClickListener(mMenuButtonClickListener);

		return rootView;
	}

	@Override
	public void onShown() {
		super.onShown();

		// register pose layout poses
		AugumentaManager detman = AugumentaManager.getInstance(this.getActivity());
		mPoseLayout.registerPoses(detman);
	}

	@Override
	public void onHide() {
		super.onHide();

		// unregister pose layout pose
		AugumentaManager detman = AugumentaManager.getInstance(this.getActivity());
		mPoseLayout.unregisterPose(detman);
	}

	@Override
	public CharSequence getTitle() {
		return TAG;
	}

	@Override
	public void goBack() {
		goHome();
	}

	private OnClickListener mMenuButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.menu_button_tasks:
					showFragment(new TaskFragment(), true);
					break;
				case R.id.menu_button_fault_codes:
					showFragment(new FaultCodeFragment(), true);
					break;
				case R.id.menu_button_about_augumenta:
					showFragment(FaultCodeDetailFragment.newInstance("file:///android_asset/about/aboutAugumenta.html"));
					break;
			}
		}
	};
}
