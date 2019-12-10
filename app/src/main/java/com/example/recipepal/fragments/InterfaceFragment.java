package com.example.recipepal.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.recipepal.R;
import com.example.recipepal.helpers.DatabaseHelper;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class InterfaceFragment extends Fragment {

    TextView timer;
    ImageButton pausePlay;
    ImageButton reset;
    ImageView imageView;

    int seconds;
    boolean running;


    public InterfaceFragment() {
        // Required empty public constructor
    }

    public static InterfaceFragment newInstance(int position, String content, int time, byte[] image) {
        position++;

        InterfaceFragment interfaceFragment = new InterfaceFragment();
        Bundle args = new Bundle();
        args.putInt("POSITION", position);
        args.putString("CONTENT", content);
        args.putInt("TIME", time);
        args.putByteArray("IMAGE", image);
        interfaceFragment.setArguments(args);
        return interfaceFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.interactive_recipe_fragment, container, false);

        TextView step = view.findViewById(R.id.interactiveRecipeFragmentStep);
        String stepStr = "Step " + getArguments().getInt("POSITION") + ":";
        step.setText(stepStr);

        TextView content = view.findViewById(R.id.interactiveRecipeFragmentContent);
        String contentStr = getArguments().getString("CONTENT");
        content.setText(contentStr);
        content.setMovementMethod(new ScrollingMovementMethod());

        imageView = view.findViewById(R.id.interactiveRecipeFragmentImageView);
        byte[] image = getArguments().getByteArray("IMAGE");
        if (image != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            imageView.setImageBitmap(bitmap);
        }

        timer = view.findViewById(R.id.interactiveRecipeFragmentTimer);
        pausePlay = view.findViewById(R.id.interactiveRecipeFragmentPausePlay);
        reset = view.findViewById(R.id.interactiveRecipeFragmentReset);

        seconds = getArguments().getInt("TIME")*60;
        running = false;
        if (getArguments().getInt("TIME") > 0){
            int hours = seconds / 3600;
            int minutes = (seconds % 3600) / 60;
            int secs = seconds % 60;
            String time = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, secs);
            timer.setText(time);
            pausePlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    running = !running;
                    if (running)
                        pausePlay.setImageResource(R.drawable.ic_pause_24px);
                    else
                        pausePlay.setImageResource(R.drawable.ic_play_arrow_24px);


                }
            });

            reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    running = false;
                    seconds = getArguments().getInt("TIME")*60;
                    int hours = seconds / 3600;
                    int minutes = (seconds % 3600) / 60;
                    int secs = seconds % 60;
                    String time = String.format(Locale.getDefault(),
                            "%d:%02d:%02d", hours, minutes, secs);
                    timer.setText(time);
                    pausePlay.setImageResource(R.drawable.ic_play_arrow_24px);
                }
            });

        } else {
            timer.setVisibility(View.INVISIBLE);
            pausePlay.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.INVISIBLE);
        }
        runTimer();

        return view;
    }

    private void runTimer() {
        final Handler handler = new Handler();
        // the Runnable interface contains a single method, run()
        // post means run this code immediately
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // format the seconds in H:mm:ss format
                String time = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours, minutes, secs);

                // update the textview
                timer.setText(time);
                // update seconds if we are in a running state
                if (running) {
                    seconds--;
                }
                // postDelayed means run this code after a delay of 1000 milliseconds
                // 1 second = 1000 milliseconds
                handler.postDelayed(this, 1000);
                // this method will keep getting called while this Activity is running
            }
        });

    }

}
