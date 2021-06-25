package com.example.flixster.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import org.apache.commons.io.FileUtils;
import android.util.Log;
import android.view.View;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.R;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.databinding.ActivityMainBinding;
import com.example.flixster.models.Movie;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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

        loadFavorites();
        fetchGenres();

        // Check if movie state has already been saved, otherwise fetch movies from API
        if (savedInstanceState != null) {
            movies = Parcels.unwrap(savedInstanceState.getParcelable("movieData"));
        } else {
            movies = new ArrayList<>();
            fetchMovies();
        }

        movieAdapter = new MovieAdapter(this, movies);

        binding.recyclerViewMovies.setAdapter(movieAdapter);
        binding.recyclerViewMovies.setLayoutManager(new LinearLayoutManager(this));
    }

    // Fetch list of movie genres from API and populate genre map
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

    // Fetch list of currently playing movies from API
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

    // When returning from movie details view, update data with new favorite status
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && resultCode == RESULT_OK) {
            int position = data.getExtras().getInt("position");
            Movie newMovie = Parcels.unwrap(data.getParcelableExtra(Movie.class.getSimpleName()));
            String title = newMovie.getTitle();
            if (newMovie.getIsFavorite()) {
                Movie.addToFavorites(title);
            } else {
                Movie.removeFromFavorites(title);
            }
            saveFavorites();
            movies.set(position, newMovie);
            movieAdapter.notifyItemChanged(position);
        }
    }

    // Code for this function was adapted from: https://stackoverflow.com/questions/44581911/saving-recyclerview-list-state
    // Save movie state so that it persists between orientation changes
    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("movieData", Parcels.wrap(movies));
    }

    // Code to save list of favorites so that they persist after app restart
    private File getDataFile() {
        return new File(getFilesDir(), "favorites.txt");
    }

    private void loadFavorites() {
        try {
            Movie.setFavorites(new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset())));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading favorites", e);
            Movie.setFavorites(new ArrayList<>());
        }
    }

    private void saveFavorites() {
        try {
            FileUtils.writeLines(getDataFile(), Movie.getFavorites());
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing favorites", e);
        }
    }
}