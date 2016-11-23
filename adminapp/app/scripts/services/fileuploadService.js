/// <reference path="../../../typings/index.d.ts" />

'use strict';

angular.module('vodadminApp')
.factory('FileUploadService',['$http', '$q','Upload','UserService',function($http, $q, Upload, userService){
    var baseEndPoint = 'http://localhost:8080/files';
    var service =  {
        getAllFiles: function(){
            return $http.get(baseEndPoint + '/?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        uploadFile: function(file){
            return Upload.upload({
                url: baseEndPoint + '/',
                data: {file: file, accesstoken: userService.accesstoken}
            });
        },
        deleteAllFiles: function(){
            return $http.delete(baseEndPoint + '/?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            });
        },
        getFilePath: function(filename){
               return $http.delete(baseEndPoint + '/path' + filename + '/?accesstoken=' + userService.accesstoken).then(function(response) {
                return $q.when(response);
            }, function(error) {
                return $q.reject(error);
            }); 
        }
    };
    return service;
}]);