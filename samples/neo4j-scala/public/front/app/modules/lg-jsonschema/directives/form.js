/**
 * Directive that generate a form from a json schema.
 */
lgJsonschema.directive('lgJsonschemaForm', ['lgJsonSchemaValidation', function( lgJsonSchemaValidation){

    return {
        restrict: 'E',
        templateUrl: './modules/lg-jsonschema/partials/directives/form.html',
        replace: true,
        scope : {
            schema : "=schema",
            content : "=content",
            form : '=name',
            submit : '&submit'
        },
        controller: function ($scope, lgJsonSchemaValidation) {
            $scope.fields = [];

            $scope.onSubmit = function(){
                $scope.errors = lgJsonSchemaValidation.validate($scope.schema, $scope.content);
                if( $scope.errors.length == 0 ) {
                    $scope.submit();
                }
            }

            $scope.$watch('schema', function(newValue, oldValue) {
                if( $scope.schema != null ) {
                    for( var property in $scope.schema.properties) {
                        var field = $scope.schema.properties[property];
                        field.name = property;

                        // is require ?
                        if ( $scope.schema.required != null && _.contains($scope.schema.required, property) ) {
                            field.require = true;
                        }

                        $scope.fields.push(field);
                    }
                }
            });

        },
        compile: function (scope, elm, attrs, ctrl) {
            return {
                post: function (scope, elm, attrs, ctrl) {
                    //Post gets called after angular has created the FormController
                    //Now pass the FormController back up to the parent scope
                    scope.form = scope[attrs.name];
                }
            };
        }
    };
}]);
