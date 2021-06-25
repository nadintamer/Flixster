package com.example.flixster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.R;
import com.example.flixster.databinding.ActivityMovieDetailsBinding;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;
    ActivityMovieDetailsBinding binding;
    Integer position;

    public static final String VIDEO_URL = "https://api.themoviedb.org/3/movie/%s/videos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        // Get movie information from intent
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        position = getIntent().getExtras().getInt("position");

        // Set up interface to display movie information
        binding.textViewTitle.setText(movie.getTitle());
        binding.textViewDate.setText(movie.getFormattedReleaseDate());
        binding.textViewNumVotes.setText(String.format("(%s)", movie.getNumVotes()));
        binding.textViewOverview.setText(movie.getOverview());
        binding.textViewGenres.setText(movie.getGenreString());
        binding.textViewCast.setText(movie.getCastString());

        if (movie.getIsFavorite()) {
            binding.buttonFavorite.setImageResource(R.drawable.ic_heart_filled);
            binding.buttonFavorite.setTag("filled");
        } else {
            binding.buttonFavorite.setImageResource(R.drawable.ic_heart_empty);
            binding.buttonFavorite.setTag("empty");
        }

        float rating = movie.getVoteAverage().floatValue();
        binding.ratingBarScore.setRating(rating / 2.0f);

        int radius = 30;
        int orientation = this.getResources().getConfiguration().orientation;

        RequestBuilder<Drawable> img = Glide.with(this)
                .load(movie.getBackdropPath())
                .centerCrop()
                .placeholder(R.drawable.flicks_backdrop_placeholder);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            img.transform(new RoundedCorners(radius))
                    .into(binding.imageViewPoster);
        } else {
            img.into(binding.imageViewPoster);
        }

        // Fetch video ID to set up YouTube player
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api_key", getString(R.string.moviedb_api_key));

        if (movie.getVideoId().isEmpty()) {
            client.get(String.format(VIDEO_URL, movie.getId()), params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        JSONArray results = jsonObject.getJSONArray("results");
                        movie.setVideoId(Movie.getFirstVideoKey(results));
                        if (!movie.getVideoId().isEmpty()) {
                            binding.imageViewPlay.setImageResource(R.drawable.ic_play_icon);
                        }
                    } catch (JSONException e) {
                        Log.d("MovieDetailsActivity", "JSON Exception", e);
                    }
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.d("MovieDetailsActivity", "onFailure");
                }
            });
        } else { // videoId has already been fetched
            binding.imageViewPlay.setImageResource(R.drawable.ic_play_icon);
        }

        // Play video trailer when poster is clicked
        binding.imageViewPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movie.getVideoId().isEmpty()) return;

                Intent i = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                i.putExtra("videoId", movie.getVideoId());
                startActivity(i);
            }
        });

        binding.buttonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavoriteButton();
                movie.toggleIsFavorite();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        finish();
    }

    // Toggles favorite between empty and filled heart image
    void toggleFavoriteButton() {
        if (binding.buttonFavorite.getTag() == "filled") {
            binding.buttonFavorite.setImageResource(R.drawable.ic_heart_empty);
            binding.buttonFavorite.setTag("empty");
        } else {
            binding.buttonFavorite.setImageResource(R.drawable.ic_heart_filled);
            binding.buttonFavorite.setTag("filled");
        }
    }
}