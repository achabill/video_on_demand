/// <reference path="../../../typings/index.d.ts" />

'use strict';

angular.module('vodadminApp')
.factory('UserService',['$http', '$q',function($http, $q){
    var baseEndPoint = 'http://localhost:8080/users';
    var service =  {
        isLoggedin: false,
        accesstoken: null,
        user: null,
        error: null,
        login: function(user){
            return $http.post(baseEndPoint + '/login',user).then(function(response){
                service.isLoggedin = true;
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