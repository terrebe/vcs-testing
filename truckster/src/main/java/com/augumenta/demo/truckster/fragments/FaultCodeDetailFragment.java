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

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.augumenta.demo.truckster.R;
import com.augumenta.demo.truckster.views.PoseLayout;
import com.augumenta.agapi.AugumentaManager;
import com.augumenta.agapi.Poses;
import com.augumenta.agapi.HandPose;
import com.augumenta.agapi.HandTransition;
import com.augumenta.agapi.HandTransitionEvent;
import com.augumenta.agapi.HandTransitionListener;

/**
 * FaultCodeDetailFragment show a fault code details in WebView.
 * WebView is scrollable with poses.
 */
public class FaultCodeDetailFragment extends BaseMenuFragment {
	private static final String TAG = FaultCodeDetailFragment.class.getSimpleName();

	private static final String ARG_URI = "uri";

	// pose layout allows dragging of the webview
	private PoseLayout mPoseLayout;

	private WebView mWebView;

	public FaultCodeDetailFragment() {
		super("fault_code_detail");
	}

	public static FaultCodeDetailFragment newInstance(String uri) {
		FaultCodeDetailFragment fragment = new FaultCodeDetailFragment();

		Bundle args = new Bundle();
		args.putString(ARG_URI, uri);

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fault_code_details, container, false);

		mPoseLayout = (PoseLayout) rootView.findViewById(R.id.pose_layout);

		mWebView = (WebView) rootView.findViewById(R.id.webview);
		mWebView.setBackgroundColor(Color.TRANSPARENT);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Log.w(TAG, "onReceivedError: " + errorCode + ": " + description);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
		});

		String uri = getArguments().getString(ARG_URI);
		mWebView.loadUrl(uri);

		return rootView;
	}

	@Override
	public void onShown() {
		super.onShown();
		AugumentaManager detman = AugumentaManager.getInstance(this.getActivity());
		mPoseLayout.registerPoses(detman);

		// register for the accept pose transition
		detman.registerListener(mPoseLayout, Poses.P201);
		detman.registerListener(mBackPoseListener, new HandPose(Poses.P201), new HandPose(Poses.P141));
	}

	@Override
	public void onHide() {
		super.onHide();

		AugumentaManager detman = AugumentaManager.getInstance(this.getActivity());
		mPoseLayout.unregisterPose(detman);

		// unregister pose listeners
		detman.unregisterListener(mPoseLayout);
		detman.unregisterListener(mBackPoseListener);
	}

	@Override
	public CharSequence getTitle() {
		return TAG;
	}

	private HandTransitionListener mBackPoseListener = new HandTransitionListener() {
		@Override
		public void onTransition(HandTransitionEvent event) {
			pop();
		}
	};
}
