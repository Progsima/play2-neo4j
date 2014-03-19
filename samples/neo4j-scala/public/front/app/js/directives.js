'use strict';
angular.module('neocms.directives', [])

    /**
    * Angular directive that display a form from a jsonSchema.
    */
    .directive('neocmsForm', function () {

        /**
         * The linked method of the directive.
         *
         * @param scope current scope of the directive
         * @param element the HTLM element of the directive
         * @param attrs array of attributes of the HTML element
         */
        function link(scope, element, attrs) {
            var jsonSchema = attrs.schema;
            var level = 0;

            /**
             * Parser of the JSON schema that will call in recursive mode.
             *
             * @param level level of the recursion
             * @param schema JSON schema of the level
             */
            function parseSchema(level, schema) {
            }

            /**
             * Retrieve the JSON schema from an URL that can be inline, locale or distante.
             *
             * @param url url of the json schema
             * @return the json schema retrieve
             */
            function retrieveJsonSchema(url){
            }
        };

        return {
            restrict: 'E',
            link: link
        };

    });