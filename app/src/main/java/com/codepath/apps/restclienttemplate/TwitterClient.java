package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.codepath.oauth.OAuthTokenClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

import org.json.JSONException;

import okhttp3.Headers;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	private static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Change this
	private static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	private static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;       // Change this inside apikey.properties
	private static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET; // Change this inside apikey.properties


	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	private static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	private static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	@Override
	protected OAuthTokenClient getTokenClient() {
		return super.getTokenClient();
	}

	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getHomeTimeline(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1);
		params.put("tweet_mode", "extended");
		client.get(apiUrl, params, handler);
	}
	public void publishTweet(String tweetContent, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("status", tweetContent);
		client.post(apiUrl, params, "", handler);
	}

	public void processLike(Boolean like, String tweetId, JsonHttpResponseHandler handler){
		// get url corresponding to like or unlike respectively
		String api =  like? "favorites/create.json": "favorites/destroy.json";
		String apiUrl = getApiUrl(api);

		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		client.post(apiUrl, params, "", handler);
	}

	public void me(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		// Can specify query string params directly or through RequestParams.
		client.get(apiUrl, handler);
	}


	public void processRetweet(Boolean retweet, String tweetId,  JsonHttpResponseHandler handler) {
		// select api based on retweet action (remove or add retweet)
		String api = retweet? String.format("statuses/retweet/%s.json", tweetId):
				String.format("/statuses/unretweet/%s.json", tweetId);

		String apiUrl = getApiUrl(api);

		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("tweet_id", tweetId);
		client.post(apiUrl, params, "", handler);
	}
}
