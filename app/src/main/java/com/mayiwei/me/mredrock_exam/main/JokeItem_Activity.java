package com.mayiwei.me.mredrock_exam.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.mayiwei.me.mredrock_exam.R;
import com.mayiwei.me.mredrock_exam.app.BaseActivity;

/**
 * Created by Frank on 15/5/30.
 */
public class JokeItem_Activity extends BaseActivity {
    TextView tv_title;
    TextView tv_date;
    TextView tv_content;
    TextView tv_vote_positive;
    TextView tv_vote_negative;
    TextView tv_comment_size;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);
        setToolbar(true);

        tv_title = $(R.id.joke_author);
        tv_date = $(R.id.joke_date);
        tv_content = $(R.id.joke_text_content);
        tv_vote_positive = $(R.id.joke_vote_positive);
        tv_vote_negative = $(R.id.joke_vote_negative);
        tv_comment_size = $(R.id.joke_comment_size);

        Intent intent = this.getIntent();
        tv_title.setText(intent.getStringExtra("title"));
        tv_date.setText(intent.getStringExtra("date"));
        tv_content.setText(intent.getStringExtra("content"));
        tv_vote_positive.setText(intent.getStringExtra("positive"));
        tv_vote_negative.setText(intent.getStringExtra("negative"));
        tv_comment_size.setText(intent.getStringExtra("size"));


    }
}
