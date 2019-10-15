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

import java.util.Map;

import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.adapter.HomePictureAdapter;
import edu.bluejack19_1.moment.viewmodel.HomePictureViewModel;

public class HomeFragment extends Fragment {

    private HomePictureAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.home_pictures);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new HomePictureAdapter(getContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        HomePictureViewModel viewModel = ViewModelProviders.of(this).get(HomePictureViewModel.class);
        viewModel.getUrls().observe(this, observer);
    }

    private Observer<Map<String, String>> observer = new Observer<Map<String, String>>() {
        @Override
        public void onChanged(Map<String, String> urls) {
            if(urls != null) {
                adapter.setData(urls);
            }
        }
    };


}
