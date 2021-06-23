package com.example.flixster.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flixster.R;
import com.example.flixster.activities.MainActivity;
import com.example.flixster.activities.MovieDetailsActivity;
import com.example.flixster.models.Movie;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    Context context;
    List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View movieItemView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieItemView);
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
        TextView textViewTitle;
        TextView textViewOverview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewOverview = itemView.findViewById(R.id.textViewOverview);
            itemView.setOnClickListener(this);
        }

        public void bind(Movie movie) {
            textViewTitle.setText(movie.getTitle());
            textViewOverview.setText(movie.getOverview());
            String imageUrl;
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                imageUrl = movie.getBackdropPath();
            } else {
                imageUrl = movie.getPosterPath();
            }
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.flicks_movie_placeholder)
                    .into(imageViewPoster);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;
            Movie movie = movies.get(position);
            Intent i = new Intent(context, MovieDetailsActivity.class);
            i.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
            context.startActivity(i);
        }
    }
}
