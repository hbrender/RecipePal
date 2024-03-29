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

// tutorial referenced: https://www.youtube.com/watch?v=ARezg1D9Zd0

public class AddIngredientDialog extends AppCompatDialogFragment {
    private EditText amountEditText;
    private EditText nameEditText;
    private AddIngredientDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_ingredient, null);

        amountEditText = view.findViewById(R.id.amountEditText);
        nameEditText = view.findViewById(R.id.nameEditText);

        builder.setView(view)
                .setTitle(getString(R.string.add_ingredient))
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String amount = amountEditText.getText().toString();
                        String name = nameEditText.getText().toString();
                        listener.applyTexts(amount, name);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddIngredientDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AddIngredientListener");
        }
    }

    public interface AddIngredientDialogListener {
        void applyTexts(String amount, String name);
    }
}
