/**
 * Factory for JSON Schema validation.
 */
lgJsonschema.service('lgJsonSchemaValidation', [function() {

    /**
     * Validate data with the specify json schema.
     *
     * @param schema JSON schema
     * @param data object to validate
     *
     * @return errors (if it's empty, it's good !).
     */
    this.validate = function (schema, data) {
        var result = tv4.validateMultiple(data, schema);
        var errors = [];
        if( !result.valid ) {
            for ( var i=0 ; i < result.errors.length ; i++ ) {
                var error = result.errors[i];

                var field;
                if ( error.dataPath != null && error.dataPath.length > 0 ) {
                    var code = "schema.properties";
                    var path = error.dataPath.split('/');
                    for ( var j=0 ; j < path.length ; j++ ) {
                        if ( path[j] !=  "" ) {
                            code += "['" + path[j] + "']";
                        }
                    }
                    field = eval(code);
                }
                else {
                    var path = error.schemaPath.split('/');
                    var code = "schema";
                    for ( var j=0 ; j < path.length ; j++ ) {
                        if ( path[j] !=  "" ) {
                            code += "['" + path[j] + "']";
                        }
                    }
                    var name = eval(code);
                    field = eval("schema.properties." + name);
                }

                errors.push({
                   code : error.code,
                   message : error.message,
                   field : field
                });
            }
        }
        return errors;
    };

}]);