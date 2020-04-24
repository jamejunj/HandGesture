package com.example.handgesture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static int TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
