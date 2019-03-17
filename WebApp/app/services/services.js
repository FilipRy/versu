
//backend services
app.factory('userService', ['$http', 'authFact', '$routeParams', function ($http, authFact, $routeParams) {
    return {
        registerUser: function (user, successCallback, errorCallback) {
            var header = authFact.createAuthorizationHeader();
            $http.post('http://localhost:8081/api/user', user, header).success(successCallback).error(errorCallback);
        },
        registerUserWithName: function (user, successCallback, errorCallback) {
            var secretPostUrl = $routeParams.id;
            var config = {headers: {'Authorization': secretPostUrl}};
            $http.post('http://localhost:8081/api/user/anonym', user, config).success(successCallback).error(errorCallback);
        },
        exchangeToken: function (successCallback, errorCallback) {
            var header = authFact.createAuthorizationHeader();
            $http.get('http://localhost:8081/api/user/exchangeToken', header).success(successCallback).error(errorCallback);
        }
    };
}]);


app.factory('postService', ['$http', 'authFact', '$routeParams', function ($http, authFact, $routeParams) {
    return {
        getDetails: function (accessToken, successCallback, errorCallback) {

            var url = null;
            var res = null;

            if (accessToken == undefined) {
                url = 'http://localhost:8081/api/post/details/' + $routeParams.id;
                res = $http.get(url);
            } else {
                var config = {headers: {'Authorization': accessToken}};
                url = 'http://localhost:8081/api/post/detailsauth/' + $routeParams.id;
                res = $http.get(url, config);
            }

            res.success(successCallback).error(errorCallback);
        }
    };
}]);

app.factory('postFeedbackService', ['$http', 'authFact', function ($http, authFact) {
    return {
        create: function (postFeedback, successCallback, errorCallback) {
            var header = authFact.createAuthorizationHeader();
            $http.post('http://localhost:8081/api/postfeedback/anonym', postFeedback, header).success(successCallback).error(errorCallback);
        },
        delete: function (postFeedbackId, successCallback, errorCallback) {
            var header = authFact.createAuthorizationHeader();
            $http.delete('http://localhost:8081/api/postfeedback/' + postFeedbackId, header).success(successCallback).error(errorCallback);
        }
    };
}]);

app.factory('commentService', ['$http', 'authFact', function ($http, authFact) {
    return {
        create: function (comment, successCallback, errorCallback) {
            var header = authFact.createAuthorizationHeader();
            $http.post('http://localhost:8081/api/comment/anonym', comment, header).success(successCallback).error(errorCallback);
        },
        findByPost: function (secretUrl, lastId, successCallback, errorCallback) {
            var url = '';
            if (lastId == undefined) {
                url = "http://localhost:8081/api/comment/findByPost/" + secretUrl + "?size=5&page=0";
            } else {
                url = "http://localhost:8081/api/comment/findByPost/" + secretUrl + "?size=5&lastId=" + lastId;
            }
            var header = authFact.createAuthorizationHeader();
            $http.get(url, header).success(successCallback).error(errorCallback);

        },
        delete: function (commentId, successCallback, errorCallback) {
            var header = authFact.createAuthorizationHeader();
            $http.delete('http://localhost:8081/api/comment/' + commentId, header).success(successCallback).error(errorCallback);
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