package com.codepath.apps.restclienttemplate.models;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.util.Log;

import androidx.annotation.FontRes;
import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
@Entity(foreignKeys=@ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
public class Tweet {
    private static final String TAG = "Tweet";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @PrimaryKey
    @ColumnInfo
    private long id;

    @ColumnInfo
    private String body;

    @ColumnInfo
    private String createdAt;

    @ColumnInfo
    private long userId;

    @Ignore
    private User user; // @fixme: this is a temporary solution

    @ColumnInfo
    private String mediaUrl;

    @ColumnInfo
    private boolean liked;

    @ColumnInfo
    private String likeCount;

    @ColumnInfo
    private boolean retweeted;

    @ColumnInfo
    private String retweetCount;

    // empty constructor needed by the Parceler library
    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        if(jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }
        tweet.createdAt = getRelativeTimeAgo(jsonObject.getString("created_at"));
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));

        tweet.userId = tweet.user.getId();

        tweet.id = jsonObject.getLong("id");
        tweet.liked = jsonObject.getBoolean("favorited");
        tweet.likeCount = jsonObject.getString("favorite_count");

        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.retweetCount = jsonObject.getString("retweet_count");

        tweet.mediaUrl = "";
        if (jsonObject.getJSONObject("entities").has("media")){
                JSONArray media = jsonObject.getJSONObject("entities").getJSONArray("media");
                if (media.length() > 0) {
                    tweet.mediaUrl = media.getJSONObject(0).getString("media_url_https");
                }
        }

        return tweet;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);


            try {
                long time = sf.parse(rawJsonDate).getTime();
                long now = System.currentTimeMillis();

                final long diff = time - now;
                if (diff < MINUTE_MILLIS) {
                    return "just now";
                } else if (diff < 2 * MINUTE_MILLIS) {
                    return "a minute ago";
                } else if (diff < 50 * MINUTE_MILLIS) {
                    return diff / MINUTE_MILLIS + " m";
                } else if (diff < 90 * MINUTE_MILLIS) {
                    return "an hour ago";
                } else if (diff < 24 * HOUR_MILLIS) {
                    return diff / HOUR_MILLIS + " h";
                } else if (diff < 48 * HOUR_MILLIS) {
                    return "yesterday";
                } else {
                    return diff / DAY_MILLIS + " d";
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return "";
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setRetweetCount(String retweetCount) {
        this.retweetCount = retweetCount;
    }

    public void setId(long tweetId) {
        this.id = tweetId;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public long getUserId() {
        return userId;
    }

    public String getRetweetCount() {
        return retweetCount;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public long getId() {
        return id;
    }

    public boolean isLiked() {
        return liked;
    }

    public String getLikeCount() {
        return likeCount;
    }
}
