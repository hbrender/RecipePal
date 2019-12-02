package com.example.recipepal.ui.recipe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecipeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RecipeViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is recipes fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}