package com.example.social.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.social.App;
import com.example.social.R;
import com.example.social.adapter.CategoriesAdapter;
import com.example.social.listener.ConnectivityReceiverListener;
import com.example.social.listener.OnFeedClickListener;
import com.example.social.model.feed.Category;
import com.example.social.model.feed.CategoryVariables;
import com.example.social.services.ConnectivityReceiver;
import com.example.social.ui.FeedDetailsActivity;
import com.example.social.adapter.FeedListAdapter;
import com.example.social.constants.Constants;
import com.example.social.databinding.FragmentFeedBinding;
import com.example.social.datasource.FeedViewModel;
import com.example.social.model.feed.Article;
import com.example.social.model.feed.Specification;

import java.util.List;
import java.util.Objects;

public class FeedFragment extends Fragment implements OnFeedClickListener,
                                                      SwipeRefreshLayout.OnRefreshListener,
                                                      View.OnClickListener,
                                                      ConnectivityReceiverListener {
    private FeedListAdapter feedListAdapter;
    private FragmentFeedBinding fragmentFeedBinding;
    private FeedViewModel viewModel;
    private Specification specification;
    private int pageIndex = 1;

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentFeedBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_feed, container, false);
        initializeToolbar();
        initializeCategories();
        initializeFeed();
        initializePageDirector();
        return fragmentFeedBinding.getRoot();
    }

    private void initializeCategories() {
        RecyclerView recyclerView = fragmentFeedBinding.categoriesFeed;
        CategoryVariables categoryVariables = new CategoryVariables();
        List<Category> categories = categoryVariables.getCategories();
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(categories, getContext());
        categoriesAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(categoriesAdapter);
    }

    private void initializeToolbar() {
        if (getView() != null) {
            if (getActivity() != null) {
                if (getActivity().getActionBar() != null) {
                    Toolbar toolbar = getView().findViewById(R.id.toolbar);
                    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intentFilter.addAction(ConnectivityManager.EXTRA_CAPTIVE_PORTAL);
            ConnectivityReceiver receiver = new ConnectivityReceiver();
            Objects.requireNonNull(getContext()).registerReceiver(receiver, intentFilter);
            App.getInstance().setConnectivityListener(this);
        }
    }

    private void initializeFeed() {
        specification = new Specification();
        viewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
        fragmentFeedBinding.swipeRefreshFeed.setOnRefreshListener(this);
        fragmentFeedBinding.listFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        feedListAdapter = new FeedListAdapter(null,getContext());
        initializeLiveData();
        feedListAdapter.setOnFeedClickListener(this);
        fragmentFeedBinding.listFeed.setAdapter(feedListAdapter);
    }

    @Override
    public void onRefresh() {
        initializeLiveData();
    }

    private void initializeLiveData(){
        fragmentFeedBinding.listFeed.smoothScrollToPosition(0);
        fragmentFeedBinding.swipeRefreshFeed.setRefreshing(true);
        viewModel.getPagedListLiveData(specification).observe(this, articles -> {
            if (articles != null){
                feedListAdapter.setArticles(articles);
            }
        });
        fragmentFeedBinding.swipeRefreshFeed.setRefreshing(false);
    }

    @Override
    public void onCategoryClick(Category category) {
        pageIndex = 1;
        specification.setCurrentPage(pageIndex);
        specification.setCategory(category.getName());
        fragmentFeedBinding.textPagingCurrentPage.setText(String.valueOf(pageIndex));
        initializeLiveData();
        checkCurrentPage();
    }

    private void initializePageDirector() {
        fragmentFeedBinding.listFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)){
                    fragmentFeedBinding.rvItemFeedLayout.setLayoutParams(new LinearLayout.LayoutParams(-1,0,11.0f));
                } else {
                    fragmentFeedBinding.rvItemFeedLayout.setLayoutParams(new LinearLayout.LayoutParams(-1,0,12.0f));
                }
            }
        });
        fragmentFeedBinding.textPagingCurrentPage.setText(String.valueOf(pageIndex));
        fragmentFeedBinding.imagePagingGoForward.setOnClickListener(this);
        fragmentFeedBinding.imagePagingGoBack.setOnClickListener(this);
        checkCurrentPage();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_paging_go_back){
            pageIndex -= 1;
            specification.setCurrentPage(pageIndex);
            fragmentFeedBinding.textPagingCurrentPage.setText(String.valueOf(pageIndex));
            initializeLiveData();
        } else if (v.getId() == R.id.image_paging_go_forward){
            pageIndex += 1;
            specification.setCurrentPage(pageIndex);
            fragmentFeedBinding.textPagingCurrentPage.setText(String.valueOf(pageIndex));
            initializeLiveData();
        }
        checkCurrentPage();
    }

    private void checkCurrentPage(){
        if (pageIndex <= 1) {
            fragmentFeedBinding.imagePagingGoBack.setImageResource(R.color.white);
            fragmentFeedBinding.imagePagingGoBack.setClickable(false);
        }
        else {
            fragmentFeedBinding.imagePagingGoBack.setImageResource(R.drawable.ic_prev_page);
            fragmentFeedBinding.imagePagingGoBack.setClickable(true);
        }
    }

    @Override
    public void onFeedClick(Article article) {
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
    public void onNetworkConnectionChanged(boolean isConnected) {
        checkConnectivity(isConnected);
    }

    private void checkConnectivity(boolean isConnected){
        if (!isConnected){
            Toast.makeText(getContext(), "Offline", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Online !", Toast.LENGTH_SHORT).show();
        }
    }
}
