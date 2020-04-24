package com.example.handgesture;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static int TIME_OUT = 2500;

    ImageView imageView;
    Uri imageUri;
    Bitmap grayBitmap,imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.previewImage);

        OpenCVLoader.initDebug();

        setBuild((TextView)findViewById(R.id.buildDetail));

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent captureIntent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivity(captureIntent);
                finish();
            }
        },TIME_OUT);
    }

    public static void setBuild(TextView build){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss", Locale.US);
        Date now = new Date();
        String str = "build " + formatter.format(now);
        build.setText(str);
    }

    public void openGallery(View v){
        Intent myIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(myIntent,100);

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK && data!= null){
            imageUri = data.getData();

            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            }catch (IOException e){
                e.printStackTrace();
            }

            imageView.setImageBitmap(imageBitmap);
        }
    }


    public void convertToGray(View v){
        Mat Rgba = new Mat();
        Mat grayMat = new Mat();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither = false;
        o.inSampleSize=4;

        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();

        grayBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);


        // bitmap to MAT

        Utils.bitmapToMat(imageBitmap,Rgba);


        Imgproc.cvtColor(Rgba,grayMat,Imgproc.COLOR_RGB2GRAY);

        Utils.matToBitmap(grayMat,grayBitmap);

        imageView.setImageBitmap(grayBitmap);




    }

}
