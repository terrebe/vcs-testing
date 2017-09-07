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
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.augumenta.demo.truckster.R;

/**
 * Indicator showing pose interaction type
 */
public class PoseIndicator extends LinearLayout {

	private TextView mTextView;
	private ImageView mImageFirstView;
	private ImageView mImageSecondView;
	private ImageView mImageArrow;

	public PoseIndicator(Context context) {
		super(context, null, 0);
	}

	public PoseIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PoseIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		initView(context);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PoseIndicator);
		try {
			setText(a.getString(R.styleable.PoseIndicator_android_text));
			setFirstImage(a.getResourceId(R.styleable.PoseIndicator_imageFirst, -1));
			setSecondImage(a.getResourceId(R.styleable.PoseIndicator_imageSecond, -1));
		} finally {
			a.recycle();
		}
	}

	public void initView(Context context) {
		View.inflate(context, R.layout.view_pose_indicator, this);

		mTextView = (TextView) findViewById(R.id.text);
		mImageFirstView = (ImageView) findViewById(R.id.image_first);
		mImageSecondView = (ImageView) findViewById(R.id.image_second);
		mImageArrow = (ImageView) findViewById(R.id.image_arrow);

		Animation arrowAnimation = AnimationUtils.loadAnimation(context, R.anim.indicator_fade);
		mImageArrow.startAnimation(arrowAnimation);
	}

	public void setText(String text) {
		mTextView.setText(text);
	}

	public void setFirstImage(int res) {
		mImageFirstView.setImageResource(res);
	}

	public void setSecondImage(int res) {
		mImageSecondView.setImageResource(res);
	}
}
