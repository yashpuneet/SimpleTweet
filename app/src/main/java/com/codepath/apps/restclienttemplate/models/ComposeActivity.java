package com.codepath.apps.restclienttemplate.models;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGTH = 280;
    public static final String TAG = "ComposeActivity";

    int numChars;

    EditText etCompose;
    Button btnTweet;
    TextView tvCharCount;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvCharCount = findViewById(R.id.tvCharCount);

        tvCharCount.setText("Characters Remaining: " + String.valueOf(MAX_TWEET_LENGTH));

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                numChars = MAX_TWEET_LENGTH - charSequence.length();

                tvCharCount.setText("Characters Remaining: " + String.valueOf(numChars));

                if (numChars < 0)
                {
                    tvCharCount.setTextColor(Color.RED);
                    btnTweet.setEnabled(false);
                }
                else
                {
                    tvCharCount.setTextColor(Color.GRAY);
                    btnTweet.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        client = TwitterApp.getRestClient(this);

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty())
                {
                    Toast.makeText(ComposeActivity.this, "Tweet cannot be empty", Toast.LENGTH_LONG).show();
                }
                if(tweetContent.length() > MAX_TWEET_LENGTH)
                {
                    Toast.makeText(ComposeActivity.this, "Tweet is too long", Toast.LENGTH_LONG).show();
                }
                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();

                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "Published Tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet.body);

                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "Failed to publish tweet", throwable);
                    }
                });
            }
        });
    }
}