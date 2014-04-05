'use strict';

/* App Module */
var app = angular.module('neocms', ['types']);

app.config(function(RestangularProvider) {
    // Restangular configuration
    RestangularProvider.setBaseUrl('/api');
});