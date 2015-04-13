package com.example.fillinfillin;

import java.nio.ByteBuffer;
import java.util.Arrays;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlayComVS extends Activity {

	MainActivity mainAct ;
	ArrayFill2D arrFill ;
	TimerThread timerThread ;
	ReplayThread replayThread ;
	TimerComThread timerComThread ;
	Etc etc ;
	
	private final int WIDTH =mainAct.FILL_WIDTH, HEIGHT =mainAct.FILL_HEIGHT,
			MATRIX =mainAct.FILL_MATRIX /2, COUNT =mainAct.FILL_CLICK_COUNT,
			TIME =mainAct.FILL_PLAY_TIME, SHAPE =mainAct.FILL_SHAPE_COUNT ;
	private int[][] colorCnt ={{0, R.drawable.blue}, {0, R.drawable.red}, {0, R.drawable.green},	// com ����� üũ
			{0, R.drawable.cyan}, {0, R.drawable.gray}, {0, R.drawable.purple}, {0, R.drawable.yellow}} ;
	private ProgressBar timebar ;	// Ÿ�̸�
	private int SECOND =2000, clickCnt =COUNT ; 	// ���̵� �� �ʿ� Ŭ�� ���Ѽ�
	private ImageButton btnReplay, btnBGsound, btnRed, btnBlue, btnGreen, btnCyan, btnGray, btnPurple, btnYellow ;
	private TextView textCnt ;
	private ImageView animTarget ;
	private RelativeLayout relativeMidLay ;
	private boolean colorFlag ;

	private Handler playHandler =new Handler () {
		public void handleMessage (Message msg) 
		{
			super.handleMessage(msg) ;
			switch (msg.what) {
			case 0 :
				timebar.incrementProgressBy(-1) ;
				if (timebar.getProgress() == 0) {
					replayClick (btnReplay) ;
				}
				break ;
			case 1 :
				clickCnt = COUNT; // ī��Ʈ �ʱ�ȭ

				arrFill.setRandomImg();
				relativeMidLay.invalidate(); // ȭ���� �ٽ� �µ�ο���

				textCnt.setText(clickCnt + "");

				timerCancel();
				timerStart();
				mainAct.replayFlag =false ;
				mainAct.playFlag =false ;
				mainAct.cancelFlag =false ;
				break ;
			case 2 :
				timerCancel();
				finish(); // ���� Activity�� ����
				mainAct.playFlag =false ;
				mainAct.cancelFlag =false ;
				break ;
			case 3:
				comChangeFill () ;
			}
		}
	} ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView (R.layout.play) ;

		relativeMidLay = (RelativeLayout) findViewById(R.id.playMidLayout);
		arrFill =new ArrayFill2D (PlayComVS.this, true) ;
		arrFill.setRandomImg() ;
		relativeMidLay.addView(arrFill) ;	// fillBase�� ä���.

		//Bundle extras =getIntent ().getExtras() ;	// MainActivity���� Intent�� �Ѱܹ��� ���� ����	
		textCnt =(TextView) findViewById (R.id.cnt) ;
		btnReplay =(ImageButton) findViewById (R.id.replay) ;
		btnRed =(ImageButton) findViewById (R.id.red) ;
		btnBlue =(ImageButton) findViewById (R.id.blue) ;
		btnGreen =(ImageButton) findViewById (R.id.green) ;
		btnCyan =(ImageButton) findViewById (R.id.cyan) ;
		btnGray =(ImageButton) findViewById (R.id.gray) ;
		btnPurple =(ImageButton) findViewById (R.id.purple) ;
		btnYellow =(ImageButton) findViewById (R.id.yellow) ;
		btnBGsound =(ImageButton) findViewById (R.id.bgSound2) ;
		timebar =(ProgressBar) findViewById (R.id.timebar) ;	
		animTarget =(ImageView) findViewById (R.id.time) ;
		
		mainAct.replayFlag =false ;
		
		etc =new Etc (PlayComVS.this) ;
		etc.Clicked(textCnt, btnGray, btnPurple, btnYellow) ;
		etc.Sounds(btnBGsound) ;
		
		if (SHAPE == 5) SECOND =1800 ;
		else if (SHAPE == 7) SECOND =1500 ;
		
		replayThread =new ReplayThread (playHandler) ;
		replayThread.setDaemon(true) ;	
		replayThread.start() ;
		
		timerThread =new TimerThread (PlayComVS.this, playHandler, TIME) ;
		timerThread.setDaemon(true) ;
		timerThread.start() ;
		
		timerComThread =new TimerComThread (playHandler, SECOND) ;
		timerComThread.setDaemon (true) ;
		timerComThread.start() ;
		
		timerStart () ;
	}
	
	// Bitmap�� id ��
	public boolean compareFill(Bitmap orgImg, int cmpImg)
	{
		/*Drawable temp = orgImg.getDrawable();
		Bitmap tempBit = ((BitmapDrawable) temp).getBitmap();
		
		Drawable temp1 = Play.this.getResources().getDrawable(cmpImg);
		Bitmap tempBit1 = ((BitmapDrawable) temp1).getBitmap();

		if (orgImg.equals(tempBit1)) {
			Toast.makeText(Play.this, "��ư��ġ", Toast.LENGTH_SHORT).show () ;
			return true;
		} else {
			Toast.makeText(Play.this, "��ư����ġ", Toast.LENGTH_SHORT).show () ;
			return false;
		}*/
		
		ByteBuffer buffer1 =ByteBuffer.allocate (orgImg.getHeight() *orgImg.getRowBytes()) ;
		orgImg.copyPixelsToBuffer(buffer1) ;
		
		Drawable temp1 = PlayComVS.this.getResources().getDrawable(cmpImg);
		Bitmap tempBit1 = ((BitmapDrawable) temp1).getBitmap();
		ByteBuffer buffer2 =ByteBuffer.allocate (tempBit1.getHeight() *tempBit1.getRowBytes()) ;
		tempBit1.copyPixelsToBuffer(buffer2) ;
		
		return Arrays.equals (buffer1.array (), buffer2.array ()) ;
	}

	// Bitmap ������
	public boolean compareFill(Bitmap orgImg, Bitmap cmpImg) 
	{
		// orgImg�� �̹������϶�
		/*Drawable temp = orgImg.getDrawable();
		Bitmap tempBit = ((BitmapDrawable) temp).getBitmap();
		
		Drawable temp1 = cmpImg.getDrawable();
		Bitmap tempBit1 = ((BitmapDrawable) temp1).getBitmap();

		if (orgImg.equals(cmpImg)) {
			return true;
		} else {
			Toast.makeText(Play.this, "�ٸ�",  Toast.LENGTH_SHORT).show () ;
			return false;
		}*/
		
		ByteBuffer buffer1 =ByteBuffer.allocate (orgImg.getHeight() *orgImg.getRowBytes()) ;
		orgImg.copyPixelsToBuffer(buffer1) ;
		
		ByteBuffer buffer2 =ByteBuffer.allocate (cmpImg.getHeight() *cmpImg.getRowBytes()) ;
		cmpImg.copyPixelsToBuffer(buffer2) ;
		
		return Arrays.equals (buffer1.array (), buffer2.array ()) ;
	}


	// �� ��ư Ŭ�� ��
	public void changeClick(final View v) 
	{	
		if (mainAct.soundFlag) mainAct.soundpool.play(mainAct.sound[3], 1, 1, 1, 0, 1) ;
		if (!colorFlag && clickCnt > 0) {
			colorFlag =true ;
			etc.buttonDisabled(btnReplay, btnBGsound, btnRed, btnBlue, 
					btnGreen, btnCyan, btnGray, btnPurple, btnYellow) ;
		playHandler.postDelayed (new Runnable () {
				public void run ()
				{
					textCnt.setText (--clickCnt +"") ;	// ī��Ʈ ���� 1�ٿ��� ����
					
					switch (v.getId()) {	// ����ư ���̵� ���� ����
					case R.id.red:
						arrFill.changeFill(R.drawable.red) ;
						break;
					case R.id.blue:
						arrFill.changeFill(R.drawable.blue) ;
						break;
					case R.id.green:
						arrFill.changeFill(R.drawable.green) ;
						break;
					case R.id.cyan:
						arrFill.changeFill(R.drawable.cyan) ;
						break;
					case R.id.gray:
						arrFill.changeFill(R.drawable.gray) ;
						break;
					case R.id.purple:
						arrFill.changeFill(R.drawable.purple) ;
						break;
					case R.id.yellow:
						arrFill.changeFill(R.drawable.yellow) ;
						break;
					}					
					relativeMidLay.invalidate() ;
					arrFill.checkFill() ;
					// ī��Ʈ�� 0�̰� flood�� �� �Ǿ��� �� 
					if (clickCnt <= 0 && arrFill.checkCnt < MATRIX) replayClick (v) ;
					else if (clickCnt >= 0 && arrFill.checkCnt == MATRIX) {
						if (mainAct.soundFlag) mainAct.soundpool.play(mainAct.sound[1], 1, 1, 1, 0, 1) ;
						
						clickCnt =COUNT ;	// ī��Ʈ �ʱ�ȭ
						arrFill.setRandomImg() ;
						textCnt.setText (clickCnt +"") ;
						
						timerCancel () ;
						timerStart () ;
					}
					
					etc.buttonEnabled(btnReplay, btnBGsound, btnRed, btnBlue, 
							btnGreen, btnCyan, btnGray, btnPurple, btnYellow) ;
					colorFlag =false ;
				}
			}, mainAct.D_TAB) ;
		}
		else return ;
	}
	
	protected void comCompareFill (int img, int r, int c)
	{		
		for (int i =0 ; i < SHAPE ; i++) 
			if (img == colorCnt[i][1]) {
				colorCnt[i][0]++ ;
				break ;
			}
	}
	
	protected int comCheckFill () 
	{
		int index =0 ;
		Data data =new Data (), tempData =new Data () ;
		for (int i =0 ; i < SHAPE ; i++) colorCnt[i][0] =0 ;
		
		while (arrFill.comDataList.size() > 0) {
	
			int cnt =0, row, col ;
			data =arrFill.comDataList.get(index) ;		
			row =data.getRow() ; col =data.getCol() ;

			if (col +1 < WIDTH && !arrFill.coord[row][col +1]) {
				if (arrFill.bitmapId[0][WIDTH /2] == arrFill.bitmapId[row][col +1]) {
					
					arrFill.coord[row][col +1] =true ;
					cnt++ ;
					tempData.setData(row, col +1) ;
					arrFill.comDataList.add(tempData.clone()) ;
				}
				else comCompareFill (arrFill.bitmapId[row][col +1], row, col +1) ;
			} else cnt++ ;
			
			if (row +1 < HEIGHT && !arrFill.coord[row +1][col]) {
				if (arrFill.bitmapId[0][WIDTH /2] == arrFill.bitmapId[row +1][col]) {

					arrFill.coord[row +1][col] =true ;
					cnt++ ;
					tempData.setData(row +1, col) ;
					arrFill.comDataList.add (tempData.clone()) ;
				}
				else comCompareFill (arrFill.bitmapId[row +1][col], row +1, col) ;
			} else cnt++ ;

			if (col > WIDTH /2 && !arrFill.coord[row][col -1]) {	
				if (arrFill.bitmapId[0][WIDTH /2] == arrFill.bitmapId[row][col -1]) {
		
					arrFill.coord[row][col -1] =true ;
					cnt++ ;
					tempData.setData (row, col -1) ;
					arrFill.comDataList.add(tempData.clone()) ;
				}
				else comCompareFill (arrFill.bitmapId[row][col -1], row, col -1) ;
			} else cnt++ ;
	
			if (row > 0 && !arrFill.coord[row -1][col]) {
				if (arrFill.bitmapId[0][WIDTH /2] == arrFill.bitmapId[row -1][col]) {

					arrFill.coord[row -1][col] =true ;
					cnt++ ;
					tempData.setData(row -1, col) ;
					arrFill.comDataList.add(tempData.clone()) ;
				}
				else comCompareFill (arrFill.bitmapId[row -1][col], row -1, col) ;
			} else cnt++ ;

			if (cnt == 4) arrFill.comDataList.remove(index) ;
			else if (index +1 == arrFill.comDataList.size ()) break ;
			else index++ ;
			
			if (index >= arrFill.comDataList.size ()) break ;
		}

		int temp =0 ;
		for (int i =1 ; i < SHAPE ; i++) {
			if (i < 2) {
				if (colorCnt[i -1][0] < colorCnt[i][0]) temp =i ;
				else temp =i -1 ;
			}
			else if (colorCnt[temp][0] < colorCnt[i][0]) temp =i ;
		}
		return colorCnt[temp][1] ;
	}
	

	protected void comChangeFill ()
	{
		int comCheckCnt =0 ;
		arrFill.coord[0][WIDTH /2] =true ;	// (0, 0)�� �׻� true
		int aroundImg =comCheckFill () ;	// (0, 0)���� Ž��
		
		for (int i =0 ; i < HEIGHT ; i++) {
			for (int  j =WIDTH /2 ; j < WIDTH ; j++) {
				if (arrFill.coord[i][j]) {	// ��ǥ�� ���� flood���� �� ��
					arrFill.bitmapId[i][j] =aroundImg ;	// ResourceId�� (0, 0)�� Id�� ����
					comCheckCnt++ ;
				}
			}
		}
		if (comCheckCnt == MATRIX) {
			timerCancel () ;
			replayClick (btnReplay) ;
		}
		relativeMidLay.invalidate () ;	// �ٲ� �׸��� ���� �׸���.
	}
	
	public void timerCancel ()
	{
		if (timerComThread != null && timerComThread.isAlive()) {
			timerComThread.runFlag =true ;
		}
		if (timerThread != null && timerThread.isAlive()) {
			timerThread.runFlag =true ;
		}	
		if (replayThread != null && replayThread.isAlive())
			replayThread.runFlag =true ;
		timerThread.animTime.cancel() ;
	}
	
	public void timerStart ()
	{		
		timebar.setMax(TIME) ;	// progressbar�� �ƽ� ������
		timebar.setProgress(TIME) ;	// progressbar�� ä���� �� ����
		timerThread.runFlag =false ;
		timerComThread.runFlag =false ;
		replayThread.runFlag =false ;
		animTarget.startAnimation (timerThread.animTime) ;
	}
	
	// �ٽ� �ϱ�
	public void replayClick (View v)
	{
		if (mainAct.replayFlag) return ;
		mainAct.replayFlag =true ;
		
		etc.buttonDisabled(btnReplay, btnBGsound, btnRed, btnBlue, btnGreen,
				btnCyan, btnGray, btnPurple, btnYellow);
		if (mainAct.soundFlag) mainAct.soundpool.play(mainAct.sound[2], 1, 1, 1, 0, 1) ;

		Intent replay =new Intent (PlayComVS.this, Replay.class) ;	// Intent ����
		startActivity (replay) ;	// Activity ȣ��
		/*AlertDialog.Builder altYN = new AlertDialog.Builder(this); // ��ȭ���� ����
		if (mainAct.soundFlag) mainAct.soundpool.play(mainAct.sound[2], 1, 1, 1, 0, 1) ;

		// �޼����� �����ϰ� ��ȭ���ڸ� ���� �� �ִ��� ���� Yes��ư�� Text�� �׿� ���� ������ ����
		altYN.setMessage("�ٽ� �Ͻðڽ��ϱ�?")
				.setCancelable(true)
				.setPositiveButton("�� ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								clickCnt = COUNT; // ī��Ʈ �ʱ�ȭ

								arrFill.setRandomImg();
								relativeMidLay.invalidate(); // ȭ���� �ٽ� �µ�ο���

								textCnt.setText(clickCnt + "");

								timerCancel();
								timerStart();
								
								replayFlag =false ;
							}
							// No��ư�� Text�� �׿� ���� ������ ����
						})
				.setNegativeButton("�� ��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel(); // ��ȭ���ڸ� ����
								timerCancel();
								finish(); // ���� Activity�� ����
							}
						});
		AlertDialog alt = altYN.create(); // ��ȭ������ ����
		alt.setTitle("RePlay"); // Title�� Text ����
		alt.show(); // ȭ���� ���*/

		etc.buttonEnabled(btnReplay, btnBGsound, btnRed, btnBlue, btnGreen,
				btnCyan, btnGray, btnPurple, btnYellow);

	}

	// ����� �Ͻ�����
	public void bgSounds2Click (View v) 
	{
		mainAct.soundFlag =!mainAct.soundFlag ;
		
		etc.buttonDisabled(btnReplay, btnBGsound, btnRed, btnBlue, 
				btnGreen, btnCyan, btnGray, btnPurple, btnYellow) ;
		
		if (mainAct.soundFlag) mainAct.soundpool.play(mainAct.sound[2], 1, 1, 1, 0, 1) ;
		etc.SoundClick(btnBGsound) ;
		
		etc.buttonEnabled(btnReplay, btnBGsound, btnRed, btnBlue, 
				btnGreen, btnCyan, btnGray, btnPurple, btnYellow) ;
	}
	
	// Activity �ҷ������� ���̾ƿ� ũ�ⱸ�ϱ�
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
	}
	
	// �ڷ� ���� ��ưŬ����
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				arrFill.recycle() ;
				finish () ;
				timerCancel () ;
				return true ;
			}
		}
		return super.onKeyDown(KeyCode, event);
	}
}
