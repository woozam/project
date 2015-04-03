package com.example.mediarobot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.view.View;

public class FaceCaptureView extends View {
	
	private Paint mPaint;
	private Face[] mFaces;
	private int mStrokeWidth;

	public FaceCaptureView(Context context) {
		super(context);
		initialize();
	}
	
	private void initialize() {
		mPaint = new Paint();
		mStrokeWidth = CommonUtils.convertDipToPx(2);
		mPaint.setStrokeWidth(mStrokeWidth);
		mPaint.setARGB(0xff, 0xff, 0xff, 0xff);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setTextSize(CommonUtils.convertDipToPx(16));
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		drawFaces(canvas, mFaces);
	}
	
	private void drawFaces(Canvas canvas, Face[] faces) {
		if (faces != null) {
			for (int i = 0; i < faces.length; i++) {
				drawFace(canvas, faces[i]);
			}
		}
	}
	
	private void drawFace(Canvas canvas, Face face) {
		final Rect rect = face.rect;
		int left = 2000 - (rect.left + 1000);
		int top = rect.top + 1000;
		int right = 2000 - (rect.right + 1000);
		int bottom = rect.bottom + 1000;
		int width = getWidth();
		int height = getHeight();
		float leftCoor = width * left / 2000;
		float topCoor = height * top / 2000;
		float rightCoor = width * right / 2000;
		float bottomCoor = height * bottom / 2000;
		canvas.drawLine(leftCoor, topCoor - mStrokeWidth / 2, leftCoor, bottomCoor + mStrokeWidth / 2, mPaint);
		canvas.drawLine(rightCoor, topCoor - mStrokeWidth / 2, rightCoor, bottomCoor + mStrokeWidth / 2, mPaint);
		canvas.drawLine(leftCoor - mStrokeWidth / 2, topCoor, rightCoor + mStrokeWidth / 2, topCoor, mPaint);
		canvas.drawLine(leftCoor - mStrokeWidth / 2, bottomCoor, rightCoor + mStrokeWidth / 2, bottomCoor, mPaint);
		float distance = (20000f / rect.width());
		float size = (3f * distance * rect.width() / 4000f);
//		canvas.drawText(String.format("%dcm, %dcm", Math.round(distance), Math.round(size)), (leftCoor + rightCoor) / 2, (topCoor + bottomCoor) / 2, mPaint);
		canvas.drawText(String.format("%dcm", Math.round(distance)), (leftCoor + rightCoor) / 2, (topCoor + bottomCoor) / 2, mPaint);
	}
	
	public void captureFaceCapture(Face[] faces, Camera camera) {
		mFaces = faces;
		invalidate();
	}
}