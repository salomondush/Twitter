package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {

    Tweet tweet;

    ImageView ivProfileImage;
    ImageView ivImage;
    TextView tvBody;
    TextView tvScreenName;
    TextView created_at;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);


        // fetch views
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivImage = findViewById(R.id.ivImage);
        tvBody = findViewById(R.id.tvBody);
        tvScreenName = findViewById(R.id.tvScreenName);
        created_at = findViewById(R.id.created_at);

        // Extract tweet details from the intent
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(TweetsAdapter.TWEET));

        // feel in the info
        tvBody.setText(tweet.getBody());
        tvScreenName.setText(tweet.getUser().getScreenName());
        created_at.setText(tweet.getCreatedAt());

        // loading the images with Glide
        int radius = 80;
        Glide.with(this).load(tweet.user.getProfileImageUrl()).transform(new RoundedCorners(radius)).into(ivProfileImage);
        Glide.with(this).load(tweet.mediaUrl).into(ivImage);
    }
}