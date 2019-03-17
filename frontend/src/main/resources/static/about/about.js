/**
 * Created by Filip on 10/6/2016.
 */


'use strict';

angular.module('myApp.about_view', ['ngRoute'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/about', {
            templateUrl: 'about/about.html',
            controller: 'aboutCtrl'
        });
    }])
    .controller('aboutCtrl', ['$scope', '$log', '$mdSidenav', function ($scope, $log, $mdSidenav) {



    }]);