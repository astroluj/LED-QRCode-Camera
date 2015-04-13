package com.example.fillinfillin;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.app.Activity;

public class Logo extends Activity {
	
	MainActivity mainAct ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo);
		new Intro ().start() ;
	}
	class Intro extends Thread {

		public void run () {

			SystemClock.sleep( 3000 );
			finish();
		}
	}
	
	// �ڷ� ���� ��ưŬ����
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				mainAct.BGmp.release() ;	// ���ҽ� ����
				finish () ;	// Activity�� �����ϰ�
				System.exit (0) ;	// ���� ����
				
				return true ;
			}
		}
		return super.onKeyDown(KeyCode, event);
	}
}