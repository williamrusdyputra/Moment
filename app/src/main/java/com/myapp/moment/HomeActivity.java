package com.myapp.moment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.myapp.moment.fragment.AddFragment;
import com.myapp.moment.fragment.ExploreFragment;
import com.myapp.moment.fragment.HomeFragment;
import com.myapp.moment.fragment.LikedFragment;
import com.myapp.moment.fragment.ProfileFragment;
import com.myapp.moment.util.DataUtil;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    public static final String EXTRA_DATA = "extra_data";

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment exploreFragment = new ExploreFragment();
    private final Fragment addFragment = new AddFragment();
    private final Fragment likedFragment = new LikedFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private Fragment active = homeFragment;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);

        setupFragments();

        final BottomNavigationView navView = findViewById(R.id.nav_bar);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void setupFragments() {
        fragmentManager.beginTransaction().add(R.id.main_container, exploreFragment, "explore").hide(exploreFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, addFragment, "add").hide(addFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, likedFragment, "liked").hide(likedFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, profileFragment, "profile").hide(profileFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, homeFragment, "home").commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch(menuItem.getItemId()) {
                case R.id.action_add:
                    fragmentManager.beginTransaction().hide(active).show(addFragment).commit();
                    active = addFragment;
                    return true;
                case R.id.action_explore:
                    fragmentManager.beginTransaction().hide(active).show(exploreFragment).commit();
                    active = exploreFragment;
                    return true;
                case R.id.action_liked:
                    fragmentManager.beginTransaction().hide(active).show(likedFragment).commit();
                    active = likedFragment;
                    return true;
                case R.id.action_profile:
                    Objects.requireNonNull(getSupportActionBar()).setTitle(DataUtil.userJSON.username);
                    ProfileFragment pf = (ProfileFragment) getSupportFragmentManager().getFragments().get(3);
                    pf.setup(DataUtil.userJSON.description, DataUtil.userJSON.postCount, DataUtil.userJSON.followingCount,
                            DataUtil.userJSON.followerCount, DataUtil.userJSON.username);
                    fragmentManager.beginTransaction().hide(active).show(profileFragment).commit();
                    active = profileFragment;
                    return true;
                case R.id.action_home:
                    fragmentManager.beginTransaction().hide(active).show(homeFragment).commit();
                    active = homeFragment;
                     return true;
            }
            return false;
        }
    };

}
