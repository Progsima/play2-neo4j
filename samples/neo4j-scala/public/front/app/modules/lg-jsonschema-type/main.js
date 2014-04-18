// Define type module
var lgJsonSchemaType = angular.module('lgJsonSchemaType', ['ngRoute', 'restangular', 'ngTable']);

// Some module route
lgJsonSchemaType.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider
            .when('/types', {templateUrl: './modules/lg-jsonschema-type/partials/index.html', controller: 'List'})
            .when('/types/new', {templateUrl: './modules/lg-jsonschema-type/partials/edit.html', controller: 'Edit'})
            .when('/types/edit/:name', {templateUrl: './modules/lg-jsonschema-type/partials/edit.html', controller: 'Edit'})
            .when('/types/delete/:name', {templateUrl: './modules/lg-jsonschema-type/partials/delete.html', controller: 'Delete'});
    }
]);
