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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.raml.v2.RamlBuilder;
import org.raml.v2.impl.commons.model.builder.ModelBuilder;
import org.raml.v2.impl.commons.nodes.RamlDocumentNode;
import org.raml.v2.api.model.v08.api.Api;
import org.raml.v2.api.model.v08.api.DocumentationItem;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v08.bodies.Response;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.methods.Trait;
import org.raml.v2.api.model.v08.resources.Resource;
import org.raml.v2.api.model.v08.resources.ResourceType;
import org.raml.v2.nodes.ErrorNode;
import org.raml.v2.nodes.Node;

public class SpecInterfacesV08TestCase
{

    @Test
    public void full() throws IOException
    {
        final RamlBuilder builder = new RamlBuilder();
        File input = new File("src/test/resources/org/raml/v2/interfaces/inputV08.raml");
        assertTrue(input.isFile());
        final Node raml = builder.build(input);
        List<ErrorNode> errors = raml.findDescendantsWith(ErrorNode.class);
        assertThat(errors.size(), is(0));

        Api api = ModelBuilder.createRaml(Api.class, (RamlDocumentNode) raml);
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
        assertThat(api.mediaType().value(), is("application/json"));
        assertThat(api.protocols().size(), is(2));
        assertThat(api.protocols().get(0), is("HTTP"));
        assertThat(api.protocols().get(1), is("HTTPS"));

        assertThat(api.schemas().size(), is(2));
        assertThat(api.schemas().get(0).key(), is("UserJson"));
        assertThat(api.schemas().get(0).value().value(), containsString("\"firstname\":  { \"type\": \"string\" }"));
        assertThat(api.schemas().get(1).key(), is("UserXml"));
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
        assertThat(traits.get(0).name(), is("one"));
        assertThat(traits.get(0).description().value(), is("method description from trait one"));
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

    private void assertBody(List<BodyLike> body)
    {
        assertThat(body.size(), is(3));

        BodyLike appJson = body.get(0);
        assertThat(appJson.name(), is("application/json"));
        assertThat(appJson.example().value(), containsString("\"firstname\": \"tato\""));
        assertThat(appJson.schema().value(), is("UserJson"));

        BodyLike multipart = body.get(2);
        assertThat(multipart.formParameters().size(), is(2));
        assertThat(multipart.formParameters().get(0).name(), is("description"));

    }
}
