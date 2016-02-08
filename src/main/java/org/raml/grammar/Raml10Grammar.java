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
package org.raml.grammar;

import com.google.common.collect.Range;
import org.raml.grammar.rule.*;
import org.raml.nodes.impl.*;

import java.math.BigInteger;

public class Raml10Grammar extends BaseGrammar
{

    public static final String USES_KEY_NAME = "uses";
    public static final String RESOURCE_TYPES_KEY_NAME = "resourceTypes";
    public static final String TRAITS_KEY_NAME = "traits";
    public static final String SECURITY_SCHEMES_KEY_NAME = "securitySchemes";

    public Rule raml()
    {
        return mapping()
                        .with(descriptionField())
                        .with(annotationField())
                        .with(schemasField())
                        .with(typesField())
                        .with(traitsField())
                        .with(resourceTypesField())
                        .with(annotationTypesField())
                        .with(securitySchemesField())
                        .with(field(usesKey(), library()))
                        .with(titleField().description("Short plain-text label for the API."))
                        .with(field(versionKey(), stringType()))
                        .with(field(baseUriKey(), stringType()))
                        .with(field(baseUriParametersKey(), parameters()))
                        .with(protocolsField())
                        .with(field(mediaTypeKey(), stringType()))
                        .with(securedByField().description("The security schemes that apply to every resource and method in the API."))
                        .with(field(resourceKey(), resourceValue()).then(ResourceNode.class))
                        .with(nonOptionalField(documentationKey(), documentations()))
                        .then(RamlDocumentNode.class);
    }

    public Rule resourceType()
    {
        return mapping()
                        .with(field(anyResourceTypeMethod(), methodValue()).then(MethodNode.class))
                        .with(resourceTypeReferenceField())
                        .with(field(stringType(), any())); // match anything else
    }

    // Documentation
    private Rule documentations()
    {
        return array(documentation());
    }

    private Rule documentation()
    {
        return mapping()
                        .with(titleField().description("Title of documentation section."))
                        .with(contentField().description("Content of documentation section."));
    }

    private KeyValueRule contentField()
    {
        return requiredField(string("content"), stringType());
    }


    // Security scheme

    private Rule securitySchemes()
    {
        return mapping()
                        .with(
                                field(stringType(), securityScheme())
                                                                     .then(SecuritySchemeNode.class)
                        );
    }

    private Rule securityScheme()
    {
        return mapping()
                        .with(descriptionField())
                        .with(field(
                                typeKey(),
                                anyOf(
                                        string("OAuth 1.0").description("The API's authentication uses OAuth 1.0 as described in RFC5849 [RFC5849]"),
                                        string("OAuth 2.0").description("The API's authentication uses OAuth 2.0 as described in RFC6749 [RFC6749]"),
                                        string("BasicSecurityScheme Authentication").description(
                                                "The API's authentication uses BasicSecurityScheme Access Authentication as described in RFC2617 [RFC2617]"),
                                        string("DigestSecurityScheme Authentication").description(
                                                "The API's authentication uses DigestSecurityScheme Access Authentication as described in RFC2617 [RFC2617]"),
                                        string("Pass Through").description("Headers or Query Parameters are passed through to the API based on a defined mapping."),
                                        regex("x-.+").description("The API's authentication uses an authentication method not listed above.")
                                )))
                        .with(field(string("describedBy"), securitySchemePart()))
                        .with(field(string("settings"), securitySchemeSettings()));
    }

    private Rule securitySchemePart()
    {
        return mapping()
                        .with(displayNameField())
                        .with(descriptionField())
                        .with(annotationField())
                        .with(field(headersKey(), parameters()))
                        .with(field(queryParametersKey(), parameters()))
                        .with(field(responseKey(), responses()));
    }

    private Rule securitySchemeSettings()
    {
        return mapping()
                        .with(field(string("requestTokenUri"), stringType()))
                        .with(field(string("authorizationUri"), stringType()))
                        .with(field(string("tokenCredentialsUri"), stringType()))
                        .with(field(string("accessTokenUri"), stringType()))
                        .with(field(string("authorizationGrants"), array(stringType())))
                        .with(field(string("scopes"), array(stringType())));
    }

