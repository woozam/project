package com.example.mediarobot;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MRPreferenceManager {

	private static volatile MRPreferenceManager sPrefManager;

	public static final String PREFERENCE = "Pref";

	private MRPreferenceManager() {
	}

	public static MRPreferenceManager getInstance() {
		if (sPrefManager == null) {
			synchronized (MRPreferenceManager.class) {
				if (sPrefManager == null) {
					sPrefManager = new MRPreferenceManager();
				}
			}
		}
		return sPrefManager;
	}

	public synchronized void loadPreference() {
	}

	public synchronized String getString(String key, String defValue) {
		SharedPreferences pref = MRApplication.getContext().getSharedPreferences(PREFERENCE, 0);
		return pref.getString(key, defValue);
	}

	public synchronized long getLong(String key, long defValue) {
		SharedPreferences pref = MRApplication.getContext().getSharedPreferences(PREFERENCE, 0);
		return pref.getLong(key, defValue);
	}

	public synchronized int getInt(String key, int defValue) {
		SharedPreferences pref = MRApplication.getContext().getSharedPreferences(PREFERENCE, 0);
		return pref.getInt(key, defValue);
	}

	public synchronized float getFloat(String key, float defValue) {
		SharedPreferences pref = MRApplication.getContext().getSharedPreferences(PREFERENCE, 0);
		return pref.getFloat(key, defValue);
	}

	public synchronized boolean getBoolean(String key, boolean defValue) {
		SharedPreferences pref = MRApplication.getContext().getSharedPreferences(PREFERENCE, 0);
		return pref.getBoolean(key, defValue);
	}

	public synchronized void putString(String key, String value) {
		SharedPreferences pref = MRApplication.getContext().getSharedPreferences(PREFERENCE, 0);
		Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public synchronized void putLong(String key, long value) {
		SharedPreferences pref = MRApplication.getContext().getSharedPreferences(PREFERENCE, 0);
		Editor editor = pref.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public synchronized void putInt(String key, int value) {
		SharedPreferences pref = MRApplication.getContext().getSharedPreferences(PREFERENCE, 0);
		Editor editor = pref.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public synchronized void putFloat(String key, float value) {
		SharedPreferences pref = MRApplication.getContext().getSharedPreferences(PREFERENCE, 0);
		Editor editor = pref.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	public synchronized void putBoolean(String key, boolean value) {
		SharedPreferences pref = MRApplication.getContext().getSharedPreferences(PREFERENCE, 0);
		Editor editor = pref.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public synchronized void clear() {
		SharedPreferences pref = MRApplication.getContext().getSharedPreferences(PREFERENCE, 0);
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
}
