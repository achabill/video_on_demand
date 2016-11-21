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
            Upload.upload({
                url: 'http://localhost:8080/upload/',
                data: {file: file, accesstoken: userService.accesstoken}
            }).then(function (resp) {
                console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
                return $q.when(resp);
                }, function (resp) {
                console.log('Error status: ' + resp);
                return $q.reject(resp);
                },function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
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