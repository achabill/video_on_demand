/// <reference path="../../../typings/index.d.ts" />

'use strict';
angular.module('vodadminApp')
.factory('UserInterceptor',['$q','$log', function($q, $log){
    return {
        request: function(config){
            $log.log('Request made with: ', config);
            return config;
        },
        requestError: function( rejection){
            $log.log('Request error due to: ', rejection);
            return $q.reject(rejection);
        },
        response: function(response){
            $log.log('Response from server: ', response);
            return response || $q.when(response);
        },
        responseError: function(rejection){
            $log.log('Rejection: ', rejection);
            return $q.reject(rejection);
        }
    };
}])
.config(['$httpProvider', function($httpProvider){
    $httpProvider.interceptors.push('UserInterceptor');
}]);