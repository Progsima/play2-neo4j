// Define type module
var lgJsonSchemaObject = angular.module('lgJsonSchemaObject', ['ngRoute', 'restangular', 'ngTable']);

// Some module route
lgJsonSchemaObject.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider
            .when('/objects/:type', {templateUrl: './modules/lg-jsonschema-object/partials/index.html', controller: 'LgJsonSchemaObjectList'})
            .when('/objects/:type/new', {templateUrl: './modules/lg-jsonschema-object/partials/edit.html', controller: 'LgJsonSchemaObjectEdit'})
            .when('/objects/:type/edit/:uuid', {templateUrl: './modules/lg-jsonschema-object/partials/edit.html', controller: 'LgJsonSchemaObjectEdit'})
            .when('/objects/:types/delete/:uuid', {templateUrl: './modules/lg-jsonschema-object/partials/delete.html', controller: 'LgJsonSchemaObjectDelete'});
    }
]);
