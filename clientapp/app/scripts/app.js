'use strict';

/**
 * @ngdoc overview
 * @name clientApp
 * @description
 * # clientApp
 *
 * Main module of the application.
 */
angular
  .module('clientApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainController',
        contorllerAs: 'ctrl'
      })
      .when('/series',{
        templateUrl: 'views/series.html',
        controller: 'SeriesController',
        controllerAs: 'ctrl'
      })
      .when('/aseries',{
        templateUrl: 'views/aseries.html',
        controller: 'ASeriesController',
        controllerAs: 'ctrl'
      })
      .when('/amovie',{
        templateUrl: 'views/amovie.html',
        controller: 'AMovieController',
        controllerAs: 'ctrl'
      })
      .when('/watchseries',{
        templateUrl: 'views/watchseries.html',
        controller: 'WatchSeriesController',
        controllerAs: 'ctrl'
      })
      .when('/watchmovie',{
        templateUrl: 'views/watchmovie.html',
        controller: 'WatchMovieController',
        controllerAs: 'ctrl'
      })
      .when('/movies',{
        templateUrl: 'views/movies.html',
        controller: 'MoviesController',
        controllerAs: 'ctrl'
      })
      .when('/aseason',{
        templateUrl: 'views/aseason.html',
        controller: 'ASeasonController',
        controllerAs: 'ctrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
