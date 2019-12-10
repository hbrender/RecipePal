package com.example.recipepal.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.recipepal.R;
import com.example.recipepal.helpers.DatabaseHelper;
import com.example.recipepal.helpers.InteractivePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class InteractiveRecipeActivity extends AppCompatActivity {

    InteractivePagerAdapter pagerAdapter;
    int recipeId;
    DatabaseHelper databaseHelper;
    Cursor cursor;
    List<String> contentList;
    List<Integer> timeList;
    List<Integer> imageList;
    int size;
    ViewPager viewPager;
    ImageButton exit;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interactive_recipe_activity);

        Intent intent = getIntent();
        recipeId = intent.getIntExtra("ID", 0);

        databaseHelper = new DatabaseHelper(this);
        cursor = databaseHelper.getAllRecipeInstructionsByIdCursor(recipeId);
        timeList = new ArrayList<>();
        contentList = new ArrayList<>();
        imageList = new ArrayList<>();

        while (cursor.moveToNext()) {
            contentList.add(cursor.getString(3));
            timeList.add(cursor.getInt(4));
            imageList.add(cursor.getInt(5));
            size++;
        }

        viewPager = findViewById(R.id.interactiveRecipeViewPager);
        pagerAdapter = new InteractivePagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, size, contentList, timeList, imageList);
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                seekBar.setProgress(position);
                // DOESNT WORK CONCURRENTLY WITH THE SEEKBAR PROGRESS CHANGE LISTENER.
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        exit = findViewById(R.id.exitImageButton);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InteractiveRecipeActivity.this.finish();
            }
        });

        seekBar = findViewById(R.id.interactiveRecipeSeekBar);

        seekBar.setMax(size-1);
        seekBar.setProgress(0);

    }
}
