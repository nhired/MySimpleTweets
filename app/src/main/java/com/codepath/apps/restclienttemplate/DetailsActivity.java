package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.codepath.apps.restclienttemplate.models.Tweet;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import org.w3c.dom.Text;
import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

//This class allows you to create a details page that will show the users profile image, screename, and tweet
public class DetailsActivity extends AppCompatActivity {
    Tweet tweet;
    TextView tBody;
    TextView tUserName;
    TextView tScreenName;
    ImageView profileImg;
    int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tBody = (TextView) findViewById(R.id.dBody);
        tUserName = (TextView) findViewById(R.id.dName);
        tScreenName = (TextView) findViewById(R.id.dScreenName);
        profileImg = (ImageView) findViewById(R.id.dProfileImg);

        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        //set all instances

        tBody.setText(tweet.body);
        tUserName.setText(tweet.user.name);
        tScreenName.setText(tweet.user.screenName);

        //upload the images
        int radius = 30; // corner radius, higher value = more rounded
        int margin = 10;
        //set Image
        GlideApp.with(this)
                .load(tweet.user.proileImageUrl)
                .fitCenter()
                .override(100, Target.SIZE_ORIGINAL)
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(profileImg);

        //button used to reply to tweets
        Button button = (Button) findViewById(R.id.reply_btn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(getApplicationContext(), ReplyActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                startActivityForResult(intent, 20);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && (requestCode == 50 || requestCode == REQUEST_CODE)) {
            Intent intent = new Intent(DetailsActivity.this, TimelineActivity.class);
            intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
            startActivityForResult(intent,REQUEST_CODE);
        }

    }
}
