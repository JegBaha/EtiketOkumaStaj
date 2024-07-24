package com.example.imagepro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    static {
        if(OpenCVLoader.initDebug()){
            Log.d("MainActivity: ","Opencv yüklendi");
        }
        else {
            Log.d("MainActivity: ","Opencv Yüklenemedi");
        }
    }

    private String  mongoDbApi="https://ap-south-1.aws.data.mongodb-api.com/app/application-0-bikphtw/endpoint/getUser";
    private EditText textInput;
    public static String insideInput;
    private Button camera_button;
    private EditText textDeneme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        camera_button=findViewById(R.id.camera_button);
        textInput=findViewById(R.id.etiketInput);



        textInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled=false;
                if(i== EditorInfo.IME_ACTION_SEND){
                    insideInput=textInput.getText().toString();
                }

                return false;
            }
        });

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insideInput=textInput.getText().toString();
                Toast.makeText(MainActivity.this,insideInput,Toast.LENGTH_SHORT).show();

                if(!insideInput.equals("")){

                    startActivity(new Intent(MainActivity.this,CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }


            }
        });

    }
}