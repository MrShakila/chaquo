package com.example.chaquo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class MainActivity extends AppCompatActivity {
    EditText num1,num2;
    Button btn;
    TextView txt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        num1 =(EditText) findViewById(R.id.num1);
        num2 =(EditText) findViewById(R.id.num2);
        btn =(Button) findViewById(R.id.btn);
        txt =(TextView) findViewById(R.id.textView);
        num1.setVisibility(View.INVISIBLE);
        num2.setVisibility(View.INVISIBLE);
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
            System.out.println("python script running");
        }
        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("script");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PyObject obj = pyObject.callAttr("main");
                txt.setText(obj.toString());
            }
        });
    }
}