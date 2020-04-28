package com.example.handgesture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LinkActivity extends AppCompatActivity {

    Button feature_btn, compare_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);

        feature_btn = findViewById(R.id.btn_featureExtraction);
        compare_btn = findViewById(R.id.btn_compare);


        feature_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent destination = new Intent(LinkActivity.this, FeatureExtractionActivity.class);
                startActivity(destination);
            }
        });



        compare_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent destination = new Intent(LinkActivity.this, CompareImages.class);
                startActivity(destination);
            }
        });


    }
}
