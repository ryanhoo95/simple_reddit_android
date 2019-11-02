package com.example.simplereddit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplereddit.R;
import com.example.simplereddit.model.Topic;
import com.google.android.material.button.MaterialButton;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Topic> topics;
    private boolean disableVote;
    private OnVotingListener onVotingListener;

    public TopicAdapter(Context context, ArrayList<Topic> topics, boolean disableVote) {
        this.context = context;
        this.topics = topics;
        this.disableVote = disableVote;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_topic,
                parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Topic topic = topics.get(position);

        // topic
        holder.tvTopic.setText(topic.getTopic());

        // upvote
        holder.btnUpvote.setText(topic.getUpVote());

        // downvote
        holder.btnDownvote.setText(topic.getDownVote());

        if (disableVote) {
            holder.btnUpvote.setEnabled(false);
            holder.btnDownvote.setEnabled(false);
        } else {
            holder.disposableUpvote = RxView.clicks(holder.btnUpvote).subscribe(
                    view -> onVotingListener.onUpvoted(topic, holder.btnUpvote, holder.getAdapterPosition())
            );

            holder.disposableDownvote = RxView.clicks(holder.btnDownvote).subscribe(
                    view -> onVotingListener.onDownvoted(topic, holder.btnDownvote, holder.getAdapterPosition())
            );
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);

        // reset the view
        holder.tvTopic.setText(null);
        holder.btnUpvote.setText("0");
        holder.btnUpvote.setText("0");

        if (holder.disposableUpvote != null && !holder.disposableUpvote.isDisposed()) {
            holder.disposableUpvote.dispose();
        }

        if (holder.disposableDownvote != null && !holder.disposableDownvote.isDisposed()) {
            holder.disposableDownvote.dispose();
        }
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTopic;
        private MaterialButton btnUpvote;
        private MaterialButton btnDownvote;
        private Disposable disposableUpvote;
        private Disposable disposableDownvote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTopic = itemView.findViewById(R.id.tv_topic);
            btnUpvote = itemView.findViewById(R.id.btn_upvote);
            btnDownvote = itemView.findViewById(R.id.btn_downvote);
        }
    }

    /*
     * use interface to handle upvote and downvote
     */
    public interface OnVotingListener {
        void onUpvoted(Topic topic, MaterialButton btnUpvote, int position);

        void onDownvoted(Topic topic, MaterialButton btnDownvote, int position);
    }

    public void addOnVotingListener(OnVotingListener onVotingListener) {
        this.onVotingListener = onVotingListener;
    }
}
