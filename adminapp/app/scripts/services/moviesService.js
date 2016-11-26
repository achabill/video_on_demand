/// <reference path="../../../typings/index.d.ts" />

'use strict';

angular.module('vodadminApp').factory('MoviesService', ['$http', '$q', 'UserService', function($http, $q, userService) {
    var baseEndPoint = 'http://localhost:8080/movies';
    var adminBaseEndPoint= 'http://localhost:8080/admin/movies';
    var service = {
        getAllMovies: function() {
            return $http.get(baseEndPoint + '/?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        postMovie: function(movie) {
            return $http.post(adminBaseEndPoint + '/' + '?accesstoken=' + userService.accesstoken, movie).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        deleteAllMovies: function() {
            return $http.delete(adminBaseEndPoint + '/', {params: {'accesstoken':userService.accesstoken}}).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getMoviebyId: function(id) {
            return $http.get(baseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        deleteMovieybyId: function(id) {
            return $http.delete(adminBaseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getAllMovieComments: function(id) {
            return $http.get(baseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        deleteAllMovieComments: function(id) {
            return $http.delete(adminBaseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getCommentbyId: function(movieId, commentId) {
            return $http.get(baseEndPoint + '/' + movieId + '/' + commentId + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        deleteCommentbyId: function(movieId, commentId) {
            return $http.delete(adminBaseEndPoint + '/' + movieId + '/' + commentId + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getGenres : function(){
             return $http.get(baseEndPoint + '/genres?accesstoken=' + userService.accesstoken).then(function(response){
                return $q.when(response);
            },function(error){
                return $q.reject(error);
            })
        },
        serveCoverImage : function(id){
            return baseEndPoint + '/' + id +'/coverimage?accesstoken=' + userService.accesstoken;
        },
        serveVideoFile: function(id){
             /*return $http.get(baseEndPoint + '/' + id +'/play?accesstoken=' + userService.accesstoken).then(function(response){
                return $q.when(response);
            },function(error){
                return $q.reject(error);
            })
            */
            return baseEndPoint + '/' + id +'/play?accesstoken=' + userService.accesstoken;
        }
    };
    return service;
}]);