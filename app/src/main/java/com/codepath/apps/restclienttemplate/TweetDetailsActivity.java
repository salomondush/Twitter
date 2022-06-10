package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView ivProfileImage;
        ImageView ivImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView created_at;

        TextView likeCount;
        ToggleButton likedBtn;
        TextView retweetCount;
        ToggleButton retweetBtn;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);


        // fetch views
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivImage = findViewById(R.id.ivImage);
        tvBody = findViewById(R.id.tvBody);
        tvScreenName = findViewById(R.id.tvScreenName);
        created_at = findViewById(R.id.createdAt);

        likeCount = findViewById(R.id.likesCount);
        likedBtn = findViewById(R.id.likeBtn);
        retweetCount = findViewById(R.id.retweetsCount);
        retweetBtn = findViewById(R.id.retweetBtn);

        // Extract tweet details from the intent
        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra(TweetsAdapter.TWEET));

        // fill in the info
        tvBody.setText(tweet.getBody());
        tvScreenName.setText(tweet.getUser().getScreenName());
        created_at.setText(tweet.getCreatedAt());
        likeCount.setText(tweet.getLikeCount());
        likedBtn.setChecked(tweet.isLiked());
        retweetCount.setText(tweet.getRetweetCount());
        retweetBtn.setChecked(tweet.isRetweeted());

        // loading the images with Glide
        Glide.with(this).load(tweet.getUser()
                        .getProfileImageUrl())
                        .transform(new RoundedCorners(TweetsAdapter.PROFILE_RADIUS))
                        .into(ivProfileImage);
        Glide.with(this)
                .load(tweet.getMediaUrl())
                .into(ivImage);
    }
}