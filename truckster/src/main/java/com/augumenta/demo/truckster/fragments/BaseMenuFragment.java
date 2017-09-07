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

import com.augumenta.agapi.AugumentaManager;
import com.augumenta.agapi.Poses;
import com.augumenta.agapi.HandPose;
import com.augumenta.agapi.HandTransition;
import com.augumenta.agapi.HandTransitionEvent;
import com.augumenta.agapi.HandTransitionListener;

/**
 * BaseMenuFragment uses poses to go back in fragments stack.
 */
public abstract class BaseMenuFragment extends BaseFragment {
	public BaseMenuFragment(String tag) {
		super(tag);
	}

	@Override
	public void onShown() {
		super.onShown();

		// Register for P201 -> P016 transition when fragment is shown
		AugumentaManager detman = AugumentaManager.getInstance(this.getActivity());
		detman.registerListener(mBackPoseListener, new HandPose(Poses.P201), new HandPose(Poses.P016));
	}

	@Override
	public void onHide() {
		super.onHide();

		// Unregister pose listener when fragment is hidden
		AugumentaManager detman = AugumentaManager.getInstance(this.getActivity());
		detman.unregisterListener(mBackPoseListener);
	}

	private HandTransitionListener mBackPoseListener = new HandTransitionListener() {
		@Override
		public void onTransition(HandTransitionEvent event) {
			goBack();
		}
	};
}
