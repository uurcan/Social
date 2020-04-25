package com.example.social.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.social.R;
import com.example.social.adapter.CategoriesAdapter;
import com.example.social.model.Category;
import com.example.social.model.CategoryVariables;
import com.example.social.ui.FeedDetailsActivity;
import com.example.social.adapter.FeedListAdapter;
import com.example.social.constants.Constants;
import com.example.social.databinding.FragmentFeedBinding;
import com.example.social.datasource.FeedViewModel;
import com.example.social.model.Article;
import com.example.social.model.Specification;

import java.util.List;

public class FeedFragment extends Fragment implements FeedListAdapter.OnItemClickListener,
           CategoriesAdapter.OnItemClickListener,SwipeRefreshLayout.OnRefreshListener{
    private FeedListAdapter feedListAdapter;
    private FragmentFeedBinding fragmentFeedBinding;
    private List<Category> categories;
    private FeedViewModel viewModel;
    private Specification specification;

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.fragmentFeedBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_feed, container, false);
        return this.fragmentFeedBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeFeed();
        initializeToolbar();
        initializeCategories();
    }

    private void initializeCategories() {
        CategoryVariables variables = new CategoryVariables();
        categories = variables.getCategories();
        CategoriesAdapter adapter = new CategoriesAdapter(categories,getContext());
        adapter.setOnItemClickListener(this);
        fragmentFeedBinding.categoriesFeed.setAdapter(adapter);
    }

    private void initializeToolbar() {
        if (getView() != null) {
            Toolbar toolbar = getView().findViewById(R.id.toolbar_main);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
    }

    private void initializeFeed() {
        fragmentFeedBinding.swipeRefreshFeed.setOnRefreshListener(this);
        specification = new Specification();
        viewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
        fragmentFeedBinding.listFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        feedListAdapter = new FeedListAdapter(null,getContext());
        initializeLiveData();
        feedListAdapter.setOnItemClickListener(this);
        //viewModel.getNetworkState().observe(this,networkState -> feedListAdapter.setNetworkState(networkState));
        fragmentFeedBinding.listFeed.setAdapter(feedListAdapter);
    }

    @Override
    public void onItemClick(Article article) {
        if (getActivity() != null && getView() != null) {
            Intent intent = new Intent(getContext(), FeedDetailsActivity.class);
            intent.putExtra(Constants.TITLE, article.getTitle());
            intent.putExtra(Constants.IMAGE, article.getUrlToImage());
            intent.putExtra(Constants.DATE, article.getPublishedAt());
            intent.putExtra(Constants.AUTHOR, article.getAuthor());
            intent.putExtra(Constants.URL, article.getUrl());
            intent.putExtra(Constants.DESCRIPTION,article.getDescription());
            intent.putExtra(Constants.SOURCE, article.getSource().getName());
            final LayoutAnimationController controller =
                    AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down);
            fragmentFeedBinding.listFeed.setLayoutAnimation(controller);
            fragmentFeedBinding.listFeed.scheduleLayoutAnimation();
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.slide_up_animation, R.anim.fade_exit_transition);
            }
        }
    }

    @Override
    public void onRefresh() {
        initializeLiveData();
    }

    @Override
    public void onClick(View view, int position) {
        Toast.makeText(getContext(),categories.get(position).getName(),Toast.LENGTH_SHORT).show();
        specification.setCategory(categories.get(position).getName());
        fragmentFeedBinding.listFeed.smoothScrollToPosition(0);
        initializeLiveData();
    }
    private void initializeLiveData(){
        fragmentFeedBinding.swipeRefreshFeed.setRefreshing(true);
        viewModel.getPagedListLiveData(specification).observe(this, articles -> {
            if (articles != null){
                feedListAdapter.setArticles(articles);
            }
        });
        fragmentFeedBinding.swipeRefreshFeed.setRefreshing(false);
    }
}
