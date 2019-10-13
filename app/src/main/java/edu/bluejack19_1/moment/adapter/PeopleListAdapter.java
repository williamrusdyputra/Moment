package edu.bluejack19_1.moment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;

import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.model.UserJSON;
import edu.bluejack19_1.moment.util.DataUtil;

public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.PeopleHolder> implements Filterable {

    private Context context;
    private ArrayList<UserJSON> people;
    private ArrayList<UserJSON> allPeople;
    private final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public PeopleListAdapter(Context context, ArrayList<UserJSON> people) {
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
    public void onBindViewHolder(@NonNull final PeopleHolder holder, int position) {
        final UserJSON user = people.get(position);

        holder.username.setText(user.username);

        if(DataUtil.userJSON.followingIDs.contains(user.userID)) {
            holder.followButton.setText(R.string.followed);
        }

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button) view;
                if(btn.getText().toString().equalsIgnoreCase("Followed")) {
                    int index = DataUtil.userJSON.followingIDs.indexOf(user.userID);
                    String key = DataUtil.userJSON.followingIDKeys.get(index);
                    DataUtil.userJSON.followingIDKeys.remove(key);
                    DataUtil.userJSON.followingIDs.remove(user.userID);
                    database.child("users").child(DataUtil.userJSON.userID).child("followings").child(key).removeValue();
                    database.child("users").child(DataUtil.userJSON.userID).child("following_keys").child(key).removeValue();
                    btn.setText(R.string.follow);
                } else {
                    String id = database.push().getKey();
                    DataUtil.userJSON.followingIDs.add(user.userID);
                    DataUtil.userJSON.followingIDKeys.add(id);
                    assert id != null;
                    database.child("users").child(DataUtil.userJSON.userID).child("followings").child(id).setValue(user.userID);
                    database.child("users").child(DataUtil.userJSON.userID).child("following_keys").push().setValue(id);
                    btn.setText(R.string.followed);
                }
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
            ArrayList<UserJSON> filteredPeople = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0) {
                filteredPeople.addAll(allPeople);
            } else {
                String pattern = charSequence.toString().toLowerCase().trim();

                for(UserJSON user : allPeople) {
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
            people.addAll((Collection<? extends UserJSON>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    class PeopleHolder extends RecyclerView.ViewHolder {

        Button followButton;
        TextView username;

        PeopleHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.people_name);
            followButton = itemView.findViewById(R.id.follow_button);
        }
    }
}
