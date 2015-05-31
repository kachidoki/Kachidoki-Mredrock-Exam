package com.mayiwei.me.mredrock_exam.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mayiwei.me.mredrock_exam.R;
import com.mayiwei.me.mredrock_exam.config.API;
import com.mayiwei.me.mredrock_exam.model.bean.JokeComments;
import com.mayiwei.me.mredrock_exam.model.bean.Result;
import com.mayiwei.me.net.Net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Frank on 15/5/18.
 */
public class View2_Fragment extends Fragment {
    private SuperRecyclerView recyclerView;
    final JokeAdapter mAdapter = new JokeAdapter();
    private int page = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view2, container, false);
        recyclerView = (SuperRecyclerView) view.findViewById(R.id.myrecyclerview);
        InitRecyclerView();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void InitRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        final Map<String,String> params = new HashMap<>();
        params.put("page",page+"");
        params.put("oxwlxojflwblxbsapi","jandan.get_duan_comments");
        Net.getInstance().netGet(API.Mredrock, params, new Net.StrCallback() {
            @Override
            public void response(String result) {
                final JokeResult Jresult = new Gson().fromJson(result, JokeResult.class);
                mAdapter.add(Jresult.getComments());
            }
        });

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Net.getInstance().netGet(API.Mredrock, params, new Net.StrCallback() {
                    @Override
                    public void response(String result) {
                        final JokeResult Jresult = new Gson().fromJson(result, JokeResult.class);

                        page = 0;
                        recyclerView.showRecycler();
                        mAdapter.jokes.clear();
                        mAdapter.add(Jresult.getComments());
                    }
                });
            }
        });

        recyclerView.setOnMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int i, int i1, int i2) {
                page++;
                params.put("page", page + "");
                Net.getInstance().netGet(API.Mredrock, params, new Net.StrCallback() {
                    @Override
                    public void response(String result) {
                        final JokeResult Jresult = new Gson().fromJson(result, JokeResult.class);

                        recyclerView.showRecycler();
                        recyclerView.hideMoreProgress();
                        if(Jresult.getComments()!=null){
                            mAdapter.add(Jresult.getComments());
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class JokeAdapter extends RecyclerView.Adapter<JokeVH>{
        private ArrayList<JokeComments> jokes = new ArrayList<>();
        public void add(JokeComments[] comments){
            jokes.addAll(Arrays.asList(comments));
            notifyDataSetChanged();
        }
        @Override
        public JokeVH onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_joke,viewGroup,false);
            return new JokeVH(view);
        }

        @Override
        public void onBindViewHolder(JokeVH jokeVH, int i) {
            jokeVH.setData(jokes.get(i));
        }

        @Override
        public int getItemCount() {
            return jokes.size();
        }
    }

    class JokeVH extends RecyclerView.ViewHolder{
        TextView tv_author;
        TextView tv_date;
        TextView tv_text_content;
        TextView tv_vote_positive;
        TextView tv_vote_negative;
        TextView tv_comment_size;
        public JokeVH(View itemView) {
            super(itemView);
            tv_author = (TextView) itemView.findViewById(R.id.author);
            tv_date = (TextView) itemView.findViewById(R.id.date);
            tv_text_content = (TextView) itemView.findViewById(R.id.text_content);
            tv_vote_positive = (TextView) itemView.findViewById(R.id.vote_positive);
            tv_vote_negative = (TextView) itemView.findViewById(R.id.vote_negative);
            tv_comment_size = (TextView) itemView.findViewById(R.id.comment_size);
        }
        public void setData(final JokeComments jokeComments){
            tv_author.setText(jokeComments.getComment_author());
            tv_date.setText(jokeComments.getComment_date());
            tv_text_content.setText(jokeComments.getText_content());
            tv_vote_positive.setText(jokeComments.getVote_positive());
            tv_vote_negative.setText(jokeComments.getVote_negative());
            tv_comment_size.setText(jokeComments.getComment_approved());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(),JokeItem_Activity.class);
                    intent.putExtra("title",jokeComments.getComment_author());
                    intent.putExtra("date",jokeComments.getComment_date());
                    intent.putExtra("content",jokeComments.getComment_content());
                    intent.putExtra("positive",jokeComments.getVote_positive());
                    intent.putExtra("negative",jokeComments.getVote_negative());
                    intent.putExtra("size",jokeComments.getComment_approved());
                    startActivity(intent);
                }
            });
        }
    }

    class JokeResult extends Result{
        private JokeComments[] comments;

        public JokeComments[] getComments() {
            return comments;
        }

        public void setComments(JokeComments[] comments) {
            this.comments = comments;
        }
    }
}
