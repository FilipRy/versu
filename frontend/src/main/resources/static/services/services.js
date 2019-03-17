

app.factory('joinAppService', ['$http', function ($http) {
    return {
        joinApp: function (user, successCallback, errorCallback) {
            var BACKEND_URL = 'https://versu.us14.list-manage.com/subscribe/post-json?u=6e2b046cfe0c027ab23cbe1a3&id=6f06df343f&FNAME=' + user.username + '&EMAIL=' +user.email+ '&b_6e2b046cfe0c027ab23cbe1a3_6f06df343f=&JOIN+THE+BETA=JOIN+THE+BETA'
            $http.get(BACKEND_URL, user).success(successCallback).error(errorCallback);
        }
    };
}]);

//backend services
app.factory('userService', ['$http', 'authFact', '$routeParams', 'BACKEND_URL', function ($http, authFact, $routeParams, BACKEND_URL) {
    return {
        registerUser: function (user, successCallback, errorCallback) {
            var header = authFact.createAuthorizationHeader();
            $http.post(BACKEND_URL + '/api/user', user, header).success(successCallback).error(errorCallback);
        },
        registerUserWithName: function (user, successCallback, errorCallback) {
            var secretPostUrl = $routeParams.id;
            var config = {headers: {'Authorization': secretPostUrl}};
            $http.post(BACKEND_URL + '/api/user/anonym', user, config).success(successCallback).error(errorCallback);
        },
        exchangeToken: function (successCallback, errorCallback) {
            var header = authFact.createAuthorizationHeader();
            $http.get(BACKEND_URL + '/api/user/exchangeToken', header).success(successCallback).error(errorCallback);
        }
    };
}]);


app.factory('postService', ['$http', 'authFact', '$routeParams', 'BACKEND_URL', function ($http, authFact, $routeParams, BACKEND_URL) {
    return {
        getDetails: function (accessToken, successCallback, errorCallback) {

            var url = null;
            var res = null;

            if (accessToken == undefined) {
                url = BACKEND_URL + '/api/post/details/' + $routeParams.id;
                res = $http.get(url);
            } else {
                var config = {headers: {'Authorization': accessToken}};
                url = BACKEND_URL + '/api/post/detailsauth/' + $routeParams.id;
                res = $http.get(url, config);
            }

            res.success(successCallback).error(errorCallback);
        }
    };
}]);

app.factory('postFeedbackService', ['$http', 'authFact', 'BACKEND_URL', function ($http, authFact, BACKEND_URL) {
    return {
        create: function (postFeedback, successCallback, errorCallback) {
            var header = authFact.createAuthorizationHeader();
            $http.post(BACKEND_URL + '/api/postfeedback/anonym', postFeedback, header).success(successCallback).error(errorCallback);
        },
        delete: function (postFeedbackId, successCallback, errorCallback) {
            var header = authFact.createAuthorizationHeader();
            $http.delete(BACKEND_URL + '/api/postfeedback/' + postFeedbackId, header).success(successCallback).error(errorCallback);
        }
    };
}]);

app.factory('commentService', ['$http', 'authFact', 'BACKEND_URL', function ($http, authFact, BACKEND_URL) {
    return {
        create: function (comment, successCallback, errorCallback) {
            var header = authFact.createAuthorizationHeader();
            $http.post(BACKEND_URL + '/api/comment/anonym', comment, header).success(successCallback).error(errorCallback);
        },
        findByPost: function (secretUrl, lastId, successCallback, errorCallback) {
            var url = '';
            if (lastId == undefined) {
                url = BACKEND_URL + "/api/comment/findByPost/" + secretUrl + "?size=5&page=0";
            } else {
                url = BACKEND_URL + "/api/comment/findByPost/" + secretUrl + "?size=5&lastId=" + lastId;
            }
            var header = authFact.createAuthorizationHeader();
            $http.get(url, header).success(successCallback).error(errorCallback);

        },
        delete: function (commentId, successCallback, errorCallback) {
            var header = authFact.createAuthorizationHeader();
            $http.delete(BACKEND_URL + '/api/comment/' + commentId, header).success(successCallback).error(errorCallback);
        }
    };
}]);


//this service is used to pass user, who is to register between DialogController and view2Controller
app.service('userPassingService', function () {

    var userPassingService = {};
    this.user = {};

    userPassingService.setUser = function (user) {
        userPassingService.user = user;
    }

    userPassingService.getUser = function () {
        return userPassingService.user;
    }


    return userPassingService;

});