package com.example.mediarobot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.view.View;

public class FaceCaptureView extends View {
	
	private Paint mPaint;
	private FaceWrapper[] mFaces;
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
	
	private void drawFaces(Canvas canvas, FaceWrapper[] faces) {
		if (faces != null) {
			for (int i = 0; i < faces.length; i++) {
				drawFace(canvas, faces[i]);
			}
		}
	}
	
	private void drawFace(Canvas canvas, FaceWrapper face) {
		final Rect rect = face.getFace().rect;
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
//		canvas.drawText(String.format("face %d", face.getNumber()), (leftCoor + rightCoor) / 2, (topCoor + bottomCoor) / 2 - 60, mPaint);
//		canvas.drawText(String.format("(%d, %d, %d), %dcm/s", face.getX(), face.getY(), face.getZ(), Math.round(face.getVelocity())), (leftCoor + rightCoor) / 2, (topCoor + bottomCoor) / 2, mPaint);
		canvas.drawText(String.format("(%d, %d, %d)", face.getX(), face.getY(), face.getZ()), (leftCoor + rightCoor) / 2, (topCoor + bottomCoor) / 2, mPaint);
	}
	
	public void captureFaceCapture(FaceWrapper[] faces) {
		mFaces = faces;
		invalidate();
	}
}