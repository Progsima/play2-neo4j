/**
 * Configuration of all type available in the application.
 */
lgJsonSchemaType.value(
    "lgJsonSchemaTypeConfig",
    {
        "http://json-schema.logisima.com/integer" : {
            name: "Integer",
            form : "./modules/types/partials/form/integer.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/integer\", title: \"{{title}}\", description: \"{{description}}\", type : \"integer\", minimum : {{minimum}}, maximum : {{maximum}}, require : {{require}} }"
        },
        "http://json-schema.logisima.com/float" : {
            name: "Float",
            form : "./modules/types/partials/form/float.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/float\" ,title: \"{{title}}\" ,description: \"{{description}}\", type : \"float\", minimum : {{minimum}}, maximum : {{maximum}}, require : {{require}} }"
        },
        "http://json-schema.logisima.com/boolean" : {
            name : "Boolean",
            form : "./modules/types/partials/form/boolean.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/boolean\" ,title: \"{{title}}\" ,description: \"{{description}}\", type : \"boolean\", require : {{require}} }"
        },
        "http://json-schema.logisima.com/string" : {
            name: "String",
            form : "./modules/types/partials/form/string.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/string\" ,title: \"{{title}}\" ,description: \"{{description}}\", type : \"string\", minLength : {{minLength}}, maxLength : {{maxLength}}, require : {{require}} }"
        },
        "http://json-schema.logisima.com/pattern" : {
            name: "Pattern",
            form : "./modules/types/partials/form/pattern.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/string\" ,title: \"{{title}}\" ,description: \"{{description}}\", type : \"string\", minLength : {{minLength}}, maxLength : {{maxLength}}, require : {{require}}, pattern: {{pattern}} }"
        }
    }
);
