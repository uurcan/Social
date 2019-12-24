package com.example.mvvmapplication.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mvvmapplication.R;
import com.example.mvvmapplication.Utils.ApplicationUtils;
import com.example.mvvmapplication.Utils.NetworkState;
import com.example.mvvmapplication.databinding.FeedItemBinding;
import com.example.mvvmapplication.databinding.NetworkItemBinding;
import com.example.mvvmapplication.model.Article;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class FeedListAdapter extends PagedListAdapter<Article, RecyclerView.ViewHolder> {
    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;
    private Context context;
    private NetworkState networkState;

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
        }else {
            FeedItemBinding feedBinding = FeedItemBinding.inflate(inflater,parent,false);
            return new FeedItemViewHolder(feedBinding);
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

    private class NetworkStateItemViewHolder extends RecyclerView.ViewHolder{
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

    private class FeedItemViewHolder extends RecyclerView.ViewHolder {
        private FeedItemBinding feedItemBinding;
        FeedItemViewHolder(FeedItemBinding feedBinding) {
            super(feedBinding.getRoot());
            this.feedItemBinding = feedBinding;
        }
        void bindTo(Article article){
            feedItemBinding.itemProfileImage.setVisibility(View.VISIBLE);
            feedItemBinding.feedItemDescription.setVisibility(View.VISIBLE);
            String author = article.getAuthor() == null || article.getAuthor().isEmpty() ? "Anonymous" : article.getAuthor();
            String titleString = String.format(context.getString(R.string.item_title),author,article.getTitle());
            SpannableString spannableString = new SpannableString(titleString);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context.getApplicationContext(),R.color.secondary_text)),
                    titleString.lastIndexOf(author) + author.length() + 1, titleString.lastIndexOf(article.getTitle()) - 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            feedItemBinding.feedItemTitle.setText(spannableString);
            feedItemBinding.feedItemTime.setText(String.format("%s at %s", ApplicationUtils.getDate(article.getPublishedAt()),
                    ApplicationUtils.getTime(article.getPublishedAt())));
            feedItemBinding.feedItemDescription.setText(article.getDescription());
            Picasso.get().load(article.getUrlToImage()).resize(250,200).into(feedItemBinding.itemDetailImage);
        }
    }
}
