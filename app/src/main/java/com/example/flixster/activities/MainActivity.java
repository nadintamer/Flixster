package com.example.flixster.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.R;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.databinding.ActivityMainBinding;
import com.example.flixster.models.Movie;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing";
    public static final String GENRES_URL = "https://api.themoviedb.org/3/genre/movie/list";
    public static final String TAG = "MainActivity";

    List<Movie> movies;
    ActivityMainBinding binding;
    MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);

        if (savedInstanceState != null) {
            movies = Parcels.unwrap(savedInstanceState.getParcelable("movieData"));
        } else {
            movies = new ArrayList<>();
            fetchMovies();
        }

        movieAdapter = new MovieAdapter(this, movies);

        binding.recyclerViewMovies.setAdapter(movieAdapter);
        binding.recyclerViewMovies.setLayoutManager(new LinearLayoutManager(this));

        fetchGenres();
    }

    void fetchGenres() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api_key", getString(R.string.moviedb_api_key));

        client.get(GENRES_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray genreArray = jsonObject.getJSONArray("genres");
                    Movie.populateGenreMap(genreArray);
                } catch (JSONException e) {
                    Log.d(TAG, "JSON Exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });
    }

    void fetchMovies() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("api_key", getString(R.string.moviedb_api_key));

        client.get(NOW_PLAYING_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    movies.addAll(Movie.fromJsonArray(results));
                    movieAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.d(TAG, "JSON Exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("MainActivity", "onFailure");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && resultCode == RESULT_OK) {
            Integer position = data.getExtras().getInt("position");
            Movie newMovie = Parcels.unwrap(data.getParcelableExtra(Movie.class.getSimpleName()));
            movies.set(position, newMovie);
            movieAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("movieData", Parcels.wrap(movies));
    }
}