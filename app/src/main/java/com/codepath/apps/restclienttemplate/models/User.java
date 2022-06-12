package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity
public class User {
    private final static String AT = "@";

    @ColumnInfo
    @PrimaryKey
    private long id;

    @ColumnInfo
    private String userName;

    @ColumnInfo
    private String screenName;

    @ColumnInfo
    private String profileImageUrl;

    @ColumnInfo
    private boolean isVerified;

    // empty constructor needed by the Parceler library
    public User() {}

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.id = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("name");
        user.userName = AT + jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        user.isVerified = jsonObject.getBoolean("verified");
        return user;
    }

    public static List<User> fromJsonTweetArray(List<Tweet> tweetsFromNetwork) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < tweetsFromNetwork.size(); i++) {
            users.add(tweetsFromNetwork.get(i).getUser());
        }
        return users;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public String getUserName() {
        return userName;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
