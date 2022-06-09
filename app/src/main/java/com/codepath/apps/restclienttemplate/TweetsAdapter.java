package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.net.ContentHandler;
import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;

    // pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // for each row, inflate the layout
    @NonNull
    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            this.ivImage = itemView.findViewById(R.id.ivImage);
            this.tvBody = itemView.findViewById(R.id.tvBody);
            this.tvScreenName = itemView.findViewById(R.id.tvScreenName);
            this.created_at = itemView.findViewById(R.id.created_at);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            created_at.setText(tweet.createdAt);
            tvScreenName.setText(tweet.user.screenName);

            int radius = 80;
            Glide.with(context).load(tweet.user.profileImageUrl).transform(new RoundedCorners(radius)).into(ivProfileImage);
            Glide.with(context).load(tweet.mediaUrl).into(ivImage);
        }
    }
}
