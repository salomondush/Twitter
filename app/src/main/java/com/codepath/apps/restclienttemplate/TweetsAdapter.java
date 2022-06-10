package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.w3c.dom.Text;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    public static final String TWEET = "TWEET";
    public static final int PROFILE_RADIUS = 80;

    private Context context;
    private List<Tweet> tweets;
    private ItemTweetBinding binding;

    // Define listener member variable
    private OnItemClickListener listener;
    private OnItemClickListener toggleListener;
    private OnItemClickListener retweetToggleListener;



    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }


    // pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickToggleListener(OnItemClickListener listener) {
        toggleListener = listener;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickRetweetToggleListener(OnItemClickListener listener) {
        retweetToggleListener = listener;
    }


    // for each row, inflate the layout
    @NonNull
    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemTweetBinding.inflate(LayoutInflater.from(context));
        // return a new holder instance
        return new TweetsAdapter.ViewHolder(binding.getRoot(), listener);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull TweetsAdapter.ViewHolder holder, int position) {
        // get the data at position
        Tweet tweet = tweets.get(position);
        // bind the data with vew holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProfileImage;
        private ImageView ivImage;
        private TextView tvBody;
        private TextView tvScreenName;
        private TextView created_at;
        private TextView likeCount;
        private ToggleButton likedBtn;
        private TextView retweetCount;
        private ToggleButton retweetBtn;
        private ImageView isVerified;
        private TextView userName;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener)  {
            super(itemView);
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


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(itemView, getAdapterPosition());
                }
            });

            likedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleListener.onItemClick(itemView, getAdapterPosition());
                }
            });

            retweetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    retweetToggleListener.onItemClick(itemView, getAdapterPosition());
                }
            });
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.getBody());
            created_at.setText(tweet.getCreatedAt());
            tvScreenName.setText(tweet.getUser().getScreenName());
            likeCount.setText(tweet.getLikeCount());
            likedBtn.setChecked(tweet.isLiked());
            retweetCount.setText(tweet.getRetweetCount());
            retweetBtn.setChecked(tweet.isRetweeted());
            isVerified.setVisibility(tweet.getUser().isVerified() ? View.VISIBLE : View.GONE);
            userName.setText(tweet.getUser().getUserName());

            Glide.with(context).load(tweet.getUser().getProfileImageUrl()).
                    transform(new RoundedCorners(PROFILE_RADIUS)).into(ivProfileImage);
            Glide.with(context).load(tweet.getMediaUrl()).into(ivImage);
        }
    }
}
