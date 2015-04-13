package com.example.fillinfillin;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
 
import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;
 
public class ArrayFillRenderer implements Renderer{
     
    MainActivity mainAct ;
    PlayCrazy playC ;
    ArrayFillCube cube;
    
    private int angle = 0;
    private Context context ;
    
    public  ArrayFillRenderer (Context context)
    {	
    	this.context =context ;
    	cube =new ArrayFillCube (context) ;	
    }
    
    @Override
    public void onDrawFrame(GL10 gl) {
    	// �ʱ�ȭ
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();	// ��ȯ���
        gl.glTranslatef(0.0f, 0.0f, -mainAct.scaleWidth /90.0f);	// ũ��
        gl.glRotatef(45.0f, 1, 1, 0);	// �ʱ⿡ �������� ����
        cube.draw(gl, context);
    }
 
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);	// ȭ�鿡 �������� ũ��
        gl.glMatrixMode(GL10.GL_PROJECTION);	// ����
        gl.glLoadIdentity();
        // ���� ��
        GLU.gluPerspective(gl, 45.0f, (float)width/height, 1.0f, 30.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);	// �̵�, ȸ�� ������
        gl.glLoadIdentity();	// ��� �ʱ�ȭ
    }
 
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    	cube.loadGLTexture(gl, context) ;
    	
        // ������� ���� �ε巴��
        gl.glShadeModel(GL10.GL_SMOOTH);
        // ���� ����
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        // ���� ���� �ʱ�ȭ
        gl.glClearDepthf(1.0f);
        // ������ ���� ���� ���ۼ���
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // ������ ���� �ؽ��� 2D
        gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
        // �������ʴ� �þ��� �۾��� ����
        gl.glDepthFunc(GL10.GL_LEQUAL);
        // ������ ǰ���� �ֻ���
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
     } 
   }
