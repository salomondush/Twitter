package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimeLineActivity";
    private final int REQUEST_CODE = 20;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            fetchTimelineAsync(0);
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        client = TwitterApp.getRestClient(this);

        // find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        // init the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);


        adapter.setOnItemClickListener(new TweetsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    // get the movie at the position, this won't work if the class is static
                    Tweet tweet = tweets.get(position);
                    // create intent for the new activity
                    Intent intent = new Intent(TimelineActivity.this, TweetDetailsActivity.class);
                    // serialize the movie using parceler, use its short name as a key
                    intent.putExtra(TweetsAdapter.TWEET, Parcels.wrap(tweet));
                    // show the activity
                    startActivity(intent);
                }
            }
        });

        adapter.setOnItemClickToggleListener(new TweetsAdapter.OnItemClickListener() {
             @Override
             public void onItemClick(View itemView, int position) {
                 if (position != RecyclerView.NO_POSITION) {

                     Tweet tweet = tweets.get(position);
                     tweets.remove(position);

                     if (tweet.isLiked()){
                         tweet.setLiked(false);
                         int likes = Integer.parseInt(tweet.getLikeCount());
                         tweet.setLikeCount(String.valueOf(--likes));
                         setTweetUnliked(tweet.getTweetId());
                     } else if (!tweet.isLiked()) {
                         tweet.setLiked(true);
                         int likes = Integer.parseInt(tweet.getLikeCount());
                         tweet.setLikeCount(String.valueOf(++likes));
                         setTweetLiked(tweet.getTweetId());
                     }

                     tweets.add(position, tweet);
                     adapter.notifyItemChanged(position);
                 }
             }
        });

        // Recycler view setup: layout manager and the adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);
        populateHomeTimeline();
    }

    public void fetchTimelineAsync(int page) {
        adapter.clear();
        populateHomeTimeline();
        swipeContainer.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       if (item.getItemId() == R.id.lobutton){
            onLogoutButton();
        }
        return super.onOptionsItemSelected(item);
    }

    public void editTweet(View view){
        Toast.makeText(this, "Compose!", Toast.LENGTH_SHORT).show();
        // navigate ot the compose activity
        Intent intent = new Intent(this, ComposeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            // Get data from the intent (tweet)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));

            // insert tweet and notify change
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void onLogoutButton() {
        // forget who's logged in
        TwitterApp.getRestClient(this).clearAccessToken();

        // navigate backwards to Login screen
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);
    }


    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e){
                    Log.e(TAG, "Json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                Log.i(TAG, BuildConfig.CONSUMER_KEY);
                Log.e(TAG, "onFailure"+ response, throwable);
            }
        });
    }

    private void setTweetLiked(String id){
        client.likeTweet(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Toast.makeText(
                        TimelineActivity.this,
                        "Liked!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(
                        TimelineActivity.this,
                        "Error!",
                        Toast.LENGTH_SHORT).show();

                Log.i(TAG, "error1: " + throwable.toString());
            }
        });
    }

    private void setTweetUnliked(String id){
        client.unlikeTweet(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Toast.makeText(
                        TimelineActivity.this,
                        "Unliked!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(
                        TimelineActivity.this,
                        "Error!",
                        Toast.LENGTH_SHORT).show();

                Log.i(TAG, "error2: " + throwable.toString() + " " + response);
            }
        });
    }
}