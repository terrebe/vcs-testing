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
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.augumenta.demo.truckster.R;

/**
 * Button to show image and text
 */
public class CompoundButton extends LinearLayout {

	private ImageView mImageView;
	private TextView mTextView;

	public CompoundButton(Context context) {
		this(context, null);
	}

	public CompoundButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CompoundButton(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, R.style.CustomButtonStyle);
	}

	public CompoundButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr);

		View.inflate(context, R.layout.view_compound_button, this);

		mImageView = (ImageView) findViewById(R.id.image);
		mTextView = (TextView) findViewById(R.id.text);


		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CompoundButton, defStyleAttr, defStyleRes);

		String text = a.getString(R.styleable.CompoundButton_android_text);
		int textColor = a.getColor(R.styleable.CompoundButton_android_textColor, Color.WHITE);
		int textAppearance = a.getResourceId(R.styleable.CompoundButton_android_textAppearance, -1);
		int image = a.getResourceId(R.styleable.CompoundButton_android_src, -1);

		a.recycle();

		setImage(image);

		setText(text);
		setTextAppearance(context, textAppearance);
		setTextColor(textColor);
	}

	public void setImage(int resId) {
		mImageView.setImageResource(resId);
	}

	public void setText(CharSequence text) {
		mTextView.setText(text);
	}

	public void setTextColor(int color) {
		mTextView.setTextColor(color);
	}

	public void setTextAppearance(Context context, int resId) {
		mTextView.setTextAppearance(context, resId);
	}
}
