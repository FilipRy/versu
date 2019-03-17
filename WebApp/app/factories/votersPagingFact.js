//VotersPagingFact constructor function to encapsulate HTTP and pagination logic
app.factory('VotersPagingFact', ['$http', 'authFact', function ($http, authFact) {
    var VotersPagingFact = function (secretUrl, voteName) {
        this.items = [];
        this.busy = false;
        this.pageNr = 0;//number of a page which is to load now
        this.lastLoadedPage = -1;
        this.pageSize = 20;
        this.after = '';
        this.secretUrl = secretUrl;
        this.voteName = voteName;
    };

    VotersPagingFact.prototype.nextPage = function () {

        console.log(this.voteName +  ': tart reading of post voters');

        if (this.busy) return;
        this.busy = true;

        var url = "http://localhost:8081/api/postfeedback/findByPostAndName/" + this.secretUrl + "/" + this.voteName + "?page=" + this.pageNr + "&limit=" + this.pageSize;

        var accessToken = authFact.getAccessToken();
        var config = {headers: {'Authorization': accessToken}};

        console.log('Reading voters of post for vote: '+this.voteName);

        if(this.lastLoadedPage == this.pageNr) {//tries to load the same page multiple times
            console.log('Try to read the same page again, cancelling');
            this.busy = false;
        } else {
            var res = $http.get(url, config).then(
                function successCallback(response) {
                    for (var i = 0; i < response.data.content.length; i++) {
                        this.items.push(response.data.content[i]);
                    }

                    console.log('Read '+ response.data.content.length + ' items of page ' + this.pageNr);

                    this.lastLoadedPage = this.pageNr;
                    if (response.data.content.length == this.pageSize) {
                        this.pageNr++;
                        console.log('Continue to next page');
                    }
                    this.busy = false;
                }.bind(this),
                function errorCallback(response) {
                    console.log(data);

                    this.busy = false;
                }
            );
        }

    };

    return VotersPagingFact;
}]);
