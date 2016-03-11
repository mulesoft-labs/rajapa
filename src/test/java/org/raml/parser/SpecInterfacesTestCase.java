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
package org.raml.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.raml.RamlBuilder;
import org.raml.impl.commons.model.builder.ModelBuilder;
import org.raml.impl.commons.nodes.RamlDocumentNode;
import org.raml.model.v10.api.Api;
import org.raml.model.v10.api.DocumentationItem;
import org.raml.model.v10.methodsAndResources.Resource;
import org.raml.model.v10.methodsAndResources.ResourceType;
import org.raml.model.v10.methodsAndResources.Trait;
import org.raml.nodes.ErrorNode;
import org.raml.nodes.Node;

public class SpecInterfacesTestCase
{

    @Test
    public void full() throws IOException
    {
        final RamlBuilder builder = new RamlBuilder();
        File input = new File("src/test/resources/org/raml/interfaces/input.raml");
        assertTrue(input.isFile());
        final Node raml = builder.build(input);
        List<ErrorNode> errors = raml.findDescendantsWith(ErrorNode.class);
        assertThat(errors.size(), is(0));

        Api api = ModelBuilder.createRaml(Api.class, (RamlDocumentNode) raml);
        assertApi(api);
        assertDocumentation(api.documentation());
        assertTraits(api.traits());
        assertResourceTypes(api.resourceTypes());
        assertResource(api.resources());
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

    private void assertResource(List<Resource> resources)
    {
        assertThat(resources.size(), is(1));
        Resource top = resources.get(0);
        assertThat(top.relativeUri().value(), is("/top"));
        assertThat(top.description().value(), is("top description"));
        assertThat(top.displayName(), nullValue()); // TODO calculated field
        assertThat(top.displayName(), nullValue());
    }
}
