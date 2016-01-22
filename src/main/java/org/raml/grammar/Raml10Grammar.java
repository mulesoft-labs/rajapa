/*
 *
 */
package org.raml.grammar;

import com.google.common.collect.Range;
import org.raml.grammar.rule.*;
import org.raml.nodes.impl.*;

public class Raml10Grammar extends BaseGrammar
{

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
                        .with(field(string("title"), stringType()))
                        .with(field(string("version"), stringType()))
                        .with(field(string("baseUri"), stringType()))
                        .with(field(string("baseUriParameters"), parameters()))
                        .with(protocolsField())
                        .with(field(string("mediaType"), stringType()))
                        .with(securedByField())
                        .with(field(resourceKey(), resource()))
                        .with(field(string("documentation"), documentations()))
                        .then(RamlDocumentNode.class);
    }

    private Rule documentations()
    {
        return array(documentation());
    }

    private Rule documentation()
    {
        return mapping()
                        .with(field(string("title"), stringType()))
                        .with(field(string("content"), stringType()));
    }


    private Rule securitySchemes()
    {
        return anyOf(array(mapping()), mapping());
    }

    private KeyValueRule typesField()
    {
        return field(string("types"), types());
    }

    private Rule types()
    {
        // TODO implement types
        return any();
    }


    private Rule protocols()
    {
        return array(anyOf(string("http"), string("https")));
    }

    private Rule traits()
    {
        return anyOf(array(trait()), trait());
    }

    private Rule library()
    {
        return mapping("library")
                                 .with(typesField())
                                 .with(schemasField())
                                 .with(traitsField())
                                 .with(securitySchemesField())
                                 .with(annotationTypesField())
                                 .with(annotationField())
                                 .with(field(usesKey(), ref("library")));
    }


    private Rule trait()
    {
        // TODO resourceRule().with(parameterKey(), any())
        return mapping("trait")
                               .with(field(stringType(), any())
                                                               .then(TraitNode.class));
    }

    private Rule resourceTypes()
    {
        // TODO resourceRule().with(parameterKey(), any())
        return mapping().with(field(stringType(), any()).then(ResourceTypeNode.class));
    }

    // private Rule resourceType() {
    // return mapping("resourceType")
    // .with(field(template(), any()))
    // .with(field(displayNameKey(), anyOf(template(), stringType())))
    // .with(field(descriptionKey(), anyOf(template(), stringType())))
    // .with(field(annotationKey(), any()))
    // .with(field(anyMethod(), any()))
    // .with(field(isKey(), array(anyOf(template(), stringType()))))
    // .with(field(typeKey(), anyOf(template(), stringType())))
    // .with(field(securedByKey(), array(anyOf(template(), stringType()))))
    // .with(field(uriParametersKey(), any()))
    // .with(field(resourceKey(), ref("resourceType")))
    // ;
    // return any();
    // }

    private Rule resource()
    {
        return mapping("resource")
                                  .with(displayNameField())
                                  .with(descriptionField())
                                  .with(annotationField())
                                  .with(field(anyMethod(), method()))
                                  .with(isField())
                                  .with(field(typeKey(), stringType().then(ResourceTypeRefNode.class)))
                                  .with(securedByField())
                                  .with(field(uriParametersKey(), parameters()))
                                  .with(field(resourceKey(), ref("resource")))
                                  .then(ResourceNode.class);
    }

    private StringValueRule displayNameKey()
    {
        return string("displayName");
    }

    private Rule schemas()
    {
        return anyOf(array(schema()), schema());
    }

    private Rule schema()
    {
        return mapping()
                        .with(field(stringType(), stringType()));
    }


    private Rule method()
    {
        // TODO query string
        return mapping()
                        .with(descriptionField())
                        .with(displayNameField())
                        .with(annotationField())
                        .with(field(string("queryParameters"), parameters()))
                        .with(headersField())
                        .with(field(string("queryString"), anyOf(stringType(), type())))
                        .with(field(string("responses"), responses()))
                        .with(bodyField())
                        .with(protocolsField())
                        .with(isField())
                        .with(securedByField())
                        .then(MethodNode.class);
    }

    private Rule type()
    {
        return any();
    }

    private Rule responses()
    {
        return mapping().with(field(responseCodes(), response()));
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
        return field(string("securitySchemes"), securitySchemes());
    }

    private KeyValueRule annotationTypesField()
    {
        return field(string("annotationTypes"), annotationTypes());
    }

    private Rule annotationTypes()
    {
        return mapping().with(field(stringType(), type()));
    }


    private KeyValueRule schemasField()
    {
        return field(string("schemas"), schemas());
    }

    private KeyValueRule resourceTypesField()
    {
        return field(string("resourceTypes"), anyOf(array(resourceTypes()), resourceTypes()));
    }

    private KeyValueRule traitsField()
    {
        return field(string("traits"), traits());
    }

    private KeyValueRule protocolsField()
    {
        return field(string("protocols"), protocols());
    }

    private KeyValueRule bodyField()
    {
        return field(string("body"), body());
    }

    private KeyValueRule headersField()
    {
        return field(string("headers"), parameters());
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
        return field(securedByKey(), array(stringType().then(SecurityRefNode.class)));
    }

    private KeyValueRule isField()
    {
        return field(isKey(), array(stringType().then(TraitRefNode.class)));
    }

    private RegexValueRule resourceKey()
    {
        return regex("/.+");
    }

    private StringValueRule usesKey()
    {
        return string("uses");
    }


    private StringValueRule uriParametersKey()
    {
        return string("uriParameters");
    }

    private StringValueRule securedByKey()
    {
        return string("securedBy");
    }

    private StringValueRule typeKey()
    {
        return string("type");
    }

    private StringValueRule isKey()
    {
        return string("is");
    }

    private RegexValueRule annotationKey()
    {
        return regex("\\(.+\\)");
    }

    private StringValueRule descriptionKey()
    {
        return string("description");
    }

    private AnyOfRule anyMethod()
    {
        return anyOf(string("get"), string("patch"), string("put"), string("post"), string("delete"), string("options"), string("head"));
    }

    private Rule responseCodes()
    {
        return range(Range.closed(100, 599));
    }
}
