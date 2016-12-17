/// <reference path=".././../../typings/index.d.ts" />
'use strict';

angular.module('clientApp').filter('trusted', ['$sce', function ($sce) {
    return function(url) {
        return $sce.trustAsResourceUrl(url);
    };
}]);