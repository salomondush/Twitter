package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.net.ContentHandler;
import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    public static final String TWEET = "TWEET";
    Context context;
    List<Tweet> tweets;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);

        // return a new holder instance
        TweetsAdapter.ViewHolder viewHolder = new TweetsAdapter.ViewHolder(view, listener);
        return viewHolder;
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

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage;
        ImageView ivImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView created_at;
        TextView likeCount;
        ToggleButton likedBtn;
        TextView retweetCount;
        ToggleButton retweetBtn;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener clickListener)  {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            created_at = itemView.findViewById(R.id.created_at);
            likeCount = itemView.findViewById(R.id.likes_count);
            likedBtn = itemView.findViewById(R.id.likeBtn);
            retweetCount = itemView.findViewById(R.id.retweets_count);
            retweetBtn = itemView.findViewById(R.id.retweetBtn);

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

            int radius = 80; // fixme: avoid using string literals
            Glide.with(context).load(tweet.getUser().getProfileImageUrl()).transform(new RoundedCorners(radius)).into(ivProfileImage);
            Glide.with(context).load(tweet.getMediaUrl()).into(ivImage);
        }
    }
}
