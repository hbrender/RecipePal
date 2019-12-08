package com.example.recipepal.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.recipepal.R;
import com.example.recipepal.fragments.RecipeListFragment;

// tutorial referenced: https://www.youtube.com/watch?v=ARezg1D9Zd0

public class AddRecipeDialog extends AppCompatDialogFragment {
    private EditText recipeNameEditText;
    private AddRecipeDialogListener listener;
    RecipeListFragment recipeListFragment;

    public AddRecipeDialog(RecipeListFragment recipeListFragment) {
        this.recipeListFragment = recipeListFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_recipe, null);

        recipeNameEditText = view.findViewById(R.id.recipeNameEditText);

        builder.setView(view)
                .setTitle(getString(R.string.add_recipe))
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = recipeNameEditText.getText().toString();
                        listener.applyTexts(name);

                        recipeListFragment.startRecipeInfoActivity(recipeListFragment.getRecipeId());
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddRecipeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AddRecipeDialogListener");
        }
    }

    public interface AddRecipeDialogListener {
        void applyTexts(String name);
    }
}
