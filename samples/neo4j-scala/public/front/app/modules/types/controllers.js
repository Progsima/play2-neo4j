'use strict';

var typesControllers = angular.module('typesControllers', ['ngRoute', 'restangular', 'ngTable']);

/**
 * List all type avaible in the application.
 */
typesControllers.controller('List', ['$scope', '$location', 'Restangular', 'ngTableParams',
    function($scope, $location, Restangular, ngTableParams) {
        $scope.tableParams = new ngTableParams(
            {
                page: 1,
                count: 10,
                sorting: {
                    name: "asc"
                }
            },
            {
                total: 0,
                getData: function($defer, params) {
                    Restangular.all('types').getList(
                        {
                            page  : params.page(),
                            row   : params.count(),
                            sort  : "name",
                            order : params.sorting().name
                        }
                    ).then(function(response) {
                        params.total(response.headers()['x-total-row']);
                        $defer.resolve(response.data);
                    });
                }
            }
        );
        $scope.fnDelete = function(name) {
            $location.path("/types/delete/" + name);
        };
        $scope.fnEdit = function(name) {
            $location.path("/types/edit/" + name);
        };
        $scope.fnNew = function() {
            $location.path("/types/new");
        };
    }
]);

/**
 * Edit a specific type.
 */
typesControllers.controller('Edit', ['$scope', 'Restangular', 'typeDefinition' ,
    function($scope, Restangular, typeDefinition) {
        // empty type
        $scope.type  = {
            name: "",
            title: "",
            description: "",
            fields: [
                {
                    "name" : "",
                    "title" : "",
                    "description" : ""
                }
            ]
        }

        // adding all available type
        $scope.typeDefinition = typeDefinition;

        //
        // function to add a field
        //
        $scope.fnAddField = function() {
            $scope.type.fields.push({
                "name" : "",
                "title" : "",
                "description" : ""
            });
        }

        //
        // function to remove a field
        //
        $scope.fnRemoveField = function(position) {
            $scope.type.fields = $scope.type.fields.filter(function(element){
                return position != $scope.type.fields.indexOf(element);
            });
        }

        //
        // function to save the type
        //
        $scope.fnSaveType = function() {

            // init schema object by clonig & delete unecessary field
            $scope.schema = angular.copy($scope.type);
            delete $scope.schema.fields;

            // Let's work with fields
            var properties ="{";
            for (var i=0; i < $scope.type.fields.length; i++) {
                var field = $scope.type.fields[i];
                properties +=  field.name + ":" + field.type.toSchema($scope.type, i);
            }
            properties += "}";
            $scope.schema.properties = eval(properties);
        }
    }
]);

/**
 * Delete a specific type.
 */
typesControllers.controller('Delete', ['$scope', '$location', '$routeParams','Restangular', 'ngTableParams',
    function($scope, $location, $routeParams, Restangular, ngTableParams) {

        // retrieve current element
        $scope.type = Restangular.one('types', $routeParams.name).get().$object;

        // Get list of content of that type
        $scope.tableParams = new ngTableParams(
            {
                page: 1,
                count: 10
            },
            {
                total: 0,
                getData: function($defer, params) {
                    Restangular.all('contents/' + $routeParams.name).getList(
                        {
                            page  : params.page(),
                            row   : params.count()
                        }
                    ).then(function(response) {
                            params.total(response.headers()['x-total-row']);
                            $defer.resolve(response.data);
                        });
                }
            }
        );

        // Return to home
        $scope.fnHome = function() {
            $location.path('/types');
        }

        // The delete function for confirmation
        $scope.fnDelete = function(name) {
            Restangular.one('types', name).remove().then(function(response){
                $scope.fnHome();
            })
        }

    }
]);

/**
 * Create a new type
 */
typesControllers.controller('New', ['$scope', '$location', 'Restangular', 'ngTableParams',
    function($scope, $location, Restangular, ngTableParams) {

    }
]);

typesControllers.factory(
    "typeDefinition",
    function() {
        return [
            {
                name: "Integer",
                form : "./modules/types/partials/form/integer.html",
                toSschema : function(type, i){
                    var schema = "{ " + type.fields[i].name + ": type : \"integer\"";
                    if(type.fields[i].min)
                        schema += ", minimum : " + type.fields[i].min;
                    if(type.fields[i].max)
                        schema += ", maximum : " + type.fields[i].max;
                    schema += "}"
                    return eval(schema);
                }
            },
            {
                name: "Float",
                form : "./modules/types/partials/form/float.html",
                schema : { type : "number" }
            },
            {
                name : "Boolean",
                form : "./modules/types/partials/form/boolean.html",
                schema : { type : "boolean" }
            },
            {
                name: "String",
                form : "./modules/types/partials/form/string.html",
                schema : { type : "string" }
            },
            {
                name: "SimpleText",
                form : "./modules/types/partials/form/simpleText.html",
                schema : { type : "string", format: "SimpleText", minLength: 5, maxLength: 255, required: true }
            },
            {
                name: "RichText",
                form : "./modules/types/partials/form/richText.html",
                schema : { type : "string", format: "RichText", minLength: 5, maxLength: 255, required: true }
            },
            {
                name: "Date",
                form : "./modules/types/partials/form/date.html",
                schema : { type : "string", format: "date-time", required: true }
            },
            {
                name: "Email",
                form : "./modules/types/partials/form/email.html",
                schema : { type : "string", format: "email", required: true }
            },
            {
                name: "Url",
                form : "./modules/types/partials/form/url.html",
                schema : { type : "string", format: "uri", required: true }
            }
        ]
    }
);