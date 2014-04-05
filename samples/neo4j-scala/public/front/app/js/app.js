/* App Module */
var app = angular.module('neocms', ['exception', 'types']);

// Configure default API endpoint for Restangular.
app.config(['RestangularProvider',
    function(RestangularProvider) {
        // Restangular configuration
        RestangularProvider.setBaseUrl('/api');
    }
]);
