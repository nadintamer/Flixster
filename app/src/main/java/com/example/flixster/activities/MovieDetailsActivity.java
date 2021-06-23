package com.example.flixster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.flixster.R;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;
    TextView textViewTitle;
    TextView textViewOverview;
    RatingBar ratingBarScore;
    ImageView imageViewPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOverview = findViewById(R.id.textViewOverview);
        ratingBarScore = findViewById(R.id.ratingBarScore);
        imageViewPoster = findViewById(R.id.imageViewPoster);

        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));

        textViewTitle.setText(movie.getTitle());
        textViewOverview.setText(movie.getOverview());

        float rating = movie.getVoteAverage().floatValue();
        ratingBarScore.setRating(rating / 2.0f);

        Glide.with(this)
                .load(movie.getBackdropPath())
                .placeholder(R.drawable.flicks_movie_placeholder)
                .into(imageViewPoster);
    }
}