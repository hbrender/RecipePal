package com.example.recipepal.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.recipepal.R;

public class AddIngredientDialog extends AppCompatDialogFragment {
    private EditText ingredientAmountEditText;
    private EditText ingredientNameEditText;
    private AddIngredientDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_ingredient, null);

        ingredientAmountEditText = view.findViewById(R.id.ingredientAmountTextView);
        ingredientNameEditText = view.findViewById(R.id.ingredientNameTextView);

        builder.setView(view)
                .setTitle(getString(R.string.add_ingredient))
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String amount = ingredientAmountEditText.getText().toString();
                        String name = ingredientNameEditText.getText().toString();
                        listener.applyTexts(amount, name);
                    }
                });

        return builder.create();
        //return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddIngredientDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement add AddIngredientListener");
        }
    }

    public interface AddIngredientDialogListener {
        void applyTexts(String amount, String name);
    }
}
