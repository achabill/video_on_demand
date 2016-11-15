/// <reference path=".././../../typings/index.d.ts" />
'use strict';

angular.module('vodadminApp')
.controller('MoviesController',['UserService', '$location',function(userService, $location){
    self = this;
        
    self.user = userService.user;
    if(self.user == null)
        $location.path("/");

    self.getAllMovies = function(){
            
    };
    self.postMovie = function(){

    };
    self.deleteAllMovies = function(){

    };
    self.getMoviebyId = function(id){

    };
    self.deleteMovieybyId = function(id){

    };
    self.getAllMovieComments = function(id){

    };
    self.deleteAllMovieComments = function(id){

    };
    self.getCommentbyId = function(movieId, commentId){

    };
    self.deleteCommentbyId = function(movieId, commentId){

    };
        
}]);