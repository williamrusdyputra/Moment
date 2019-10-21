package edu.bluejack19_1.moment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import edu.bluejack19_1.moment.adapter.ChatAdapter;
import edu.bluejack19_1.moment.model.Chat;
import edu.bluejack19_1.moment.model.User;
import edu.bluejack19_1.moment.notification.Client;
import edu.bluejack19_1.moment.notification.Data;
import edu.bluejack19_1.moment.notification.MyResponse;
import edu.bluejack19_1.moment.notification.Sender;
import edu.bluejack19_1.moment.notification.Token;
import edu.bluejack19_1.moment.util.APIService;
import edu.bluejack19_1.moment.util.DataUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private EditText textSend;
    private ChatAdapter adapter;
    private ArrayList<Chat> mChat;
    private RecyclerView recyclerView;
    private User user;
    private APIService apiService;

    private boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        Bundle data = getIntent().getBundleExtra("bundle");
        assert data != null;
        user = data.getParcelable("user");

        assert user != null;
        Objects.requireNonNull(getSupportActionBar()).setTitle(user.username);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        ImageButton btnSend = findViewById(R.id.btn_send);
        textSend = findViewById(R.id.text_send);

        readMessage(DataUtil.user.userID, user.userID);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String message = textSend.getText().toString();
                if(!message.equals("")) {
                    sendMessage(DataUtil.user.userID, user.userID, message);
                } else {
                    Toast.makeText(ChatActivity.this, getResources().getString(R.string.empty_message), Toast.LENGTH_SHORT).show();
                }
                textSend.setText("");
            }
        });

        recyclerView = findViewById(R.id.chat_messages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        ref.child("chats").push().setValue(hashMap);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chatlist")
                .child(DataUtil.user.userID)
                .child(user.userID);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())  {
                    chatRef.child("id").setValue(user.userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(notify) {
            sendNotification(receiver, user.username, message);
        }
        notify = false;
    }

    private void sendNotification(String receiver, final String username, final String message) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = ref.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(DataUtil.user.userID, R.mipmap.ic_launcher, username + ": " + message, "New Message", user.userID);

                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                    if(response.code() == 200) {
                                        assert response.body() != null;
                                        if(response.body().success != 1) {
                                            Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.d("UNIQUE", "Message sent");
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                                    Log.d("UNIQUE", Objects.requireNonNull(t.getMessage()));
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage(final String myID, final String userID) {
        mChat = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(myID) && chat.getSender().equals(userID) || chat.getReceiver().equals(userID) && chat.getSender().equals(myID)) {
                        mChat.add(chat);
                    }

                    adapter = new ChatAdapter(ChatActivity.this, mChat, "default");
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
