package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

// leeloo oAuth lib https://bitbucket.org/smartproject/oauth-2.0/wiki/Home

/**
 * Created with IntelliJ IDEA.
 * Author: Adrian Maurer
 * Date: 1/29/13
 * Time: 7:46 PM
 */
public class MeetupAuthActivity extends AppCompatActivity {
    private final String TAG = MainActivity.TAG;

    // Meetup OAuth Endpoints
    public static final String AUTH_URL = "https://secure.meetup.com/oauth2/authorize";
    public static final String TOKEN_URL = "https://secure.meetup.com/oauth2/access";

    public static final String REDIRECT_URI = "whatshotonstack://auth";
    public static final String CONSUMER_KEY = "na2bv7dnlof5hki09dgsrg9ec3";
    public static final String CONSUMER_SECRET = "38do0if6b2ddvjfhu7cueaflad";

    private WebView webView;
    private Intent intent;
    private Context context;

    Subscription subscribe;

    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        intent = getIntent();
        context = getApplicationContext();

        webView = new WebView(this);
        webView.clearCache(false);
        webView.setWebViewClient(new MyWebViewClient());
        setContentView(webView);

        webView.getSettings().setJavaScriptEnabled(true);
//        OAuthClientRequest request = null;
//        try {
//            request = OAuthClientRequest.authorizationLocation(
//                    AUTH_URL).setClientId(
//                    CONSUMER_KEY).setRedirectURI(
//                    REDIRECT_URI).buildQueryMessage();
//        } catch (OAuthSystemException e) {
//            Log.d(TAG, "OAuth request failed", e);
//        }
//        Log.d(TAG, "onCreate: URI " + request.getLocationUri());
//
//        webView.loadUrl(request.getLocationUri() + "&response_type=code&set_mobile=on");
    }

    @Override
    protected void onDestroy() {

        if (!subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }

        super.onDestroy();
    }

    @Override
    public void finish() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.finish();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Uri uri = Uri.parse(url);

            String code = uri.getQueryParameter("code");
            String error = uri.getQueryParameter("error");

            Log.d(TAG, "shouldOverrideUrlLoading: the URL " + url);
            Log.d(TAG, "shouldOverrideUrlLoading: code + " + code);
            if (code != null) {
                if (!url.startsWith(REDIRECT_URI)) {
                    return false;
                }
                Log.d(TAG, "shouldOverrideUrlLoading: firing the observable");
                subscribe = getOAuthTokensObservable(uri)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<HashMap<String, String>>() {
                            @Override
                            public void onCompleted() {
                                setResult(RESULT_OK, intent);
                                finish();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: there was an error!!!");
                            }

                            @Override
                            public void onNext(HashMap<String, String> tokens) {
                                Log.d(TAG, "onNext: " + tokens.toString());
                                    intent.putExtra("access_token", tokens.get("access_token"));
                                    intent.putExtra("refresh_token",tokens.get("refresh_token"));
                                    intent.putExtra("expires_in",tokens.get("expires_ib"));
                            }
                        });
                return true;
            } else if (error != null) {
                setResult(RESULT_CANCELED, intent);
                finish();
                return true;
            }

            return false;
        }

    }

    protected HashMap<String,String> getOAuthTokens(Uri uri) {

        String code = uri.getQueryParameter("code");
        HashMap<String,String> tokens = new HashMap<>();

        try {

            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("client_secret",CONSUMER_SECRET)
                    .add("redirect_uri",REDIRECT_URI)
                    .add("client_id",CONSUMER_KEY)
                    .add("code",code)
                    .add("grant_type","authorization_code")
                    .build();

            Request request = new Request.Builder()
                    .url(TOKEN_URL)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                Toast.makeText(context, "Error Getting Token", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
            }

            JSONObject json = new JSONObject(response.body().string());
            tokens.put("access_token",json.getString("access_token"));
            tokens.put("refresh_token",json.getString("refresh_token"));
            tokens.put("expires_in",json.getString("expires_in"));

            Log.d(TAG, "doInBackground: response " + response.body().string());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tokens;
    }

    public Observable<HashMap<String,String>> getOAuthTokensObservable(final Uri uri) {
        return Observable.defer(new Func0<Observable<HashMap<String, String>>>() {
            @Override
            public Observable<HashMap<String, String>> call() {
                return Observable.just(getOAuthTokens(uri));
            }
        });
    }




}
