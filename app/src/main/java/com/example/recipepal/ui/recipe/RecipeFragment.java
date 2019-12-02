package com.example.recipepal.ui.recipe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.recipepal.R;

public class RecipeFragment extends Fragment {
    static final String TAG = "RecipeFragment";

    //private RecipeViewModel recipeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //recipeViewModel =
                ViewModelProviders.of(this).get(RecipeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recipes, container, false);
        TextView nameTextView = root.findViewById(R.id.nameTextView);
        /*recipeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        Button startRecipeButton = root.findViewById(R.id.startRecipeButton);
        startRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "click", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    public void startRecipeButtonOnClick(View view) {

    }
}