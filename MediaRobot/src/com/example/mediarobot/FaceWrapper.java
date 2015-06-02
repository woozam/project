package com.example.mediarobot;

import android.graphics.Rect;
import android.hardware.Camera.Face;

public class FaceWrapper {
	
	private int mNumber;
	private Face mFace;
	private Rect mPredictRect;
	private float mVelocity;
	private long mLastTime;
	private int mX;
	private int mY;
	private int mZ;
	
	public void setNumber(int number) {
		mNumber = number;
	}
	
	public int getNumber() {
		return mNumber;
	}
	
	public void setFace(Face face) {
		Face temp = mFace;
		int tempX = mX;
		int tempY = mY;
		int tempZ = mZ;
		long tempLastTime = mLastTime;
		mFace = face;
		setLastTime();
		setCoor();
		if (temp != null) {
			mVelocity = (float) (Math.sqrt(Math.pow(tempX - mX, 2) + Math.pow(tempY - mY, 2) + Math.pow(tempZ - mZ, 2)) / (mLastTime - tempLastTime) * 1000);
		}
	}
	
	public Face getFace() {
		return mFace;
	}
	
	public void setVelocity(float velocity) {
		mVelocity = velocity;
	}
	
	public float getVelocity() {
		return mVelocity;
	}
	
	public Rect getPredictRect() {
		return mPredictRect;
	}
	
	public void setLastTime() {
		mLastTime = System.currentTimeMillis();
	}
	
	public long getLastTime() {
		return mLastTime;
	}
	
	public int getX() {
		return mX;
	}
	
	public int getY() {
		return mY;
	}
	
	public int getZ() {
		return mZ;
	}
	
	public void setX(int x) {
		mX = x;
	}
	
	public void setY(int y) {
		mY = y;
	}
	
	public void setZ(int z) {
		mZ = z;
	}
	
	private void setCoor() {
		final Rect rect = mFace.rect;
		mZ = (20000 / rect.width());
		mX = -(rect.centerX() * 75) * mZ / 100 / 1000;
		mY = -(rect.centerY() * 75)* mZ / 100 / 1000;
	}
}