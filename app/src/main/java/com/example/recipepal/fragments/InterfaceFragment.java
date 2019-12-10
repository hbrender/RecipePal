package com.example.recipepal.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.recipepal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InterfaceFragment extends Fragment {

    public InterfaceFragment() {
        // Required empty public constructor
    }

    public static InterfaceFragment newInstance(int position, String content, int time) {
        position++;

        InterfaceFragment interfaceFragment = new InterfaceFragment();
        Bundle args = new Bundle();
        args.putInt("POSITION", position);
        args.putString("CONTENT", content);
        args.putInt("TIME", time);
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

        TextView timer = view.findViewById(R.id.interactiveRecipeFragmentTimer);
        ImageButton pausePlay = view.findViewById(R.id.interactiveRecipeFragmentPausePlay);
        ImageButton reset = view.findViewById(R.id.interactiveRecipeFragmentReset);
        String time = getArguments().getInt("TIME")+ ":00";
        if (getArguments().getInt("TIME") > 0){
            timer.setText(time);
            pausePlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                }
            });

            reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                }
            });

        } else {
            timer.setVisibility(View.INVISIBLE);
            pausePlay.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.INVISIBLE);
        }

        return view;
    }

}
