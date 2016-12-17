/// <reference path=".././../../typings/index.d.ts" />
'use strict';

angular.module('clientApp').controller('WatchMovieController', ['UserService', 'MoviesService','$location', '$http',
    function (userService, moviesService, $location, $http) {
        self = this;
        self.movie = moviesService.selectedMovie;
        

        self.getMovieCoverImage = function (movie) {
            console.log('movies cover image');
            return moviesService.serveFile(movie.coverimageuuid);
        };

        self.getOtherMovies = function(){
            moviesService.getAllMovies().then(function(response){
                self.otherMovies = [];
                var size = 6;
                for(var i = 0; i < response.data.length; i++){
                    console.log('res', response.data);
                    if(i < size)
                        self.otherMovies.push(response.data[i]);
                    else
                        break;
                }
            },function(error){
                console.log(error.data.message);
            });
        };

        self.playMovie = function (movie) {
            self.selectedMovie = movie;
            return moviesService.serveFile(movie.videouuid);
            console.log('move move', movie);
        };

        self.setSelectedMovie = function(movie){
            moviesService.selectedMovie = movie;
        }

        self.getOtherMovies();
}]);