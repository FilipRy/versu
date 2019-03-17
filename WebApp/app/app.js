'use strict';

// Declare app level module which depends on views, and components

var app = angular.module('myApp', [
    'ngRoute',
    'ngCookies',
    'ngMaterial',
    'ngAnimate',
    'ngAria',
    'ngSanitize',
    'myApp.post_detail',
    'myApp.version',
    'infinite-scroll',
    'dbaq.emoji'
]);


app.config(['$locationProvider', '$routeProvider', 'emojiConfigProvider', function ($locationProvider, $routeProvider, emojiConfigProvider) {

    $routeProvider.otherwise({redirectTo: '/view1'});

}]);


window.fbAsyncInit = function () {
    FB.init({
        appId: '223725951340040',
        xfbml: true,
        version: 'v2.7'
    });


};


// Load the SDK asynchronously
(function (d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s);
    js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));
