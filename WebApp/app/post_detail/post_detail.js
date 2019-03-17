'use strict';

angular.module('myApp.post_detail', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/p/:id', {
            templateUrl: 'post_detail/post_detail.html',
            controller: 'postDetailCtrl'
        });
    }])
    .controller('postDetailCtrl', ['$scope', '$routeParams', '$http', 'authFact', 'VotersPagingFact', '$cookieStore',
        '$mdDialog', 'userPassingService', 'userService', 'postService', 'postFeedbackService', 'commentService',
        '$mdToast',

        function ($scope, $routeParams, $http, authFact, VotersPagingFact, $cookieStore,
                  $mdDialog, userPassingService, userService, postService, postFeedbackService, commentService,
                  $mdToast) {

            $scope.loggedInUser = undefined;

            $scope.post = {};
            $scope.loadingComments = false;
            $scope.comments = {};
            $scope.possibiliesHashtag = {};
            $scope.postTime = {};

            $scope.myNewComment = {};

            $scope.isAuthenticated = false;
            $scope.isLoadingUser = false;
            $scope.isLoadingPost = true;

            $scope.postLoadingError = false;
            $scope.postErrorMessage = '';

            /**
             * These variables are used by VotersDialogController to decide which post + possibility is beeing displayed.
             */
            var votersDialogPossibilityIndex = 0;
            var votersDialogPost = {};


            var user = authFact.getuserObj();
            var accessToken = authFact.getAccessToken();

            if (accessToken != undefined) {
                checkAccessToken();
            } else {
                displayWelcomeDialog();
                retrievePostFromBackend(undefined);
            }


            $scope.login = function () {
                displayWelcomeDialog();
            }

            $scope.logout = function () {
                logout();
            };

            $scope.loadMoreComments = function () {
                loadMoreComments();
            };

            $scope.createComment = function () {
                createComment();
            };

            $scope.removeComment = function (index) {
                console.log('removing comment at index: ' + index + ': ' + $scope.comments[index].content);
                removeComment(index);
            };

            $scope.vote = function (possibilityIndex) {

                if ($scope.post.myPostFeedback == null ||
                    $scope.post.myPostFeedback.name != $scope.post.feedbackPossibilities[possibilityIndex]) {//creating new postFeedback

                    createVoteAtPost(possibilityIndex);

                } else {
                    if ($scope.post.myPostFeedback.id != null) {//deleting post

                        deleteVoteAtPost(possibilityIndex);
                    }
                }
            };


            $scope.displayVoters = function (possibilityIndex) {
                displayPossibilityVotersDialog(possibilityIndex);
            };


            function retrievePostFromBackend(accessToken) {
                $scope.isLoadingPost = true;
                $scope.postLoadingError = false;

                postService.getDetails(accessToken,
                    function (data) {
                        $scope.isLoadingPost = false;
                        $scope.post = data;
                        processRetrievedPost();
                    }, function (data, status) {
                        $scope.isLoadingPost = false;
                        if (status == 401) {
                            $scope.postErrorMessage = 'You are not authorized, login again!'
                        } else if (status == 404) {
                            $scope.postErrorMessage = 'The requested post was not found:('
                        } else {
                            $scope.postErrorMessage = 'Uups, something went wrong!';
                        }
                        $scope.postLoadingError = true;
                        console.log(data);
                    }
                );
            }


            function createVoteAtPost(possibilityIndex) {

                if ($scope.post.myPostFeedback != null) {//if there is already a vote by me
                    if (possibilityIndex == 0) {
                        $scope.post.possibilitiesVotesCount[1]--;
                    } else {
                        $scope.post.possibilitiesVotesCount[0]--;
                    }
                }

                var feedbackPossibility = {};
                feedbackPossibility.postDTO = $scope.post;
                feedbackPossibility.owner = $scope.loggedInUser;
                feedbackPossibility.name = $scope.post.feedbackPossibilities[possibilityIndex];

                $scope.post.myPostFeedback = {};//this is to avoid circular relationship in JSON

                $scope.post.myPostFeedback.name = $scope.post.feedbackPossibilities[possibilityIndex];
                $scope.post.possibilitiesVotesCount[possibilityIndex]++;


                postFeedbackService.create(feedbackPossibility,
                    function (data) {
                        $scope.post.myPostFeedback = data;
                    }, function (data, status) {
                        $scope.post.myPostFeedback = null;

                        var failure_mesage = {};

                        if (status == 401) {
                            failure_mesage = "You are not authorized, try to login again :(";
                        } else if (status == 404) {
                            failure_mesage = "You cannot vote here, maybe the post was deleted in a meanwhile :(";
                        } else {
                            failure_mesage = "Uups, something went wrong!";
                        }
                        displayAlertDialog('Error', failure_mesage);
                    }
                );
            }

            function deleteVoteAtPost(possibilityIndex) {
                $scope.post.myPostFeedback = null;
                $scope.post.possibilitiesVotesCount[possibilityIndex]--;

                postFeedbackService.delete($scope.post.myPostFeedback.id,
                    function (data, status, headers, config) {
                    }, function (data, status) {

                        var failure_mesage = {};

                        if (status == 401) {
                            failure_mesage = "You are not authorized, try to login again :(";
                        } else if (status == 404) {
                            failure_mesage = "You cannot vote here, maybe the post was deleted in a meanwhile :(";
                        } else {
                            failure_mesage = "Uups, something went wrong!";
                        }
                        displayAlertDialog('Error', failure_mesage);
                    }
                );
            }

            function processRetrievedPost() {

                $scope.possibiliesHashtag = '#' + $scope.post.feedbackPossibilities[0] + 'VS' + $scope.post.feedbackPossibilities[1];

                var postCreationTime = $scope.post.publishTime;

                $scope.postTime = getItemAge(postCreationTime);
                $scope.comments = $scope.post.comments;

                for (var i = 0; i < $scope.comments.length; i++) {
                    $scope.comments[i].age = getItemAge($scope.comments[i].timestamp);
                }

            }


            function getItemAge(creationTime) {
                var d = new Date();
                var n = d.getTime();

                var diff = n - creationTime;
                diff = Math.floor(diff / 1000);
                var minsAge = Math.floor(diff / 60);
                var hoursAge = Math.floor(minsAge / 60);
                var daysAge = Math.floor(hoursAge / 24);

                if (daysAge > 7) {
                    return moment(creationTime).format('MMM Do YYYY');
                } else {
                    return moment(creationTime).fromNow();
                }
            }

            function createComment() {
                $scope.addingComment = true;

                $scope.myNewComment.owner = $scope.loggedInUser;
                $scope.myNewComment.postDTO = $scope.post;

                commentService.create($scope.myNewComment,
                    function (data) {
                        $scope.myNewComment = data;
                        $scope.myNewComment.age = getItemAge($scope.myNewComment.timestamp);
                        $scope.comments.push($scope.myNewComment);

                        $scope.myNewComment = {};
                        $scope.addingComment = false;
                    }, function (data, status) {
                        $scope.addingComment = false;
                        var failure_mesage = {};

                        if (status == 401) {
                            failure_mesage = "You are not authorized, try to login again :(";
                        } else if (status == 404) {
                            failure_mesage = "You cannot comment here, maybe the post was deleted in a meanwhile :(";
                        } else {
                            failure_mesage = "Uups, something went wrong!";
                        }
                        displayAlertDialog('Error', failure_mesage);
                    }
                );
            }

            function removeComment(index) {
                $scope.comments.splice(index, 1);//removing comment

                commentService.delete($scope.comments[index].id,
                    function (data, status, headers, config) {
                    }, function (data, status) {
                        var failure_mesage = {};
                        if (status == 401) {
                            failure_mesage = "You are not authorized, try to login again :(";
                        } else if (status == 404) {
                            failure_mesage = "You cannot remove a comment here, maybe the post was deleted in a meanwhile :(";
                        } else {
                            failure_mesage = "Uups, something went wrong!";
                        }
                        displayAlertDialog('Error', failure_mesage);
                    });
            }


            function loadMoreComments() {
                $scope.loadingComments = true;

                var lastId = undefined;
                if ($scope.comments.length > 0) {
                    var lastId = $scope.comments[0].id;
                }

                commentService.findByPost($scope.post.secretUrl, lastId, function (data) {
                        $scope.loadingComments = false;
                        var newComments = data.content;

                        for (var i = 0; i < $scope.comments.length; i++) {
                            newComments.push($scope.comments[i]);
                        }

                        $scope.comments = newComments;

                        for (var i = 0; i < $scope.comments.length; i++) {
                            $scope.comments[i].age = getItemAge($scope.comments[i].timestamp);
                        }

                    }, function (data, status) {
                        $scope.loadingComments = false;
                    }
                );
            }

            //this function is called to test is user's access token is still up to date.
            function checkAccessToken() {
                $scope.isLoadingUser = true;
                console.log('Testing if users access token is up to date...');

                userService.exchangeToken(
                    function (data) {
                        $scope.isLoadingUser = false;
                        processAppUser(data);
                    }, function (data, status) {
                        $scope.isLoadingUser = false;
                        logout();
                        displayWelcomeDialog();
                        retrievePostFromBackend(undefined);
                    }
                );

            }

            function loginWithFB() {
                $scope.isLoadingUser = true;
                FB.login(function (response) {
                    if (response.authResponse) {
                        FB.api('/me', function (response) {
                            console.log('Fetching data from Facebook');

                            var accessToken = FB.getAuthResponse();
                            authFact.setAccessToken(accessToken.accessToken);

                            exchangeToken(accessToken.accessToken);
                        });
                    } else {
                        $scope.isLoadingUser = false;
                        console.log('User cancelled login or did not fully authorize.');
                    }
                }, {scope: 'email'});
            }


            $scope.openMenu = function ($mdOpenMenu, ev) {
                $mdOpenMenu(ev);
            };


            function exchangeToken(accesstoken) {
                userService.exchangeToken(
                    function (data) {
                        $scope.isLoadingUser = false;
                        processAppUser(data);
                    }, function (data, status, headers, config) {
                        $scope.isLoadingUser = false;

                        logout();

                        var failure_mesage = "Uups, something went wrong!";
                        displayAlertDialog('Error', failure_mesage);
                    }
                );
            };


            function processAppUser(appUser) {
                if (appUser.id == null) {
                    displayRegistrationDialog(appUser);
                    retrievePostFromBackend(undefined);
                } else {
                    logInUser(appUser);
                }
            }

            function logInUser(appUser) {
                $cookieStore.put('userObj', appUser);
                $scope.loggedInUser = appUser;
                $scope.isAuthenticated = true;

                console.log('Logged in user: ' + appUser.username + '.');
                displayToast('Logged in successfully!', 3000);

                retrievePostFromBackend(authFact.getAccessToken());
            }


            function logout() {
                $scope.isAuthenticated = false;
                $scope.loggedInUser = undefined;
                authFact.setAccessToken(undefined);
                $cookieStore.put('userObj', undefined);

                retrievePostFromBackend(undefined);
            }

            function registerUser(appUser) {
                console.log('Registering user: ' + appUser.username + '.');
                $scope.isLoadingUser = true;

                userService.registerUser(appUser,
                    function (data) {
                        $scope.isLoadingUser = false;
                        logInUser(data);
                    }, function (data, status) {
                        $scope.isLoadingUser = false;
                        var failure_mesage = data;
                        if (data == null) {
                            failure_mesage = "Uups, something went wrong!";
                        }
                        displayAlertDialog('Error', failure_mesage);
                    }
                );
            }


            function registerWithName(name) {
                console.log('Registering user with username: ' + name + '.');
                $scope.isLoadingUser = true;

                var appUser = {username: name};

                userService.registerUserWithName(appUser,
                    function (data) {
                        $scope.isLoadingUser = false;
                        authFact.setAccessToken(data.accessToken);
                        logInUser(data);
                    }, function (data, status) {
                        $scope.isLoadingUser = false;
                        var failure_mesage = data;

                        if (status == 401) {
                            failure_mesage = "You cannot continue, maybe your provided url is not valid :("
                        } else if (status == 409) {
                            failure_mesage = "Your chosen name is already taken, choose another!";
                        } else {
                            failure_mesage = "Uups, something went wrong, you cannot continue!";
                        }
                        displayAlertDialog('Error', failure_mesage);
                    }
                );
            }

            function displayWelcomeDialog() {
                $mdDialog.show({
                    controller: WelcomeDialogController,
                    templateUrl: 'welcomedialog.tmpl.html',
                    parent: angular.element(document.body),
                    clickOutsideToClose: false,
                    fullscreen: $scope.customFullscreen // Only for -xs, -sm breakpoints.
                })
                    .then(function (user) {

                    }, function () {
                    });
            }

            function displayRegistrationDialog(appUser) {
                userPassingService.setUser(appUser);
                $mdDialog.show({
                    controller: RegistrationDialogController,
                    templateUrl: 'registrationdialog.tmpl.html',
                    parent: angular.element(document.body),
                    clickOutsideToClose: false,
                    fullscreen: $scope.customFullscreen // Only for -xs, -sm breakpoints.
                })
                    .then(function (user) {
                        if (userPassingService.getUser() == undefined) {
                            console.log('dialog cancelled');
                            logout();
                        } else {
                            appUser = userPassingService.getUser();
                            registerUser(appUser);
                        }
                    }, function () {
                        console.log('dialog cancelled');
                        logout();
                    });
            };

            function displayPossibilityVotersDialog(possibilityIndex) {

                votersDialogPost = $scope.post;
                votersDialogPossibilityIndex = possibilityIndex;

                $mdDialog.show({
                    controller: VotersDialogController,
                    templateUrl: 'votersdialog.tmpl.html',
                    parent: angular.element(document.body),
                    clickOutsideToClose: false,
                    fullscreen: $scope.customFullscreen // Only for -xs, -sm breakpoints.
                })
                    .then(function () {

                    }, function () {
                    });
            }

            function displayAlertDialog(title, msg) {
                // Appending dialog to document.body to cover sidenav in docs app
                // Modal dialogs should fully cover application
                // to prevent interaction outside of dialog
                $mdDialog.show(
                    $mdDialog.alert()
                        .parent(angular.element(document.querySelector('#popupContainer')))
                        .clickOutsideToClose(true)
                        .title(title)
                        .textContent(msg)
                        .ok('Got it!')
                );
            };

            function displayToast(msg, timeMillins) {

                $mdToast.show(
                    $mdToast.simple()
                        .textContent(msg)
                        .hideDelay(timeMillins)
                );
            }

            function VotersDialogController($scope, $mdDialog) {

                if (votersDialogPossibilityIndex == 0) {

                    //want to display the first possibility as first
                    $scope.poss1Name = votersDialogPost.feedbackPossibilities[0];
                    $scope.poss2Name = votersDialogPost.feedbackPossibilities[1];

                    $scope.poss1Count = votersDialogPost.possibilitiesVotesCount[0];
                    $scope.poss2Count = votersDialogPost.possibilitiesVotesCount[1];

                    $scope.votersLeftTabFact = new VotersPagingFact(votersDialogPost.secretUrl, $scope.poss1Name);
                    $scope.votersRightTabFact = new VotersPagingFact(votersDialogPost.secretUrl, $scope.poss2Name);
                } else {//want to display the second possibility as first

                    $scope.poss2Name = votersDialogPost.feedbackPossibilities[0];
                    $scope.poss1Name = votersDialogPost.feedbackPossibilities[1];

                    $scope.poss2Count = votersDialogPost.possibilitiesVotesCount[0];
                    $scope.poss1Count = votersDialogPost.possibilitiesVotesCount[1];

                    $scope.votersLeftTabFact = new VotersPagingFact(votersDialogPost.secretUrl, $scope.poss1Name);
                    $scope.votersRightTabFact = new VotersPagingFact(votersDialogPost.secretUrl, $scope.poss2Name);

                }

                $scope.cancel = function () {
                    $mdDialog.hide();
                }

            }


            function RegistrationDialogController($scope, $mdDialog, userPassingService) {

                $scope.user = userPassingService.getUser();

                $scope.cancel = function () {
                    userPassingService.setUser(undefined);
                    $mdDialog.hide();
                };

                $scope.register = function (user) {
                    // $scope.user.username = user.username;
                    // $scope.user.email = user.email;
                    userPassingService.setUser($scope.user);
                    $mdDialog.hide();
                };
            }

            function WelcomeDialogController($scope, $mdDialog) {

                $scope.username = '';

                $scope.continueWithFB = function () {
                    console.log('user wants to login per fb');
                    loginWithFB();
                    $mdDialog.hide();
                };

                $scope.continueWithName = function () {
                    registerWithName($scope.username);
                    console.log('user wants to continue with name: ' + $scope.username);
                    $mdDialog.hide();
                };

                $scope.displayFacebookLoginExplanation = function () {
                    displayAlertDialog('Info', 'You can return to your account in the app and enjoy the full functionality!');
                };

            }


        }]);
