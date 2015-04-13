package com.example.dcamera;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class SensorThread extends Thread{

	protected static boolean runFlag, startSensorFlag, stopSensorFlag ;
	
	// ���� ����
	private SensorManager sensorM ;
	// ���� ������ ��� �� 
	private SensorEventListener sensorListener =new SensorEventListener () {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		public void onSensorChanged(SensorEvent event) {		
			//Log.d ("Azimuth(����)", "" +event.values[0]) ;
			//Log.d ("Pitch(��絵)", "" +event.values[1]) ;
			//Log.d ("Roll(ȸ��)", "" +event.values[2]) ;
			Message msg =new Message () ;
			msg.what =1 ;
			msg.obj =(float) event.values[2] ;
			handler.sendMessage(msg) ;
		}
	} ;
	private Handler handler ;
	private Sensor sensor ;


	public SensorThread (Context context, Handler handler)
	{
		runFlag =false ;	// ������ run
		startSensorFlag =false ;	// ������ ����
		stopSensorFlag =false ;	// ������ ����
		
		this.handler =handler ;
		
		// ������ ���� �ý����� ��´�.
		sensorM =(SensorManager) context.getSystemService(Context.SENSOR_SERVICE) ;
		sensor =sensorM.getDefaultSensor(Sensor.TYPE_ORIENTATION) ;		// ȸ�� ���� ���� ����
	
	}
	
	public void run () {
		Looper.prepare() ;
		while (!isInterrupted() && !runFlag) {
			try {
				sleep (1000) ;	// 1�� �������� ����
				if (startSensorFlag) {	// ���� ���� �޼����� ������
					// ������ ���� �߰� �ӵ��� �޴´�.
					sensorM.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL) ;
					startSensorFlag =false ;
				}
				else if (stopSensorFlag) {	// ���� ���� �޼����� ������
					// ���� Ž�� ����
					sensorM.unregisterListener(sensorListener) ;
					stopSensorFlag =false ;
				}

			} catch (InterruptedException e) {
				e.printStackTrace() ;
			}
		}
	}
}
