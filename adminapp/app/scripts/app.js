/// <reference path="../../typings/index.d.ts" />
'use strict';

/**
 * @ngdoc overview
 * @name vodadminApp
 * @description
 * # vodadminApp
 *
 * Main module of the application.
 */
angular
  .module('vodadminApp', [
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
        templateUrl: 'views/login.html',
        controller: 'UserController',
        controllerAs: 'ctrl'
      })
      .when('/dashboard', {
        templateUrl: 'views/dashboard.html',
        controller: 'DashboardController',
        controllerAs: 'ctrl'
      })
      .when('/messages',{
         templateUrl: 'views/messages.html'
      })
      .when('/movies',{
        templateUrl: 'views/movies.html',
        controller: 'MoviesController',
        controllerAs: 'ctrl'
      })
      .when('/series',{
        templateUrl: '/views/series.html'
      })
      .when('/login',{
        templateUrl: '/views/login.html'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
