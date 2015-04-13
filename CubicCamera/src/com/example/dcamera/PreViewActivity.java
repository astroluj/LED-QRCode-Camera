package com.example.dcamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class PreViewActivity extends Activity {

	MainActivity mainAct ;
	CameraActivity cameraAct;	// ī�޶� ȭ��
	PreViewThread auto ;		// ������ȭ
	SensorThread sensorThread ;	// ����
	
	private boolean saveFlag, motionFlag ;	// ���� �� ���� �÷���
	private float preX, preRoll ;	// ��ġ ��ǥ, ���� ��
	private RelativeLayout viewRel ;	// ���� ȭ���� ������ ���̾ƿ�
	private ImageView autoImg ;
	private Toast toast ; // toast �ߺ� ����
	private VelocityTracker velocityTracker ;
	private Handler preHandler =new Handler () {
		public void handleMessage (Message msg) {
			switch (msg.what) {
			case 0 :	// ���� �޼����� 0�϶�
				// ���� ������ ������ȭ
				if (cameraAct.img.size() > 0) 
					cameraAct.imgNum =onPreView (cameraAct.imgNum+1) ;
				break ;
			case 1 :	// ���� �޼����� 1�϶�
				// ���� �ڵ����� ȸ�� ���� ���� ���� ������ ������ȭ
				if (cameraAct.img.size () > 0) {
					// �ڵ��� ȸ�� limit���� 42���� ���� �� ���� ���� ������ �ο���
					int size =42 /cameraAct.img.size() ;	
					// ���� ȸ�� ������ ���� ������ ���� ���
					float tempVal =(Float)msg.obj -preRoll ;
					// ���� ȸ��
					if (cameraAct.imgNum < cameraAct.img.size () -1 && tempVal > size) {
						cameraAct.imgNum =onPreView (cameraAct.imgNum +1) ;
					}
					// ������ ȸ��
					else if (cameraAct.imgNum > 0 && tempVal < size *(-1)) {
						cameraAct.imgNum =onPreView (cameraAct.imgNum -1) ;
					}
				}
				preRoll =(Float)msg.obj ;	// ���� ȸ�� ���� ���� 
				break ;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preview);
		
		ImageButton play =(ImageButton) findViewById (R.id.preview_play),
				save =(ImageButton) findViewById (R.id.preview_save),
				motion =(ImageButton) findViewById (R.id.preview_motion) ;
		
		// save Ŭ�� ���̾ƿ� ��
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT) ;
		params.setMargins(15, 0, 15, 0) ;
		save.setLayoutParams(params) ;
		save.setAdjustViewBounds(true) ;
		save.setScaleType(ScaleType.FIT_XY) ;
		
		// play Ŭ�� ���̾ƿ� ��
		params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.CENTER_HORIZONTAL) ;
		params.setMargins(15, 0, 15, 0) ;
		play.setLayoutParams(params) ;
		play.setAdjustViewBounds(true) ;
		play.setScaleType(ScaleType.FIT_XY) ;
		
		// motion ���� ���̾ƿ� ��
		params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.RIGHT_OF, R.id.preview_play) ;
		params.setMargins(15, 0, 15, 0) ;
		motion.setLayoutParams(params) ;
		motion.setAdjustViewBounds(true) ;
		motion.setScaleType(ScaleType.FIT_XY) ;
		
		// ó���� ȭ�鿡 ������ ���� ��
		autoImg =(ImageView) findViewById (R.id.preview_autoImg) ;
		autoImg.setImageBitmap(cameraAct.img.get(0)) ;
		
		// ���� ������
		sensorThread =new SensorThread (this, preHandler) ;
		sensorThread.setDaemon(true) ;
		sensorThread.start() ;
		
		// ������ȭ ������
		auto =new PreViewThread (preHandler) ;
		auto.setDaemon(true) ;
		auto.start() ;
	}
	
	// �� ������ ������ó�� ǥ��
	public int onPreView (int num) {
		if (num >= cameraAct.img.size()) num =0 ;	// index�� �̹������� Ŀ���� 0���� �ʱ�ȭ
		else if (num < 0) num =cameraAct.img.size () -1 ;	// index�� 0���� �۾����� ���������� �ʱ�ȭ
		
		autoImg.setImageBitmap (cameraAct.img.get(num)) ; 	// �̹��� ����
		
		return num ;
	}
	
	// auto Ŭ��
	public void autoClick (View v) {
		auto.autoFlag =!auto.autoFlag ;	// ������ ���� ����
		ImageButton play =(ImageButton) findViewById (R.id.preview_play) ;
		// ������ �����尡 ���� �� �� 
		if (auto.autoFlag) {
			play.setBackgroundResource(R.drawable.stop_selector) ;
			sensorThread.stopSensorFlag =true ;	// ���� �����带 �����.
		}
		// ������ �����尡 ���� �� ��
		else {
			play.setBackgroundResource(R.drawable.play_selector) ;
			// ���� �����尡 ���� �Ǿ��� �� ������ ����
			if (!motionFlag)
				sensorThread.startSensorFlag =true ;
		}
	}
	
	// motion Ŭ��
	public void motionClick (View v) {
		motionFlag =!motionFlag ;	// ������ ���� ����
		ImageButton motion =(ImageButton) findViewById (R.id.preview_motion) ;
		// ���� �����尡 ���� �� ��
		if (motionFlag) {
			motion.setBackgroundResource(R.drawable.non_motion_selector) ;
			sensorThread.stopSensorFlag =true ;	// ���� �����带 �����.
		}
		// ���� �����尡 ���� �� ��
		else {
			motion.setBackgroundResource(R.drawable.motion_selector) ;
			sensorThread.startSensorFlag =true ;	// ���� ������ ����
		}
	}
	
	// save Ŭ��
	public void saveClick (View v) {
		// ������ �̹����� ���� ��
		if (cameraAct.img.size () == 0) {
			if (toast == null)
				toast =Toast.makeText(this, "���� �� ������ �����ϴ�.", Toast.LENGTH_LONG) ;
			toast.show() ;
			return ;
		}
	
		// �̹����� ���� ��
		final ProgressDialog saver = new ProgressDialog(this);
		saver.setMessage("���� ���Դϴ�.");	// ���α׷����� ����	
		saver.show() ;
		
		// �⺻ ��ο� ���� ���� ��� ����
		File pathOrg =Environment.getExternalStorageDirectory() ;
		String dir ="/CubicCamera/Cubic_" ;
		int num =1 ;
		// ����
		try {
			File pathApp =new File (pathOrg +dir +num) ; // ������
			//if (!pathApp.isDirectory()) pathApp.mkdirs(); // ������ ����

			while (true) {
				if (pathApp.isDirectory())	// ��λ��� ������ ������
					pathApp =new File (pathOrg +dir +(++num)) ;	// ���� ���� Ž��
				else {	// ��λ��� ������ ������
					pathApp.mkdirs() ;	// ���� ����
					break ;
				}
			}
			// pngȮ���ڷ� ������ ������ ����
			for (int i = 0; i < cameraAct.img.size(); i++) {
				FileOutputStream outFile = new FileOutputStream(pathApp +"/Cubic_" + (i) + ".png"); // ����
				cameraAct.img.get(i).compress(Bitmap.CompressFormat.PNG, 100, outFile); // ��Ʈ����PNG��
			}
		} catch (FileNotFoundException e) {
			Toast.makeText(this, "������ �����ϴ� ���� ������ �߻� �Ͽ����ϴ�.", Toast.LENGTH_LONG).show () ;
			finish () ; 
			}
		saver.dismiss() ;		// ���α׷����� ����
		saveFlag =true ;	// ���� �ߴٴ� ���� üũ
		Toast.makeText(this, "������ ���� �Ǿ����ϴ�.", Toast.LENGTH_LONG).show () ;
	}
	
	// ��ġ�� ���� �̺�Ʈ
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

	     // ���� ������ ��ġ ������ x, y ��ǥ
        float X = event.getX();

        // �ν��Ͻ� ����
        if (velocityTracker == null) {
        	velocityTracker =VelocityTracker.obtain() ;
        }
        velocityTracker.addMovement(event) ;
        
		switch (event.getAction() & event.ACTION_MASK) { // �̺�Ʈ�� ��������
		case MotionEvent.ACTION_MOVE :	// �巡�� �� ��
	          float dX = X - preX;	// ���� ��ġ ��ǥ�� ���� ��ġ��ǥ�� ���� �� ���

	          if (dX > 10.0f)  cameraAct.imgNum++ ;	// ���� �巡��
	          else if (dX < 0 && dX > -10.0f)  cameraAct.imgNum-- ;	// ������ �巡��
	          
	          cameraAct.imgNum =onPreView (cameraAct.imgNum) ;	// ȭ���� ����
		}
        preX = X ;	// ���� ��ġ�� ��ǥ ����
		return true;
	}
	
	// �ڷ� ���� ��ưŬ����
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				// ������ ���� ���·� �ڷ� ���� ��ư�� ������ �� �ൿ�� ���� ��ȭ���� ���
				if (!saveFlag) {
					AlertDialog.Builder altYN = new AlertDialog.Builder(this); 
					// ��ȭ���� ���� �޼����� �����ϰ� ��ȭ���ڸ� ���� �� �ִ��� ���� Yes��ư�� Text�� �׿� ���� ������ ����
					altYN.setMessage("�̾ ��ڽ��ϱ�?").setCancelable(true)
							.setPositiveButton("Connect",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											sensorThread.stopSensorFlag =true ;
											finish();	// �̾ ���� ��� Activity�� ����
										}
										// No��ư�� Text�� �׿� ���� ������ ����
									})
							.setNegativeButton("new Capture",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											dialog.cancel(); // ��ȭ���ڸ� ����
											// ���� ���� ��� ����� �������� ����
											cameraAct.img.removeAll(cameraAct.img);
											sensorThread.stopSensorFlag =true ;
											finish();
										}
									});
					AlertDialog alt = altYN.create(); // ��ȭ������ ����
					alt.setTitle("ReCapture"); // Title�� Text ����
					alt.show(); // ȭ���� ���
				}
				// ������ �� ���� �� ���
				else {
					// ����� �������� ����
					cameraAct.img.removeAll(cameraAct.img) ;
					sensorThread.stopSensorFlag =true ;
					finish();
				}
				return true;
			}
		}
		return super.onKeyDown(KeyCode, event) ;
	}
	
	// Activity Restart
	public void onResume () {
		super.onResume () ;
		sensorThread.startSensorFlag =true ;	// ���� ������ ����
	}
	
	// Activity Pause
	public void onPause () {
		super.onPause() ;
		sensorThread.stopSensorFlag =true ;	// ���� ������ ����
	}
	
	// Activity Stop
	public void onStop () {
		super.onStop() ;
		// �������� ������ ����
		auto.interrupt () ;
		auto.runFlag =true ;
		sensorThread.interrupt() ;
		sensorThread.runFlag=true ;
	}
}
