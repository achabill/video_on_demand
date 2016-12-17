/// <reference path=".././../../typings/index.d.ts" />
'use strict';

angular.module('clientApp').controller('WatchSeriesController', ['UserService', 'SeriesService','$location', '$http',
    function (userService, seriesService, $location, $http) {
        self = this;
        self.episode = seriesService.selectedEpisode;
        self.season = seriesService.selectedSeason;
        self.series = seriesService.selectedSeries;

        console.log('select ep', self.episode);

        self.getOtherEpisodes = function(){
            seriesService.getAllSeasonEpisodes(self.series.id, self.season.id).then(function(response){
                self.episodes = [];
                
                for(var i = 0; i < response.data.length; i++){
                        self.episodes.push(response.data[i]);
                }
            },function(error){
                console.log(error.data.message);
            });
        };

        self.playMovie = function (movie) {
            return seriesService.serveFile(movie.videouuid);
            console.log('mov', movie);
        };

        self.setSelectedEpisode = function(episode){
            seriesService.selectedEpisode = episode;
        }
        self.getOtherEpisodes();
}]);