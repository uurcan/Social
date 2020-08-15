package com.example.social.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social.R;
import com.example.social.listener.OnFeedClickListener;
import com.example.social.model.feed.Category;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private List<Category> categoryList;
    private Context context;
    private OnFeedClickListener onItemClickListener;
    private int rowIndex = 0;

    public CategoriesAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categories,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.ViewHolder holder, int position) {
        holder.textCategory.setText(categoryList.get(position).getName());
        if (rowIndex == position){
            holder.textCategory.setBackgroundColor(Color.argb(40, 100, 100, 230));
        } else {
            holder.textCategory.setBackgroundColor(Color.argb(0, 0, 0, 0));
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setOnItemClickListener(OnFeedClickListener onFeedClickListener){
        this.onItemClickListener = onFeedClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textCategory;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.categoryName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onCategoryClick(categoryList.get(getAdapterPosition()));
            notifyItemChanged(rowIndex);
            rowIndex = getAdapterPosition();
            notifyItemChanged(rowIndex);
        }
    }
}