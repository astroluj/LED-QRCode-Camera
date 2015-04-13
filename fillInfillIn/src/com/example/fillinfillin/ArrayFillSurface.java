package com.example.fillinfillin;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

 
public class ArrayFillSurface extends GLSurfaceView {

    ArrayFillRenderer renderer ;

    // ���� ��ġ ������ x, y ��ǥ
    private float preX, preY;
    
 // ������
    public ArrayFillSurface(Context context) {
    	super(context);
    		
        renderer = new ArrayFillRenderer(context.getApplicationContext());
        setRenderer(renderer); 
    }
     
    public boolean onTouchEvent(MotionEvent event) {
        // ���� ������ ��ġ ������ x, y ��ǥ
        float X = event.getX();
        float Y = event.getY();
         
        switch (event.getAction()) {
        case MotionEvent.ACTION_MOVE:
            // ���� ���� ������ ���� ���� ���� ������ �Ÿ� ���
            float dX = X - preX;
            float dY = Y - preY;
            // cube ��ü�� ȸ�� ���� ������ �ش�.
            renderer.cube.angleY += (dX * 0.5f);
            renderer.cube.angleX += (dY * 0.5f);
             
            break;
        }
         
        // ���� ��ġ ���� ����
        preX = X ;
        preY = Y ;
         
        return true;
    }

}
