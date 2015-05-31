package com.mayiwei.me.mredrock_exam.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mayiwei.me.mredrock_exam.R;
import com.mayiwei.me.mredrock_exam.config.API;
import com.mayiwei.me.mredrock_exam.model.bean.GirlComments;
import com.mayiwei.me.mredrock_exam.model.bean.Result;
import com.mayiwei.me.net.Net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Frank on 15/5/18.
 */
public class View1_Fragment extends Fragment {
    private SuperRecyclerView recyclerView;
    private GirlAdapter mAdapter = new GirlAdapter();
    private int page = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view1, container, false);
        recyclerView = (SuperRecyclerView) view.findViewById(R.id.myrecyclerview_girl);
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
        params.put("oxwlxojflwblxbsapi","jandan.get_ooxx_comments");
        params.put("page",page+"");
        Net.getInstance().netGet(API.Mredrock, params, new Net.StrCallback() {
            @Override
            public void response(String result) {
                final GirlResult Gresult = new Gson().fromJson(result, GirlResult.class);
                mAdapter.add(Gresult.getComments());
            }
        });

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Net.getInstance().netGet(API.Mredrock, params, new Net.StrCallback() {
                    @Override
                    public void response(String result) {
                        final GirlResult Gresult = new Gson().fromJson(result, GirlResult.class);

                        page = 0;
                        recyclerView.showRecycler();
                        mAdapter.girls.clear();
                        mAdapter.add(Gresult.getComments());

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
                        final GirlResult Gresult = new Gson().fromJson(result, GirlResult.class);

                        recyclerView.showRecycler();
                        recyclerView.hideMoreProgress();
                        if(Gresult.getComments()!=null){
                            mAdapter.add(Gresult.getComments());
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

    class GirlAdapter extends RecyclerView.Adapter<GirlVH>{
        private ArrayList<GirlComments> girls = new ArrayList<>();
        public void add(GirlComments[] comments){
            girls.addAll(Arrays.asList(comments));
            notifyDataSetChanged();
        }
        @Override
        public GirlVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_girl,parent,false);
            return new GirlVH(view);
        }

        @Override
        public void onBindViewHolder(GirlVH holder, int position) {
            holder.setData(girls.get(position));
        }

        @Override
        public int getItemCount() {
            return girls.size();
        }
    }

    class GirlVH extends RecyclerView.ViewHolder{
        TextView tv_author;
        TextView tv_date;
        ImageView img_girl;
        TextView tv_vote_positive;
        TextView tv_vote_negative;
        TextView tv_comment_size;
        public GirlVH(View itemView) {
            super(itemView);
            tv_author = (TextView) itemView.findViewById(R.id.girl_author);
            tv_date = (TextView) itemView.findViewById(R.id.girl_date);
            img_girl = (ImageView) itemView.findViewById(R.id.girl_img);
            tv_vote_positive = (TextView) itemView.findViewById(R.id.girl_positive);
            tv_vote_negative = (TextView) itemView.findViewById(R.id.girl_negative);
            tv_comment_size = (TextView) itemView.findViewById(R.id.girl_comment_size);
        }
        public void setData(final GirlComments girlComments){
            tv_author.setText(girlComments.getComment_author());
            tv_date.setText(girlComments.getComment_date());
            tv_vote_positive.setText(girlComments.getVote_positive());
            tv_vote_negative.setText(girlComments.getVote_negative());
            tv_comment_size.setText(girlComments.getComment_approved());
            Net.getInstance().netImage(girlComments.getPics()[0], new Net.ImageCallback() {
                @Override
                public void response(Bitmap result) {
                    img_girl.setImageBitmap(result);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(),GirlItem_Activity.class);
                    intent.putExtra("title",girlComments.getComment_author());
                    intent.putExtra("date",girlComments.getComment_date());

                    intent.putExtra("pic",girlComments.getPics()[0]);
                    startActivity(intent);
                }
            });
        }
    }
    class GirlResult extends Result{
        private GirlComments[] comments;

        public GirlComments[] getComments() {
            return comments;
        }

        public void setComments(GirlComments[] comments) {
            this.comments = comments;
        }
    }

}
