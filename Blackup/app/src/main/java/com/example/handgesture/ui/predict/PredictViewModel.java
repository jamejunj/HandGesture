package com.example.handgesture.ui.predict;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PredictViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PredictViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is parameter fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}