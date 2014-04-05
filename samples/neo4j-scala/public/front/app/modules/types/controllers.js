/**
 * List all type avaible in the application.
 */
types.controller('List', ['$scope', '$location', 'Restangular', 'ngTableParams',
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
types.controller('Edit', ['$scope', 'Restangular', '$routeParams', 'typeValue', 'typeService',
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
            $scope.neo4jType = {};
            $scope.type = {};
        }

        // adding all available type
        $scope.types = typeValue;

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
            typeService.updateNeo4jTypeWithForm($scope.neo4jType, $scope.type);
            if (isNew) {
                $scope.neo4jType.post();
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
types.controller('Delete', ['$scope', '$location', '$routeParams','Restangular', 'ngTableParams',
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

/**
 * Create a new type
 */
types.controller('New', ['$scope', '$location', 'Restangular', 'ngTableParams',
    function($scope, $location, Restangular, ngTableParams) {

    }
]);
