package com.example.dcamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Files;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.graphics.Bitmap; 

public class AutoItemActivity extends Activity {
	
	MainActivity mainAct ;
	PreViewThread auto ;	// ������ȭ ������
	SensorThread sensorThread ;	// ���� ������

	private int imgNum ;	// ���� �������� �̹����� index
	private float preX, preRoll ;	// ��ġ ��ǥ, ���� ��
	private boolean motionFlag ;	// ���� �÷���
	private ArrayList <Bitmap> img ;	// ������ ���� ����Ʈ
	private LinearLayout linearLay ;		// �޴� ���̾ƿ�
	private ImageView autoImg ;
	private VelocityTracker velocityTracker ;
	private Handler preHandler =new Handler () {
		public void handleMessage (Message msg) {
			switch (msg.what) {
			case 0 :	// ���� �޼����� 0�϶�
				// ���� ������ ������ȭ
				if (img.size () > 0) 
					imgNum =onPreView (imgNum+1) ;
				break ;
			case 1 :	// ���� �޼����� 1�϶�
				// ���� �ڵ����� ȸ�� ���� ���� ���� ������ ������ȭ
				if (img.size () > 0) {
					// �ڵ��� ȸ�� limit���� 42���� ���� �� ���� ���� ������ �ο���
					int size =42 /img.size() ;
					// ���� ȸ�� ������ ���� ������ ���� ���
					float tempVal =(Float)msg.obj -preRoll ;
					// ���� ȸ��
					if (imgNum < img.size () -1 && tempVal > size) {
						imgNum =onPreView (imgNum +1) ;
					}
					// ������ ȸ��
					else if (imgNum > 0 && tempVal < size *(-1)) {
						imgNum =onPreView (imgNum -1) ;
					}
				}
				preRoll =(Float)msg.obj ;	// ���� ȸ�� ���� ����
				break ;
			}
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView (R.layout.item) ;
		
		img =new ArrayList<Bitmap> () ;
		
		linearLay =(LinearLayout) findViewById (R.id.item_menu) ;
		
		// playŬ�� ���̾ƿ� ��
		ImageButton play =(ImageButton) findViewById (R.id.item_play) ;
		RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.CENTER_HORIZONTAL) ;
		params.setMargins(15, 0, 15, 0) ;
		play.setLayoutParams(params) ;
		play.setAdjustViewBounds(true) ;
		play.setScaleType(ScaleType.FIT_XY) ;
		
		// motion ���� ���̾ƿ� ��
		ImageButton motion =(ImageButton) findViewById (R.id.item_motion) ;
		params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.RIGHT_OF, R.id.item_play) ;
		params.setMargins(15, 0, 15, 0) ;
		motion.setLayoutParams(params) ;
		motion.setAdjustViewBounds(true) ;
		motion.setScaleType(ScaleType.FIT_XY) ;
		
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
		if (num >= img.size()) num =0 ;	// index�� �̹������� Ŀ���� 0���� �ʱ�ȭ
		else if (num < 0) num =img.size () -1 ;	// index�� 0���� �۾����� ���������� �ʱ�ȭ
		
