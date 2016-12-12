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
        templateUrl: 'views/main.html'
      })
      .when('/series',{
        templateUrl: 'views/series.html'
      })
      .when('/aseries',{
        templateUrl: 'views/aseries.html'
      })
      .when('/amovie',{
        templateUrl: 'views/amovie.html'
      })
      .when('/movies',{
        templateUrl: 'views/movies.html'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
