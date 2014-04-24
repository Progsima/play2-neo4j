/**
 * Directive that generate a field from json schema property.
 */
lgJsonschema.directive('lgJsonschemaField', ['lgJsonSchemaTypeConfig', function(lgJsonSchemaTypeConfig) {

    return {
        restrict: 'E',
        replace: true,
        templateUrl : './modules/lg-jsonschema/partials/directives/field.html',
        scope : {
            field : "=field",
            content : "=content"
        },
        controller: function ($scope, lgJsonSchemaTypeConfig) {
            $scope.templateField = lgJsonSchemaTypeConfig[$scope.field.id].field;
        }
    }
}]);
