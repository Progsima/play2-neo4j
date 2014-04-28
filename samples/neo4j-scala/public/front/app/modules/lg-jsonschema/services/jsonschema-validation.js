/**
 * Factory for JSON Schema validation.
 */
lgJsonschema.service('lgJsonSchemaValidation', [ function() {

    /**
     * Validate data with the specify json schema.
     *
     * @param schema JSON schema
     * @param data object to validate
     * @return errors (if it's empty, it's good !).
     */
    this.validate = function (schema, data) {
        var result = tv4.validateMultiple(data, schema);
        var errors = [];
        if( !result.valid ) {
            errors = result.errors.concat(result.missing);
        }
        return errors;
    };

}]);