/// <reference path=".././../../typings/index.d.ts" />
'use strict';

angular.module('clientApp').controller('SeriesController', ['UserService', 'SeriesService', '$location', function (userService, seriesService, $location) {
    self = this;
    self.partial;
    self.user = userService.user;


    self.getTopRatedSeries = function(){
        seriesService.getTopRatedSeries().then(function(response){
            self.topRatedSeries= [];
            for(var i = 0; i < response.data.length; i++)
                self.topRatedSeries.push(response.data[i]);
        });
    };

    self.getNewSeries = function(){
        seriesService.getNewSeries().then(function(response){
            self.newSeries = [];
            for(var i = 0; i < response.data.length; i++)
                self.newSeries.push(response.data[i]);
            console.log('new s', self.newSeries);
        });
    };

    self.setSelectedSeries = function(series){
        console.log('sel s');
        seriesService.selectedSeries = series;
    };
    
    self.getAllSeries = function () {
        seriesService.getAllSeries().then(function (response) {
            self.response = response.data;
            self.partial = 'get-all-series';
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.postSeries = function () {
        seriesService.postSeries(self.newSeries).then(function (response) {
            self.partial = 'post-a-series';
            self.response = response.data;
            self.newSeries= {};
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.deleteAllSeries = function () {
        seriesService.deleteAllSeries().then(function (response) {
            self.partial = 'delete';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.getSeriesbyId = function () {
        seriesService.getSeriesbyId(self.seriesId).then(function (response) {
            self.partial = 'get-series-by-id';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.deleteSeriesbyId = function () {
        seriesService.deleteSeriesbyId(self.seriesId).then(function (response) {
            self.partial = 'delete';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.getAllSeriesComments = function () {
        seriesService.getAllSeriesComments(self.seriesId).then(function (response) {
            self.partial = 'get-series-comments';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.deleteAllSeriesComments = function () {
        seriesSiervice.deleteAllSeriesComments(self.seriesId).then(function (response) {
            self.partial = 'delete';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.getCommentbyId = function () {
        seriesService.getCommentbyId(self.seriesId, self.commentId).then(function (response) {
            self.partial = 'get-series-comment-by-id';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.deleteCommentbyId = function () {
        seriesService.deleteCommentbyId(self.seriesId, self.commentId).then(function (response) {
            self.partial = 'delete';
            self.response = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.getGenres = function () {
        seriesService.getGenres().then(function (response) {
            self.genres = response.data;
        }, function (error) {
            console.log(error.data.message);
        });
    };
    self.getCoverImage = function (movie) {
            return seriesService.serveFile(movie.coverimageuuid);
    };

    self.getTopRatedSeries();
    self.getNewSeries();
}]);