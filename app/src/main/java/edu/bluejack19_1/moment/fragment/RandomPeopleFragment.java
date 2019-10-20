package edu.bluejack19_1.moment.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Objects;

import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.adapter.PeopleListAdapter;
import edu.bluejack19_1.moment.model.User;
import edu.bluejack19_1.moment.notification.Token;
import edu.bluejack19_1.moment.util.DataUtil;

public class RandomPeopleFragment extends Fragment {

    private PeopleListAdapter adapter;
    private ContentLoadingProgressBar progressBar;

    public RandomPeopleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_random_people, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<User> users = DataUtil.people;

        progressBar = view.findViewById(R.id.people_progress_bar);
        progressBar.show();

        setupRecyclerView(view, users);
        setupSearchView(view);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("UNIQUE", "getInstanceId failed", task.getException());
                        }
                        String token = Objects.requireNonNull(task.getResult()).getToken();

                        updateToken(token);
                    }
                });
    }

    private void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tokens");
        Token token1 = new Token(token);
        ref.child(DataUtil.user.userID).setValue(token1);
    }

    private void setupRecyclerView(View view, ArrayList<User> people) {
        progressBar.hide();
        RecyclerView rvList = view.findViewById(R.id.people_list);
        rvList.setHasFixedSize(true);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PeopleListAdapter(getContext(), people);
        rvList.setAdapter(adapter);
    }

    private void setupSearchView(View view) {
        SearchView searchView = view.findViewById(R.id.search_view);

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

}
