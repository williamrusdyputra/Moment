package edu.bluejack19_1.moment.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.adapter.LikedPictureAdapter;
import edu.bluejack19_1.moment.viewmodel.LikedViewModel;

public class LikedFragment extends Fragment {

    private LikedPictureAdapter adapter;
    private LikedViewModel viewModel;

    public LikedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_liked, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView likedRv = view.findViewById(R.id.liked_rv);
        likedRv.setHasFixedSize(true);
        likedRv.addItemDecoration(new DividerItemDecoration(likedRv.getContext(), DividerItemDecoration.HORIZONTAL));
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        likedRv.setLayoutManager(manager);

        adapter = new LikedPictureAdapter(getContext());
        likedRv.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(LikedViewModel.class);
        viewModel.getUrls().observe(this, observer);
    }

    private Observer<List<String>> observer = new Observer<List<String>>() {
        @Override
        public void onChanged(List<String> urls) {
            if(urls != null) {
                adapter.setData(urls);
            }
        }
    };

    public void updateData() {
        viewModel.getUrls().observe(this, observer);
    }
}
