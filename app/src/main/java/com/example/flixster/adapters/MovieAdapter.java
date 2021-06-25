package com.example.flixster.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.R;
import com.example.flixster.activities.MovieDetailsActivity;
import com.example.flixster.activities.MovieTrailerActivity;
import com.example.flixster.models.Movie;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    public static final String VIDEO_URL = "https://api.themoviedb.org/3/movie/%s/videos";

    Context context;
    List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageViewPoster;
        ImageView imageViewPlay;
        ImageView imageViewFavorite;
        TextView textViewTitle;
        TextView textViewOverview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
            imageViewPlay = itemView.findViewById(R.id.imageViewPlay);
            imageViewFavorite = itemView.findViewById(R.id.imageViewFavorite);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewOverview = itemView.findViewById(R.id.textViewOverview);
            itemView.setOnClickListener(this);
        }

        public void bind(Movie movie) {
            textViewTitle.setText(movie.getTitle());
            textViewOverview.setText(movie.getOverview());

            // Determine if image should be poster or backdrop, depending on orientation
            String imageUrl;
            int placeholder;
            int orientation = context.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                imageUrl = movie.getBackdropPath();
                placeholder = R.drawable.flicks_backdrop_placeholder;
            } else {
                imageUrl = movie.getPosterPath();
                placeholder = R.drawable.flicks_movie_placeholder;
            }

            // Show heart icon if movie is favorited
            if (movie.getIsFavorite()) {
                imageViewFavorite.setVisibility(View.VISIBLE);
            } else {
                imageViewFavorite.setVisibility(View.INVISIBLE);
            }

            int radius = 30;
            Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .transform(new RoundedCorners(radius))
                    .placeholder(placeholder)
                    .into(imageViewPoster);

            // If in landscape mode, fetch video ID to set up YouTube player
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("api_key", context.getResources().getString(R.string.moviedb_api_key));

                // Only request from API if we haven't previously fetched the video ID
                if (movie.getVideoId().isEmpty()) {
                    client.get(String.format(VIDEO_URL, movie.getId()), params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            JSONObject jsonObject = json.jsonObject;
                            try {
                                JSONArray results = jsonObject.getJSONArray("results");
                                movie.setVideoId(Movie.getFirstVideoKey(results));
                                if (!movie.getVideoId().isEmpty()) {
                                    imageViewPlay.setImageResource(R.drawable.ic_play_icon);
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
                    imageViewPlay.setImageResource(R.drawable.ic_play_icon);
                }

                imageViewPoster.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (movie.getVideoId().isEmpty()) return;

                        Intent i = new Intent(context, MovieTrailerActivity.class);
                        i.putExtra("videoId", movie.getVideoId());
                        context.startActivity(i);
                    }
                });
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;
            Movie movie = movies.get(position);
            Intent i = new Intent(context, MovieDetailsActivity.class);
            i.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
            i.putExtra("position", position);
            ((Activity) context).startActivityForResult(i, 20);
        }
    }
}
