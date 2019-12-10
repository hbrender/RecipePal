package com.example.recipepal.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.recipepal.R;
import com.example.recipepal.activities.RecipeInfoActivity;
import com.example.recipepal.helpers.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

// tutorial referenced: https://www.youtube.com/watch?v=ARezg1D9Zd0

public class AddInstructionDialog extends AppCompatDialogFragment {
    static final String TAG = "AddInstructionDialog";
    private int instructionId;
    private String step;
    private String content;
    private String time;
    private byte[] image;

    private View view;
    private EditText stepNumEditView;
    private EditText contentEditText;
    private EditText timerEditText;
    private ImageView imageView;
    private Button addInstructionPhotoButton;
    private AddInstructionDialogListener listener;
    private DatabaseHelper databaseHelper;

    public AddInstructionDialog() {
        this.instructionId = -1;
    }

    public AddInstructionDialog(int instructionId, String step, String content, String time, byte[] image) {
        this.instructionId = instructionId;
        this.step = step;
        this.content = content;
        this.time = time;
        this.image = image;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_instruction, null);

        databaseHelper = new DatabaseHelper(getContext());
        stepNumEditView = view.findViewById(R.id.stepNumEditView);
        contentEditText = view.findViewById(R.id.contentEditText);
        timerEditText = view.findViewById(R.id.timerEditText);
        imageView = view.findViewById(R.id.imageView);
        addInstructionPhotoButton = view.findViewById(R.id.addInstructionPhotoButton);

        if (instructionId != -1) {
            stepNumEditView.setText(step);
            contentEditText.setText(content);
            timerEditText.setText(time);

            if (image != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                imageView.setImageBitmap(bitmap);
                addInstructionPhotoButton.setVisibility(View.GONE);
            }

            builder.setView(view)
                    .setTitle(getString(R.string.add_instruction))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String step = stepNumEditView.getText().toString();
                            String content = contentEditText.getText().toString();
                            String timer = timerEditText.getText().toString();
                            byte[] image = imageViewToByte(imageView);

                            listener.applyTexts(instructionId, step, content, timer, image);
                        }
                    });
        } else {
            builder.setView(view)
                    .setTitle(getString(R.string.add_instruction))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String step = stepNumEditView.getText().toString();
                            String content = contentEditText.getText().toString();
                            String timer = timerEditText.getText().toString();
                            byte[] image = imageViewToByte(imageView);

                            listener.applyTexts(instructionId, step, content, timer, image);
                        }
                    });
        }

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddInstructionDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement AddInstructionDialogListener");
        }
    }

    public interface AddInstructionDialogListener {
        void applyTexts(int position, String step, String content, String timer, byte[] image);
    }

    public void sendImageBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        addInstructionPhotoButton.setVisibility(View.GONE);
    }

    public static byte[] imageViewToByte(ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;
        }
        return null;
    }
}
