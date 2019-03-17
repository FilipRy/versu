/**
 * Created by Filip on 10/3/2016.
 */

'use strict';

angular.module('myApp.main_view', ['ngRoute'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/', {
            templateUrl: 'main/main.html',
            controller: 'mainCtrl'
        });
    }])
    .controller('mainCtrl', ['$scope', '$log', '$mdSidenav', 'joinAppService', '$mdDialog', '$document', '$location',
        function ($scope, $log, $mdSidenav, joinAppService, $mdDialog, $document, $location) {

            $scope.sections = [{'name': 'Home', 'link': '/#/'},
                                {'name': 'About', 'link': '/#/'}];

            $scope.toggleLeft = buildToggler('left');

            $scope.user = {};


            $scope.scrollTo = function(id) {
                var joinBetaElement = angular.element(document.getElementById(id));
                $document.scrollToElement(joinBetaElement, 40, 1000);
                $location.hash(id);
            };

            $scope.joinApp = function () {
                joinAppService.joinApp($scope.user,
                    function (data, status, headers, config) {

                        $scope.user = {};

                        displayAlertDialog('Success', 'You are almost there! To complete the subscription process, please click the link in the email we just sent you.');
                    }, function (data, status) {
                        displayAlertDialog('Opps!', 'Something went wrong, recheck if you entered a valid email...');
                    }
                )
            };

            function buildToggler(componentId) {
                return function () {
                    $mdSidenav(componentId).open();
                }
            }

            function displayAlertDialog(title, msg) {
                // Appending dialog to document.body to cover sidenav in docs app
                // Modal dialogs should fully cover application
                // to prevent interaction outside of dialog
                $mdDialog.show(
                    $mdDialog.alert()
                        .parent(angular.element(document.querySelector('#popupContainer')))
                        .clickOutsideToClose(false)
                        .title(title)
                        .textContent(msg)
                        .ok('Got it!')
                );
            };

        }]);