package edu.bluejack19_1.moment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

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
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
