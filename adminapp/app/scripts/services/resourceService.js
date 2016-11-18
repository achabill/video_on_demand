/// <reference path="../../../typings/index.d.ts" />

'use strict';

angular.module('vodadminApp')
.factory('ResourceService',['$http', '$q',function($http, $q){
    var baseEndPoint = 'http://localhost:8080/resource?path=';
    var service =  {
        getResource: function(path){
            return baseEndPoint + path;
        }
    };
    return service;
}]);