		autoImg.setImageBitmap (img.get(num)) ;	// �̹��� ����
		return num ;
	}
	
	// play Ŭ��
	public void autoClick (View v) {
		auto.autoFlag =!auto.autoFlag ;	// ������ ���� ����
		ImageButton play =(ImageButton) findViewById (R.id.item_play) ;
		// ������ �����尡 ���� �� �� 
		if (auto.autoFlag) {
			play.setBackgroundResource(R.drawable.stop_selector) ;
			sensorThread.stopSensorFlag =true ;	// ���� �����带 �����.
		}
		// ������ �����尡 ���� �� ��
		else {
			// ���� �����尡 ���� �Ǿ��� �� ������ ����
			play.setBackgroundResource(R.drawable.play_selector) ;
			if (!motionFlag)
				sensorThread.startSensorFlag =true ;
		}
	}
	
	// motion Ŭ��
	public void motionClick (View v) {
		motionFlag =!motionFlag ;	// ������ ���� ����
		ImageButton motion =(ImageButton) findViewById (R.id.item_motion) ;
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
	
	// �ð��� ����
	public void sortFiles (File [] files) {
		// �⺻ sort���� �ð��� ���� �߰�
		Arrays.sort (files, new Comparator<Object> () {
			public int compare (Object obj1, Object obj2) {
				String s1 =((File)obj1).lastModified () +"" ;
				String s2 =((File)obj2).lastModified () +"" ;
			
				return s1.compareTo (s2) ;	// s1�� s2 ��
			}
		}) ;
	}
	
	// ���� �̸� ����
	public void renameFiles (String path) {
		// �Ķ���ͷ� ���� ��η� ���� ���� �� ���� ���� Ž��
		File pathFile =new File (path) ;
		File[] childFiles =pathFile.listFiles() ;
		String dir ="/Cubic_" ;

		sortFiles (childFiles) ;	// listFiles()�� �� ���� ���ϵ��� �ð� ������ ����
		// 0�κ��� .nomedia�����̹Ƿ� ����
		for (int i =1 ; i < childFiles.length ; i++) {
			// ������ ���� ���� Ž��
			File temp =new File (path +dir +i) ;
			if (!temp.exists()) {	// ������ ������
				try {
					// ���ĵ� ���������� �̸��� ����
					childFiles[i].renameTo(new File (path +dir +i)) ;
				} catch (Exception e) {}
			}
		}
	}
	
	// ���� ����
	public void deleteFiles (String path) {
		// �Ķ���ͷ� ���� ��η� ���� ���� �� ���� ���� Ž��
		File pathFile =new File (path) ;
		File[] childFiles =pathFile.listFiles() ;
		
		for (File childFile : childFiles) {
			// ���� ���ϰ� ���� �� ��� ���ȣ��� ���� ���� ���� ����
			if (childFile.isDirectory()) 	
				deleteFile (childFile.getAbsolutePath()) ;
			// ���� ������ ������ ����
			else childFile.delete() ;
		}
		pathFile.delete() ;	// ���� ���� ����
	}
	
	// delete Ŭ��
	public void deleteClick (View v) {
		// Intent�� ���� ���� ���� �⺻��� �� ���� ���� ��� ����
		Bundle bundle = getIntent().getExtras();
		File pathOrg = Environment.getExternalStorageDirectory();
		String dir = "/CubicCamera/Cubic_";
		
		deleteFiles (pathOrg +dir +bundle.getInt("item")) ;	// ����
		renameFiles (pathOrg +"/CubicCamera") ;	// ������ ���ϵ��� �̸� �� ����
				
		img.removeAll(img);	// ����Ʈ �ʱ�ȭ
		// �������� ������ ����
		auto.interrupt () ;
		auto.runFlag =true ;
		sensorThread.interrupt() ;
		sensorThread.runFlag =true ;
		finish(); // Activity�� �����ϰ�
	}
	
	// add Ŭ��
	public void addClick (View v) {
		linearLay.setVisibility(View.GONE) ;	// �޴� ���̾ƿ��� ���ְ�
		// cameraActivity Intent ����
		Intent camera =new Intent (this, CameraActivity.class) ;
		startActivity (camera) ;
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
		case MotionEvent.ACTION_MOVE :	// �巡�� �϶�
	          float dX = X - preX ;	// ���� ��ġ ��ǥ�� ���� ��ġ��ǥ�� ���� �� ���

	          if (dX > 10.0f)  imgNum++ ;	// ���� �巡��
	          else if (dX < 0 && dX > -10.0f)  imgNum-- ;	// ������ �巡��
	          
	          imgNum =onPreView (imgNum) ;	// ȭ���� �����Ѵ�.
	          break ;
		case MotionEvent.ACTION_DOWN :	// ���� �϶�
			// �޴� ���̾ƿ��� ���̸� ���ش�.
			if (linearLay.getVisibility() == View.VISIBLE) {
				linearLay.setVisibility(View.GONE) ;

				return true ;
			}
		}
		// ���� ��ġ ���� ����
        preX = X ;	// ���� ��ġ�� ��ǥ ����
		return true;
	}
	
	// ȭ���� ���̰� ���� �� ����
	public void onWindowFocusChanged (boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus) ;
		// ȭ���� ���� ���̰� �̹��� ����� 0�� �ƴϸ�
		if (hasFocus && img.size() == 0) {
			// Intent�� ���� ���� ���� �⺻��� �� ���� ���� ��� ����
			Bundle bundle = getIntent().getExtras();
			File pathOrg = Environment.getExternalStorageDirectory();
			String dir = "/CubicCamera/Cubic_" + bundle.getInt("item");
			int num = 0;
			// Load
			try {
				File pathApp = new File(pathOrg + dir + "/Cubic_" + num+ ".png"); // Load���
				// if (!pathApp.isDirectory()) pathApp.mkdirs(); // ������ ����

				while (true) {
					if (pathApp.exists()) {	// �̹��� ������ ������
						img.add(BitmapFactory.decodeFile(pathApp + ""));	// �̹����� Load�Ͽ� ����Ʈ�� �߰�
						pathApp = new File(pathOrg + dir + "/Cubic_" + (++num) + ".png");	// ���� �̹��� Ž��
					}
					// �̹��� ������ ������ ���� Ż��
					else break ;
				}
			} catch (Exception e) {
				Toast.makeText(this, "������ �ҷ����� ���� ������ �߻� �Ͽ����ϴ�.",Toast.LENGTH_LONG).show();
				finish();
			}
			// ó�� �������� �̹��� ��
			autoImg =(ImageView) findViewById (R.id.item_autoImg) ;
			autoImg.setImageBitmap(img.get(0)) ;	
		}
	}

	// �ڷ� ���� ��ưŬ����
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {	
				// �޴� ���̾ƿ��� ���̸� ���ش�.
				if (linearLay.getVisibility() == View.VISIBLE) {
					linearLay.setVisibility(View.GONE) ;

					return true ;
				}
				// �޴� ���̾ƿ��� ������ ���� ��
				img.removeAll(img);	// ����Ʈ �ʱ�ȭ
				sensorThread.stopSensorFlag =true ;
				finish(); // Activity�� �����ϰ�

				return true;
			}
			else if (KeyCode == KeyEvent.KEYCODE_MENU) {
				if (linearLay.getVisibility() == View.GONE)
					linearLay.setVisibility(View.VISIBLE) ;
				else linearLay.setVisibility(View.GONE) ;
				//openOptionsMenu(); 
				
				return true ;
			}
		}
		return super.onKeyDown(KeyCode, event);
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
	
	// Acitivity Stop
	public void onStop () {
		super.onStop() ;
		// �������� ������ ����
		auto.interrupt () ;
		auto.runFlag =true ;
		sensorThread.runFlag =true ;
		sensorThread.interrupt() ;
	}
}
