package com.example.mobilelap_01_calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends Activity {

    private EditText editNum1, editNum2, editResult ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View Id ����
        // EditText
        editNum1 =(EditText) findViewById (R.id.edit_num1) ;
        editNum2 =(EditText) findViewById (R.id.edit_num2) ;
        editResult =(EditText) findViewById (R.id.edit_result) ;
    }
    
    // ���� Ŭ�� �̺�Ʈ
    public void unsignedClick (View v) {

        if (v.getId () == R.id.btn_unsigned1)
            // ������ �ƴ� ��
            if (!editNum1.getText().toString().equals(""))
                // ����ȭ
                editNum1.setText (Double.parseDouble(editNum1.getText().toString()) *-1 +"") ;
        else if (!editNum2.getText().toString().equals(""))
             // ����ȭ
            editNum2.setText(Double.parseDouble(editNum2.getText().toString()) *-1 +"") ;
    }

    // Plsu Fuction
    public void plusClick (View v) {
        double num1, num2, result ;

        try {
            num1 =Double.parseDouble(editNum1.getText().toString()) ;
        } catch (NumberFormatException e) {
            num1 =0 ;
        }

        try {
            num2 =Double.parseDouble(editNum2.getText().toString()) ;
        } catch (NumberFormatException e) {
            num2 =0 ;
        }

        result =num1 +num2 ;
        editResult.setText (result +"") ;
    }

    // Minus Fuction
    public void minusClick (View v) {
        double num1, num2, result ;

        try {
            num1 =Double.parseDouble(editNum1.getText().toString()) ;
        } catch (NumberFormatException e) {
            num1 =0 ;
        }

        try {
            num2 =Double.parseDouble(editNum2.getText().toString()) ;
        } catch (NumberFormatException e) {
            num2 =0 ;
        }

        result =num1 -num2 ;
        editResult.setText (result +"") ;
    }

    // Multiplication Fuction
    public void mulClick (View v) {
        double num1, num2, result ;

        try {
            num1 =Double.parseDouble(editNum1.getText().toString()) ;
        } catch (NumberFormatException e) {
            num1 =0 ;
        }

        try {
            num2 =Double.parseDouble(editNum2.getText().toString()) ;
        } catch (NumberFormatException e) {
            num2 =0 ;
        }

        result =num1 *num2 ;
        editResult.setText (result +"") ;
    }

    // Division Fuction
    public void divClick (View v) {
        double num1, num2, result;

        try {
            num1 = Double.parseDouble(editNum1.getText().toString());
        } catch (NumberFormatException e) {
            num1 = 0;
        }

        try {
            num2 = Double.parseDouble(editNum2.getText().toString());
        } catch (NumberFormatException e) {
            num2 = 0;
        }

        if (num2 == 0) editResult.setText("0���� ������ �����ϴ�.");
        else {
            result = num1 /num2;
            editResult.setText(result + "");
        }
    }
}
