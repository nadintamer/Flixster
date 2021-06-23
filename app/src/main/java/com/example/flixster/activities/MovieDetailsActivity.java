package com.example.flixster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.flixster.R;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;
    TextView textViewTitle;
    TextView textViewOverview;
    TextView textViewDate;
    RatingBar ratingBarScore;
    ImageView imageViewPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOverview = findViewById(R.id.textViewOverview);
        textViewDate = findViewById(R.id.textViewDate);
        ratingBarScore = findViewById(R.id.ratingBarScore);
        imageViewPoster = findViewById(R.id.imageViewPoster);

        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));

        getSupportActionBar().setTitle(movie.getTitle());

        textViewTitle.setText(movie.getTitle());
        textViewDate.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverview());
        textViewOverview.setMovementMethod(new ScrollingMovementMethod());

        float rating = movie.getVoteAverage().floatValue();
        ratingBarScore.setRating(rating / 2.0f);

        int radius = 30;
        Glide.with(this)
                .load(movie.getBackdropPath())
                .centerCrop()
                .transform(new RoundedCorners(radius))
                .placeholder(R.drawable.flicks_backdrop_placeholder)
                .into(imageViewPoster);
    }
}