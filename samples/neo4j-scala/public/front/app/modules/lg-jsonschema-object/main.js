// Define type module
var lgJsonSchemaObject = angular.module('lgJsonSchemaObject', ['ngRoute', 'restangular', 'ngTable']);

// Some module route
lgJsonSchemaObject.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider
            .when('/objects/:type', {templateUrl: './modules/lg-jsonschema-object/partials/index.html', controller: 'List'})
            .when('/objects/:type/new', {templateUrl: './modules/lg-jsonschema-object/partials/edit.html', controller: 'Edit'})
            .when('/objects/:type/edit/:id', {templateUrl: './modules/lg-jsonschema-object/partials/edit.html', controller: 'Edit'})
            .when('/objects/:types/delete/:id', {templateUrl: './modules/lg-jsonschema-object/partials/delete.html', controller: 'Delete'});
    }
]);
