package com.example.fillinfillin;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class Etc extends View {

	MainActivity mainAct ;
	
	public Etc (Context context)
	{
		super (context) ;
	}
	
	// Ŭ���� �ʱ�ȭ �� Ŭ�� �� �ʱ�ȭ
	public void Clicked (TextView textCnt, ImageButton btnGray, ImageButton btnPurple, ImageButton btnYellow)
	{
		textCnt.setText (mainAct.FILL_CLICK_COUNT +"") ;	// �ʱ� ī��Ʈ
		textCnt.setTextSize((float)(mainAct.scaleWidth /10 *5.5 /10 *1.5 /1.5)) ;
		
		switch (mainAct.FILL_SHAPE_COUNT) {
		case 4 :
			btnGray.setVisibility(View.GONE) ;
			btnPurple.setVisibility(View.GONE) ;
			btnYellow.setVisibility(View.GONE) ;
			break ;
		case 5 :	
			btnPurple.setVisibility(View.GONE) ;
			btnYellow.setVisibility(View.GONE) ;
			break ;
		}	
	}
	
	// �÷��� ���¿� �׸� ����
	public void Sounds (ImageButton btnBGsound)
	{		
		if (!mainAct.BGmp.isPlaying()) // �÷������̾ƴϸ�
			btnBGsound.setBackgroundResource(R.drawable.non_sound) ;
		
		else
			btnBGsound.setBackgroundResource(R.drawable.on_sound) ;
	}
	
	// ��ǥ �׸� Ŭ��
	public void SoundClick (ImageButton btnBGsound)
	{
		if (mainAct.BGmp.isPlaying()) {	// �÷��� ���̸�
			mainAct.BGmp.pause () ;	// �Ͻ����� 
			btnBGsound.setBackgroundResource(R.drawable.non_sound) ;
			btnBGsound.setTag(false) ;
		} 
		else {	// �÷������̾ƴϸ�
			mainAct.BGmp.start () ;	// ���
			btnBGsound.setBackgroundResource(R.drawable.on_sound) ;
			btnBGsound.setTag(true) ;
		}
	}
	
	// Hell ���
	public void setHellDifferent ()
	{
		mainAct.FILL_CLICK_COUNT =mainAct.HELL_CLICK_COUNT ;
		mainAct.FILL_PLAY_TIME =mainAct.HELL_TIME ;
		mainAct.FILL_MATRIX =mainAct.HELL_MATRIX ;
		mainAct.FILL_SHAPE_COUNT =mainAct.HELL_SHAPE_COUNT ;
		mainAct.FILL_WIDTH =mainAct.HELL_WIDTH;
		mainAct.FILL_HEIGHT  =mainAct.HELL_HEIGHT ;
	}
	
	// Hard ���
	public void setHardDifferent () 
	{
		mainAct.FILL_CLICK_COUNT =mainAct.HARD_CLICK_COUNT ;
		mainAct.FILL_PLAY_TIME =mainAct.HARD_TIME ;
		mainAct.FILL_MATRIX =mainAct.HARD_MATRIX ;
		mainAct.FILL_SHAPE_COUNT =mainAct.HARD_SHAPE_COUNT ;
		mainAct.FILL_WIDTH =mainAct.HARD_WIDTH;
		mainAct.FILL_HEIGHT  =mainAct.HARD_HEIGHT ;
	}
	
	// Easy ���
	public void setEasyDifferent ()
	{
		mainAct.FILL_CLICK_COUNT =mainAct.EASY_CLICK_COUNT ;
		mainAct.FILL_PLAY_TIME =mainAct.EASY_TIME ;
		mainAct.FILL_MATRIX =mainAct.EASY_MATRIX ;
		mainAct.FILL_SHAPE_COUNT =mainAct.EASY_SHAPE_COUNT ;
		mainAct.FILL_WIDTH =mainAct.EASY_WIDTH;
		mainAct.FILL_HEIGHT  =mainAct.EASY_HEIGHT ;
	}
	// ��ư ��Ȱ��ȭ
	public void buttonDisabled (ImageButton a, ImageButton b, ImageButton c,
			ImageButton d, ImageButton e, ImageButton f,
			ImageButton g, ImageButton h, ImageButton i)
	{
		if (a != null)
			a.setClickable(false) ;
		if (b != null)
			b.setClickable(false) ;
		if (c != null)
			c.setClickable(false) ;
		if (d != null)
			d.setClickable(false) ;
		if (e != null)
			e.setClickable(false) ;
		if (f != null)
			a.setClickable(false) ;
		if (g != null)
			b.setClickable(false) ;
		if (h != null)
			c.setClickable(false) ;
		if (i != null)
			d.setClickable(false) ;
	}

	// ��ư Ȱ��ȭ
	public void buttonEnabled (ImageButton a, ImageButton b, ImageButton c,
			ImageButton d, ImageButton e, ImageButton f,
			ImageButton g, ImageButton h, ImageButton i)
	{
		if (a != null)
			a.setClickable(true) ;
		if (b != null)
			b.setClickable(true) ;
		if (c != null)
			c.setClickable(true) ;
		if (d != null)
			d.setClickable(true) ;
		if (e != null)
			e.setClickable(true) ;
		if (f != null)
			a.setClickable(true) ;
		if (g != null)
			b.setClickable(true) ;
		if (h != null)
			c.setClickable(true) ;
		if (i != null)
			d.setClickable(true) ;
	}
}
