package com.example.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social.R;
import com.example.social.listener.OnSavedItemClickListener;
import com.example.social.model.feed.Article;

import java.util.List;

public class SavedFeedAdapter extends RecyclerView.Adapter<SavedFeedAdapter.ViewHolder> {
    private Context context;
    private List<Article> articles;
    private OnSavedItemClickListener onSavedItemClickListener;

    public SavedFeedAdapter(Context context, List<Article> articleList){
        this.context = context;
        this.articles = articleList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_article,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.savedTextArticle.setText(article.getTitle());
        Glide.with(context).load(article.getUrlToImage()).into(holder.imageViewSavedArticle);
    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.size();
    }

    public void setArticles(List<Article> articles) {
        if (articles != null) {
            this.articles = articles;
            notifyDataSetChanged();
        }
    }

    public void setOnSavedItemClickListener(OnSavedItemClickListener onSavedItemClickListener) {
        this.onSavedItemClickListener = onSavedItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        private ImageView imageViewSavedArticle;
        private TextView savedTextArticle;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSavedArticle = itemView.findViewById(R.id.saved_article_image);
            savedTextArticle = itemView.findViewById(R.id.saved_article_text);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onSavedItemClickListener.onSavedItemClick(articles.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View v) {
            onSavedItemClickListener.onSavedItemLongClick(articles.get(getAdapterPosition()));
            return true;
        }
    }
}
