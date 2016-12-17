/// <reference path="../../../typings/index.d.ts" />

'use strict';

angular.module('clientApp').factory('MoviesService', ['$http', '$q', 'UserService', function($http, $q, userService) {
    var baseEndPoint = 'http://localhost:8080/movies';
    var service = {
        selectedMovie: null,
        getAllMovies: function() {
            return $http.get(baseEndPoint + '/').then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },

        getSimilarMovies: function(id){
            return $http.get(baseEndPoint + '/' + id + '/similar').then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getNewMovies: function(){
            return $http.get(baseEndPoint + '/?property=releaseyear&order=desc').then(function(response){
                return $q.when(response);
            }, function(error){
                return $q.reject(error);
            });
        },
        getTopRatedMovies : function(){
            return $http.get(baseEndPoint + '/?property=overallrating&order=desc').then(function(response){
                return $q.when(response);
            },function(error){
                return $q.reject(error);
            });
        },
        getMoviebyId: function(id) {
            return $http.get(baseEndPoint + '/' + id).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getAllMovieComments: function(id) {
            return $http.get(baseEndPoint + '/' + id).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getGenres : function(){
             return $http.get(baseEndPoint + '/genres').then(function(response){
                return $q.when(response);
            },function(error){
                return $q.reject(error);
            })
        },
        serveFile : function(uuid){
            return "http://localhost:8090/archive/document/" + uuid;
        }
    };
    return service;
}]);