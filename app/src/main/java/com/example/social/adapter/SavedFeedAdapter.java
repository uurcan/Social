package com.example.social.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.social.R;
import com.example.social.model.feed.Article;

import java.util.List;
import java.util.Objects;

public class SavedFeedAdapter extends RecyclerView.Adapter<SavedFeedAdapter.ViewHolder> {
    private Context context;
    private List<Article> articles;

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


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewSavedArticle;
        private TextView savedTextArticle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSavedArticle = itemView.findViewById(R.id.saved_article_image);
            savedTextArticle = itemView.findViewById(R.id.saved_article_text);
        }
    }

}
