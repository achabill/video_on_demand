/// <reference path="../../../typings/index.d.ts" />

'use strict';

angular.module('vodadminApp')
.factory('MoviesService',['$http', '$q','UserService',function($http, $q, userService){
    var baseEndPoint = 'http://localhost:8080/admin/movies';
    var service =  {
        accesstoken: userService.accesstoken,
        error: null,
        getAllMovies: function(){
            return $http.post(baseEndPoint + '/').then(function(response){
                service.accesstoken = response.data.accessToken.accesstoken;
                service.user = response.data.user;
            }, function(error){
                service.error = error.data.message;
                return $q.reject(error);
            });
        },
        signup: function(user){
            return $http.post(baseEndPoint + '/signup', user).then(function(response){
                service.isLoggedin = true;
                service.accesstoken = response.data.accessToken.accesstoken;
                service.user = response.data.user;
            }, function(error){
                service.error = error.data.message;
                return $q.reject(error);
            });
        },
        logout: function(){
            return $http.get(baseEndPoint + '/logout?accesstoken='+ service.accesstoken).then(function(){},
            function(error){
                service.error = error.data.message;
                console.log(error.data);
                return $q.reject(error);
            });
        }
    };
    return service;
}]);