package com.codepath.apps.restclienttemplate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.protocol.RequestUserAgent;

import static com.codepath.apps.restclienttemplate.R.id.tweetId;

//This page sets up the ability to reply to a tweet on your timeline
public class ReplyActivity extends AppCompatActivity {
    TwitterClient tClient;
    EditText tweet;
    Tweet replyTweet;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        tClient = TwitterApp.getRestClient(this);
        tweet = (EditText) findViewById(R.id.tweetId);


        replyTweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        id = replyTweet.uid;
        tweet.setText(replyTweet.user.screenName);
    }

    public void onComposeClick(View view) {

        tClient.replyToTweet(tweet.getText().toString(), id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet tweet = Tweet.fromJSON(response);
                    Intent data = new Intent();
                    data.putExtra("tweet", Parcels.wrap(tweet));
                    setResult(RESULT_OK, data);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        });

    }

}
