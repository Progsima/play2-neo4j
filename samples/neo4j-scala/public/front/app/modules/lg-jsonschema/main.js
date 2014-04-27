/**
 * Logisima Json Schema enable main file.
 */

// Define the module
var lgJsonschema = angular.module('lgJsonSchema', ['ngRoute', 'restangular', 'ngTable']);

// Define module route
lgJsonschema.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider
            .when( '/types',                         { templateUrl: './modules/lg-jsonschema/partials/type/index.html',     controller: 'LgJsonSchemaTypeList' })
            .when( '/types/new',                     { templateUrl: './modules/lg-jsonschema/partials/type/edit.html',      controller: 'LgJsonSchemaTypeEdit' })
            .when( '/types/edit/:name',              { templateUrl: './modules/lg-jsonschema/partials/type/edit.html',      controller: 'LgJsonSchemaTypeEdit' })
            .when( '/types/delete/:name',            { templateUrl: './modules/lg-jsonschema/partials/type/delete.html',    controller: 'LgJsonSchemaTypeDelete' })
            .when( '/contents/:type',                 { templateUrl: './modules/lg-jsonschema/partials/object/index.html',   controller: 'LgJsonSchemaObjectList' })
            .when( '/contents/:type/new',             { templateUrl: './modules/lg-jsonschema/partials/object/edit.html',    controller: 'LgJsonSchemaObjectEdit' })
            .when( '/contents/:type/edit/:uuid',      { templateUrl: './modules/lg-jsonschema/partials/object/edit.html',    controller: 'LgJsonSchemaObjectEdit' })
            .when( '/contents/:type/delete/:uuid',   { templateUrl: './modules/lg-jsonschema/partials/object/delete.html',  controller: 'LgJsonSchemaObjectDelete' });
    }
]);



