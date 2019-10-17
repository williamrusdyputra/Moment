package edu.bluejack19_1.moment.adapter;

import android.content.Context;
import android.util.Log;
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
import java.util.Objects;

import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.model.Story;
import edu.bluejack19_1.moment.util.DataUtil;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private Context context;
    private ArrayList<Story> story;

    public StoryAdapter(Context context, ArrayList<Story> story) {
        this.context = context;
        this.story = story;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.add_story_item, parent, false);
            return new StoryViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.story_item, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final StoryViewHolder holder, int position) {
        Story st = story.get(position);

        userInfo(holder, st.getUserID(), position);

        if(holder.getAdapterPosition() != 0) {
            seenStory(holder, st.getUserID());
        }

        if(holder.getAdapterPosition() == 0) {
            myStory(holder.addStoryText, holder.storyPlus, false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.getAdapterPosition() == 0) {
                    myStory(holder.addStoryText, holder.storyPlus, true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return story.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

    class StoryViewHolder extends RecyclerView.ViewHolder {

        ImageView storyPhoto, storyPlus, storySeen;
        TextView storyUsername, addStoryText;

        StoryViewHolder(@NonNull View itemView) {
            super(itemView);

            storyPhoto = itemView.findViewById(R.id.story_photo);
            storyPlus = itemView.findViewById(R.id.story_add);
            storySeen = itemView.findViewById(R.id.story_photo_seen);
            storyUsername = itemView.findViewById(R.id.story_username);
            addStoryText = itemView.findViewById(R.id.add_story_text);
        }
    }

    private void userInfo(final StoryViewHolder viewHolder, final String userID, final int position) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(position != 0) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    viewHolder.storyUsername.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void myStory(final TextView textView, final ImageView imageView, final boolean click) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("story").child(DataUtil.user.userID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                long time = System.currentTimeMillis();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Story story = ds.getValue(Story.class);
                    assert story != null;
                    if(time > story.getTimeStart() && time < story.getTimeEnd()) {
                        count++;
                    }
                }

                if(!click) {
                    if(count > 0) {
                        textView.setText(context.getResources().getString(R.string.my_story));
                        imageView.setVisibility(View.GONE);
                    } else {
                        textView.setText(context.getResources().getString(R.string.add_story));
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenStory(final StoryViewHolder viewHolder, String userID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("story").child(userID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(!ds.child("views").child(DataUtil.user.userID).exists() && System.currentTimeMillis() < Objects.requireNonNull(ds.getValue(Story.class)).getTimeEnd()) {
                        count++;
                    }
                }

                if(count > 0) {
                    viewHolder.storyPhoto.setVisibility(View.VISIBLE);
                    viewHolder.storySeen.setVisibility(View.GONE);
                } else {
                    viewHolder.storyPhoto.setVisibility(View.GONE);
                    viewHolder.storySeen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
