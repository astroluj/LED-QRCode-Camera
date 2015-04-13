package com.example.dcamera;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class FocusThread extends Thread {

	CameraActivity cameraAct;
	
	protected static boolean runFlag ;
	
	private Handler handler ;

	public FocusThread(Handler handler) {
		this.handler =handler ;
		runFlag =false ;
	}

	public void run() {
		Looper.prepare() ;
		while (!isInterrupted() && !runFlag) {
			try {
				sleep (100) ;	// 0.1�� �������� ����
				if (cameraAct.focusFlag)	// ������ ���� ���� ��
					handler.sendEmptyMessage(3) ;
				else	// ������ ������ ���°� �ƴ� ��
					handler.sendEmptyMessage(4) ;
			} catch (InterruptedException e) {
				e.printStackTrace() ;
			}
		}
	}
}
