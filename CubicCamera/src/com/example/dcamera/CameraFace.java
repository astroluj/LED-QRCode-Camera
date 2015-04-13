package com.example.dcamera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class CameraFace extends SurfaceView implements SurfaceHolder.Callback {

	MainActivity mainAct ;
	CameraActivity cameraAct ;
	
	protected static SurfaceHolder holder ;
	
	private Camera camera = null;
	private Bitmap img;

	public CameraFace(Context context) {
		super(context);

		holder = getHolder(); // callback���� �����Ȱ��� ������
		holder.addCallback(this); // callback ȣ��
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open(); // ����� ī�޶� ����
		camera.setDisplayOrientation(90); // ���θ��� ������ 90ȸ��
		try {
			camera.setPreviewDisplay(holder); // �̻������ ȭ�� ���
			/*
			 * Camera.Parameters p =camera.getParameters() ;
			 * p.setPictureSize(1300, 1600) ; camera.setParameters(p) ;
			 */
		} catch (IOException exception) {
			camera.release();	// ī�޶� ����
			camera = null;
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Camera.Parameters parameters = camera.getParameters(); // ī�޶�ȭ���� ������
		parameters.setPreviewSize(w, h);
		camera.setParameters(parameters);
		camera.startPreview(); // ������� ȭ�� ���
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview(); // ī�޶� ȭ���� ���߰�
		if (img != null) img.recycle() ;	// �޸𸮻��� �̹��� ����
		camera.release(); // Resource ����
		camera = null;
	}

	// ��ġ�� ���� �̺�Ʈ
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		switch (event.getAction() & event.ACTION_MASK) { // �̺�Ʈ�� ��������
		case MotionEvent.ACTION_UP: // ���� ���� �� ��
			camera.autoFocus(autoFocus);	// autoFocus ȣ��
		}
		return true;
	}

	// �̸����⸦ ��������
	public Camera.PreviewCallback timerShutter = new Camera.PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters params = camera.getParameters();
			int cameraWidth = params.getPreviewSize().width, cameraHeight = params
					.getPreviewSize().height, cameraFormat = params
					.getPreviewFormat();

			Rect area = new Rect(0, 0, cameraWidth, cameraHeight);
			YuvImage yuv;
			// prieview�� ���� �� YuvImage�� ó��
			yuv = new YuvImage(data, cameraFormat, cameraWidth, cameraHeight,
					null);
			ByteArrayOutputStream outByte = new ByteArrayOutputStream();
			yuv.compressToJpeg(area, 100, outByte);
			img = BitmapFactory.decodeByteArray(outByte.toByteArray(), 0,
					outByte.size());
			// ȸ�� �� �ڸ���
			Matrix m = new Matrix();
			m.postRotate(90); // 90�� ȸ��
			img = Bitmap.createBitmap(img, 0, 0, img.getWidth(),
					img.getHeight(), m, true); // ȸ��
			cameraAct.img.add(Bitmap.createBitmap(img));
		}
	};
	
	// ��Ŀ�� ����
	public Camera.AutoFocusCallback autoFocus = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			if (success) {	// ��Ŀ���� �Ǿ��� ��
				shutter.start() ;	// ��ȭ���� ĸ���ϰ�
				cameraAct.focusFlag =true ;	// ���� ���� �÷��� true
				cameraAct.captureFlag =true ;	// ĸ�� ���� �÷��� true
			}
			//camera.autoFocus(null);

			// requestPreview();
		}
	};
	
	// ĸ��
	CountDownTimer shutter = new CountDownTimer(1000, 800) {
		public void onTick(long millisUntilFinished) {
			// �̸����� ĸ��
			camera.setOneShotPreviewCallback(timerShutter);
		}

		public void onFinish() {
			// ����ٴ� ǥ��
			camera.stopPreview();
			camera.setOneShotPreviewCallback(null);
			camera.startPreview(); // ī�޶� �̸����� ȭ�� �ٽ� ���
			cameraAct.afterFlag =true ;	// �ܻ� ���� true
			cameraAct.focusFlag =false ;	// ���� ���� false
		}
	} ;
}
