'use strict';

// Declare app level module which depends on views, and components

var app = angular.module('myApp', [
    'ngRoute',
    'ngCookies',
    'ngMaterial',
    'ngMessages',
    'ngAnimate',
    'ngAria',
    'ngSanitize',
    'ui.bootstrap',
    'myApp.post_detail',
    'myApp.main_view',
    'myApp.about_view',
    'myApp.version',
    'infinite-scroll',
    'duScroll'
]);


app.config(['$locationProvider', '$routeProvider', '$mdThemingProvider', function ($locationProvider, $routeProvider, $mdThemingProvider) {

    $routeProvider.otherwise({redirectTo: '/'});

    var customPrimaryWhite = {
        '50': '#ffffff',
        '100': '#ffffff',
        '200': '#ffffff',
        '300': '#ffffff',
        '400': '#ffffff',
        '500': '#ffffff',
        '600': '#f2f2f2',
        '700': '#e6e6e6',
        '800': '#d9d9d9',
        '900': '#cccccc',
        'A100': '#ffffff',
        'A200': '#ffffff',
        'A400': '#ffffff',
        'A700': '#bfbfbf',
        'contrastDefaultColor': 'dark',
        'contrastDarkColors': '50 100 200 300 A100 A200'
    };

    var customPrimaryBlue = {
        '50': '#e0edf4',
        '100': '#a8cce0',
        '200': '#7eb3d1',
        '300': '#4994bf',
        '400': '#3d84ad',
        '500': '#357396',
        '600': '#2d627f',
        '700': '#255069',
        '800': '#1d3f52',
        '900': '#152e3c',
        'A100': '#e0edf4',
        'A200': '#a8cce0',
        'A400': '#3d84ad',
        'A700': '#255069',
        'contrastDefaultColor': 'light',
        'contrastLightColors': '50 100 200 300 A100 A200'
    };

    $mdThemingProvider
        .definePalette('customPrimary',
            customPrimaryBlue);

    $mdThemingProvider.theme('default')
        .primaryPalette('customPrimary')

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