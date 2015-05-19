package com.example.mediarobot;

import android.app.Application;
import android.content.Context;

public class MRApplication extends Application {
	
	public static Context CONTEXT;

	@Override
	public void onCreate() {
		super.onCreate();
		CONTEXT = this;
	}
	
	public static Context getContext() {
		return CONTEXT;
	}
}