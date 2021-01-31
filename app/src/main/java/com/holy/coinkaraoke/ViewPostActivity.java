package com.holy.coinkaraoke;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.holy.coinkaraoke.helpers.SQLiteHelper;
import com.holy.coinkaraoke.helpers.YoutubeUrlHelper;
import com.holy.coinkaraoke.models.Post;


public class ViewPostActivity extends AppCompatActivity implements
        YouTubePlayer.OnInitializedListener, View.OnClickListener {

    public static final String EXTRA_POST_ID = "com.holy.coinkaraoke.postId";

    private int mPostId;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        mPostId = getIntent().getIntExtra(EXTRA_POST_ID, -1);

        // Display post data to views
        displayPostData();

        // Initialize youtube view
        YouTubePlayerFragment youTubePlayerFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtube_player);

        youTubePlayerFragment.initialize("app", this);

        // Set view listeners
        TextView likeText = findViewById(R.id.txt_i_like_it);
        TextView returnText = findViewById(R.id.txt_return);
        likeText.setOnClickListener(this);
        returnText.setOnClickListener(this);

        // Get user id
        mUserId = ((App)getApplication()).getCurrentId();
    }

    // Display post data to views

    private void displayPostData() {

        Post post = SQLiteHelper.getInstance(this).getPost(mPostId);

        EditText titleEdit = findViewById(R.id.edit_post_title);
        EditText userIdEdit = findViewById(R.id.edit_post_user_id);
        TextView messageText = findViewById(R.id.txt_post_message);

        titleEdit.setText(post.getTitle());
        userIdEdit.setText(post.getUserId());
        messageText.setText(post.getMessage());
    }

    // Process youtube view initialization

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

        Post post = SQLiteHelper.getInstance(this).getPost(mPostId);

        if (!b) {
            // Get video ID from url
            String videoId = YoutubeUrlHelper.getYoutubeVideoId(post.getYoutubeUrl());
            if (videoId == null) {
                Toast.makeText(this,
                        "Invalid Youtube URL", Toast.LENGTH_SHORT).show();
                return;
            }

            // Load video
            youTubePlayer.cueVideo(videoId, 1);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        Toast.makeText(this, "Failed to load video", Toast.LENGTH_SHORT).show();
    }

    // Process view click


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txt_i_like_it:
                // Like post
                if (tryLikePost()) {
                    setResult(RESULT_OK);
                }
                break;
            case R.id.txt_return:
                // Finish activity
                finish();
                break;
        }
    }

    private boolean tryLikePost() {

        Post post = SQLiteHelper.getInstance(this).getPost(mPostId);

        // Failure: User already liked this post
        if (post.getLikedUsers().contains(mUserId)) {
            Toast.makeText(this,
                    "You already liked this video.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Failure: User is the writer of this post
        if (mUserId.equals(post.getUserId())) {
            Toast.makeText(this,
                    "You can't like your own video.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Like the post
        SQLiteHelper.getInstance(this).likePost(mPostId, mUserId);

        Toast.makeText(this, "You like this video!", Toast.LENGTH_SHORT).show();

        return true;
    }

}




