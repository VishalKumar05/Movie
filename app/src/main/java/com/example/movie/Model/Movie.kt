package com.example.movie.Model

class Movie() {

    private var movieName:String? = null
    private var thumbnailUrl:String? = null
    private var movieRating:Double = 0.0

    /*init {
        this.movieName = movieName
        this.thumbnailUrl = thumbnailUrl
        this.movieRating = movieRating
    }*/

    constructor(movieName:String, thumbnailUrl:String, movieRating:Double):this(){
        this.movieName = movieName
        this.thumbnailUrl = thumbnailUrl
        this.movieRating = movieRating
    }

    fun getMovieName(): String? {
        return movieName
    }

    fun setMovieName(name: String) {
        this.movieName = name
    }

    fun getThumbnailUrl(): String? {
        return thumbnailUrl
    }

    fun setThumbnailUrl(url: String){
        this.thumbnailUrl = url
    }

    fun getMovieRating():Double{
        return movieRating
    }

    fun setMovieRating(rating:Double){
        this.movieRating = rating
    }

}