package com.holy.coinkaraoke;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.holy.coinkaraoke.adapters.PostAdapter;
import com.holy.coinkaraoke.helpers.SQLiteHelper;
import com.holy.coinkaraoke.helpers.YoutubeUrlHelper;
import com.holy.coinkaraoke.models.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PollFragment extends Fragment implements
        TabLayout.OnTabSelectedListener, View.OnClickListener, PostAdapter.OnItemClickListener {

    public static final int REQUEST_VIEW_POST = 100;

    private String mUserId;

    private TabLayout mTabLayout;

    private List<Post> mPostList;
    private PostAdapter mPostAdapter;

    private View mBestPostView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() == null) {
            return null;
        }

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_poll, container, false);

        // Get current user id
        mUserId = ((App) getActivity().getApplication()).getCurrentId();

        // Initialize tab layout
        mTabLayout = root.findViewById(R.id.tab_layout);
        mTabLayout.addOnTabSelectedListener(this);

        // Build song recycler view
        buildPostRecycler(root);
        loadRecyclerPosts();

        // Set button listeners
        ImageButton writeButton = root.findViewById(R.id.btn_write_post);
        writeButton.setOnClickListener(this);

        mBestPostView = root.findViewById(R.id.view_best_post);
        displayBestPost();

        return root;
    }

    // Process tab selection

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        loadRecyclerPosts();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    // Build song recycler view

    private void buildPostRecycler(View root) {

        // Initialize recycler view
        RecyclerView postRecycler = root.findViewById(R.id.recycler_post);
        postRecycler.setHasFixedSize(true);
        postRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapters
        mPostList = new ArrayList<>();
        mPostAdapter = new PostAdapter(mPostList);
        postRecycler.setAdapter(mPostAdapter);

        mPostAdapter.setOnItemClickListener(this);
    }

    // Load post data into recycler view

    private void loadRecyclerPosts() {

        mPostList.clear();

        switch (mTabLayout.getSelectedTabPosition()) {
            case 0:
                // Load hot posts
                mPostList.addAll(SQLiteHelper.getInstance(getContext()).getHotPosts());
                break;
            case 1:
                // Load new posts
                mPostList.addAll(SQLiteHelper.getInstance(getContext()).getNewPosts());
                break;
        }

        mPostAdapter.notifyDataSetChanged();
    }

    // Process post click

    @Override
    public void onItemClick(int position) {

        // Start view post activity
        int postId = mPostList.get(position).getId();
        startViewPostActivity(postId);
    }

    // Start view post activity

    private void startViewPostActivity(int postId) {

        Intent intent = new Intent(getContext(), ViewPostActivity.class);
        intent.putExtra(ViewPostActivity.EXTRA_POST_ID, postId);
        startActivityForResult(intent, REQUEST_VIEW_POST);
    }

    // Process button click

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_write_post) {

            // Show dialog for writing post
            showWritePostDialog();
        }
    }

    // Show dialog for writing post

    private void showWritePostDialog() {

        // Inflate view that prompts information of post
        View writePostView = View.inflate(getContext(), R.layout.write_post_view, null);
        EditText titleEdit = writePostView.findViewById(R.id.edit_post_title);
        EditText youtubeUrlEdit = writePostView.findViewById(R.id.edit_post_youtube_url);
        EditText messageEdit = writePostView.findViewById(R.id.edit_post_message);

        // Build and show dialog
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.write_new_post)
                .setView(writePostView)
                .setPositiveButton(R.string.write_new_post, (dialog, which) -> {

                    // Get user input
                    String title = titleEdit.getText().toString().trim();
                    String youtubeUrl = youtubeUrlEdit.getText().toString().trim();
                    String message = messageEdit.getText().toString().trim();

                    // Failure: Empty fields
                    if (title.isEmpty() || youtubeUrl.isEmpty() || message.isEmpty()) {
                        Toast.makeText(getContext(),
                                "Please enter all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Check youtube url
                    String videoId = YoutubeUrlHelper.getYoutubeVideoId(youtubeUrl);
                    if (videoId == null) {
                        Toast.makeText(getContext(),
                                "Please enter valid Youtube URL", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Make post object with the input
                    Post post = new Post(
                            mUserId, title, message, youtubeUrl, LocalDateTime.now(), 0, null);

                    // Add the post into DB
                    SQLiteHelper.getInstance(getContext()).addPost(post);

                    // Update recycler view
                    loadRecyclerPosts();

                    // Show Toast
                    Toast.makeText(getContext(),
                            "Your post has been uploaded", Toast.LENGTH_SHORT).show();

                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // View post activity returned
        if (requestCode == REQUEST_VIEW_POST && resultCode == Activity.RESULT_OK) {

            // Update recycler view and best post (number of likes might have been changed)
            loadRecyclerPosts();
            displayBestPost();
        }
    }

    // Display weekly best post

    private void displayBestPost() {

        List<Post> hotPosts = SQLiteHelper.getInstance(getContext()).getHotPosts();
        if (hotPosts.isEmpty()) {
            mBestPostView.setVisibility(View.INVISIBLE);
            return;
        }

        Post bestPost = hotPosts.get(0);
        if (bestPost.getLikes() == 0) {
            mBestPostView.setVisibility(View.INVISIBLE);
            return;
        }


        PostAdapter.ViewHolder viewHolder = new PostAdapter.ViewHolder(mBestPostView);
        viewHolder.bind(bestPost, null);
        mBestPostView.setVisibility(View.VISIBLE);

        mBestPostView.setOnClickListener(v -> startViewPostActivity(bestPost.getId()));
    }

}






