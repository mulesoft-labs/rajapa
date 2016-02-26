/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.impl.v10;

import org.raml.grammar.rule.*;
import org.raml.impl.BaseRamlGrammar;
import org.raml.types.factories.TypeNodeFactory;

public class Raml10Grammar extends BaseRamlGrammar
{

    public ObjectRule raml()
    {
        return super.raml()
                    .with(annotationTypesField())
                    .with(annotationField())
                    .with(typesField())
                    .with(usesField());
    }


    @Override
    protected ObjectRule resourceValue()
    {
        return super.resourceValue().with(annotationField());
    }

    @Override
    protected ObjectRule methodValue()
    {
        return super.methodValue()
                    .with(field(queryStringKey(), anyOf(stringType(), type())))
                    .with(annotationField());
    }

    @Override
    protected ObjectRule securitySchemePart()
    {
        return super.securitySchemePart().with(annotationField());
    }

    @Override
    protected ObjectRule response()
    {
        return super.response().with(annotationField());
    }

    protected KeyValueRule typesField()
    {
        return field(typesKey(), types());
    }

    protected StringValueRule typesKey()
    {
        return string("types")
                              .description("Declarations of (data) types for use within this API.");
    }


    // Common fields between rules
    protected KeyValueRule annotationField()
    {
        return field(annotationKey(), any());
    }

    protected RegexValueRule annotationKey()
    {
        return regex("\\(.+\\)")
                                .label("(Annotation)")
                                .suggest("(<cursor>)")
                                .description("Annotations to be applied to this API. " +
                                             "Annotations are any property whose key begins with \"(\" and ends with \")\" " +
                                             "and whose name (the part between the beginning and ending parentheses) " +
                                             "is a declared annotation name..");
    }


    protected KeyValueRule usesField()
    {
        return field(usesKey(), library());
    }

    // Library
    public Rule library()
    {
        return objectType("library")
                                    .with(field(
                                            stringType(),
                                            objectType()
                                                        .with(typesField())
                                                        .with(schemasField())
                                                        .with(resourceTypesField())
                                                        .with(traitsField())
                                                        .with(securitySchemesField())
                                                        .with(annotationTypesField())
                                                        .with(annotationField())
                                                        .with(field(usesKey(), ref("library")))
                                            )
                                    );
    }

    protected KeyValueRule annotationTypesField()
    {
        return field(annotationTypesKey(), annotationTypes());
    }

    protected StringValueRule annotationTypesKey()
    {
        return string("annotationTypes").description("Declarations of annotation types for use by annotations.");
    }

    protected Rule annotationTypes()
    {
        return objectType().with(field(stringType(), type()));
    }


    protected Rule types()
    {
        return objectType()
                           .with(field(stringType(), type()));
    }


    protected Rule parameter()
    {
        return type();
    }


    public Rule type()
    {
        // TODO schema example examples missing
        // TODO missing descriptions
        return objectType("type")
                                 .with(field(typeKey(), typeReference()))
                                 .with(displayNameField())
                                 .with(descriptionField())
                                 .with(annotationField())
                                 .with(exampleField())
                                 .with(
                                         when("type", // todo what to do with inherited does not match object
                                                 is(objectTypeLiteral())
                                                                        .add(field(string("properties"), properties()))
                                                                        .add(field(string("minProperties"), integerType()))
                                                                        .add(field(string("maxProperties"), integerType()))
                                                                        .add(field(string("additionalProperties"), anyOf(stringType(), ref("type"))))
                                                                        .add(field(string("patternProperties"), properties()))
                                                                        .add(field(string("discriminator"), anyOf(stringType(), booleanType())))
                                                                        .add(field(string("discriminatorValue"), stringType())),
                                                 is(arrayTypeLiteral())
                                                                       .add(field(string("uniqueItems"), booleanType()))
                                                                       .add(field(string("items"), any())) // todo review this don't get what it is
                                                                       .add(field(string("minItems"), integerType()))
                                                                       .add(field(string("maxItems"), integerType())),
                                                 is(stringTypeLiteral())
                                                                        .add(field(string("pattern"), stringType()))
                                                                        .add(field(string("minLength"), integerType()))
                                                                        .add(field(string("maxLength"), integerType()))
                                                                        .add(field(string("required"), booleanType()))
                                                                        .add(field(string("enum"), array(stringType()))),
                                                 is(numericTypeLiteral())
                                                                         .add(field(string("minimum"), integerType()))
                                                                         .add(field(string("maximum"), integerType()))
                                                                         .add(field(string("format"), stringType()))
                                                                         .add(field(string("multipleOf"), integerType()))
                                                                         .add(field(string("enum"), array(integerType()))),
                                                 is(fileTypeLiteral())
                                                                      .add(field(string("fileTypes"), any())) // todo finish
                                                                      .add(field(string("minLength"), integerType()))
                                                                      .add(field(string("maxLength"), integerType()))


                                         )
                                 ).then(new TypeNodeFactory())

        ;
    }

    protected KeyValueRule exampleField()
    {
        return field(string("example"), any());
    }

    private AnyOfRule typeReference()
    {
        return anyOf(objectTypeLiteral(),
                arrayTypeLiteral(),
                stringTypeLiteral(),
                numericTypeLiteral(),
                booleanTypeLiteral(),
                dateTypeLiteral(),
                fileTypeLiteral(),
                new TypeNodeReferenceRule("types"));
    }

    protected StringValueRule fileTypeLiteral()
    {
        return string("file");
    }

    protected Rule numericTypeLiteral()
    {
        return anyOf(numberTypeLiteral(), integerTypeLiteral());
    }

    protected Rule numberTypeLiteral()
    {
        return string("number");
    }

    protected Rule integerTypeLiteral()
    {
        return string("integer");
    }

    protected Rule booleanTypeLiteral()
    {
        return string("boolean");
    }

    protected StringValueRule stringTypeLiteral()
    {
        return string("string");
    }

    protected StringValueRule dateTypeLiteral()
    {
        return string("date");
    }

    protected RegexValueRule arrayTypeLiteral()
    {
        return regex(".+\\[\\]");
    }

    protected ObjectRule properties()
    {
        return objectType()
                           .with(field(stringType(), ref("type")));
    }

    protected Rule objectTypeLiteral()
    {
        return allOf(anyOf(string("object"), regex("^(?:[^|]+(?:\\|[^|]+)*)?$")), not(anyBuiltinType()));
    }


}
