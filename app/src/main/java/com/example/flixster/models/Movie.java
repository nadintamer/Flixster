package com.example.flixster.models;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

@Parcel
public class Movie {
    String posterPath;
    String backdropPath;
    String title;
    String overview;
    String releaseDate;
    Double voteAverage;
    String videoId = "";
    Integer id;
    Integer numVotes;
    List<String> genres;
    List<String> cast;
    Boolean isFavorite;
    static Map<Integer, String> genreMap = new HashMap<>();
    static ArrayList<String> favorites = new ArrayList<>();

    public static final int REDUCED_CAST_COUNT = 4;

    public Movie() {}

    // Constructs a Movie object from a JSONObject
    public Movie(JSONObject jsonObject) throws JSONException {
        posterPath = jsonObject.getString("poster_path");
        backdropPath = jsonObject.getString("backdrop_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        voteAverage = jsonObject.getDouble("vote_average");
        releaseDate = jsonObject.getString("release_date");
        id = jsonObject.getInt("id");
        numVotes = jsonObject.getInt("vote_count");
        cast = new ArrayList<>();

        // Convert genre IDs into String representations
        genres = new ArrayList<>();
        JSONArray genreIds = jsonObject.getJSONArray("genre_ids");
        for (int i = 0; i < genreIds.length(); i++) {
            String genre = genreMap.get(genreIds.get(i));
            genres.add(genre);
        }

        // Check if this movie was previously marked as a favorite
        isFavorite = favorites.contains(title);
    }

    // Constructs a list of Movie objects given a JSONArray
    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }

    // Returns the key for the first video that is hosted on YouTube, if any
    public static String getFirstVideoKey(JSONArray videoArray) throws JSONException {
        if (videoArray.length() == 0) return "";
        for (int i = 0; i < videoArray.length(); i++) {
            JSONObject obj = videoArray.getJSONObject(i);
            // Ignore videos from sites that aren't YouTube since they won't work with player
            if (obj.has("site") && !obj.getString("site").equals("YouTube")) {
                continue;
            }
            if (obj.has("key")) {
                return obj.getString("key");
            }
        }
        return "";
    }

    // Populates the genre map with IDs and their string representations
    public static void populateGenreMap(JSONArray genreArray) throws JSONException {
        for (int i = 0; i < genreArray.length(); i++) {
            JSONObject obj = genreArray.getJSONObject(i);
            genreMap.put(obj.getInt("id"), obj.getString("name"));
        }
    }

    public void addCast(JSONArray castArray) throws JSONException {
        for (int i = 0; i < REDUCED_CAST_COUNT; i++) {
            JSONObject obj = castArray.getJSONObject(i);
            cast.add(obj.getString("name"));
        }
    }

    public void toggleIsFavorite() {
        isFavorite = !isFavorite;
    }

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath);
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    // Formats the movie release date to be more readable, e.g. 2021-06-17 becomes June 17, 2021
    public String getFormattedReleaseDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date result =  df.parse(releaseDate);
            SimpleDateFormat written = new SimpleDateFormat("MMMM d, yyyy");
            return written.format(result);
        } catch (ParseException e) {
            Log.d("Movie", "Error parsing date string", e);
        }
        return releaseDate; // return raw date if formatting fails
    }

    public Integer getId() {
        return id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String id) {
        videoId = id;
    }

    public Integer getNumVotes() { return numVotes; }

    public String getGenreString() {
        return String.format("Genres: %s", TextUtils.join(", ", genres));
    }

    public String getCastString() {
        return String.format("Cast: %s, and more", TextUtils.join(", ", cast));
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public static void setFavorites(ArrayList<String> newFavorites) {
        favorites = newFavorites;
    }

    public static ArrayList<String> getFavorites() {
        return favorites;
    }

    public static void addToFavorites(String title) {
        favorites.add(title);
    }

    public static void removeFromFavorites(String title) {
        if (!favorites.contains(title)) return;
        favorites.remove(title);
    }
}
