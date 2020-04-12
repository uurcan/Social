package com.example.social.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.social.utils.ApplicationUtils;
import com.example.social.utils.DateFormat;
import com.example.social.utils.NetworkState;
import com.example.social.databinding.FeedItemBinding;
import com.example.social.databinding.NetworkItemBinding;
import com.example.social.model.Article;

import java.util.Objects;

public class FeedListAdapter extends PagedListAdapter<Article, RecyclerView.ViewHolder> {
    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;
    private Context context;
    private NetworkState networkState;
    private OnItemClickListener onItemClickListener;

    public FeedListAdapter(Context context) {
        super(Article.DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PROGRESS){
            NetworkItemBinding headerBinding = NetworkItemBinding.inflate(inflater,parent,false);
            return new NetworkStateItemViewHolder(headerBinding);
            //todo : progress bar not working ?
        }else {
            FeedItemBinding feedBinding = FeedItemBinding.inflate(inflater,parent,false);
            return new FeedItemViewHolder(feedBinding,onItemClickListener);
        }
    }
    private boolean hasExtraRow(){
        return networkState != null && networkState != NetworkState.LOADED;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FeedItemViewHolder){
            ((FeedItemViewHolder)holder).bindTo(Objects.requireNonNull(getItem(position)));
        }else {
            ((NetworkStateItemViewHolder)holder).bindView(networkState);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() -1){
            return TYPE_PROGRESS;
        }else {
            return TYPE_ITEM;
        }
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow){
            if (previousExtraRow){
                notifyItemRemoved(getItemCount());
            }else {
                notifyItemInserted(getItemCount());
            }
        }else if(newExtraRow && previousState != newNetworkState){
            notifyItemChanged(getItemCount() - 1);
        }
    }

    private static class NetworkStateItemViewHolder extends RecyclerView.ViewHolder{
        private NetworkItemBinding networkItemBinding;
        NetworkStateItemViewHolder(NetworkItemBinding headerBinding) {
            super(headerBinding.getRoot());
            this.networkItemBinding = headerBinding;
        }
        void bindView(NetworkState networkState){
            if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING){
                 networkItemBinding.progressBar.setVisibility(View.VISIBLE);
            }else {
                networkItemBinding.progressBar.setVisibility(View.GONE);
            }
            if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED){
                networkItemBinding.errorMsg.setVisibility(View.VISIBLE);
                networkItemBinding.errorMsg.setText(networkState.getMsg());
            }else {
                networkItemBinding.errorMsg.setVisibility(View.GONE);
            }
        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    private class FeedItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private FeedItemBinding feedItemBinding;
        private OnItemClickListener onItemClickListener;
        FeedItemViewHolder(FeedItemBinding feedBinding,OnItemClickListener onItemClickListener) {
            super(feedBinding.getRoot());
            this.feedItemBinding = feedBinding;
            this.onItemClickListener = onItemClickListener;
        }
        void bindTo(Article article){
            feedItemBinding.txtFeedDescription.setVisibility(View.VISIBLE);
            feedItemBinding.txtFeedAuthor.setText(article.getAuthor() == null || article.getAuthor().isEmpty() ? "Anonymous" : article.getAuthor());
            feedItemBinding.txtFeedTitle.setText(article.getTitle());
            feedItemBinding.txtFeedTime.setText(ApplicationUtils.getTime(article.getPublishedAt()));
            feedItemBinding.txtPublishDate.setText(DateFormat.formatDate(article.getPublishedAt()));
            feedItemBinding.txtFeedDescription.setText(article.getDescription());
            itemView.setOnClickListener(this);
            Glide.with(context)
                    .load(article.getUrlToImage())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            feedItemBinding.pbLoadPhoto.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            feedItemBinding.pbLoadPhoto.setVisibility(View.GONE);
                            return false;
                        }
                    })
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(feedItemBinding.itemDetailImage);
            ViewCompat.setTransitionName(feedItemBinding.itemDetailImage,article.getUrlToImage());
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v,getAdapterPosition());
        }
    }
}

