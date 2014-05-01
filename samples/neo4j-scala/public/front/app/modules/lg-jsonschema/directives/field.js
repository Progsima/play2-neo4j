/**
 * Directive that generate a field from json schema property.
 */
lgJsonschema.directive('lgJsonschemaField', ['lgJsonSchemaTypeConfig', function(lgJsonSchemaTypeConfig) {

    return {
        restrict: 'E', // only activate on element
        replace: true,
        templateUrl : './modules/lg-jsonschema/partials/directives/field.html',
        controller: function ($scope, lgJsonSchemaTypeConfig) {
            $scope.templateField = lgJsonSchemaTypeConfig[$scope.field.id].field;
        }
    };
}]);


/**
 * Directive that make an input field model nullable.
 * Instead of returning an empty string when input is empty, it's return null.
 */
lgJsonschema.directive('lgFieldUndefined', [function() {

    return {
        restrict: 'A', // only activate on element attribute
        require: '?ngModel', // get a hold of NgModelController
        link: function (scope, elm, attrs, ngModel) {

            // do nothing if no ng-model
            if (!ngModel) return;

            var inputType = angular.lowercase(attrs.type);

            // do nhtong if it's an input radio or checkbox
            if (!ngModel || inputType === 'radio' || inputType === 'checkbox') return;

            // let's modify the model parser
            ngModel.$parsers.push(function(value) {
                if ((ngModel.$invalid && angular.isUndefined(value)) || value === '') {
                    return undefined;
                } else {
                    return value;
                }
            });
        }
    }
}]);