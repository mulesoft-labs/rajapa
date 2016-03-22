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
package org.raml.impl.v10.grammar;

import org.raml.grammar.rule.AnyOfRule;
import org.raml.grammar.rule.KeyValueRule;
import org.raml.grammar.rule.ObjectRule;
import org.raml.grammar.rule.RegexValueRule;
import org.raml.grammar.rule.Rule;
import org.raml.grammar.rule.StringValueRule;
import org.raml.grammar.rule.TypeNodeReferenceRule;
import org.raml.impl.commons.grammar.BaseRamlGrammar;
import org.raml.impl.commons.nodes.AnnotationNode;
import org.raml.impl.commons.nodes.AnnotationTypeNode;
import org.raml.impl.commons.nodes.ExtendsNode;
import org.raml.impl.commons.nodes.PropertyNode;
import org.raml.impl.v10.nodes.types.factories.TypeNodeFactory;
import org.raml.nodes.StringNodeImpl;

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
        return field(annotationKey(), any()).then(AnnotationNode.class);
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

    // Extension
    public Rule extension()
    {
        return untitledRaml()
                             .with(requiredField(extendsKey(), stringType()).then(ExtendsNode.class))
                             .with(optionalTitleField());
    }

    protected StringValueRule extendsKey()
    {
        return string("extends").description("The path to the base RAML document to be extended.");
    }

    protected KeyValueRule optionalTitleField()
    {
        return field(titleKey(), stringType());
    }


    // Library
    public Rule library()
    {
        return objectType("library").with(field(stringType(), libraryValue()));
    }

    public Rule libraryValue()
    {
        return objectType()
                           .with(typesField())
                           .with(schemasField())
                           .with(resourceTypesField())
                           .with(traitsField())
                           .with(securitySchemesField())
                           .with(annotationTypesField())
                           .with(annotationField())
                           .with(field(usesKey(), ref("library")))
                           .with(field(string("usage"), stringType()));
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
        return objectType().with(field(stringType(), type()).then(AnnotationTypeNode.class));
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
                                 .with(defaultField())
                                 .with(field(string("required"), booleanType()))
                                 .with(exampleField())
                                 .with(
                                         when("type", // todo what to do with inherited does not match object
                                                 is(stringTypeLiteral())
                                                                        .add(field(string("pattern"), stringType()))
                                                                        .add(field(string("minLength"), integerType()))
                                                                        .add(field(string("maxLength"), integerType()))
                                                                        .add(field(string("enum"), array(stringType()))),
                                                 is(arrayTypeLiteral())
                                                                       .add(field(string("uniqueItems"), booleanType()))
                                                                       .add(field(string("items"), any())) // todo review this don't get what it is
                                                                       .add(field(string("minItems"), integerType()))
                                                                       .add(field(string("maxItems"), integerType())),
                                                 is(numericTypeLiteral())
                                                                         .add(field(string("minimum"), integerType()))
                                                                         .add(field(string("maximum"), integerType()))
                                                                         .add(field(string("format"), stringType()))
                                                                         .add(field(string("multipleOf"), integerType()))
                                                                         .add(field(string("enum"), array(integerType()))),
                                                 is(fileTypeLiteral())
                                                                      .add(field(string("fileTypes"), any())) // todo finish
                                                                      .add(field(string("minLength"), integerType()))
                                                                      .add(field(string("maxLength"), integerType())),
                                                 is(objectTypeLiteral())
                                                                        .add(field(string("properties"), properties()))
                                                                        .add(field(string("minProperties"), integerType()))
                                                                        .add(field(string("maxProperties"), integerType()))
                                                                        .add(field(string("additionalProperties"), anyOf(stringType(), ref("type"))))
                                                                        .add(field(string("patternProperties"), properties()))
                                                                        .add(field(string("discriminator"), anyOf(stringType(), booleanType())))
                                                                        .add(field(string("discriminatorValue"), stringType()))


                                         ).defaultValue(new StringNodeImpl("string"))
                                 ).then(new TypeNodeFactory())

        ;
    }

    protected KeyValueRule exampleField()
    {
        return field(string("example"), any());
    }

    private KeyValueRule defaultField()
    {
        return field(string("default"), any());
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

    @Override
    protected ObjectRule mimeType()
    {
        return super.mimeType().merge((ObjectRule) type());
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
                           .with(field(stringType(), ref("type")).then(PropertyNode.class));
    }

    protected Rule objectTypeLiteral()
    {
        return not(anyBuiltinType());
    }

    protected KeyValueRule mediaTypeField()
    {
        return field(mediaTypeKey(), anyOf(stringType(), array(stringType())));
    }

}
