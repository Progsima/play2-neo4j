'use strict';

var typesControllers = angular.module('typesControllers', ['ngRoute', 'restangular', 'ngTable', 'neocms.directives']);

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
        $scope.delete = function(name) {
            $location.url = "/types/delete/" + name;
        };
        $scope.edit = function(name) {
            $location.url = "/types/edit/" + name;
        };
        $scope.new = function() {
            $location.url = "/types/new";
        };
    }
]);

/**
 * Edit a specific type.
 */
typesControllers.controller('Edit', ['$scope', '$location', 'Restangular', 'ngTableParams',
    function($scope, $location, Restangular, ngTableParams) {

    }
]);

/**
 * Delete a specific type.
 */
typesControllers.controller('Delete', ['$scope', '$location', 'Restangular', 'ngTableParams',
    function($scope, $location, Restangular, ngTableParams) {

    }
]);

/**
 * Create a new type
 */
typesControllers.controller('New', ['$scope', '$location', 'Restangular', 'ngTableParams',
    function($scope, $location, Restangular, ngTableParams) {

    }
]);
