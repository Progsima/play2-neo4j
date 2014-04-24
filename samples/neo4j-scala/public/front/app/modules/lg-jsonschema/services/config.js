/**
 * Configuration of all type available in the application.
 */
lgJsonschema.value(
    "lgJsonSchemaTypeConfig",
    {
        "http://json-schema.logisima.com/integer" : {
            name: "Integer",
            form : "./modules/lg-jsonschema/partials/type/form/integer.html",
            field : "./modules/lg-jsonschema/partials/type/field/integer.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/integer\", title: \"{{title}}\", description: \"{{description}}\", type : \"integer\", minimum : {{minimum}}, maximum : {{maximum}}, require : {{require}} }"
        },
        "http://json-schema.logisima.com/float" : {
            name: "Float",
            form : "./modules/lg-jsonschema/partials/type/form/float.html",
            field : "./modules/lg-jsonschema/partials/type/field/float.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/float\" ,title: \"{{title}}\" ,description: \"{{description}}\", type : \"float\", minimum : {{minimum}}, maximum : {{maximum}}, require : {{require}} }"
        },
        "http://json-schema.logisima.com/boolean" : {
            name : "Boolean",
            form : "./modules/lg-jsonschema/partials/type/form/boolean.html",
            field : "./modules/lg-jsonschema/partials/type/field/boolean.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/boolean\" ,title: \"{{title}}\" ,description: \"{{description}}\", type : \"boolean\", require : {{require}} }"
        },
        "http://json-schema.logisima.com/string" : {
            name: "String",
            form : "./modules/lg-jsonschema/partials/type/form/string.html",
            field : "./modules/lg-jsonschema/partials/type/field/string.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/string\" ,title: \"{{title}}\" ,description: \"{{description}}\", type : \"string\", minLength : {{minLength}}, maxLength : {{maxLength}}, require : {{require}} }"
        },
        "http://json-schema.logisima.com/pattern" : {
            name: "Pattern",
            form : "./modules/lg-jsonschema/partials/type/form/pattern.html",
            field : "./modules/lg-jsonschema/partials/type/field/pattern.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/string\" ,title: \"{{title}}\" ,description: \"{{description}}\", type : \"string\", minLength : {{minLength}}, maxLength : {{maxLength}}, require : {{require}}, pattern: {{pattern}} }"
        }
    }
);
