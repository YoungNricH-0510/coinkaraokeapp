package com.holy.coinkaraoke;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ChatBotFragment mChatBotFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Show cover and fade out.
        if (savedInstanceState == null) {
            fadeOutCover();
        }

        // Show Intro Fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frag_container, new IntroFragment())
                .commit();

        // Set view click listener
        ImageButton chatBotButton = findViewById(R.id.ibtn_chat_bot);
        chatBotButton.setOnClickListener(this);

        mChatBotFragment = new ChatBotFragment();
    }

    // Show cover and fade out.

    private void fadeOutCover() {

        View cover = findViewById(R.id.cover);
        cover.setVisibility(View.VISIBLE);

        // fade out animation : use custom fade-out animation resource
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                cover.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        // apply it 3 seconds later
        cover.postDelayed(() -> cover.startAnimation(fadeOut), 3000);
    }

    // Process view click

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.ibtn_chat_bot) {

            // Show or hide chat bot
            if (!isChatBotFragmentShown()) {
                showChatBotFragment();
            } else {
                hideChatBotFragment();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (isChatBotFragmentShown()) {
                hideChatBotFragment();
                return true;
            }
        }

        return super.onTouchEvent(event);
    }

    private boolean isChatBotFragmentShown() {

        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.chat_bot_container);

        return (fragment != null);
    }

    private void showChatBotFragment() {

        if (!isChatBotFragmentShown()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.chat_bot_container, mChatBotFragment)
                    .commit();
        }
    }

    private void hideChatBotFragment() {

        if (isChatBotFragmentShown()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .remove(mChatBotFragment)
                    .commit();
        }
    }

}