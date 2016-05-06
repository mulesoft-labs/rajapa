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
package org.raml.v2.parser;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.DocumentationItem;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.methods.Trait;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.v2.api.model.v10.resources.ResourceType;

public class SpecInterfacesV10TestCase
{

    @Test
    public void full() throws IOException
    {
        File input = new File("src/test/resources/org/raml/v2/interfaces/inputV10.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        assertFalse(ramlModelResult.hasErrors());
        Api api = ramlModelResult.getApiV10();

        assertApi(api);
        assertDocumentation(api.documentation());
        assertTraits(api.traits());
        assertResourceTypes(api.resourceTypes());
        assertResources(api.resources());
    }

    private void assertApi(Api api)
    {
        assertThat(api.title(), is("api title"));
        assertThat(api.version(), is("v1"));
        assertThat(api.baseUri().value(), is("http://base.uri"));
        assertThat(api.mediaType().size(), is(1));
        assertThat(api.mediaType().get(0).value(), is("application/json"));
        assertThat(api.protocols().size(), is(2));
        assertThat(api.protocols().get(0), is("HTTP"));
        assertThat(api.protocols().get(1), is("HTTPS"));
        assertThat(api.types().size(), is(1));
        assertThat(api.types().get(0).name(), is("User"));
        assertThat(api.schemas().size(), is(0));
    }

    private void assertDocumentation(List<DocumentationItem> documentation)
    {
        assertThat(documentation.size(), is(2));
        assertThat(documentation.get(0).title(), is("doc title 1"));
        assertThat(documentation.get(0).content().value(), is("single line"));
        assertThat(documentation.get(1).title(), is("doc title 2"));
        assertThat(documentation.get(1).content().value(), is("multi\nline\n"));
    }

    private void assertTraits(List<Trait> traits)
    {
        assertThat(traits.size(), is(2));
        assertThat(traits.get(0).displayName(), is("uno"));
        assertThat(traits.get(0).description().value(), is("method description"));
    }

    private void assertResourceTypes(List<ResourceType> resourceTypes)
    {
        assertThat(resourceTypes.size(), is(1));
        assertThat(resourceTypes.get(0).usage(), is("first usage"));
    }

    private void assertResources(List<Resource> resources)
    {
        assertThat(resources.size(), is(1));
        Resource top = resources.get(0);
        assertThat(top.relativeUri().value(), is("/top"));
        assertThat(top.resourcePath(), is("/top"));
        assertThat(top.description().value(), is("top description"));
        assertThat(top.displayName(), is("/top"));
        assertMethods(top.methods());

        List<Resource> children = top.resources();
        assertThat(children.size(), is(1));
        Resource child = children.get(0);
        assertThat(child.relativeUri().value(), is("/child"));
        assertThat(child.resourcePath(), is("/top/child"));
        assertThat(child.parentResource().resourcePath(), is("/top"));
    }

    private void assertMethods(List<Method> methods)
    {
        assertThat(methods.size(), is(2));
        Method get = methods.get(0);
        assertThat(get.description().value(), is("get something"));
        assertThat(get.displayName(), is("get"));
        assertThat(get.method(), is("get"));
        assertThat(get.resource().relativeUri().value(), is("/top"));

        Method post = methods.get(1);
        assertThat(post.method(), is("post"));
        assertBody(post.body());
        assertResponses(post.responses());
    }

    private void assertResponses(List<Response> responses)
    {
        assertThat(responses.size(), is(2));
        Response response200 = responses.get(0);
        assertThat(response200.code().value(), is("200"));
        assertBody(response200.body());
    }

    private void assertBody(List<TypeDeclaration> body)
    {
        assertThat(body.size(), is(2));

        TypeDeclaration appJson = body.get(0);
        assertThat(appJson.name(), is("application/json"));
        String jsonExample = appJson.example().value();
        assertThat(jsonExample, containsString("\"firstname\": \"tato\""));
        assertThat(appJson.type().size(), is(1));
        assertThat(appJson.type().get(0), is("User"));
        List<ValidationResult> validationResults = appJson.validate(jsonExample);
        assertThat(validationResults.size(), is(0));

        TypeDeclaration appXml = body.get(1);
        assertThat(appXml.name(), is("application/xml"));
        assertThat(appXml.examples().size(), is(2));
        assertThat(appXml.examples().get(0).value(), is("<first/>\n"));
        assertThat(appXml.schema(), is("<?xml version=\"1.0\" encoding=\"utf-16\"?>\n" +
                                       "<xsd:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" version=\"1.0\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
                                       "  <xsd:element name=\"first\" type=\"xsd:string\" />\n" +
                                       "  <xsd:element name=\"second\" type=\"xsd:string\" />\n" +
                                       "</xsd:schema>\n"));
    }
}
