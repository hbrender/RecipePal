package com.example.recipepal.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.recipepal.R;
import com.example.recipepal.fragments.GroceryListFragment;
import com.example.recipepal.fragments.RecipeListFragment;

public class AddGroceryDialog extends AppCompatDialogFragment {
    private EditText groceryName;
    private EditText groceryQuantity;
    AddGroceryDialogListener listener;
    GroceryListFragment groceryListFragment;

    public AddGroceryDialog(GroceryListFragment groceryListFragment){
        this.groceryListFragment = groceryListFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_add_grocery, null);

        groceryName = (EditText) view.findViewById(R.id.groceryItemName);
        groceryQuantity = (EditText) view.findViewById(R.id.groceryItemQuantity);

        builder.setView(view)
                .setTitle(R.string.add_grocery)
                .setNegativeButton(getString(R.string.cancel),null)
                .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = groceryName.getText().toString();
                        String quantity = groceryQuantity.getText().toString();
                        listener.applyTexts(name, quantity);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddGroceryDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AddRecipeDialogListener");
        }
    }

    public interface AddGroceryDialogListener {
        void applyTexts(String name, String quantity);
    }
}
