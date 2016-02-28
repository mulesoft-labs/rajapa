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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;
import org.raml.RamlBuilder;
import org.raml.impl.commons.nodes.RamlDocumentNode;
import org.raml.impl.commons.model.builder.ModelBuilder;
import org.raml.model.v10.api.Api;
import org.raml.model.v10.bodies.MimeType;
import org.raml.model.v10.methodsAndResources.Resource;
import org.raml.model.v10.methodsAndResources.Trait;
import org.raml.nodes.ErrorNode;
import org.raml.nodes.Node;

public class ModelBuilderTestCase
{

    @Test
    public void build() throws FileNotFoundException
    {
        final RamlBuilder builder = new RamlBuilder();
        File input = new File("src/test/resources/org/raml/interfaces/input.raml");
        assertTrue(input.isFile());
        final Node raml = builder.build(input);
        List<ErrorNode> errors = raml.findDescendantsWith(ErrorNode.class);
        assertThat(errors.size(), is(0));

        Api api = ModelBuilder.createRaml(Api.class, (RamlDocumentNode) raml);
        assertThat(api.title(), is("hola"));

        MimeType mimeType = api.mediaType();
        assertThat(mimeType.value(), is("application/json"));

        List<Trait> traits = api.traits();
        assertThat(traits.size(), is(2));
        assertThat(traits.get(0).displayName(), is("uno"));
        assertThat(traits.get(0).description().value(), is("method description"));

        List<Resource> resources = api.resources();
        assertThat(resources.size(), is(1));

    }
}
