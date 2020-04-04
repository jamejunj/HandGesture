/*package com.example.handgesture.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.handgesture.R;

public class Capture extends AppCompatActivity {
    ImageView imageView;
    Button btCap;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.fragment_capture);

        imageView = findViewById(R.id.capturePreview);
        btCap = findViewById(R.id.ActionCapture);

        if(ContextCompat.checkSelfPermission(Capture.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Capture.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }

        btCap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,100);

            }
        });
    }


    public void onActivityReenter(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100){
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(captureImage);
        }
    }
}
 */
