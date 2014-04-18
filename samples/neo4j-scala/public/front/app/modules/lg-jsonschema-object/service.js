/**
 * Factory for communication between Neo4j API and type model (ie. HTML forms).
 */
types.service('typeService', ['Restangular', 'typeValue',
    function(Restangular, typeValue){

        /**
         * Transform a model to a neo4j object.
         *
         * @param neo4jType
         * @param type
         * @returns {*}
         */
        this.updateNeo4jTypeWithForm = function(neo4jType, type) {
            // let's copy main neo4j fields
            neo4jType.name = type.name;
            neo4jType.description = type.description;

            // let's work on json schema
            var schema = {
                name: type.name,
                title : type.title,
                description : type.description,
                type: "object"
            };

            // Let's work on fields to generate properties
            var properties ="{";
            for ( var i=0 ; i < type.fields.length ; i++ ) {
                var field = type.fields[i];
                properties +=  this.schemaTemplate(type.fields[i].type.schema, field);
                if ( i < (type.fields.length  - 1) ) {
                    properties += ",";
                }
            }
            properties += "}";
            schema.properties = eval("(" + properties + ")");

            // finally adding schema to neo4j object
            neo4jType.schema = JSON.stringify(schema);

            return neo4jType;
        };

        /**
         * Transform a Neo4j object to a type model.
         *
         * @param neo4jType : type object that is given the API
         * @returns the object type that will be used for the form.
         */
        this.neo4j2Form = function(neo4jType) {
            // eval the json schema string
            var schema = eval( "(" + neo4jType.schema + ")");

            // create the form object
            var type = {
                name : neo4jType.name,
                title: schema.title,
                description : neo4jType.description,
                fields: []
            };

            for( var property in schema.properties) {
                var field = schema.properties[ property];
                field.name = property;
                field.type = typeValue[field.id];
                delete field.id;
                type.fields.push(field);
            }

            return type;
        };

        /**
         * Simple template generator (like mustache).
         * It helps to generate part of a JSON schema for a field.
         *
         * @param template : template
         * @param obj : object with properties that we should replace to the template.
         * @returns The string wanted
         */
        this.schemaTemplate = function(template, obj) {
            var schema = template;

            // we replace all variable with value that are available in the obj.
            for(var property in obj){
                schema = schema.replace("{{" + property + "}}" , obj[property]);
            }

            // we remove variable that haven't value uin obj.
            schema = schema.replace(/({{\w*}})/g, "null");  // replace {{var}} by null
            schema = schema.replace(/,[^,]*:\snull/g, ""); // we remove the entire json-schema line with no value

            return schema;
        };

    }
]);
