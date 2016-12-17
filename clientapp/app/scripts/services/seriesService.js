/// <reference path="../../../typings/index.d.ts" />

'use strict';

angular.module('clientApp').factory('SeriesService', ['$http', '$q', 'UserService', function($http, $q, userService) {
    var baseEndPoint = 'http://localhost:8080/series';
    var service = {
        selectedSeries: null,
        selectedSeason: null,
        selectedEpisode: null,
        
        getAllSeries: function() {
            return $http.get(baseEndPoint + '/').then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getNewSeries: function(){
            return $http.get(baseEndPoint + '/?property=releaseyear&order=desc').then(function(response){
                return $q.when(response);
            },function(error){
                $q.reject(error);
            });
        },

        getTopRatedSeries: function(){
            return $http.get(baseEndPoint + '/?property=overallrating&order=desc').then(function(response){
                return $q.when(response);
            },function(error){
                $q.reject(error);
            });
        },
        getSeriesbyId: function(id) {
            return $http.get(baseEndPoint + '/' + id).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getAllSeriesComments: function(id) {
            return $http.get(baseEndPoint + '/' + id + '/comments').then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        playSeries: function(id){
            return $http.get('http://localhost:8080/series/' + id + '/?play=true').then(function(response){
                return $q.when(response);
            },function(error){
                return $q.reject(error);
            });
        },
        getAllSeasonsComments: function(id) {
            return $http.get(baseEndPoint + '/' + id + '/comments').then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
         getEpisodesId: function(id) {
            return $http.get(baseEndPoint + '/' + id).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
         },

         getSimilarSeries: function(id){
            return $http.get(baseEndPoint + '/' + id + '/similar').then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
         },

        getEpisodebyId: function(id) {
            return $http.get(baseEndPoint + '/' + id).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getAllEpisodeComments: function(id) {
            return $http.get(baseEndPoint + '/' + id + '/comments').then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getAllSeriesSeasons: function(seriesId){
            return $http.get(baseEndPoint + '/' + seriesId + '/seasons').then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            }); 
        },
        serveFile : function(uuid){
            return "http://localhost:8090/archive/document/" + uuid;
        },
        getSeasonById: function(seriesId, seasonId){
            return $http.get(baseEndPoint + '/' + seriesId + '/seasons/' + seasonId ).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            }); 
        },
        getAllSeasonEpisodes: function(seriesId, seasonId){
            return $http.get(baseEndPoint + '/' + seriesId + '/seasons/' + seasonId + '/episodes').then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            }); 
        },
        getSeasonEpisodeById: function(seriesId, seasonId, episodeId){
            return $http.get(baseEndPoint + '/' + seriesId + '/seasons' + seasonId + '/episodes/' + episodeId).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            }); 
        }
    };
    return service;
}]);