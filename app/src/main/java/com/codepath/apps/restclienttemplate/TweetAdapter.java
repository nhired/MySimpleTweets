package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    private List<Tweet> mTweets;
    Context context;


    //pass in tweets array into constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }


    //for each row, inflate layout and cache references into viewholder

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);




        return viewHolder;
    }


    //bind values based on position of element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get data according to position
        Tweet tweet = mTweets.get(position);

        //populate the views according to data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvScreenName.setText(tweet.user.screenName);
        holder.tvBody.setText(tweet.body);
        holder.tvDate.setText(ParseRelativeDate.getRelativeTimeAgo(tweet.createdAt));
        int radius = 30; // corner radius, higher value = more rounded
        int margin = 0;
        //set Image

        GlideApp.with(context)
                .load(tweet.user.proileImageUrl)
                .fitCenter()
                .override(100, Target.SIZE_ORIGINAL)
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(holder.ivProfileImage);
    }


    //create viewholder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Nullable @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvUserName) TextView tvUsername;
        @BindView(R.id.tvBody) TextView tvBody;
        @BindView(R.id.tvDate) TextView tvDate;
        @BindView(R.id.screenName) TextView tvScreenName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //lookup view object by id
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Tweet tweet = mTweets.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, DetailsActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                // show the activity
                context.startActivity(intent);

            }
        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }


}
