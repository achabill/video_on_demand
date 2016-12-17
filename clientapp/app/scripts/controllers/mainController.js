/// <reference path=".././../../typings/index.d.ts" />
'use strict';

angular.module('clientApp').controller('MainController', ['UserService', 'MoviesService','SeriesService','$location', '$http',
    function(UserService, moviesService, seriesService, $location, $http){
        self = this;

        self.getNewMovies = function(){
            var size = 6;   //the number of movies to get
            moviesService.getNewMovies().then(function(response){
                self.newMovies = [];
                
                for(var i = 0; i < response.data.length; i++){
                    if(i < size)
                        self.newMovies.push(response.data[i]);
                    else
                        break;
                }
                console.log('new moveis.length', self.newMovies.length);
            }, function(error){
                console.log(error.data.message);
            });
        };

        self.setSelectedMovie = function(movie){
            console.log('set selected movie');
            moviesService.selectedMovie = movie;
            console.log('selected movie',moviesService.selectedMovie);
        };

        self.setSelectedSeries = function(series){
            seriesService.selectedSeries = series;
        }

        self.getTopRatedMovies = function(){
           
            var size = 6;
            moviesService.getTopRatedMovies().then(function(response){
                 self.topRatedMovies = [];
                
                for(var i = 0; i < response.data.length; i++){
                    if(i < size)
                        self.topRatedMovies.push(response.data[i]);
                    else
                        break;
                }
            }, function(error){
                console.log(error.data.message);
            });
        };
        self.getNewSeries = function(){
           
            var size = 6;   //the number of movies to get
            seriesService.getNewSeries().then(function(response){
                 self.newSeries = [];
               
                for(var i = 0; i < response.data.length; i++){
                    if(i < size)
                        self.newSeries.push(response.data[i]);
                    else
                        break;
                }
            }, function(error){
                console.log(error.data.message);
            });
        };

        self.getTopRatedSeries = function(){
            var size = 6;
            
            seriesService.getTopRatedSeries().then(function(response){
                self.topRatedSeries = [];
               
                for(var i = 0; i < response.data.length; i++){
                    if(i < size)
                        self.topRatedSeries.push(response.data[i]);
                    else
                        break;
                }
            }, function(error){
                console.log(error.data.message);
            });
        };

        self.getMovieCoverImage = function (movie) {
            console.log('movies cover image');
            return moviesService.serveFile(movie.coverimageuuid);
        };
        
        self.getSeriesCoverImage = function (series) {
            console.log('series cover image');
             return moviesService.serveFile(series.coverimageuuid);
        };

        self.getNewMovies();
        self.getTopRatedMovies();
        self.getNewSeries();
        self.getTopRatedSeries();
}]);