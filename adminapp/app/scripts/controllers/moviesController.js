/// <reference path=".././../../typings/index.d.ts" />
'use strict';

angular.module('vodadminApp').controller('MoviesController', ['UserService', 'MoviesService','$location', '$http',
    function (userService, moviesService, $location, $http) {
        self = this;
        self.partial;
        self.user = userService.user;
        self.allGenres = [];
        self.archiveImages = [];
        self.archiveVideos = [];
        self.videoUploadProgresse = 0;
        self.coverimageUploadProgress = 0;

        var _id = 1;
        moviesService.getGenres().then(function (response) {
            angular.forEach(response.data, function (genre) {
                self.allGenres.push({
                    id: _id++,
                    label: genre
                });
            });
        }, function (error) {
        });

        if (self.user == null)
            $location.path("/");

        self.getAllMovies = function () {
            moviesService.getAllMovies().then(function (response) {
                self.response = response.data;
                self.partial = 'get-all-movies';
            }, function (error) {
                self.partial = 'get-all-movies';
                console.log(error.data.message);
            });
        };
        self.postMovie = function () {
            var g = self.newMovie.genre;
            var l = g.label;
            self.newMovie.genre = l;
            self.newMovie.cast = [];

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
                self.partial = 'delete';
                self.response = response.data;
                console.log(error);
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
        self.getCoverImage = function (movie) {
            return moviesService.serveFile(movie.coverimageuuid);
        };
        self.playMovie = function (movie) {
            self.selectedMovie = movie;
            self.playsrc = moviesService.serveFile(movie.videofileuuid);
        };
        self.stopPlayback = function () {
            $('#playerModal').hide();
            $('#playerModal video').attr("src", "null");
        };

        self.getarchiveImages = function(){
            moviesService.getarchiveImages().then(function(response){
                    self.archiveImages = response.data;
                    console.log("archiveimages",self.archiveImages);
            },function (error){
            });
        };
        self.getarchiveVideos = function(){
            moviesService.getarchiveVideos().then(function(response){
                    self.archiveVideos = response.data;
                    console.log("archivevides: ", self.archiveVideos);
            },function (error){
            });
        };

        self.getarchiveImages();
        self.getarchiveVideos();
}]);