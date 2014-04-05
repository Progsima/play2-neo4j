(function () {
    'use strict';

    /* App Module */
    var app = angular.module('neocms', ['types']);

    app.config(['RestangularProvider',
        function(RestangularProvider) {
            // Restangular configuration
            RestangularProvider.setBaseUrl('/api');
        }
    ]);

}());