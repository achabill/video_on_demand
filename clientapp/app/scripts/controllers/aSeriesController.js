/// <reference path=".././../../typings/index.d.ts" />
'use strict';

angular.module('clientApp').controller('ASeriesController', ['UserService', 'SeriesService','$location', '$http',
    function (userService, seriesService, $location, $http) {
        self = this;
        self.series = seriesService.selectedSeries;
        

            self.getSeriesCoverImage = function (series) {
            console.log('series cover image');
             return seriesService.serveFile(series.coverimageuuid);
        };

        self.setSelectedSeason = function(season){
            seriesService.selectedSeason = season;
        }
        self.getSimiarSeries = function(){
            seriesService.getSimilarSeries(self.series.id).then(function(response){
                self.similarSeries = [];
                var size = 6;
                for(var i = 0; i < response.data.length; i++){
                    if(i < size)
                        self.similarSeries.push(response.data[i]);
                    else
                        break;
                }
            },function(error){
                console.log(error.data.message);
            });
        };

        self.getAllSeriesSeasons = function(){
            seriesService.getAllSeriesSeasons(self.series.id).then(function(response){
                self.seasons = [];
                for(var i = 0; i < response.data.length; i++)
                    self.seasons.push(response.data[i]);
            });
        };

        self.getSimilarSeries = function(){
            seriesService.getSimilarSeries(self.series.id).then(function(response){
                self.similarSeries =[];
                for(var i = 0; i < response.data.length; i++)
                    self.similarSeries.push(response.data[i]);
            },function(error){
                console.log(error.data.message);
            });
        };

        
        self.getSimilarSeries();
        self.getAllSeriesSeasons();
}]);