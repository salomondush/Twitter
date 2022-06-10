package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {

    ActivityTweetDetailsBinding binding;

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

        ImageView isVerified;
        TextView userName;

        super.onCreate(savedInstanceState);
        binding = ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);


        // fetch views
        ivProfileImage = binding.ivProfileImage;
        ivImage = binding.ivImage;
        tvBody = binding.tvBody;
        tvScreenName = binding.tvScreenName;
        created_at = binding.createdAt;

        likeCount = binding.likesCount;
        likedBtn = binding.likeBtn;
        retweetCount = binding.retweetsCount;
        retweetBtn = binding.retweetBtn;

        isVerified = binding.isVerified;
        userName = binding.userName;

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
        userName.setText(tweet.getUser().getUserName());
        isVerified.setVisibility(tweet.getUser().isVerified() ? View.VISIBLE : View.GONE);

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