/**
 * List all content of the specified type..
 */
lgJsonschema.controller('LgJsonSchemaObjectList', ['$routeParams', '$scope', '$location', 'Restangular', 'ngTableParams',
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
            $location.path("/contents/" + $scope.type + "/delete/" + uuid);
        };

        //
        // Function to go to edit page
        //
        $scope.fnEdit = function(uuid) {
            $location.path("/contents/" + $scope.type + "/edit/" +uuid);
        };

        //
        // Function to go to new page
        //
        $scope.fnNew = function() {
            $location.path("/contents/" + $scope.type + "/new");
        };

    }
]);

/**
 * Edit a specific type.
 */
lgJsonschema.controller('LgJsonSchemaObjectEdit', ['$scope', 'Restangular', '$routeParams',
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
                $scope.isNew = true;
                if ($routeParams.uuid != null) {
                    $scope.isNew = false;
                    // retreive the element from database
                    Restangular.one('contents/' + $scope.type.name, $routeParams.uuid).get().then(function(result){
                        // we dont use .$object due ti see https://github.com/mgonto/restangular/issues/579
                        $scope.content = result;
                    });
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
            if (!$scope.isNew) {
                $scope.content.put();
            }
            else {
                Restangular.all('contents/' + $scope.type.name).post($scope.content);
            }
        };

    }
]);

/**
 * Delete a specific type.
 */
lgJsonschema.controller('LgJsonSchemaObjectDelete', ['$scope', '$location', '$routeParams','Restangular', 'ngTableParams',
    function($scope, $location, $routeParams, Restangular, ngTableParams) {

        if ( $routeParams.type != null && $routeParams.uuid != null ) {
            $scope.type = $routeParams.type;
            $scope.uuid = $routeParams.uuid;

            // retrieve current element
            $scope.content = Restangular.one('contents/' + $scope.type, $scope.uuid).get().$object;
        }

        //
        // Return to home
        //
        $scope.fnHome = function() {
            $location.path('/contents/' + $scope.type);
        };

        //
        // The delete function for confirmation
        //
        $scope.fnDelete = function(name) {
            Restangular.one('contents/' + $scope.type, $scope.uuid).remove().then(function(response){
                $scope.fnHome();
            });
        };
    }
]);
