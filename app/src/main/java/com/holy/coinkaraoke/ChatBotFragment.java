package com.holy.coinkaraoke;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.holy.coinkaraoke.adapters.MentionAdapter;
import com.holy.coinkaraoke.models.Mention;

import java.util.ArrayList;
import java.util.List;

public class ChatBotFragment extends Fragment implements
        MentionAdapter.OnQuestionClickListener, AdapterView.OnItemClickListener {

    private List<Mention> mMentionList;
    private MentionAdapter mMentionAdapter;
    private RecyclerView mMentionRecycler;

    private String[] mAnswerArray;
    private String[] mQuestionArray;
    private ListView mQuestionListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_chat_bot, container, false);

        // Build mention recycler view
        buildMentionRecycler(root);

        // Insert greeting mentions
        insertGreetingMentions();

        // Load questions and answers from resources
        mQuestionArray = getResources().getStringArray(R.array.question_array);
        mAnswerArray = getResources().getStringArray(R.array.answer_array);

        // Build question list view
        buildQuestionListView(root);

        return root;
    }

    // Build mention recycler view

    private void buildMentionRecycler(View root) {

        mMentionRecycler = root.findViewById(R.id.recycler_mention);
        mMentionRecycler.setHasFixedSize(true);
        mMentionRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mMentionList = new ArrayList<>();
        mMentionAdapter = new MentionAdapter(mMentionList);
        mMentionRecycler.setAdapter(mMentionAdapter);

        // Set question button click listener
        mMentionAdapter.setOnQuestionClickListener(this);
    }

    // Insert greeting mentions

    private void insertGreetingMentions() {

        mMentionList.add(new Mention("Me", "Hello?"));
        mMentionAdapter.notifyItemInserted(0);

        mMentionRecycler.postDelayed(() -> {
            mMentionList.add(new Mention("Chat Bot", "Hello! Have any questions?"));
            mMentionAdapter.notifyItemInserted(1);
        }, 1000);
    }

    // Process question button click

    @Override
    public void onQuestionClick() {

        mMentionRecycler.setVisibility(View.INVISIBLE);
        mQuestionListView.setVisibility(View.VISIBLE);
    }

    // Build question list view

    private void buildQuestionListView(View root) {

        mQuestionListView = root.findViewById(R.id.list_question);
        ArrayAdapter<String> questionAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_list_item_1, mQuestionArray);
        mQuestionListView.setAdapter(questionAdapter);
        mQuestionListView.setOnItemClickListener(this);
    }

    // Process question item click

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mQuestionListView.setVisibility(View.INVISIBLE);
        mMentionRecycler.setVisibility(View.VISIBLE);

        Mention questionMention = new Mention("Me", mQuestionArray[position]);
        mMentionList.add(questionMention);
        mMentionAdapter.notifyItemInserted(mMentionList.size() - 1);
        mMentionRecycler.scrollToPosition(mMentionList.size());

        mMentionRecycler.postDelayed(() -> {
            Mention answerMention = new Mention("Chat Bot", mAnswerArray[position]);
            mMentionList.add(answerMention);
            mMentionAdapter.notifyItemInserted(mMentionList.size() - 1);
            mMentionRecycler.scrollToPosition(mMentionList.size());
        }, 1500);
    }
}