    // Types

    private Rule types()
    {
        return mapping()
                        .with(field(stringType(), type()));
    }

    private Rule type()
    {
        // TODO schema example examples missing
        return mapping("type")
                              .with(field(typeKey(), typeReference()))
                              .with(displayNameField())
                              .with(descriptionField())
                              .with(annotationField())
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
                                                                     .add(field(string("enum"), array(stringType()))),
                                              is(numberTypeLiteral())
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
                              )

        ;
    }

    private AnyOfRule typeReference()
    {
        return anyOf(objectTypeLiteral(),
                arrayTypeLiteral(),
                stringTypeLiteral(),
                numberTypeLiteral(),
                string("boolean"),
                string("date"),
                fileTypeLiteral(),
                regex("[A-z]+|[A-z]+"),
                stringType());
    }

    private StringValueRule fileTypeLiteral()
    {
        return string("file");
    }

    private Rule numberTypeLiteral()
    {
        return anyOf(string("number"), string("integer"));
    }

    private StringValueRule stringTypeLiteral()
    {
        return string("string");
    }

    private RegexValueRule arrayTypeLiteral()
    {
        return regex(".+\\[\\]");
    }

    private ObjectRule properties()
    {
        return mapping()
                        .with(field(stringType(), ref("type")));
    }

    private StringValueRule objectTypeLiteral()
    {
        return string("object");
    }

    // Traits
    private Rule traits()
    {
        return anyOf(array(trait()), trait());
    }

    private Rule trait()
    {
        // TODO resourceRule().with(parameterKey(), any())
        return mapping("trait")
                               .with(field(stringType(), any())
                                                               .then(TraitNode.class));
    }

    // Resource Types
    private Rule resourceTypes()
    {
        // TODO resourceRule().with(parameterKey(), any())
        return mapping().with(field(stringType(), any()).then(ResourceTypeNode.class));
    }

    // Library
    private Rule library()
    {
        return mapping("library")
                                 .with(field(
                                         stringType(),
                                         mapping()
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


    // Resources
    private Rule resourceValue()
    {
        return mapping("resourceValue")
                                       .with(displayNameField())
                                       .with(descriptionField())
                                       .with(annotationField())
                                       .with(field(anyMethod(), methodValue()).then(MethodNode.class))
                                       .with(isField().description("A list of the traits to apply to all methods declared (implicitly or explicitly) for this resource. "))
                                       .with(resourceTypeReferenceField())
                                       .with(securedByField().description("The security schemes that apply to all methods declared (implicitly or explicitly) for this resource."))
                                       .with(field(uriParametersKey(), parameters()))
                                       .with(field(resourceKey(), ref("resourceValue")).then(ResourceNode.class));
    }


    private Rule schemas()
    {
        return mapping()
                        .with(field(stringType(), stringType()));
    }

    // Method
    private Rule methodValue()
    {
        // TODO query string
        return mapping()
                        .with(descriptionField())
                        .with(displayNameField())
                        .with(annotationField())
                        .with(field(queryParametersKey(), parameters()))
                        .with(headersField())
                        .with(field(queryStringKey(), anyOf(stringType(), type())))
                        .with(field(responseKey(), responses()))
                        .with(bodyField())
                        .with(protocolsField().description("A method can override the protocols specified in the resource or at the API root, by employing this property."))
                        .with(isField()
                                       .description("A list of the traits to apply to this method."))
                        .with(securedByField().description("The security schemes that apply to this method."));
    }

    private StringValueRule responseKey()
    {
        return string("responses")
                                  .description("Information about the expected responses to a request");
    }

    private StringValueRule queryStringKey()
    {
        return string("queryString")
                                    .description("Specifies the query string needed by this method." +
                                                 " Mutually exclusive with queryParameters.");
    }

    private StringValueRule queryParametersKey()
    {
        return string("queryParameters")
                                        .description("Detailed information about any query parameters needed by this method. " +
                                                     "Mutually exclusive with queryString.");
    }


    private Rule responses()
    {
        return mapping()
                        .with(field(responseCodes(), response()));
    }

    private Rule response()
    {
        return mapping()
                        .with(displayNameField())
                        .with(descriptionField())
                        .with(annotationField())
                        .with(headersField())
                        .with(bodyField());
    }

    private Rule body()
    {
        return mapping().with(field(regex("[A-z-_]+\\/[A-z-_]+"), mimeType()));
    }

    private Rule mimeType()
    {
        return mapping()
                        .with(field(string("schema"), stringType()))
                        .with(field(string("example"), stringType()));
    }

    private Rule parameters()
    {
        return mapping().with(field(stringType(), parameter()));
    }

    private Rule parameter()
    {
        // TODO review type in raml 1.0 with the type system???
        // TODO review defaultValue
        // TODO review example
        return mapping()
                        .with(displayNameField())
                        .with(descriptionField())
                        .with(field(typeKey(), stringType()))
                        .with(field(string("required"), booleanType()))
                        .with(field(string("default"), any()))
                        .with(field(string("example"), stringType()));
    }


    // Common fields between rules
    private KeyValueRule annotationField()
    {
        return field(annotationKey(), any());
    }

    private KeyValueRule securitySchemesField()
    {
        return field(securitySchemesKey(), anyOf(array(securitySchemes()), securitySchemes()));
    }

    private StringValueRule securitySchemesKey()
    {
        return string(SECURITY_SCHEMES_KEY_NAME).description("Declarations of security schemes for use within this API.");
    }

    private KeyValueRule annotationTypesField()
    {
        return field(annotationTypesKey(), annotationTypes());
    }

    private StringValueRule annotationTypesKey()
    {
        return string("annotationTypes").description("Declarations of annotation types for use by annotations.");
    }

    private Rule annotationTypes()
    {
        return mapping().with(field(stringType(), type()));
    }


    private KeyValueRule schemasField()
    {
        return field(schemasKey(), anyOf(array(schemas()), schemas()));
    }

    private StringValueRule schemasKey()
    {
        return string("schemas")
                                .description("Alias for the equivalent \"types\" property, for compatibility " +
                                             "with RAML 0.8. Deprecated - API definitions should use the \"types\" property, " +
                                             "as the \"schemas\" alias for that property name may be removed in a future RAML version. " +
                                             "The \"types\" property allows for XML and JSON schemas.");
    }

    private KeyValueRule resourceTypesField()
    {
        return field(resourceTypesKey(), anyOf(array(resourceTypes()), resourceTypes()));
    }

    private StringValueRule resourceTypesKey()
    {
        return string(RESOURCE_TYPES_KEY_NAME).description("Declarations of resource types for use within this API.");
    }

    private KeyValueRule traitsField()
    {
        return field(traitsKey(), traits());
    }

    private StringValueRule traitsKey()
    {
        return string(TRAITS_KEY_NAME).description("Declarations of traits for use within this API.");
    }

    private KeyValueRule protocolsField()
    {
        return field(protocolsKey(), protocols());
    }

    private StringValueRule protocolsKey()
    {
        return string("protocols").description("The protocols supported by the API.");
    }

    private KeyValueRule bodyField()
    {
        return field(bodyKey(), body());
    }

    private StringValueRule bodyKey()
    {
        return string("body")
                             .description("Some methods admit request bodies, which are described by this property.");
    }

    private KeyValueRule headersField()
    {
        return field(headersKey(), parameters());
    }

    private StringValueRule headersKey()
    {
        return string("headers")
                                .description("Detailed information about any request headers needed by this method.");
    }

    private KeyValueRule descriptionField()
    {
        return field(descriptionKey(), stringType());
    }

    private KeyValueRule displayNameField()
    {
        return field(displayNameKey(), stringType());
    }

    private KeyValueRule securedByField()
    {
        return field(securedByKey(), array(stringType().then(new NodeReferenceFactory(SecuritySchemeRefNode.class))));
    }

    private KeyValueRule isField()
    {
        return field(isKey(), array(stringType().then(new NodeReferenceFactory(TraitRefNode.class))));
    }

    private KeyValueRule resourceTypeReferenceField()
    {
        return field(typeKey(), stringType().then(new NodeReferenceFactory(ResourceTypeRefNode.class)));
    }

    private KeyValueRule typesField()
    {
        return field(typesKey(), types());
    }

    private StringValueRule typesKey()
    {
        return string("types").description("Declarations of (data) types for use within this API.");
    }

    private KeyValueRule titleField()
    {
        return requiredField(titleKey(), stringType());
    }

    private StringValueRule titleKey()
    {
        return string("title");
    }


    // Repeated keys

    private RegexValueRule resourceKey()
    {
        return regex("/.+")
                           .label("/Resource")
                           .suggest("/<cursor>")
                           .description("The resources of the API, identified as relative URIs that begin with a slash (/). " +
                                        "Every property whose key begins with a slash (/), and is either at the root of the API definition " +
                                        "or is the child property of a resource property, is a resource property, e.g.: /users, /{groupId}, etc");
    }

    private StringValueRule usesKey()
    {
        return string(USES_KEY_NAME).description("Importing libraries.");
    }


    private StringValueRule uriParametersKey()
    {
        return string("uriParameters").description("Detailed information about any URI parameters of this resourc");
    }

    private StringValueRule securedByKey()
    {
        return string("securedBy");
    }

    private StringValueRule typeKey()
    {
        return string("type")
                             .description("The resource type which this resource inherits.");
    }

    private StringValueRule isKey()
    {
        return string("is");
    }

    private StringValueRule displayNameKey()
    {
        return string("displayName")
                                    .description("An alternate, human-friendly name for the method (in the resource's context).");
    }

    private RegexValueRule annotationKey()
    {
        return regex("\\(.+\\)")
                                .label("(Annotation)")
                                .suggest("(<cursor>)")
                                .description("Annotations to be applied to this API. " +
                                             "Annotations are any property whose key begins with \"(\" and ends with \")\" " +
                                             "and whose name (the part between the beginning and ending parentheses) " +
                                             "is a declared annotation name..");
    }

    private StringValueRule descriptionKey()
    {
        return string("description").description("A longer, human-friendly description of the API");
    }

    private StringValueRule documentationKey()
    {
        return string("documentation")
                                      .description("Additional overall documentation for the API.");
    }

    private StringValueRule mediaTypeKey()
    {
        return string("mediaType")
                                  .description("The default media type to use for request and response bodies (payloads), e.g. \"application/json\".");
    }

    private StringValueRule baseUriParametersKey()
    {
        return string("baseUriParameters").description("Named parameters used in the baseUri (template).");
    }

    private StringValueRule baseUriKey()
    {
        return string("baseUri").description("A URI that's to be used as the base of all the resources' URIs." +
                                             " Often used as the base of the URL of each resource, containing the location of the API. " +
                                             "Can be a template URI.");
    }

    private StringValueRule versionKey()
    {
        return string("version").description("The version of the API, e.g. \"v1\".");
    }


    // Enum of values

    private AnyOfRule anyMethod()
    {
        return anyOf(
                string("get"),
                string("patch"),
                string("put"),
                string("post"),
                string("delete"),
                string("options"),
                string("head"));
    }

    private AnyOfRule anyOptionalMethod()
    {
        return anyOf(string("get?"), string("patch?"), string("put?"), string("post?"), string("delete?"), string("options?"), string("head?"));
    }

    private AnyOfRule anyResourceTypeMethod()
    {
        return anyOf(anyMethod(), anyOptionalMethod());
    }

    private Rule protocols()
    {
        return array(anyOf(string("HTTP"), string("HTTPS")));
    }

    private Rule responseCodes()
    {
        return range(Range.closed(new BigInteger("100"), new BigInteger("599")));
    }
}
