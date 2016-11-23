/// <reference path="../../../typings/index.d.ts" />

'use strict';

angular.module('vodadminApp').factory('SeriesService', ['$http', '$q', 'UserService', function($http, $q, userService) {
    var baseEndPoint = 'http://localhost:8080/admin/series';
    var service = {
        getAllSeries: function() {
            return $http.get(baseEndPoint + '/?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        postSeries: function(series) {
            console.log('here', series.title);
            return $http.post(baseEndPoint + '/?accesstoken=' + userService.accesstoken, series).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        deleteAllSeries: function() {
            return $http.delete(baseEndPoint + '/' + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getSeriesbyId: function(id) {
            return $http.get(baseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        deleteSeriesybyId: function(id) {
            return $http.delete(baseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getAllSeriesComments: function(id) {
            return $http.get(baseEndPoint + '/' + id + '/comments' + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        deleteAllSeriesComments: function(id) {
            return $http.delete(baseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getCommentbyId: function(seriesId, commentId) {
            return $http.get(baseEndPoint + '/' + seriesId + '/' + commentId + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        deleteCommentbyId: function(seriesId, commentId) {
            return $http.delete(baseEndPoint + '/' + seriesId + '/' + commentId + '?accesstoken=' + userService.accesstoken).then(function(response) {
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
         getSeasonId: function(id) {
            return $http.get(baseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
         },
          postSeason: function(season) {
            console.log('here', season.title);
            return $http.post(baseEndPoint + '/?accesstoken=' + userService.accesstoken, season).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        deleteSeasonbyId: function(id) {
            return $http.delete(baseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getAllSeasonsComments: function(id) {
            return $http.get(baseEndPoint + '/' + id + '/comments' + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        deleteAllSeasonsComments: function(id) {
            return $http.delete(baseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
         getEpisodesId: function(id) {
            return $http.get(baseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
         },
           postEpisode: function(season) {
            console.log('here', season.title);
            return $http.post(baseEndPoint + '/?accesstoken=' + userService.accesstoken, season).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
          deleteAllEpisodes: function() {
            return $http.delete(baseEndPoint + '/' + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getEpisodebyId: function(id) {
            return $http.get(baseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        deleteEpisodebyId: function(id) {
            return $http.delete(baseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getAllEpisodeComments: function(id) {
            return $http.get(baseEndPoint + '/' + id + '/comments' + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        deleteAllEpisodeComments: function(id) {
            return $http.delete(baseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
         deleteEpisodeCommentbyId: function(episodeId, commentId) {
            return $http.delete(baseEndPoint + '/' + episodeId + '/' + commentId + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
    };
    return service;
}]);