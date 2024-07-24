package com.example.imagepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.LogPrinter;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2{
    private static final String TAG="MainActivity";

    private Mat mRgba;
    private Mat mGray;
    private CameraBridgeViewBase mOpenCvCameraView;
    private  MainActivity mainClass;
    private ImageView translateThing;
    private ImageView takePicThing;
    private ImageView showImage;




    private boolean isItChecked=false;

    private String deneme;

    private ImageView currentImageThing;
    private static TextView textView;
    private static TextView etiketCheckText;

    private TextRecognizer textRecThing;

    private String cameraRecoThing="kamera";

    private String etiketThing="elbo132";

    private ImageView imageThing;

    int counterThing=0;
    int trueCheckThing=0;




    private Bitmap bitmap=null;
    private BaseLoaderCallback mLoaderCallback =new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface
                             .SUCCESS:{
                    Log.i(TAG,"OpenCv Is loaded");
                    mOpenCvCameraView.enableView();
                }
                default:
                {
                    super.onManagerConnected(status);

                }
                break;
            }
        }
    };

    public CameraActivity(){
        Log.i(TAG,"Instantiated new "+this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int MY_PERMISSIONS_REQUEST_CAMERA=0;

        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(CameraActivity.this, new String[] {Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }

        setContentView(R.layout.activity_camera);

        mOpenCvCameraView=(CameraBridgeViewBase) findViewById(R.id.frame_Surface);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);


        textView=findViewById(R.id.textView);

        textRecThing= TextRecognition.getClient(new TextRecognizerOptions.Builder().build());

        takePicThing=findViewById(R.id.takePicThing);
        etiketCheckText.findViewById(R.id.checkEtiketThing);

        takePicThing.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    return true;
                }if(event.getAction()==MotionEvent.ACTION_UP){
                    Toast.makeText(CameraActivity.this,MainActivity.insideInput,Toast.LENGTH_SHORT).show();
                    takePicThing.setColorFilter(Color.GRAY);
                    if(cameraRecoThing=="kamera"){

                        Mat a=mRgba.t();
                        Core.flip(a,mRgba,1);
                        a.release();
                        bitmap=Bitmap.createBitmap(mRgba.cols(),mRgba.rows() ,Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(mRgba, bitmap);

                        mOpenCvCameraView.disableView();
                        cameraRecoThing="recoTextThing";
                    }
                    return true;
                }
                return false;
            }
        });
        showImage=findViewById(R.id.showImage);

        showImage.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    return true;
                }if(event.getAction()==MotionEvent.ACTION_UP){
                    if(cameraRecoThing=="recoTextThing"){

                    }else{
                        Toast.makeText(CameraActivity.this,"Foti çek.",Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });
        translateThing=findViewById(R.id.translateThing);

        translateThing.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){

                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    translateThing.setColorFilter(Color.GRAY);
                    return true;
                }if(event.getAction()==MotionEvent.ACTION_UP){
                    if(etiketThing.equals(MainActivity.insideInput)){


                    }else{
                        translateThing.setColorFilter(Color.WHITE);
                    }

                    if(cameraRecoThing=="recoTextThing"){

                        textView.setVisibility(View.VISIBLE);
                        InputImage image=InputImage.fromBitmap(bitmap,0);
                        Task<Text> result=textRecThing.process(image)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text text) {
                                        textView.setText(text.getText());
                                        deneme= (String) textView.getText();
                                        String[] sep=deneme.toLowerCase().split(System.lineSeparator());



                                        for(String a: sep){
                                            char[] sonDeneme=a.toCharArray();
                                            char[] sonMainActivity=MainActivity.insideInput.toCharArray();
                                            for (char b : sonDeneme){
                                                for(char c:sonMainActivity){
                                                    if(b==c){
                                                        counterThing++;

                                                    }
                                                }
                                            }
                                            if(counterThing>=3){
                                                showImage.setColorFilter(Color.GREEN);
                                            }
                                            if(a.toLowerCase().equals(MainActivity.insideInput.toLowerCase())){



                                                isItChecked=true;
                                            }
                                        }

                                        Toast.makeText(CameraActivity.this,deneme,Toast.LENGTH_SHORT).show();

                                        if(isItChecked){
                                            translateThing.setColorFilter(Color.GREEN);




                                        }else{
                                            translateThing.setColorFilter(Color.WHITE);
                                        }
                                        Log.d("Kamera Aktivite","thing:"+text.getText());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                    }
                    return true;
                }
                else{
                }
                return false;
            }
        });



    }



    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()){
            //if load success
            Log.d(TAG,"Opencv initialization is done");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else{
            //if not loaded
            Log.d(TAG,"Opencv is not loaded. try again");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,mLoaderCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView !=null){
            mOpenCvCameraView.disableView();
        }
    }

    public void onDestroy(){
        super.onDestroy();
        if(mOpenCvCameraView !=null){
            mOpenCvCameraView.disableView();
        }

    }

    public void onCameraViewStarted(int width ,int height){
        mRgba=new Mat(height,width, CvType.CV_8UC4);
        mGray =new Mat(height,width,CvType.CV_8UC1);
    }
    public void onCameraViewStopped(){
        mRgba.release();
    }
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        mRgba=inputFrame.rgba();
        mGray=inputFrame.gray();

        return mRgba;

    }

}