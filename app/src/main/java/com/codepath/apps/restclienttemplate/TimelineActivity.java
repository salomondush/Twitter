package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
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

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final int RESTRICTED_COUNT = 0;
    public static final int INCREMENT = 1;
    private final int REQUEST_CODE = 20;

    TweetDao tweetDao;
    TwitterClient client; // fixme: make these private
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    SwipeRefreshLayout swipeContainer;
    ActivityTimelineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) binding.swipeContainer;

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            fetchTimelineAsync(0);
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        client = TwitterApp.getRestClient(this);
        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();


        // find the recycler view
        rvTweets = binding.rvTweets;
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

                     int numLikes = Integer.parseInt(tweet.getLikeCount());
                     if (tweet.isLiked()){
                         tweet.setLiked(false);
                         tweet.setLikeCount(String.valueOf(numLikes == RESTRICTED_COUNT?
                                 numLikes-INCREMENT: --numLikes));
                         handleLikeAction(false, Long.toString(tweet.getId()));
                     } else if (!tweet.isLiked()) {
                         tweet.setLiked(true);
                         tweet.setLikeCount(String.valueOf(numLikes == RESTRICTED_COUNT?
                                 numLikes+INCREMENT: ++numLikes));
                         handleLikeAction(true, Long.toString(tweet.getId()));
                     }

                     tweets.add(position, tweet);
                     adapter.notifyItemChanged(position);
                 }
             }
        });

        adapter.setOnItemClickRetweetToggleListener(new TweetsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (position != RecyclerView.NO_POSITION) {

                    Tweet tweet = tweets.get(position);
                    tweets.remove(position);

                    int numRetweets = Integer.parseInt(tweet.getRetweetCount());
                    if (tweet.isRetweeted()){
                        tweet.setRetweeted(false);
                        tweet.setRetweetCount(String.valueOf(numRetweets == RESTRICTED_COUNT?
                                numRetweets-INCREMENT:--numRetweets));
                        handleRetweetAction(false, Long.toString(tweet.getId()));
                    } else if (!tweet.isRetweeted()) {
                        tweet.setRetweeted(true);
                        tweet.setRetweetCount(String.valueOf(numRetweets == RESTRICTED_COUNT?
                                numRetweets+INCREMENT:++numRetweets));
                        handleRetweetAction(true, Long.toString(tweet.getId()));
                    }

                    tweets.add(position, tweet);
                    adapter.notifyItemChanged(position);
                }
            }
        });

        // Recycler view setup: layout manager and the adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);


        // Query for existing tweets in the Db
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i("TimelineActivity", "fetching tweets from db");
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
                tweetDao.recentItems();
                List<Tweet> tweetsFromDB = TweetWithUser.getTweetList(tweetWithUsers);

                // clear adapter and update with new db data
                adapter.clear();
                tweets.addAll(tweetsFromDB);
            }
        });

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
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonArray);
                    tweets.addAll(tweetsFromNetwork);
                    adapter.notifyDataSetChanged();

                    // Query for existing tweets in the Db
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("TimelineActivity", "saving data to db");
                            // insert users first for the foreign key connection to work
                            List<User> usersFromNetwork = User.fromJsonTweetArray(tweetsFromNetwork);
                            tweetDao.insertModel(usersFromNetwork.toArray(new User[0])); //
                            // converting List to array -- the parameter of newly created array
                            // that we passed in is auto resizable

                            // insert tweets next
                            tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                        }
                    });
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void handleLikeAction(Boolean like, String id){
        client.processLike(like, id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Toast.makeText(
                        TimelineActivity.this,
                        like? "Liked!": "Unliked!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(
                        TimelineActivity.this,
                        "Error!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void handleRetweetAction(Boolean  retweet, String tweetId){
        client.processRetweet(retweet, tweetId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Toast.makeText(
                        TimelineActivity.this,
                        retweet? "Retweeted!":"Unretweeted!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(
                        TimelineActivity.this,
                        "Error!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}