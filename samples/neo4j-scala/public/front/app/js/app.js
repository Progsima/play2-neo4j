/* App Module */
var app = angular.module('neocms', ['exception', 'lgJsonSchema']);

// Configure default API endpoint for Restangular.
app.config(['RestangularProvider',
    function(RestangularProvider) {
        // Restangular configuration
        RestangularProvider.setBaseUrl('http://localhost:9000/api');
    }
]);
