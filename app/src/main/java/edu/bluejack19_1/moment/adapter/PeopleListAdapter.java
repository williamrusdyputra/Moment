package edu.bluejack19_1.moment.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.Collection;

import edu.bluejack19_1.moment.ChatActivity;
import edu.bluejack19_1.moment.HomeActivity;
import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.fragment.HomeFragment;
import edu.bluejack19_1.moment.model.User;
import edu.bluejack19_1.moment.util.DataUtil;

public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.PeopleHolder> implements Filterable {

    private Context context;
    private ArrayList<User> people;
    private ArrayList<User> allPeople;
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public PeopleListAdapter(Context context, ArrayList<User> people) {
        this.context = context;
        this.people = people;
        allPeople = new ArrayList<>(people);
    }

    @NonNull
    @Override
    public PeopleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.people_item, parent, false);
        return new PeopleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PeopleHolder holder, final int position) {
        final User user = people.get(position);

        holder.username.setText(user.username);

        if(user.profilePictureUrl != null && user.profilePictureUrl.equals("default")) {
            Glide.with(context)
                    .load(R.drawable.default_picture)
                    .into(holder.userImage);
        } else {
            Glide.with(context)
                    .load(user.profilePictureUrl)
                    .into(holder.userImage);
        }

        if(DataUtil.user.followingIDs.contains(user.userID)) {
            holder.followButton.setText(R.string.followed);
        }

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button) view;
                if(btn.getText().toString().equalsIgnoreCase("Followed")) {
                    int index = DataUtil.user.followingIDs.indexOf(user.userID);
                    String key = DataUtil.user.followingIDKeys.get(index);
                    DataUtil.user.followingIDKeys.remove(key);
                    DataUtil.user.followingIDs.remove(user.userID);
                    database.child("users").child(DataUtil.user.userID).child("followings").child(key).removeValue();
                    final Query ref = database.child("users").child(DataUtil.user.userID).child("following_keys").orderByValue().equalTo(key);
                    ref.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            dataSnapshot.getRef().setValue(null);
                            ref.removeEventListener(this);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    database.child("users").child(DataUtil.user.userID).child("following_count").runTransaction(new Transaction.Handler() {
                        @NonNull @SuppressWarnings("ConstantConditions")
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            int value = mutableData.getValue(Integer.class);
                            value -= 1;
                            mutableData.setValue(value);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                        }
                    });
                    database.child("users").child(user.userID).child("follower_count").runTransaction(new Transaction.Handler() {
                        @NonNull @SuppressWarnings("ConstantConditions")
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            int value = mutableData.getValue(Integer.class);
                            value -= 1;
                            mutableData.setValue(value);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                        }
                    });
                    btn.setText(R.string.follow);
                } else {
                    String id = database.push().getKey();
                    DataUtil.user.followingIDs.add(user.userID);
                    DataUtil.user.followingIDKeys.add(id);
                    assert id != null;
                    database.child("users").child(DataUtil.user.userID).child("followings").child(id).setValue(user.userID);
                    database.child("users").child(DataUtil.user.userID).child("following_keys").push().setValue(id);
                    database.child("users").child(DataUtil.user.userID).child("following_count").runTransaction(new Transaction.Handler() {
                        @NonNull @SuppressWarnings("ConstantConditions")
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            int value = mutableData.getValue(Integer.class);
                            value += 1;
                            mutableData.setValue(value);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                        }
                    });
                    database.child("users").child(user.userID).child("follower_count").runTransaction(new Transaction.Handler() {
                        @NonNull @SuppressWarnings("ConstantConditions")
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                            int value = mutableData.getValue(Integer.class);
                            value += 1;
                            mutableData.setValue(value);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                        }
                    });
                    btn.setText(R.string.followed);
                }

                HomeFragment hf = (HomeFragment) ((HomeActivity) context).getSupportFragmentManager().getFragments().get(4);
                hf.updateData();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                Bundle data = new Bundle();
                data.putParcelable("user", DataUtil.people.get(position));
                intent.putExtra("bundle", data);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    @Override
    public Filter getFilter() {
        return filteredList;
    }

    private Filter filteredList = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<User> filteredPeople = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0) {
                filteredPeople.addAll(allPeople);
            } else {
                String pattern = charSequence.toString().toLowerCase().trim();

                for(User user : allPeople) {
                    if(user.username.toLowerCase().contains(pattern)) {
                        filteredPeople.add(user);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredPeople;

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            people.clear();
            people.addAll((Collection<? extends User>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    class PeopleHolder extends RecyclerView.ViewHolder {

        Button followButton;
        TextView username;
        ImageView userImage;

        PeopleHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.people_name);
            followButton = itemView.findViewById(R.id.follow_button);
            userImage = itemView.findViewById(R.id.user_image);
        }
    }
}
