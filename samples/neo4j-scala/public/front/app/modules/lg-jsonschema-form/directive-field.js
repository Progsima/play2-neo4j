/**
 * Directive that generate a field from json schema property.
 */
lgJsonschemaForm.directive('lgJsonschemaField', function(lgJsonSchemaTypeConfig){

    return {
        restrict: 'E',
        replace: true,
        templateUrl : './modules/lg-jsonschema-form/partials/field/field.html',
        controller: function ($scope, lgJsonSchemaTypeConfig) {
            $scope.templateField = lgJsonSchemaTypeConfig[$scope.field.id].field;
        }
    }
});
