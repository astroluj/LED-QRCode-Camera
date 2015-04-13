package com.example.dcamera;

import android.os.Handler;
import android.os.Looper; 

public class AfterImgThread extends Thread {
	
	CameraActivity cameraAct ;
	
	protected static boolean runFlag ;
	private Handler handler ;
	
	public AfterImgThread (Handler handler) {
		this.handler =handler ;
		runFlag =false ;
	}

	public void run () {
		Looper.prepare () ;
		while (!interrupted() && !runFlag) {
			try {
				sleep (100) ;	// 0.1�� �������� ����
				if (cameraAct.afterFlag) {	// �ܻ� ǥ�� �޼����� ���� ��
					handler.sendEmptyMessage(1) ;
					cameraAct.afterFlag =false ;
				}
				if (cameraAct.captureFlag) {	// ĸ�� �޼����� ���� ��
					handler.sendEmptyMessage(2) ;
					cameraAct.captureFlag =false ;
				}
			} catch (InterruptedException e) {
				e.printStackTrace() ;
			}
		}
	}
}
