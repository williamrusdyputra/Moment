package edu.bluejack19_1.moment.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import edu.bluejack19_1.moment.fragment.RandomPeopleFragment;
import edu.bluejack19_1.moment.fragment.RandomPictureFragment;

public class ExploreAdapter extends FragmentPagerAdapter {

    private int num;

    public ExploreAdapter(@NonNull FragmentManager fm, int behavior, int num) {
        super(fm, behavior);
        this.num = num;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0 : return new RandomPictureFragment();
            case 1 : return new RandomPeopleFragment();
        }
        return new RandomPictureFragment();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0 : return "Pictures";
            case 1 : return "People";
        }
        return "";
    }

    @Override
    public int getCount() {
        return num;
    }
}
