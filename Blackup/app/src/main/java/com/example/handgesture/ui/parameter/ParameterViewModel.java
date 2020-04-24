package com.example.handgesture.ui.parameter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ParameterViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ParameterViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is parameter fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}