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

// tutorial referenced: https://www.youtube.com/watch?v=ARezg1D9Zd0

public class AddInstructionDialog extends AppCompatDialogFragment {
    private EditText stepNumEditView;
    private EditText contentEditText;
    private AddInstructionDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_instruction, null);

        stepNumEditView = view.findViewById(R.id.stepNumEditView);
        contentEditText = view.findViewById(R.id.contentEditText);

        builder.setView(view)
                .setTitle(getString(R.string.add_instruction))
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //String amount = amountEditText.getText().toString();
                        //String name = nameEditText.getText().toString();
                        //listener.applyTexts(amount, name);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        //try {
        //    listener = (AddInstructionDialogListener) context;
        //} catch (ClassCastException e) {
        //    throw new ClassCastException(context.toString() + "must implement AddInstructionDialogListener");
        //}
    }

    public interface AddInstructionDialogListener {
        void applyTexts(String amount, String name);
    }
}
