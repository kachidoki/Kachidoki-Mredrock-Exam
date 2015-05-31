package com.mayiwei.me.mredrock_exam.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mayiwei.me.mredrock_exam.R;
import com.mayiwei.me.mredrock_exam.config.API;
import com.mayiwei.me.mredrock_exam.model.bean.QuestionResult;
import com.mayiwei.me.mredrock_exam.util.PagerView.JPagerView;
import com.mayiwei.me.mredrock_exam.util.PagerView.JStatePagerAdapter;
import com.mayiwei.me.mredrock_exam.util.RecyclerView.BaseViewHolder;
import com.mayiwei.me.mredrock_exam.util.RecyclerView.RecyclerArrayAdapter;
import com.mayiwei.me.net.Net;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Frank on 15/5/18.
 */
public class View3_Fragment extends Fragment {
    private SuperRecyclerView recyclerView;
    private QuestionAdapter2 mAdapter2;
    private int page = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view3, container, false);
        recyclerView = (SuperRecyclerView) view.findViewById(R.id.mrecyclerview);
        InitRecyclerView();
        return view;
    }

    private void InitRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter2 = new QuestionAdapter2(getActivity());
        recyclerView.setAdapter(mAdapter2);
        final Map<String,String> params = new HashMap<>();
        params.put("page", page + "");
        Net.getInstance().netPost(API.GetQuestions, params, new Net.StrCallback() {
            @Override
            public void response(String result) {
                final QuestionResult Qresult = new Gson().fromJson(result, QuestionResult.class);
                mAdapter2.addAll(Qresult.getData().getQuestions());
            }
        });

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Net.getInstance().netPost(API.GetQuestions, params, new Net.StrCallback() {
                    @Override
                    public void response(String result) {
                        final QuestionResult Qresult = new Gson().fromJson(result, QuestionResult.class);

                        page = 0;
                        params.put("page", page + "");
                        recyclerView.showRecycler();
                        mAdapter2.clear();
                        mAdapter2.addAll(Qresult.getData().getQuestions());
                    }
                });
            }
        });

        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int i, int i1, int i2) {

                page++;
                params.put("page", page + "");
                Net.getInstance().netPost(API.GetQuestions, params, new Net.StrCallback() {
                    @Override
                    public void response(String result) {
                        final QuestionResult Qresult = new Gson().fromJson(result, QuestionResult.class);

                        recyclerView.showRecycler();
                        recyclerView.hideMoreProgress();
                        if (Qresult.getData().getQuestions() != null) {
                            mAdapter2.addAll(Qresult.getData().getQuestions());
                        }

                    }
                });
            }
        }, 10);

        mAdapter2.addHeader(new QuestionHeader());

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    //使用别人封装的RecyclerView的优化的Adapter

    class QuestionAdapter2 extends RecyclerArrayAdapter<QuestionResult.QuestionInfo.Question> {

        public QuestionAdapter2(Context context){
            super(context);
        }
        @Override
        public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            return new QuestionVH2(parent);
        }

        @Override
        public void OnBindViewHolder(BaseViewHolder holder, int position) {
            holder.setData(getItem(position));
        }
    }

    //优化的ViewHolder

    class QuestionVH2 extends BaseViewHolder<QuestionResult.QuestionInfo.Question> {
        TextView tv_title;
        TextView tv_content;
        TextView tv_data;
        TextView tv_userName;
        public QuestionVH2(ViewGroup parent) {
            super(parent, R.layout.item_question);
            tv_title=(TextView) itemView.findViewById(R.id.title);
            tv_content=(TextView) itemView.findViewById(R.id.content);
            tv_data = (TextView) itemView.findViewById(R.id.data);
            tv_userName = (TextView) itemView.findViewById(R.id.user_name);
        }

        @Override
        public void setData(final QuestionResult.QuestionInfo.Question question) {
            tv_title.setText(question.getTitle());
            tv_content.setText(question.getContent());
            tv_data.setText(question.getDate());
            tv_userName.setText(question.getAuthorName());
        }
    }

    class QuestionHeader implements RecyclerArrayAdapter.HeaderView{
        private JPagerView jPagerView;

        @Override
        public View onCreateView(ViewGroup parent) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.header_question,parent,false);
            jPagerView = (JPagerView)view.findViewById(R.id.jpagerview);
            jPagerView.setAdapter(new MyBinearAdapter());
            return view;
        }

        @Override
        public void onBindView(View headerView) {

        }
    }

    class MyBinearAdapter extends JStatePagerAdapter {
        int[] res ={
                R.drawable.test1,
                R.drawable.test2,
                R.drawable.test3,
                R.drawable.test4,
                R.drawable.test5
        };

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView img = new ImageView(getActivity());
            //图片拉伸
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //设置图片长宽
            img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            img.setImageResource(res[position]);
            return img;
        }

        @Override
        public int getCount() {
            return res.length;
        }
    }
}
