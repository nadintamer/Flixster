# Project 2 - *Flixster*

**Flixster** shows the latest movies currently playing in theaters. The app utilizes the Movie Database API to display images and basic information about these movies to the user.

Time spent: **13** hours spent in total

## User Stories

The following **required** functionality is completed:

* [X] User can **scroll through current movies** from the Movie Database API
* [X] Display a nice default [placeholder graphic](https://guides.codepath.org/android/Displaying-Images-with-the-Glide-Library#advanced-usage) for each image during loading
* [X] For each movie displayed, user can see the following details:
  * [X] Title, Poster Image, Overview (Portrait mode)
  * [X] Title, Backdrop Image, Overview (Landscape mode)
* [X] Allow user to view details of the movie including ratings within a separate activity

The following **stretch** features are implemented:

* [X] Improved the user interface by experimenting with styling and coloring
  * [X] Added custom toolbar that displays app name and logo
  * [X] Added gradient effect to movie backdrop image in details view
* [X] Apply rounded corners for the poster or background images using [Glide transformations](https://guides.codepath.org/android/Displaying-Images-with-the-Glide-Library#transformations)
* [X] Apply the popular [View Binding annotation library](http://guides.codepath.org/android/Reducing-View-Boilerplate-with-ViewBinding) to reduce boilerplate code
* [X] Allow video trailers to be played in full-screen using the YouTubePlayerView from the details screen

The following **additional** features are implemented:

* [X] Video trailers can be played from the movies list when in landscape mode
* [X] User can mark movies as favorites, and they persist after app restart
* [X] User can view additional information about the movie like release date, genres, and number of votes

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='https://i.imgur.com/arl8Jc7.gif' title='Flixster Portrait Walkthrough' width='' alt='Flixster Portrait Walkthrough' />
<img src='https://i.imgur.com/TkptWAK.gif' title='Flixster Landscape Walkthrough' width='' alt='Flixster Landscape Walkthrough' />

GIF created with [LICEcap](https://www.cockos.com/licecap/).

## Notes

Describe any challenges encountered while building the app.

* While implementing the additional feature of being able to mark movies as favorites, I had some trouble figuring out how to pass data between the two screens when one of them is inside the MovieAdapter and not an Activity. Referring back to my SimpleToDo code and StackOverflow helped me resolve this.
* I found out while implementing the favorites feature that activities are created from scratch when the orientation changes, which led to my changes to the data not persisting between orientation changes. I referred to StackOverflow to fix this issue, and I ended up using saveInstanceState to save and retrieve previous data (if it exists).

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android

## License

    Copyright 2021 Nadin Tamer

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
