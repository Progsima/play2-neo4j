'use strict';

var types = angular.module('types', ['typesControllers']);

types.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider
            .when('/types', {templateUrl: './modules/types/partials/index.html', controller: 'List'})
            .when('/types/edit/:name', {templateUrl: './modules/types/partials/edit.html', controller: 'Edit'})
            .when('/types/delete/:name', {templateUrl: './modules/types/partials/delete.html', controller: 'Delete'})
            .when('/types/new', {templateUrl: './modules/types/partials/new.html', controller: 'New'})
    }
]);