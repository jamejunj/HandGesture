package com.example.handgesture.ui.DisplayFE;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.handgesture.R;

public class DisplayFeatureExtraction extends Fragment {

    private DisplayFeatureExtractionViewModel mViewModel;

    public static DisplayFeatureExtraction newInstance() {
        return new DisplayFeatureExtraction();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_display_feature_extraction, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DisplayFeatureExtractionViewModel.class);
        // TODO: Use the ViewModel
    }

}
