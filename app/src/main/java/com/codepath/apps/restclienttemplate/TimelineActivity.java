package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

//This page sets up the main timeline of twitter and enables the ability to compose tweets
public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    private SwipeRefreshLayout swipeContainer;
    int REQUEST_CODE = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);
        //find recycler view
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);
        //init arraylist
        tweets = new ArrayList<>();
        //construct adapter from data source
        tweetAdapter = new TweetAdapter(tweets);
        //recycler view setup
        rvTweets .setLayoutManager(new LinearLayoutManager(this));
        //set adapter
        rvTweets.setAdapter(tweetAdapter);
        populateTimeline();


        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,

                android.R.color.holo_red_light);

    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            public void onSuccess(JSONArray json) {
                // Remember to CLEAR OUT old items before appending in the new ones
                tweetAdapter.clear();
                // ...the data has come back, add new items to your adapter...
                tweetAdapter.addAll(tweets);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }
            public void onFailure(Throwable e) {
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public void onComposeAction(MenuItem mi) {
        Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && (requestCode == 20 || requestCode == REQUEST_CODE)) {

            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }
    }

    private void populateTimeline() {
       client.getHomeTimeline(new JsonHttpResponseHandler() {

           @Override
           public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
               //Log.d("Twitter Client", response.toString());
               for(int i = 0; i < response.length(); i++) {
                   try {
                       Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                       tweets.add(tweet);
                       tweetAdapter.notifyItemInserted(tweets.size() - 1);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
              Log.d("Twitter Client", responseString);
              throwable.printStackTrace();
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
               Log.d("Twitter Client", errorResponse.toString());
               throwable.printStackTrace();
           }

           @Override
           public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
               Log.d("Twitter Client", errorResponse.toString());
               throwable.printStackTrace();
           }
       });
    }
}
