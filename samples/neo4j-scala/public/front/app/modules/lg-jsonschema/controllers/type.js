/**
 * List all type available in the application.
 */
lgJsonschema.controller('LgJsonSchemaTypeList', ['$scope', '$location', 'Restangular', 'ngTableParams',
    function($scope, $location, Restangular, ngTableParams) {

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
                    }).all('types').getList(
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

        //
        // Function to instantiate
        //
        $scope.fnInstantiate = function(type) {
            $location.path("/objects/" + type + "/new");
        };
    }
]);

/**
 * Edit a specific type.
 */
lgJsonschema.controller('LgJsonSchemaTypeEdit', ['$scope', 'Restangular', '$routeParams', 'lgJsonSchemaTypeService', 'lgJsonSchemaTypeConfig',
    function($scope, Restangular, $routeParams, lgJsonSchemaTypeService, lgJsonSchemaTypeConfig) {

        // configure Restangular
        Restangular.setRestangularFields({
            id: "name"
        });

        // init controller with data
        if($routeParams.name != null) {
            // retreive the element from database
            Restangular.one('types', $routeParams.name).get().then(function(neo4jType){
                $scope.neo4jType = neo4jType;
                $scope.type = lgJsonSchemaTypeService.neo4j2Form($scope.neo4jType);
            });
        }
        else {
            $scope.neo4jType = {};
            $scope.type = {};
        }

        // adding all available type
        $scope.types =  lgJsonSchemaTypeConfig;

        //
        // function to add a field
        //
        $scope.fnAddField = function() {
            if( $scope.type['fields'] == null ) {
                $scope.type.fields = [];
            }
            $scope.type.fields.push({
                "name" : "",
                "title" : "",
                "description" : ""
            });
        };

        //
        // function to remove a field
        //
        $scope.fnRemoveField = function(position) {
            $scope.type.fields = $scope.type.fields.filter(function(element){
                return position != $scope.type.fields.indexOf(element);
            });
        };

        //
        // function to save the type
        //
        $scope.fnSaveType = function() {
            var isNew = false;
            if($scope.neo4jType === null) {
                isNew = true;
            }
            lgJsonSchemaTypeService.updateNeo4jTypeWithForm($scope.neo4jType, $scope.type);
            if (!isNew) {
                $scope.neo4jType.put();
            }
            else {
                Restangular.all('types').post($scope.neo4jType);
            }
        };
    }
]);

/**
 * Delete a specific type.
 */
lgJsonschema.controller('LgJsonSchemaTypeDelete', ['$scope', '$location', '$routeParams','Restangular', 'ngTableParams',
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
        };

        //
        // The delete function for confirmation
        //
        $scope.fnDelete = function(name) {
            Restangular.one('types', name).remove().then(function(response){
                $scope.fnHome();
            });
        };

    }
]);
