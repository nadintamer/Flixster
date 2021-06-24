package com.example.flixster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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
    String videoId = "";
    ActivityMovieDetailsBinding binding;

    public static final String VIDEO_URL = "https://api.themoviedb.org/3/movie/%s/videos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));

        getSupportActionBar().setTitle(movie.getTitle());

        binding.textViewTitle.setText(movie.getTitle());
        binding.textViewDate.setText(movie.getReleaseDate());
        binding.textViewNumVotes.setText(String.format("(%s)", movie.getNumVotes()));
        binding.textViewOverview.setText(movie.getOverview());

        float rating = movie.getVoteAverage().floatValue();
        binding.ratingBarScore.setRating(rating / 2.0f);

        int radius = 30;
        Glide.with(this)
                .load(movie.getBackdropPath())
                .centerCrop()
                .transform(new RoundedCorners(radius))
                .placeholder(R.drawable.flicks_backdrop_placeholder)
                .into(binding.imageViewPoster);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api_key", getString(R.string.moviedb_api_key));

        client.get(String.format(VIDEO_URL, movie.getId()), params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    videoId = Movie.getFirstVideoKey(results);
                    if (!videoId.isEmpty()) {
                        ImageView playButton = findViewById(R.id.imageViewPlay);
                        playButton.setImageResource(R.drawable.ic_play_icon);
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

        binding.imageViewPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Video id", videoId);
                if (videoId.isEmpty()) return;

                Intent i = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                i.putExtra("videoId", videoId);
                startActivity(i);
            }
        });
    }
}