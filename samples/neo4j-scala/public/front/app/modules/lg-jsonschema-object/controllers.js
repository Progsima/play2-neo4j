/**
 * List all content of the specified type..
 */
lgJsonSchemaObject.controller('LgJsonSchemaObjectList', ['$routeParams', '$scope', '$location', 'Restangular', 'ngTableParams',
    function($routeParams, $scope, $location, Restangular, ngTableParams) {

        // TODO : what we if there is no type ? And if the type doesn't exist in database
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
                    }).all('contents/' + $scope.type).getList(
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
            $location.path("/objects/" + $scope.type + "/" + uuid);
        };

        //
        // Function to go to edit page
        //
        $scope.fnEdit = function(uuid) {
            $location.path("/types/edit/" + $scope.type + "/" +uuid);
        };

        //
        // Function to go to new page
        //
        $scope.fnNew = function() {
            $location.path("/types/" + $scope.type);
        };

    }
]);

/**
 * Edit a specific type.
 */
lgJsonSchemaObject.controller('LgJsonSchemaObjectEdit', ['$scope', 'Restangular', '$routeParams',
    function($scope, Restangular, $routeParams ) {

        $scope.content = {};

        // configure Restangular
        Restangular.setRestangularFields({
            id: "uuid"
        });

        if ($routeParams.type != null) {

            Restangular.one('types', $routeParams.type).get().then( function(type) {

                $scope.type = type;
                $scope.schema = eval( "(" + type.schema + ")");

                // init controller with data
                if ($routeParams.uuid != null) {
                    // retreive the element from database
                    $scope.content = Restangular.one('content/' + $scope.type.name, $routeParams.uuid).get().$object;
                }

            });

        }
        else {
            //redirect to error page
        }


        //
        // Function to save the object
        //
        $scope.fnSave = function() {
            console.log($scope.content);
        };

    }
]);

/**
 * Delete a specific type.
 */
lgJsonSchemaObject.controller('LgJsonSchemaObjectDelete', ['$scope', '$location', '$routeParams','Restangular', 'ngTableParams',
    function($scope, $location, $routeParams, Restangular, ngTableParams) {

        // retrieve current element
        $scope.type = Restangular.one('contents/' + type, $routeParams.uuid).get().$object;

    }
]);
