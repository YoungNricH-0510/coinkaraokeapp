package com.holy.coinkaraoke;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.holy.coinkaraoke.adapters.SongAddAdapter;
import com.holy.coinkaraoke.adapters.SongPreferenceAdapter;
import com.holy.coinkaraoke.adapters.SongRemoveAdapter;
import com.holy.coinkaraoke.helpers.SQLiteHelper;
import com.holy.coinkaraoke.models.Song;

import java.util.ArrayList;
import java.util.List;


public class CategoryFragment extends Fragment implements
        TabLayout.OnTabSelectedListener,
        SongAddAdapter.OnAddToFavoriteClickListener,
        SongPreferenceAdapter.OnAddToFavoriteClickListener,
        SongRemoveAdapter.OnRemoveFromFavoriteClickListener {

    private String mUserId;

    private List<Song> mSongList;
    private SongAddAdapter mSongAddAdapter;
    private SongPreferenceAdapter mSongPrefAdapter;
    private SongRemoveAdapter mSongRemoveAdapter;
    private RecyclerView mSongRecycler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() == null) {
            return null;
        }

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_category, container, false);

        // Get current user id
        mUserId = ((App) getActivity().getApplication()).getCurrentId();

        // Initialize tab layout
        TabLayout tabLayout = root.findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(this);

        // Build song recycler view
        buildSongRecycler(root);
        loadRecyclerAllSongs();

        return root;
    }

    // Process tab selection

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                loadRecyclerAllSongs();
                break;
            case 1:
                loadRecyclerFavoriteSongs();
                break;
            case 2:
                loadRecyclerRankedSongs();
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    // Build song recycler view

    private void buildSongRecycler(View root) {

        // Initialize recycler view
        mSongRecycler = root.findViewById(R.id.recycler_song);
        mSongRecycler.setHasFixedSize(true);
        mSongRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapters
        mSongList = new ArrayList<>();
        mSongAddAdapter = new SongAddAdapter(mSongList);
        mSongPrefAdapter = new SongPreferenceAdapter(mSongList);
        mSongRemoveAdapter = new SongRemoveAdapter(mSongList);

        // Set add-to-favorite button click listener
        mSongAddAdapter.setOnAddToFavoriteClickListener(this);
        mSongPrefAdapter.setOnAddToFavoriteClickListener(this);
        mSongRemoveAdapter.setOnRemoveFromFavoriteClickListener(this);
    }

    // Process add-to-favorite button click

    @Override
    public void onAddToFavoriteClick(int position) {

        // Get id of clicked song
        int songId = mSongList.get(position).getId();

        // Insert the song into favorite list
        boolean result = SQLiteHelper
                .getInstance(getContext())
                .insertUserFavoriteSong(mUserId, songId);

        if (result) {
            Toast.makeText(getContext(),
                    "Added to favorite songs", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(),
                    "The song is already in the list", Toast.LENGTH_SHORT).show();
        }
    }

    // Process remove-from-favorite button click

    @Override
    public void onRemoveFromFavoriteClick(int position) {

        // Get id of clicked song
        int songId = mSongList.get(position).getId();

        // Remove the song from favorite list
        boolean result = SQLiteHelper
                .getInstance(getContext())
                .removeUserFavoriteSong(mUserId, songId);

        if (result) {
            mSongList.remove(position);
            mSongRemoveAdapter.notifyItemRemoved(position);

            Toast.makeText(getContext(),
                    "Removed from favorite songs", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(),
                    "The song is not in the list", Toast.LENGTH_SHORT).show();
        }
    }

    // Load song data into recycler view

    private void loadRecyclerAllSongs() {

        mSongList.clear();
        mSongList.addAll(SQLiteHelper.getInstance(getContext()).getAllSongs());
        mSongRecycler.setAdapter(mSongAddAdapter);
    }

    private void loadRecyclerRankedSongs() {

        mSongList.clear();
        mSongList.addAll(SQLiteHelper.getInstance(getContext()).getRankedSongs());
        mSongRecycler.setAdapter(mSongPrefAdapter);
    }

    private void loadRecyclerFavoriteSongs() {

        mSongList.clear();
        mSongList.addAll(SQLiteHelper.getInstance(getContext()).getFavoriteSongs(mUserId));
        mSongRecycler.setAdapter(mSongRemoveAdapter);
    }

}