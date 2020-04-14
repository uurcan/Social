package com.example.social.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.social.Application;
import com.example.social.adapter.CategoriesAdapter;
import com.example.social.constants.Constants;
import com.example.social.databinding.FeedActivityBinding;
import com.example.social.datasource.FeedViewModel;
import com.example.social.adapter.FeedListAdapter;
import com.example.social.R;
import com.example.social.model.Article;
import com.example.social.model.Category;
import com.example.social.utils.CategoryVariables;
import com.firebase.ui.auth.AuthUI;

import java.util.List;

public class MainActivity extends AppCompatActivity implements FeedListAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        CategoriesAdapter.OnItemClickListener{
    private List<Category> categories;
    FeedViewModel feedViewModel;
    FeedListAdapter feedListAdapter;
    FeedActivityBinding feedItemBinding;
    CategoriesAdapter categoriesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeFeed();
        initializeToolbar();
        initializeCategories();
    }

    private void initializeCategories() {
        categories = CategoryVariables.getCategories();
        categoriesAdapter = new CategoriesAdapter(categories,getApplicationContext());
        categoriesAdapter.setOnItemClickListener(this);
        RecyclerView recyclerViewCategories = findViewById(R.id.categories_feed);
        recyclerViewCategories.setAdapter(categoriesAdapter);
        feedItemBinding.swipeRefreshFeed.setOnRefreshListener(this);
        feedItemBinding.swipeRefreshFeed.setColorSchemeResources(R.color.navy);
    }

    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_log_out){
          firebaseLogout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeFeed(){
        feedItemBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        feedViewModel = new FeedViewModel(Application.create(this));
        feedItemBinding.listFeed.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        feedListAdapter = new FeedListAdapter(getApplicationContext());
        feedListAdapter.setOnItemClickListener(this);
        feedViewModel.getPagedListLiveData().observe(this, (PagedList<Article> pagedList) -> {
            feedItemBinding.swipeRefreshFeed.setRefreshing(true);
            feedListAdapter.submitList(pagedList);
            feedItemBinding.swipeRefreshFeed.setRefreshing(false);
        });
        feedViewModel.getNetworkState().observe(this,networkState -> feedListAdapter.setNetworkState(networkState));
        feedItemBinding.listFeed.setAdapter(feedListAdapter);
    }
    private void firebaseLogout(){
        AuthUI.getInstance()
                .signOut(MainActivity.this)
                .addOnCompleteListener(task -> startActivity(new Intent(this,LoginActivity.class))).addOnFailureListener(e ->
                Toast.makeText(getApplicationContext(),"Fail while logging out..",Toast.LENGTH_LONG).show());
        getApplicationContext().getSharedPreferences(Constants.AUTH_PERSISTENCE, Context.MODE_PRIVATE).edit().clear().apply();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (feedViewModel.getPagedListLiveData().getValue() != null) {
            ImageView imageView = view.findViewById(R.id.item_detail_image);
            Intent intent = new Intent(MainActivity.this, FeedDetailsActivity.class);
            Article article = feedViewModel.getPagedListLiveData().getValue().get(position);
            if (article != null) {
                intent.putExtra(Constants.TITLE, article.getTitle());
                intent.putExtra(Constants.IMAGE, article.getUrlToImage());
                intent.putExtra(Constants.DATE, article.getPublishedAt());
                intent.putExtra(Constants.AUTHOR, article.getAuthor());
                intent.putExtra(Constants.URL, article.getUrl());
                intent.putExtra(Constants.SOURCE, article.getSource().getName());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageView.setTransitionName(getString(R.string.textImage));
                    }
                    Pair<View, String> viewStringPair = Pair.create(imageView, ViewCompat.getTransitionName(imageView));
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, viewStringPair);
                    startActivity(intent, optionsCompat.toBundle());
                } else {
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        feedItemBinding.swipeRefreshFeed.post(() -> feedViewModel.getPagedListLiveData().observe(MainActivity.this, (PagedList<Article> pagedList) -> {
            feedItemBinding.swipeRefreshFeed.setRefreshing(true);
            feedListAdapter.submitList(pagedList);
            feedListAdapter.notifyDataSetChanged();
            feedItemBinding.swipeRefreshFeed.setRefreshing(false);
        }));
    }

    @Override
    public void onClick(View view, int position) {
        //todo: categories implementation is required
        String category = categories.get(position).getName();
        Toast.makeText(getApplicationContext(),category,Toast.LENGTH_LONG).show();
        Application.getRestApi().fetchFeed(category,Constants.API_KEY,1,10);
    }
}
