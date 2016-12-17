/// <reference path=".././../../typings/index.d.ts" />
'use strict';

angular.module('clientApp').controller('ASeasonController', ['UserService', 'SeriesService','$location', '$http',
    function (userService, seriesService, $location, $http) {
        self = this;
        self.season = seriesService.selectedSeason;
        self.series = seriesService.selectedSeries;

        
            self.getSeriesCoverImage = function (series) {
            console.log('series cover image');
             return seriesService.serveFile(series.coverimageuuid);
            };

        self.getSeasonEpisodes = function(){
            seriesService.getAllSeasonEpisodes(self.series.id, self.season.id).then(function(response){
                self.episodes = [];
                for(var i = 0; i < response.data.length; i++){
                    self.episodes.push(response.data[i]);
                }
            },function(error){
                console.log(error.data.message);
            });
        };

        self.setSelectedEpisode = function(episode){
            seriesService.selectedEpisode = episode;
        }

        self.getSeasonEpisodes();
}]);