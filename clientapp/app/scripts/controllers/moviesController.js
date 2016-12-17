/// <reference path=".././../../typings/index.d.ts" />
'use strict';

angular.module('clientApp').controller('MoviesController', ['UserService', 'MoviesService','$location', '$http',
    function (userService, moviesService, $location, $http) {
        self = this;
        self.partial;
        self.errormessage = null;
        self.user = userService.user;
        

             self.getNewMovies = function () {
            moviesService.getNewMovies().then(function (response) {
               self.newMovies =[];
               for(var i = 0; i < response.data.length; i++)
                self.newMovies.push(response.data[i]);

                console.log('new m',self.newMovies);

            }, function (error) {
                console.log(error.data.message);
            });
        };

        self.getTopRatedMovies = function () {
            moviesService.getTopRatedMovies().then(function (response) {
               self.newMovies =[];
               for(var i = 0; i < response.data.length; i++)
                self.newMovies.push(response.data[i]);

            }, function (error) {
                console.log(error.data.message);
            });
        };

        self.getAllMovies = function () {
            moviesService.getAllMovies().then(function (response) {
                self.response = response.data;
                self.partial = 'get-all-movies';
            }, function (error) {
                console.log(error.data.message);

                self.partial = 'error';
                self.errormessage = error.data.message;
            });
        };
        self.getMoviebyId = function () {
            moviesService.getMoviebyId(self.movieId).then(function (response) {
                self.partial = 'get-movie-by-id';
                self.response = response.data;
                console.log("getmovie by id",self.response);
            }, function (error) {
                console.log(error.data.message);
                self.partial = 'error';
                self.errormessage = error.data.message;
            });
        };
        self.getAllMovieComments = function () {
            moviesService.getAllMovieComments(self.movieId).then(function (response) {
                self.partial = 'get-movie-comments';
                self.response = response.data;
            }, function (error) {
                console.log(error.data.message);
                self.partial = 'error';
                self.errormessage = error.data.message;
            });
        };
        self.getCommentbyId = function () {
            moviesService.getCommentbyId(self.movieId, self.commentId).then(function (response) {
                self.partial = 'get-movie-comment-by-id';
                self.response = response.data;
            }, function (error) {
                console.log(error.data.message);
                self.partial = 'error';
                self.errormessage = error.data.message;
            });
        };
        self.setSelectedMovie = function(movie){
            moviesService.selectedMovie = movie;
        };
        
        self.getCoverImage = function (movie) {
            return moviesService.serveFile(movie.coverimageuuid);
        };
        self.playMovie = function (movie) {
            self.selectedMovie = movie;
            return moviesService.serveFile(movie.videouuid);
        };
        self.stopPlayback = function () {
            $('#playerModal').hide();
            $('#playerModal video').attr("src", "null");
        };

        self.getNewMovies();
        self.getTopRatedMovies();
}]);