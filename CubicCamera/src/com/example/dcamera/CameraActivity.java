package com.example.dcamera;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Message; 

public class CameraActivity extends Activity {

	MainActivity mainAct ;
	CameraFace camera;	// ī�޶� ȭ��
	FocusThread focusThread ;	// ��Ŀ��
	AfterImgThread aftImgThread ;	// �ܻ�
	PreViewMiniThread auto ;	// ������ȭ
	
	protected static ArrayList<Bitmap> img ;	// ���� �̹��� ����
	protected static int imgNum ;	// ���� �������� �̹����� index
	protected static boolean afterFlag, captureFlag, focusFlag ;
	
	private RelativeLayout viewRel, preRel ;	// viewRel ī�޶� ȭ��  preRel ������ ȭ��
	private ImageView focusImg, autoImg, aftImg ;	
	private Toast toast  =null ;	// toast �ߺ� ����
	private Handler handler =new Handler () {
		public void handleMessage (Message msg) {
			aftImg =(ImageView) findViewById (R.id.camera_afterImg) ;
			autoImg =(ImageView) findViewById (R.id.camera_autoImg) ;
			switch (msg.what) {
			case 0 :	// ���� �޼����� 0�϶�
				// ���� ������ ������ȭ
				if (img.size() > 0) imgNum =onPreView (imgNum+1) ;	// onPreView ȣ��
				break ;
			case 1 :	// ���� �޼����� 1�϶�
				// ������� ������ �����ϰ� ������  (�ܻ�ȿ��)
				aftImg.setImageBitmap(img.get(img.size() -1)) ;
				aftImg.setAlpha(150) ;
				break ;
			case 2:	// ���� �޼����� 2�϶�
				// �ܻ��� ���� (ĸ�� ��)
				aftImg.setAlpha(0) ;
				break ;
			case 3 :	// ���� �޼����� 3�϶�
				// ������ ������ ����
				focusImg.setImageResource(R.drawable.focus_t) ;
				break ;
			case 4 :	// ���� �޼����� 4�϶�
				// ĸ���� ���� (ĸ�� �Ϸ�)
				focusImg.setImageResource(R.drawable.focus_f) ;
			}
		}
	} ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.camera); // ī�޶�ȭ�� ���
		
		img =new ArrayList<Bitmap> () ;
		
		afterFlag =false ;	// �ܻ� ����
		focusFlag =false ;	// ���� ����
		
		camera = new CameraFace(this); // CameraFace ��ü ����
		viewRel=(RelativeLayout) findViewById (R.id.camera_viewRel) ;
		viewRel.addView(camera) ;	// viewRel ���̾ƿ��� ī�޶�ȭ���� ����.
		
		// ������� ���� �������� ������ȭ�Ͽ� preRel ���̾ƿ��� ����.
		preRel =(RelativeLayout) findViewById (R.id.camera_preRel) ;
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /8, (int)mainAct.scaleHeight /8) ;
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM) ;
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT) ;
		preRel.setLayoutParams(params) ;
		
		// ��Ŀ�� �̹��� ���̾ƿ� ��
		focusImg =(ImageView) findViewById (R.id.camera_focus) ;
		params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.CENTER_IN_PARENT) ;
		focusImg.setLayoutParams(params) ;
		focusImg.setScaleType(ScaleType.FIT_XY) ;
		
		// undoŬ�� ���̾ƿ� ��
		ImageButton undo =(ImageButton) findViewById (R.id.camera_undo),
				play =(ImageButton) findViewById (R.id.camera_play),
				save =(ImageButton) findViewById (R.id.camera_save) ;
		params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.LEFT_OF, R.id.camera_save) ;
		params.setMargins(15, 0, 15, 0) ;
		undo.setLayoutParams(params) ;
		undo.setScaleType(ScaleType.FIT_XY) ;
		undo.setAdjustViewBounds(true) ;
		
		// saveŬ�� ���̾ƿ� ��
		params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT) ;
		params.setMargins(15, 0, 15, 0) ;
		save.setLayoutParams(params) ;
		save.setScaleType(ScaleType.FIT_XY) ;
		save.setAdjustViewBounds(true) ;
		
		// playŬ�� ���̾ƿ� ��
		params =new RelativeLayout.LayoutParams(
				(int)mainAct.scaleHeight /10, (int)mainAct.scaleHeight /10) ;
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT) ;
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM) ;
		params.setMargins(15, 0, 15, 15) ;
		play.setLayoutParams(params) ;
		play.setScaleType(ScaleType.FIT_XY) ;
		play.setAdjustViewBounds(true) ;

		// �ܻ� ȿ�� ������ 
		aftImgThread =new AfterImgThread (handler) ;
		aftImgThread.setDaemon(true) ;
		aftImgThread.start() ;
		
		// ��Ŀ�� ������
		focusThread =new FocusThread (handler) ;
		focusThread.setDaemon(true) ;
		focusThread.start() ;
		
		// ������ȭ ������
		auto =new PreViewMiniThread (handler) ;
		auto.setDaemon(true) ;
		auto.start() ;
		auto.autoFlag =true ;	// true�� ���������ν� ��� ����
	}
	
	// �� ������ ������ó�� ǥ��
	public int onPreView (int num) {	
		if (num >= img.size()) num =0 ;	// index�� �̹������� Ŀ���� 0���� �ʱ�ȭ
		autoImg.setImageBitmap (img.get(num)) ;	// �̹��� ����
		return num ;
	}
	
	// undo Ŭ��
	public void undoClick (View v) {
		// �̹��� ����� ���� ��
		if (img.size () == 0) {	
			if (toast == null) 
				toast =Toast.makeText(this, "��� �� ������ �����ϴ�.", Toast.LENGTH_LONG) ;
			toast.show () ;
			
			return ;
		}
		// �̹����� �� ���̶� ���� ��
		img.remove(img.size() -1) ;	// �ֱٿ� ���� ������ �����.
		if (img.size () == 0) onRestart() ;	// �̹����� ���� ��� restart (�ܻ� �� ������ ����)
		Toast.makeText(this, "������ ���� ������ ��� �Ǿ����ϴ�.", Toast.LENGTH_LONG).show () ;
	}
	
	// save Ŭ��
	public void saveClick (View v) {
		// �̹����� ���� ��
		if (img.size () == 0) {
			if (toast == null) 
				toast =Toast.makeText(this, "���� �� ������ �����ϴ�.", Toast.LENGTH_LONG) ;
			toast.show () ;
			
			return ;
		}
		// �̹����� ���� ��
		final ProgressDialog saver = new ProgressDialog(this);
		saver.setMessage("���� ���Դϴ�.");
		saver.show () ;
		
		// �⺻ ��ο� ���� ���� ��� ����
		File pathOrg =Environment.getExternalStorageDirectory() ;
		String dir ="/CubicCamera/Cubic_" ;
		int num =1 ;
		// ����
		try {
			File pathApp =new File (pathOrg +dir +num) ; // ������
			//if (!pathApp.isDirectory()) pathApp.mkdirs(); // ������ ����

			while (true) {
				if (pathApp.exists()) 	// ��λ��� ������ ������
					pathApp =new File (pathOrg +dir +(++num)) ;	// ���� ���� Ž��
				else {	// ��λ��� ������ ������
					pathApp.mkdirs() ;	// ���� ����
					break ;
				}
			}
			// pngȮ���ڷ� ������ ������ ����
			for (int i = 0; i < img.size(); i++) {
				FileOutputStream outFile = new FileOutputStream(pathApp +"/Cubic_" + (i) + ".png"); // ����
				img.get(i).compress(Bitmap.CompressFormat.PNG, 100, outFile); // ��Ʈ����PNG��
			}
		} catch (FileNotFoundException e) {
			Toast.makeText(this, "������ �����ϴ� ���� ������ �߻� �Ͽ����ϴ�.", Toast.LENGTH_LONG).show () ;
			finish () ; 
			}
		saver.dismiss() ;	// ���α׷����� ����
		img.removeAll(img) ;	// ����Ʈ �ʱ�ȭ
		onRestart () ;	// �ܻ� �� ������ ����
		Toast.makeText(this, "������ ���� �Ǿ����ϴ�.", Toast.LENGTH_LONG).show () ;
	}
	
	// play Ŭ��
	public void previewClick (View v) {
		// �̹����� ���� ��
		if (img.size() == 0) {
			if (toast == null) 
				toast =Toast.makeText(this, "���� �� ������ �����ϴ�.", Toast.LENGTH_LONG) ;
			toast.show () ;
			
			return ;
		}
		// �̹����� ���� �� PreViewActivity Intent ����
		Intent preview =new Intent (CameraActivity.this, PreViewActivity.class) ;
		startActivity (preview) ;
	}	
	
	// �ڷ� ���� ��ưŬ����
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				img.removeAll(img);	// ����Ʈ �ʱ�ȭ
				finish(); // Activity�� ����

				return true;
			}
		}
		return super.onKeyDown(KeyCode, event);
	}

	// Activity Restart 
	public void onRestart () {
		super.onRestart() ;
		// Restart�ÿ� �̹����� ������ �ܻ� �� ������ ����
		if (img.size () == 0) {
			autoImg.setImageResource(R.drawable.alpha) ;
			aftImg.setAlpha(0) ;
		}
	}
	
	// Activity Stop 
	public void onStop () {
		super.onStop() ;
		// ���� �������� ������ ��� ����
		auto.interrupt () ;	
		auto.runFlag =true ;
		focusThread.interrupt () ;
		focusThread.runFlag =true ;
		aftImgThread.interrupt() ;
		aftImgThread.runFlag =true ;
	}
}
