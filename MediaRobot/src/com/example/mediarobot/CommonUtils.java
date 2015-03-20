package com.example.mediarobot;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class CommonUtils {
	
	public static DisplayMetrics DISPLAY_METRICS;
	private static int screenWidth;
	private static int screenHeight;

	public static int convertDipToPx(int dip) {
		if (DISPLAY_METRICS == null)
			DISPLAY_METRICS = MRApplication.CONTEXT.getResources().getDisplayMetrics();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, DISPLAY_METRICS);
	}

	public static int convertDipToPx(Context context, int dip) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
	}
	
	public static int convertDipToPx(int dip, Context context) {
		if (DISPLAY_METRICS == null)
			DISPLAY_METRICS = context.getResources().getDisplayMetrics();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, DISPLAY_METRICS);
	}

	public static int getScreenWidth() {
		screenWidth = MRApplication.CONTEXT.getResources().getDisplayMetrics().widthPixels;
		return screenWidth;
	}

	public static int getScreenHeight() {
		screenHeight = MRApplication.CONTEXT.getResources().getDisplayMetrics().heightPixels;
		return screenHeight;
	}
}