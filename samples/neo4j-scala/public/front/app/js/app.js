'use strict';

/* App Module */
var app = angular.module('neocms', ['ngRoute', 'restangular', 'ngTable', 'neocms.directives']);

app.config(function($routeProvider, RestangularProvider) {
    // Routing configuration
    $routeProvider
        .when('/', {templateUrl: 'partials/contentType/index.html', controller: ContenTypeCtrl})
        .when('/error', {templateUrl: 'partials/error.html', controller: ErrorCtrl})
        .otherwise({redirectTo: '/'});

    // Restangular configuration
    RestangularProvider.setBaseUrl('/api');
});