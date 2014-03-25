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
typesControllers.controller('Edit', ['$scope', '$location', 'Restangular', 'ngTableParams',
    function($scope, $location, Restangular, ngTableParams) {
        $scope.type  = {
            name: "My Name",
            title: "My title",
            description: "My description",
            fields: [""]
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
