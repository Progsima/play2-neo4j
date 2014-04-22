/* App Module */
var app = angular.module('neocms', ['exception', 'lgJsonSchemaType', 'lgJsonSchemaObject', 'lgJsonSchemaForm']);

// Configure default API endpoint for Restangular.
app.config(['RestangularProvider',
    function(RestangularProvider) {
        // Restangular configuration
        RestangularProvider.setBaseUrl('http://localhost:9000/api');
    }
]);
