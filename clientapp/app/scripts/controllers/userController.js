/// <reference path=".././../../typings/index.d.ts" />
'use strict';

angular.module('clientApp')
.controller('UserController',['UserService', '$location',function(userService, $location){
    var self = this;
    self.login = function(){
        userService.login(self.user).then(function(){  
            $location.path("/dashboard");
        },function(){
            self.errorMessage = userService.error;
        });
    };
}]);