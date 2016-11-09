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
        template: '<p>Welcome to video on Demand on Commufi</p>'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
