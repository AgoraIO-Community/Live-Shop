package io.agora.liveshow.demo.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import demo.liveshow.agora.io.R;
import io.agora.liveshow.demo.data.ChatMessage;

public class MessageContainer {
    private static final int MAX_MSG_COUNT = 20;
    private Context mContext;
    private LayoutInflater mInflater;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private List<ChatMessage> mMessageList;

    public MessageContainer(RecyclerView listView) {
        mRecyclerView = listView;
        mContext = mRecyclerView.getContext();
        mInflater = LayoutInflater.from(mContext);
        mMessageList = new ArrayList<>();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mAdapter = new MyAdapter(mMessageList);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void addMessage(ChatMessage message) {
        while (mMessageList.size() >= MAX_MSG_COUNT) {
            mMessageList.remove(0);
        }

        mMessageList.add(message);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.scrollToPosition(mMessageList.size() - 1);
            }
        });
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private List<ChatMessage> mMessageList;

        MyAdapter(List<ChatMessage> list) {
            mMessageList = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(mInflater.inflate(R.layout.item_chat_list, null));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            int adapterPosition = holder.getAdapterPosition();
            ChatMessage m = mMessageList.get(adapterPosition);
            TextView name = holder.itemView.findViewById(R.id.item_chat_name_tv);
            TextView content = holder.itemView.findViewById(R.id.item_chat_content_tv);
            if (TextUtils.isEmpty(m.getName())) {
                name.setVisibility(View.GONE);
                name.setText("");
            } else {
                name.setVisibility(View.VISIBLE);
                name.setText(m.getName() + "：");
            }
            content.setText(m.getContent());
        }

        @Override
        public int getItemCount() {
            return mMessageList.size();
        }
    }
}
