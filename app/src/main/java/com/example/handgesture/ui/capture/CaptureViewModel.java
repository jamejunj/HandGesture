package com.example.handgesture.ui.capture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CaptureViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CaptureViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is parameter fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}