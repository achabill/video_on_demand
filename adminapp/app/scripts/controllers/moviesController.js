/// <reference path=".././../../typings/index.d.ts" />
'use strict';

angular.module('vodadminApp').controller('MoviesController', ['UserService', 'MoviesService', '$location', function (userService, moviesService, $location) {
    self = this;
    self.partial;
    self.user = userService.user;

    if (self.user == null)
        $location.path("/");

    self.getAllMovies = function () {
        moviesService.getAllMovies().then(function (response) {
            self.response = response.data;
            self.partial = 'get-all-movies';
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.postMovie = function () {
        var date = self.newMovie.releaseyear;
        date = date.toString().replace('/','-');
        self.newMovie.releaseyear = date;
        var year = self.newMovie.releaseyear.date.getUTCFullYear();
        self.newMovie.releaseyear = year;
        console.log(self.newMovie);
        moviesService.postMovie(self.newMovie).then(function (response) {
            self.partial = 'post-a-movie';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.deleteAllMovies = function () {
        moviesService.deleteAllMovies().then(function (response) {
            self.partial = 'delete';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.getMoviebyId = function () {
        moviesService.getMoviebyId(self.movieId).then(function (response) {
            self.partial = 'get-movie-by-id';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.deleteMovieybyId = function () {
        moviesService.deleteMovieybyId(self.movieId).then(function (response) {
            self.partial = 'delete';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.getAllMovieComments = function () {
        moviesService.getAllMovieComments(self.movieId).then(function (response) {
            self.partial = 'get-movie-comments';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.deleteAllMovieComments = function () {
        moviesService.deleteAllMovieComments(self.movieId).then(function (response) {
            self.partial = 'delete';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.getCommentbyId = function () {
        moviesService.getCommentbyId(self.movieId, self.commentId).then(function (response) {
            self.partial = 'get-movie-comment-by-id';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.deleteCommentbyId = function () {
        moviesService.deleteCommentbyId(self.movieId, self.commentId).then(function (response) {
            self.partial = 'delete';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
}]);