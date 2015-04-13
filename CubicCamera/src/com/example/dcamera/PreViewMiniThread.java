package com.example.dcamera;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class PreViewMiniThread extends Thread {

	protected static boolean autoFlag, runFlag ;
	private Handler handler ;
	
	public PreViewMiniThread (Handler handler) {
		this.handler =handler ;
		autoFlag =false ;
		runFlag =false ;
	}
	
	public void run () {
		Looper.prepare() ;
		while (!interrupted() && !runFlag) {
			try {
				sleep (100) ;	// 0.1�� �������� ����
				if (autoFlag)	// play �޼����� ������ 
					handler.sendEmptyMessage(0) ;	// �׿� ���� �޼��� ����
			} catch (InterruptedException e) {
				e.printStackTrace() ;
			}
		}
	}
}
