package com.example.recipepal.helpers;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.recipepal.fragments.InterfaceFragment;

import java.util.List;

public class InteractivePagerAdapter extends FragmentPagerAdapter {

    private int count;
    private List<String> contentList;
    private List<Integer> timeList;
    private List<byte[]> imageList;

    public InteractivePagerAdapter(@NonNull FragmentManager fm, int behavior, int count, List<String> contentList, List<Integer> timeList, List<byte[]> imageList) {
        super(fm, behavior);
        this.count = count;
        this.contentList = contentList;
        this.timeList = timeList;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public InterfaceFragment getItem(int position) {
        return InterfaceFragment.newInstance(position, this.contentList.get(position), this.timeList.get(position), this.imageList.get(position));
    }

    @Override
    public int getCount() {
        return count;
    }

}
