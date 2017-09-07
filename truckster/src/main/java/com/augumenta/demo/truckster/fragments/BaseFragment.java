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

import android.support.v4.app.Fragment;
import android.util.Log;

import com.augumenta.demo.truckster.MainActivity;

/**
 * Base fragment class to help with transitioning between fragments
 */
public abstract class BaseFragment extends Fragment {
	public final String tag;

	public BaseFragment(String tag) {
		this.tag = tag;
	}

	public abstract CharSequence getTitle();

	@Override
	public void onResume() {
		super.onResume();

		// Trigger onShown if this fragment is shown
		if (getUserVisibleHint()) {
			this.onShown();
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		// Trigger onHide if this fragment is hidden
		if (getUserVisibleHint()) {
			this.onHide();
		}
	}

	public void onShown() {
		Log.d(tag, "onShow");
	}

	public void onHide() {
		Log.d(tag, "onHide");
	}

	public void pop() {
		((MainActivity) getActivity()).popFragment();
	}

	public void popRoot() {
		((MainActivity) getActivity()).popRootFragment();
	}

	public void showFragment(BaseFragment fragment) {
		showFragment(fragment, true);
	}

	public void showFragment(BaseFragment fragment, boolean push_to_stack) {
		((MainActivity) getActivity()).showFragment(fragment, push_to_stack);
	}

	public void goBack() {
		((MainActivity) getActivity()).popFragment();
	}

	public void goHome() {
		((MainActivity) getActivity()).popRootFragment();
	}

	public void goNext() {
		// no default action
	}
}
