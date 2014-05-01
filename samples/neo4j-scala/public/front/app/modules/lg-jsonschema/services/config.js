/**
 * Configuration of all type available in the application.
 *
 * Id of the item must be unique
 *   > name : will be display on the list choice of field type. It's only used to construct the schema (cf. type part of the module).
 *   > form : is the template location for the construction of the type (ie. validation rules. It's only used construct the schema (cf. type part of the module).
 *   > field : is the template location of the render of the item (ie. the input field form generated). It's only used when we render the json schema form.
 *   > schema : it's the json schema template of the field. The 'require' rule is outside. You can create your own type (for special render like date) but validation's rules are only json-schema one.
 */
lgJsonschema.value(
    "lgJsonSchemaTypeConfig",
    {
        "http://json-schema.logisima.com/integer" : {
            name: "Integer",
            form : "./modules/lg-jsonschema/partials/type/form/integer.html",
            field : "./modules/lg-jsonschema/partials/type/field/integer.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/integer\", title: \"{{title}}\", description: \"{{description}}\", type : \"integer\", minimum : {{minimum}}, maximum : {{maximum}} }"
        },
        "http://json-schema.logisima.com/float" : {
            name: "Float",
            form : "./modules/lg-jsonschema/partials/type/form/float.html",
            field : "./modules/lg-jsonschema/partials/type/field/float.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/float\" ,title: \"{{title}}\" ,description: \"{{description}}\", type : \"float\", minimum : {{minimum}}, maximum : {{maximum}} }"
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
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/string\" ,title: \"{{title}}\" ,description: \"{{description}}\", type : \"string\", minLength : {{minLength}}, maxLength : {{maxLength}} }"
        },
        "http://json-schema.logisima.com/pattern" : {
            name: "Pattern",
            form : "./modules/lg-jsonschema/partials/type/form/pattern.html",
            field : "./modules/lg-jsonschema/partials/type/field/pattern.html",
            schema : "\"{{name}}\" : { id: \"http://json-schema.logisima.com/string\" ,title: \"{{title}}\" ,description: \"{{description}}\", type : \"string\", minLength : {{minLength}}, maxLength : {{maxLength}} }"
        }
    }
);
