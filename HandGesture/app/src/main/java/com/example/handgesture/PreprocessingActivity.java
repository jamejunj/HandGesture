package com.example.handgesture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PreprocessingActivity extends AppCompatActivity {
    Uri imageUri;
    Bitmap imageBitmap, grayBitmap, binaryBitmap, denoiseBitmap, contourBitmap;
    Button btn_back, btn_rgb, btn_gray, btn_binary, btn_denoise, btn_contour;
    ImageView preview;
    Mat sampledImgMat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preprocessing);

        btn_back = findViewById(R.id.returnBtn);
        btn_rgb = findViewById(R.id.change_rgb);
        btn_gray = findViewById(R.id.change_gray);
        btn_binary = findViewById(R.id.change_bin);
        btn_denoise = findViewById(R.id.change_denoise);
        btn_contour = findViewById(R.id.change_contour);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        btn_rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview.setImageBitmap(imageBitmap);
            }
        });

        btn_gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToGray(v);
                //displayImage(grayBitmap);
            }
        });

        btn_binary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToBinaryImage(v);
                //displayImage(binaryBitmap);
            }
        });

        btn_denoise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToDenoiseImage(v);
                //displayImage(denoiseBitmap);
            }
        });

        btn_contour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToContourImage(v);
                //displayImage(contourBitmap);
            }
        });

        preview = findViewById(R.id.previewImage);
        if (getIntent().getExtras().getString("type").equals("bmp")){
            imageBitmap = getIntent().getParcelableExtra("bmp");
            preview.setImageBitmap(imageBitmap);
        }else{
            imageUri = Uri.parse(getIntent().getExtras().getString("uri"));
            String path = getPath(imageUri);
            try{
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                imageBitmap = BitmapFactory.decodeStream(inputStream);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            preview.setImageBitmap(imageBitmap);
        }
    }

    private String getPath(Uri uri){
        if(uri == null){
            return null;
        }else{
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null){
                int col_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(col_index);
            }
        }
        return uri.getPath();
    }

    private Mat loadImage(String path){
        Mat originImage = Imgcodecs.imread(path); //Image will be in BGR format
        Mat rgbImg = new Mat();

        //Convert BGR to RGB
        Imgproc.cvtColor(originImage,rgbImg, Imgproc.COLOR_BGR2RGB);

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        int mobile_width = size.x;
        int mobile_height = size.y;

        sampledImgMat = new Mat();
        double downSampleRatio = calculateSubSimpleSize(rgbImg, mobile_width, mobile_height);

        Imgproc.resize(rgbImg,sampledImgMat, new Size(), downSampleRatio, downSampleRatio, Imgproc.INTER_AREA);

        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    sampledImgMat = sampledImgMat.t();
                    Core.flip(sampledImgMat, sampledImgMat,1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    sampledImgMat = sampledImgMat.t();
                    Core.flip(sampledImgMat, sampledImgMat,0);
                    break;
            }
        }catch(IOException e) {
            e.printStackTrace();
        }

        return sampledImgMat;
    }

    private double calculateSubSimpleSize(Mat src, int mobile_width, int mobile_height) {
        final int width = src.width();
        final int height = src.height();
        double inSampleSize = 1;

        if (height > mobile_height || width > mobile_width){
            //Calculate the ratio
            final double heightRatio = (double)mobile_height / (double)height;
            final double widthRatio = (double)mobile_width / (double)width;

            inSampleSize = heightRatio < widthRatio ? height : width;
        }
        return inSampleSize;
    }

    private Bitmap convertMatToImageRGB(Mat mat){
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(),mat.rows(),Bitmap.Config.RGB_565);
        //Convert mat to bitmap
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    private void convertToGray(View v){
        //Read image from imageUri
//        try {
//            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//        }catch(IOException e){
//            e.printStackTrace();
//        }

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither = false;
        o.inSampleSize = 4;

        //Read size of Mat
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();

        //Create Mat
        Mat Rgba = new Mat();       //Input
        Mat grayMat = new Mat();    //Output

        //Convert Bitmap into Mat
        Utils.bitmapToMat(imageBitmap, Rgba);

        //Image Processing
        Imgproc.cvtColor(Rgba, grayMat, Imgproc.COLOR_BGR2GRAY);

        //Convert Mat into bitmap value
        //Create grayBitmap
        grayBitmap = Bitmap.createBitmap(width, height,Bitmap.Config.RGB_565);
        Utils.matToBitmap(grayMat, grayBitmap);

        preview.setImageBitmap(grayBitmap);
    }

    private void convertToBinaryImage(View v) {
        //Read image from imageUri
//        try {
//            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//        }catch(IOException e){
//            e.printStackTrace();
//        }

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither = false;
        o.inSampleSize = 4;

        //Read size of Mat
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();

        //Create Mat
        Mat Rgba = new Mat();       //Input

        //Convert Bitmap into Mat
        Utils.bitmapToMat(imageBitmap, Rgba);

        //Image Processing
        Imgproc.cvtColor(Rgba, Rgba, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(Rgba, Rgba, new Size(7,7),0);
        Imgproc.threshold(Rgba, Rgba, 50,255.0,Imgproc.THRESH_BINARY);

        //Convert Mat into bitmap value
        //Create binaryBitmap
        binaryBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(Rgba,binaryBitmap);

        preview.setImageBitmap(binaryBitmap);
    }

    private void convertToDenoiseImage(View v){
        //Read size of Mat
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();

        //Create Mat
        Mat Rgba = new Mat();

        //Convert Bitmap into Mat
        Utils.bitmapToMat(imageBitmap, Rgba);

        //Image Processing
        Imgproc.cvtColor(Rgba, Rgba, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(Rgba, Rgba, new Size(7,7),0);
        Imgproc.threshold(Rgba, Rgba, 50,255.0,Imgproc.THRESH_BINARY);

        Mat kernalErode = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(7,7));
        Mat kernalDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5));
        Imgproc.erode(Rgba, Rgba, kernalErode);
        Imgproc.dilate(Rgba, Rgba, kernalDilate);
        Imgproc.dilate(Rgba, Rgba, kernalDilate);

        //Convert Mat into bitmap value
        //Create binaryBitmap
        denoiseBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(Rgba,denoiseBitmap);

        preview.setImageBitmap(denoiseBitmap);
    }

    private void convertToContourImage(View v){
        //Read image to Bitmap, then convert to Mat
        //Read size of Mat
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();

        //Create Mat
        Mat Rgba = new Mat();

        //Convert Bitmap into Mat
        Utils.bitmapToMat(imageBitmap, Rgba);

        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        Mat hierarchy = new Mat();
        Mat contours = new Mat();

        //Create A list to store all the contours
        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();

        //Image Processing
        Imgproc.cvtColor(Rgba, Rgba, Imgproc.COLOR_BGR2GRAY);
        //Thresholding
        Imgproc.threshold(Rgba, Rgba, 50,255.0,Imgproc.THRESH_BINARY);
        //Morphology
        Mat kernalErode = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(7,7));
        Mat kernalDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5));
        Imgproc.erode(Rgba, Rgba, kernalErode);
        Imgproc.dilate(Rgba, Rgba, kernalDilate);
        Imgproc.dilate(Rgba, Rgba, kernalDilate);

        //Process Canny edge and store in cannyEdge
        Imgproc.Canny(Rgba, cannyEdges, 10, 100);

        //Finding contours
        Imgproc.findContours(cannyEdges, contourList, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        //Create a Mat for store contour
        contours.create(cannyEdges.rows(), cannyEdges.cols(), CvType.CV_8UC3);

        //Draw contour
        Imgproc.drawContours(contours, contourList, -1, new Scalar(255,255,255), -1);

        //Converting Mat back to Bitmap
        contourBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(contours, contourBitmap);

        preview.setImageBitmap(contourBitmap);
    }
}
