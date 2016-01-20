package org.raml.grammar;

import com.google.common.collect.Range;
import org.raml.grammar.rule.*;
import org.raml.nodes.impl.*;

public class Raml10Grammar extends BaseGrammar
{

    public Rule raml()
    {
        return mapping()
                        .with(field(string("title"), stringType()))
                        .with(field(string("version"), stringType()))
                        .with(field(string("baseUri"), stringType()))
                        .with(field(string("mediaType"), stringType()))
                        .with(field(string("baseUriParameters"), parameters()))
                        .with(field(string("schemas"), schemas()))
                        .with(field(string("resourceTypes"), resourceTypes()))
                        .with(protocolsField())
                        .with(field(resourceKey(), resource()))
                        .then(RamlDocumentNode.class);
    }

    private KeyValueRule protocolsField()
    {
        return field(string("protocols"), protocols());
    }

    private Rule protocols()
    {
        return array(anyOf(string("http"), string("https")));
    }

    private Rule resourceTypes()
    {
        return anyOf(array(resourceType()), resourceType());
    }

    private Rule resourceType()
    {
        // TODO resourceRule().with(parameterKey(), any())
        return mapping().with(field(stringType(), any()));
    }

    private Rule schemas()
    {
        return anyOf(array(schema()), schema());
    }

    private MappingRule schema()
    {
        return mapping().with(field(stringType(), stringType()));
    }

    private Rule resource()
    {
        return resourceRule()
                             .then(ResourceNode.class);
    }


    private Rule method()
    {
        return mapping()
                        .with(descriptionField())
                        .with(displayNameField())
                        .with(headersField())
                        .with(field(string("queryParameters"), parameters()))
                        .with(bodyField())
                        .with(field(string("responses"), responses()))
                        .with(isField())
                        .with(protocolsField())
                        .with(securedByField())
                        .then(MethodNode.class);
    }

    private KeyValueRule bodyField()
    {
        return field(string("body"), body());
    }

    private KeyValueRule headersField()
    {
        return field(string("headers"), parameters());
    }

    private Rule responses()
    {
        return mapping().with(field(responseCodes(), response()));
    }

    private Rule responseCodes()
    {
        return range(Range.closed(100, 599));
    }

    private Rule response()
    {
        return mapping()
                        .with(descriptionField())
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
                        .with(field(string("type"), stringType()))
                        .with(field(string("defaultValue"), stringType()))
                        .with(field(string("example"), stringType()));
    }

    private KeyValueRule descriptionField()
    {
        return field(string("description"), stringType());
    }

    private KeyValueRule displayNameField()
    {
        return field(string("displayName"), stringType());
    }

    private MappingRule resourceRule()
    {
        return mapping("resource")
                                  .with(descriptionField())
                                  .with(displayNameField())
                                  .with(field(string("type"), stringType().then(ResourceTypeRefNode.class)))
                                  .with(isField())
                                  .with(securedByField())
                                  .with(field(string("uriParameters"), parameters()))
                                  .with(field(anyMethod(), method()))
                                  .with(field(resourceKey(), ref("resource")));
    }

    private KeyValueRule securedByField()
    {
        return field(string("securedBy"), array(stringType().then(SecurityRefNode.class)));
    }

    private KeyValueRule isField()
    {
        return field(string("is"), array(stringType().then(TraitRefNode.class)));
    }

    private RegexValueRule parameterKey()
    {
        return regex("<.+>");
    }

    private RegexValueRule resourceKey()
    {
        return regex("/.+");
    }

    private AnyOfRule anyMethod()
    {
        return anyOf(string("get"), string("post"), string("put"));
    }
}
