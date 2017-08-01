package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>>, SwipeRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int NEWS_LOADER_ID = 1;
    private static String BASE_REQUEST_URL =
            "http://content.guardianapis.com/search?q=";
    @BindView(R.id.article_list)
    ListView articleList;
    @BindView(R.id.empty_view)
    TextView mEmptyStateTextView;
    SwipeRefreshLayout swipe;
    ProgressBar mProgressBar;
    String APP_KEY = "d02b9599-b6ae-445d-b022-e2622f129edb";
    private SwipeRefreshLayout swipeContainer;
    private ArticleAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        articleList.setEmptyView(mEmptyStateTextView);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipe.setOnRefreshListener(this);
        // Configure the refreshing colors
        swipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_blue_light,
                android.R.color.holo_blue_dark,
                android.R.color.holo_purple);


        // Create a new adapter that takes an empty list of books as input
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        articleList.setAdapter(mAdapter);

        // getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        // Get a reference to the LoaderManager, in order to interact with loaders.
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.

            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        Log.e(LOG_TAG, " Loader created " + BASE_REQUEST_URL);


        return new ArticleLoader(this, BASE_REQUEST_URL, APP_KEY, mProgressBar);

    }


    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        swipe.setRefreshing(false);
        ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.loading);
        mProgressBar.setVisibility(View.GONE);
        articleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = mAdapter.getItem(position);
                Log.e("APP", "final i'M HERE");
                String url = article.getWebUrl();
                //Log.e("APP", "GLITCHxx :" + temp1);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                startActivity(intent);
            }
        });

        mEmptyStateTextView.setText(R.string.no_News);
        mAdapter.clear();
        // If there is a valid list , then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        } else {

            mAdapter.clear();

        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();

    }

    @Override
    public void onRefresh() {
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
    }
}
