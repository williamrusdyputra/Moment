package edu.bluejack19_1.moment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import edu.bluejack19_1.moment.adapter.ChatListAdapter;
import edu.bluejack19_1.moment.model.ChatList;
import edu.bluejack19_1.moment.model.User;
import edu.bluejack19_1.moment.util.DataUtil;

public class ChatListActivity extends AppCompatActivity {

    private ArrayList<ChatList> userList;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        progressBar = findViewById(R.id.progress_bar);
        Sprite sprite = new ThreeBounce();
        sprite.setColor(Color.BLACK);
        progressBar.setIndeterminateDrawable(sprite);
        progressBar.setVisibility(View.VISIBLE);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        userList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chatlist").child(DataUtil.user.userID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    ChatList chatList = ds.getValue(ChatList.class);
                    userList.add(chatList);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void chatList() {
        ArrayList<User> mUsers = new ArrayList<>();
        for(User user : DataUtil.people) {
            for(ChatList chatList : userList) {
                if(user.userID.equals(chatList.getId())) {
                    mUsers.add(user);
                }
            }
        }
        ChatListAdapter chatListAdapter = new ChatListAdapter(this, mUsers);
        recyclerView.setAdapter(chatListAdapter);

        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
