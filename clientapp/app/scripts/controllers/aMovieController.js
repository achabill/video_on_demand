/// <reference path=".././../../typings/index.d.ts" />
'use strict';

angular.module('clientApp').controller('AMovieController', ['UserService', 'MoviesService','$location', '$http',
    function (userService, moviesService, $location, $http) {
        self = this;
        self.movie = moviesService.selectedMovie;
        

        self.getMovieCoverImage = function (movie) {
            console.log('movies cover image');
            return moviesService.serveFile(movie.coverimageuuid);
        };

        self.getSimilarMovies = function(){
            moviesService.getSimilarMovies(self.movie.id).then(function(response){
                self.similarMovies = [];
                var size = 6;
                for(var i = 0; i < response.data.length; i++){
                    if(i < size)
                        self.similarMovies.push(response.data[i]);
                    else
                        break;
                }
            },function(error){
                console.log(error.data.message);
            });
        };


        self.getSimilarMovies();
}]);