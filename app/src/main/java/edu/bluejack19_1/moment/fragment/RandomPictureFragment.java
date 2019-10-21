package edu.bluejack19_1.moment.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import java.util.ArrayList;

import edu.bluejack19_1.moment.R;
import edu.bluejack19_1.moment.adapter.ExplorePictureAdapter;

public class RandomPictureFragment extends Fragment {

    private boolean isLoading = false;
    private int[] lastPositions;
    private ArrayList<String> urls = new ArrayList<>();
    private ExplorePictureAdapter adapter;
    private int pastVisibleItems;
    private int visibleItemCount;
    private int totalItemCount;
    private ProgressBar progressBar;

    public RandomPictureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_random_picture, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        Sprite sprite = new ThreeBounce();
        sprite.setColor(Color.BLACK);
        progressBar.setIndeterminateDrawable(sprite);
        progressBar.setVisibility(View.VISIBLE);

        final RecyclerView rv = view.findViewById(R.id.staggered_rv);
        rv.setHasFixedSize(true);
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(layoutManager);

        rv.setItemAnimator(null);

        rv.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);

        rv.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        rv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        rv.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });

        for(int i = 0; i < 10; i++) {
            int width = 400;
            int height = (int) (Math.random() * 150) + 650 + (int) (Math.random() * 50);
            String url = "https://picsum.photos/" + width + "/" + height;
            urls.add(url);
        }

        adapter = new ExplorePictureAdapter(getContext(), urls);
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();

                lastPositions = layoutManager.findFirstVisibleItemPositions(lastPositions);

                assert lastPositions != null;
                if(lastPositions.length > 0)
                    pastVisibleItems = lastPositions[0];

                if(!isLoading) {
                    if((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loadPictures();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadPictures() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int size = urls.size()-1;
                int nextLimit = size + 10;

                while(size < nextLimit) {
                    int width = 200;
                    int height = (int) (Math.random() * 150) + 650 + (int) (Math.random() * 50);
                    String url = "https://picsum.photos/" +  width + "/" + height;

                    urls.add(url);
                    size++;
                }

                adapter.setData(urls);
                adapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 1000);
    }

    @Override
    public void onStart() {
        super.onStart();
        setRetainInstance(true);
    }
}
