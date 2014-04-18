/**
 * List all content of the specified type..
 */
lgJsonSchemaObject.controller('List', ['$routeParams', '$scope', '$location', 'Restangular', 'ngTableParams',
    function($routeParams, $scope, $location, Restangular, ngTableParams) {

        // TODO : what we if there is no type ?
        $scope.type = $routeParams.type;

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
                    Restangular.withConfig(function(RestangularConfigurer) {
                        RestangularConfigurer.setFullResponse(true);
                    }).all('contents/' + type).getList(
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
        $scope.fnDelete = function(uuid) {
            $location.path("/objects/" + uuid);
        };

        //
        // Function to go to edit page
        //
        $scope.fnEdit = function(uuid) {
            $location.path("/types/edit/" + uuid);
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
lgJsonSchemaObject.controller('Edit', ['$scope', 'Restangular', '$routeParams', 'typeValue', 'typeService',
    function($scope, Restangular, $routeParams, typeValue, typeService) {

        // configure Restangular
        Restangular.setRestangularFields({
            id: "uuid"
        });

    }
]);

/**
 * Delete a specific type.
 */
lgJsonSchemaObject.controller('Delete', ['$scope', '$location', '$routeParams','Restangular', 'ngTableParams',
    function($scope, $location, $routeParams, Restangular, ngTableParams) {

        // retrieve current element
        $scope.type = Restangular.one('contents/' + type, $routeParams.uuid).get().$object;

    }
]);

/**
 * Create a new type
 */
lgJsonSchemaObject.controller('New', ['$scope', '$location', 'Restangular', 'ngTableParams',
    function($scope, $location, Restangular, ngTableParams) {

    }
]);
