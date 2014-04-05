/**
 * Configuration of all type available in the application.
 */
types.value(
    "typeValue",
    {
        "http://json-schema.logisima.com/integer" : {
            name: "Integer",
            form : "./modules/types/partials/form/integer.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/integer\" ,title: \"{{title}}\" ,description: \"{{description}}\", type : \"integer\" , minimum : {{minimum}} , maximum : {{maximum}} , require : {{require}} }"
        },
        "http://json-schema.logisima.com/float" : {
            name: "Float",
            form : "./modules/types/partials/form/float.html",
            schema : { type : "number" }
        },
        "http://json-schema.logisima.com/boolean" : {
            name : "Boolean",
            form : "./modules/types/partials/form/boolean.html",
            schema : { type : "boolean" }
        },
        "http://json-schema.logisima.com/string" : {
            name: "String",
            form : "./modules/types/partials/form/string.html",
            schema : { type : "string" }
        }
    }
);
