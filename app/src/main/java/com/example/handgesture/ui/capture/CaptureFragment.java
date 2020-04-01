package com.example.handgesture.ui.capture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.handgesture.R;
import com.example.handgesture.ui.capture.CaptureViewModel;

public class CaptureFragment extends Fragment {

    private CaptureViewModel captureViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        captureViewModel =
        ViewModelProviders.of(this).get(CaptureViewModel.class);
        View root = inflater.inflate(R.layout.fragment_capture, container, false);
        /*final TextView textView = root.findViewById(R.id.text_parameter);
        parameterViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
}