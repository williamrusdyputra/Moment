package edu.bluejack19_1.moment.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.bluejack19_1.moment.ChatActivity;
import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.model.Chat;
import edu.bluejack19_1.moment.model.User;
import edu.bluejack19_1.moment.util.DataUtil;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> people;
    private String theLastMessage;

    public ChatListAdapter(Context context, ArrayList<User> people) {
        this.context = context;
        this.people = people;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.username.setText(people.get(position).username);
        lastMessage(people.get(position).userID, holder.lastMessage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                Bundle data = new Bundle();
                data.putParcelable("user", people.get(position));
                intent.putExtra("bundle", data);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView username, lastMessage;
        ImageView profileImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            lastMessage = itemView.findViewById(R.id.last_message);
            profileImage = itemView.findViewById(R.id.profile_image);
        }

    }

    private void lastMessage(final String userID, final TextView lastMessage) {
        theLastMessage = "default";
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("chats");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(DataUtil.user.userID) && chat.getSender().equals(userID) ||
                            chat.getReceiver().equals(userID) && chat.getSender().equals(DataUtil.user.userID)) {
                        theLastMessage = chat.getMessage();
                    }
                }

                if ("default".equals(theLastMessage)) {
                    lastMessage.setText(context.getResources().getString(R.string.no_message));
                } else {
                    lastMessage.setText(theLastMessage);
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
