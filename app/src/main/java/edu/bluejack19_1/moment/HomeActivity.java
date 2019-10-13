package edu.bluejack19_1.moment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.bluejack19_1.moment.fragment.AddFragment;
import edu.bluejack19_1.moment.fragment.ExploreFragment;
import edu.bluejack19_1.moment.fragment.HomeFragment;
import edu.bluejack19_1.moment.fragment.LikedFragment;
import edu.bluejack19_1.moment.fragment.ProfileFragment;
import edu.bluejack19_1.moment.util.DataUtil;

import java.util.ArrayList;
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

        setupData();

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle(DataUtil.userJSON.username);

        setupFragments();

        final BottomNavigationView navView = findViewById(R.id.nav_bar);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void setupData() {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        final String username = DataUtil.userJSON.username;

        database.child("users").addValueEventListener(new ValueEventListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.child("username").getValue(String.class);
                    String description = ds.child("description").getValue(String.class);
                    String url = ds.child("profile_picture_url").getValue(String.class);
                    int postCount = ds.child("post_count").getValue(Integer.class);
                    int followerCount = ds.child("follower_count").getValue(Integer.class);
                    int followingCount = ds.child("following_count").getValue(Integer.class);
                    String userID = ds.child("user_id").getValue(String.class);

                    DataSnapshot posts = ds.child("picture_urls");
                    Iterable<DataSnapshot> items = posts.getChildren();
                    ArrayList<String> postUrls = new ArrayList<>();
                    for(DataSnapshot item : items) {
                        postUrls.add(item.getValue(String.class));
                    }

                    DataSnapshot followings = ds.child("followings");
                    items = followings.getChildren();
                    ArrayList<String> followingIDs = new ArrayList<>();
                    for(DataSnapshot item : items) {
                        followingIDs.add(item.getValue(String.class));
                    }

                    DataSnapshot followers = ds.child("followers");
                    items = followers.getChildren();
                    ArrayList<String> followerIDs = new ArrayList<>();
                    for(DataSnapshot item : items) {
                        followerIDs.add(item.getValue(String.class));
                    }

                    DataSnapshot followerKeys = ds.child("follower_keys");
                    items = followerKeys.getChildren();
                    ArrayList<String> followerIDKeys = new ArrayList<>();
                    for(DataSnapshot item : items) {
                        followerIDKeys.add(item.getValue(String.class));
                    }

                    DataSnapshot followingKeys = ds.child("following_keys");
                    items = followingKeys.getChildren();
                    ArrayList<String> followingIDKeys = new ArrayList<>();
                    for(DataSnapshot item : items) {
                        followingIDKeys.add(item.getValue(String.class));
                    }

                    DataUtil.userJSON.userID = userID;
                    DataUtil.userJSON.username = name;
                    DataUtil.userJSON.postCount = postCount;
                    DataUtil.userJSON.followerCount = followerCount;
                    DataUtil.userJSON.followingCount = followingCount;
                    DataUtil.userJSON.profilePictureUrl = url;
                    DataUtil.userJSON.description = description;
                    DataUtil.userJSON.pictureUrls = postUrls;
                    DataUtil.userJSON.followerIDs = followerIDs;
                    DataUtil.userJSON.followingIDs = followingIDs;
                    DataUtil.userJSON.followerIDKeys = followerIDKeys;
                    DataUtil.userJSON.followingIDKeys = followingIDKeys;

                    if(username.equals(name)) {
                        database.removeEventListener(this);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("UNIQUE", "onCancelled", databaseError.toException());
            }
        });
    }

    private void setupFragments() {
        fragmentManager.beginTransaction().add(R.id.include, exploreFragment, "explore").hide(exploreFragment).commit();
        fragmentManager.beginTransaction().add(R.id.include, addFragment, "add").hide(addFragment).commit();
        fragmentManager.beginTransaction().add(R.id.include, likedFragment, "liked").hide(likedFragment).commit();
        fragmentManager.beginTransaction().add(R.id.include, profileFragment, "profile").hide(profileFragment).commit();
        fragmentManager.beginTransaction().add(R.id.include, homeFragment, "home").commit();
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
                    ProfileFragment pf = (ProfileFragment) getSupportFragmentManager().getFragments().get(3);
                    pf.setup(DataUtil.userJSON.description, DataUtil.userJSON.postCount, DataUtil.userJSON.followingCount,
                            DataUtil.userJSON.followerCount);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.action_logout) {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.user_pref_key), MODE_PRIVATE);
            sharedPref.edit().clear().apply();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
