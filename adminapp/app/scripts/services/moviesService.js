/// <reference path="../../../typings/index.d.ts" />

'use strict';

angular.module('vodadminApp').factory('MoviesService', ['$http', '$q', 'UserService', function($http, $q, userService) {
    var baseEndPoint = 'http://localhost:8080/admin/movies';
    var service = {
        getAllMovies: function() {
            return $http.get(baseEndPoint + '/?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        postMovie: function(movie) {
            console.log('here', movie.title);
            return $http.post(baseEndPoint + '/', movie + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        deleteAllMovies: function() {
            return $http.delete(baseEndPoint + '/' + '?accesstoken=' + userService.accesstoken).then(function(response) {
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
            return $http.delete(baseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
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
            return $http.delete(baseEndPoint + '/' + id + '?accesstoken=' + userService.accesstoken).then(function(response) {
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
            return $http.delete(baseEndPoint + '/' + movieId + '/' + commentId + '?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        playMovie: function(id){
            return $http.get('http://localhost:8080/movies/' + id + '/?play=true').then(function(response){
                return $q.when(response);
            },function(error){
                return $q.reject(error);
            })
        }
    };
    return service;
}]);