'use strict';

var typesControllers = angular.module('typesControllers', ['ngRoute', 'restangular', 'ngTable']);

/**
 * List all type avaible in the application.
 */
typesControllers.controller('List', ['$scope', '$location', 'Restangular', 'ngTableParams',
    function($scope, $location, Restangular, ngTableParams) {

        // configure restangular
        Restangular.setFullResponse(true);

        // ng table configuration
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

        //
        // Function to go to delete page
        //
        $scope.fnDelete = function(name) {
            $location.path("/types/delete/" + name);
        };

        //
        // Function to go to edit page
        //
        $scope.fnEdit = function(name) {
            $location.path("/types/edit/" + name);
        };

        //
        // Function to go to new page
        //
        $scope.fnNew = function() {
            $location.path("/types/new");
        };
    }
]);

/**
 * Edit a specific type.
 */
typesControllers.controller('Edit', ['$scope', 'Restangular', '$routeParams', 'typeValue', 'typeService',
    function($scope, Restangular, $routeParams, typeValue, typeService) {

        // configure Restangular
        Restangular.setRestangularFields({
            id: "name"
        });

        // init controller with data
        if($routeParams.name != null) {
            // retreive the element from database
            Restangular.one('types', $routeParams.name).get().then(function(neo4jType){
                $scope.neo4jType = neo4jType;
                $scope.type = typeService.neo4j2Form($scope.neo4jType);
            });
        }
        else {
            $scope.neo4jType = Restangular.all('types');
        }

        // adding all available type
        $scope.types = typeValue;

        //
        // function to add a field
        //
        $scope.fnAddField = function() {
            if( $scope.type.fields == null ) {
                $scope.type.fields = [];
            }
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
            typeService.updateNeo4jTypeWithForm($scope.neo4jType, $scope.type);
            $scope.neo4jType.put();
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

        //
        // Return to home
        //
        $scope.fnHome = function() {
            $location.path('/types');
        }

        //
        // The delete function for confirmation
        //
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

/**
 * Factory for communication between Neo4j API and type model (ie. HTML forms).
 */
typesControllers.service('typeService', ['Restangular', 'typeValue',
    function(Restangular, typeValue){

        //
        // Transform a model to a neo4j object (but kept restangular wrapper).
        //
        this.updateNeo4jTypeWithForm = function(neo4jType, type) {
            // let's copy main neo4j fields
            neo4jType.name = type.name;
            neo4jType.description = type.description;

            // let's work on json schema
            var schema = {
                name: type.name,
                title : type.title,
                description : type.description,
                type: "object"
            }

            // Let's work on fields to generate properties
            var properties ="{";
            for ( var i=0 ; i < type.fields.length ; i++ ) {
                var field = type.fields[i];
                properties +=  this.schemaTemplate(type.fields[i].type.schema, field);
                if ( i < (type.fields.length  - 1) ) {
                    properties += ",";
                }
            }
            properties += "}";
            schema.properties = eval("(" + properties + ")");

            // finally adding schema to neo4j object
            neo4jType.schema = JSON.stringify(schema);

            return neo4jType;
        };

        //
        // Transform a Neo4j object to a type model.
        //
        this.neo4j2Form = function(neo4jType) {
            // eval the json schema string
            var schema = eval( "(" + neo4jType.schema + ")");

            // create the form object
            var type = {
                name : neo4jType.name,
                title: schema.title,
                description : neo4jType.description,
                fields: []
            }

            for( var property in schema.properties) {
                var field = schema.properties[ property];
                field.name = property;
                field.type = typeValue[field.id];
                delete field.id;
                type.fields.push(field);
            }

            return type;
        };

        this.schemaTemplate = function(template, obj) {
            var schema = template;
            for(var property in obj){
                schema = schema.replace("{{" + property + "}}" , obj[property]);
            }
            schema = schema.replace(/({{\w*}})/g, "null");

            schema = schema.replace(/,[^,]*:\snull/g, "");

            return schema;
        }

    }
]);

typesControllers.value(
    "typeValue",
    {
        "http://json-schema.logisima.com/integer" : {
            name: "Integer",
            form : "./modules/types/partials/form/integer.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/integer\" ,title: \"{{title}}\" ,description: \"{{description}}\", type : \"integer\" , minimum : {{minimum}} , maximum : {{maximum}} , require : {{require}} }"
        },
        "http://json-schema.logisima.com/float" : {
            name: "Float",
            form : "./modules/types/partials/form/float.html",
            schema : { type : "number" }
        },
        "http://json-schema.logisima.com/boolean" : {
            name : "Boolean",
            form : "./modules/types/partials/form/boolean.html",
            schema : { type : "boolean" }
        },
        "http://json-schema.logisima.com/string" : {
            name: "String",
            form : "./modules/types/partials/form/string.html",
            schema : { type : "string" }
        }
    }
);