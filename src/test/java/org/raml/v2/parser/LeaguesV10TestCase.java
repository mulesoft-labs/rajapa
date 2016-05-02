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
import org.raml.v2.model.common.ValidationResult;
import org.raml.v2.model.v10.api.Api;
import org.raml.v2.model.v10.api.DocumentationItem;
import org.raml.v2.model.v10.bodies.Response;
import org.raml.v2.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.model.v10.methods.Method;
import org.raml.v2.model.v10.methods.Trait;
import org.raml.v2.model.v10.resources.Resource;
import org.raml.v2.model.v10.resources.ResourceType;
import org.raml.v2.nodes.ErrorNode;
import org.raml.v2.nodes.Node;

public class LeaguesV10TestCase
{

    @Test
    public void full() throws IOException
    {
        final RamlBuilder builder = new RamlBuilder();
        File input = new File("src/test/resources/org/raml/v2/interfaces/leaguesV10.raml");
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
        assertThat(api.title(), is("Leagues API"));
        assertThat(api.version(), is("v1"));
        assertThat(api.baseUri().value(), is("http://localhost/api"));
        assertThat(api.types().size(), is(2));
        assertThat(api.types().get(0).name(), is("league-json"));
        assertThat(api.types().get(1).name(), is("league-xml"));
        assertThat(api.schemas().size(), is(0));
    }

    private void assertDocumentation(List<DocumentationItem> documentation)
    {
        assertThat(documentation.size(), is(2));
        assertThat(documentation.get(0).title(), is("Leagues"));
        assertThat(documentation.get(0).content().value(), containsString("football leagues"));
        assertThat(documentation.get(1).title(), is("Contact"));
        assertThat(documentation.get(1).content().value(), containsString("please contact"));
    }

    private void assertTraits(List<Trait> traits)
    {
        assertThat(traits.size(), is(1));
        assertThat(traits.get(0).name(), is("taxed"));
        assertThat(traits.get(0).displayName(), is("taxed trait"));
    }

    private void assertResourceTypes(List<ResourceType> resourceTypes)
    {
        assertThat(resourceTypes.size(), is(1));
        assertThat(resourceTypes.get(0).name(), is("collection"));
        assertThat(resourceTypes.get(0).displayName(), is("collection type"));
    }

    private void assertResources(List<Resource> resources)
    {
        assertThat(resources.size(), is(1));
        Resource leagues = resources.get(0);
        assertThat(leagues.relativeUri().value(), is("/leagues"));
        assertThat(leagues.resourcePath(), is("/leagues"));
        assertThat(leagues.description().value(), is("World Soccer Leagues"));
        assertThat(leagues.displayName(), is("Leagues"));
        assertMethods(leagues.methods());

        List<Resource> children = leagues.resources();
        assertThat(children.size(), is(1));

        Resource leagueId = children.get(0);
        assertThat(leagueId.relativeUri().value(), is("/{leagueId}"));
        assertThat(leagueId.resourcePath(), is("/leagues/{leagueId}"));
        assertUriParameters(leagueId.uriParameters());

        Resource teams = leagueId.resources().get(1);
        assertThat(teams.relativeUri().value(), is("/teams"));
        Method getTeams = teams.methods().get(0);
        assertQueryParameters(getTeams.queryParameters());
        assertHeaders(getTeams.headers());
    }

    private void assertHeaders(List<TypeDeclaration> headers)
    {
        assertThat(headers.size(), is(1));
        TypeDeclaration preferred = headers.get(0);
        assertThat(preferred.displayName(), is("Preferred"));
        assertThat(preferred.required(), is(false));
        assertThat(preferred.defaultValue(), is("BCN"));
    }

    private void assertQueryParameters(List<TypeDeclaration> queryParameters)
    {
        assertThat(queryParameters.size(), is(2));
        TypeDeclaration offset = queryParameters.get(0);
        assertThat(offset.displayName(), is("Offset"));
        assertThat(offset.required(), is(false));
        assertThat(offset.defaultValue(), is("0"));
        List<ValidationResult> results = offset.validate("3");
        assertThat(results.size(), is(0));
    }

    private void assertUriParameters(List<TypeDeclaration> uriParameters)
    {
        assertThat(uriParameters.size(), is(1));
        TypeDeclaration id = uriParameters.get(0);
        List<ValidationResult> results = id.validate("acceptable");
        assertThat(results.size(), is(0));
        results = id.validate("longer than twenty characters");
        assertThat(results.size(), is(1));
    }

    private void assertMethods(List<Method> methods)
    {
        assertThat(methods.size(), is(2));
        Method get = methods.get(0);
        assertThat(get.displayName(), is("get"));
        assertThat(get.method(), is("get"));
        assertThat(get.resource().relativeUri().value(), is("/leagues"));

        Method post = methods.get(1);
        assertThat(post.method(), is("post"));
        assertBody(post.body());
        assertResponses(post.responses());
    }

    private void assertBody(List<TypeDeclaration> body)
    {
        assertThat(body.size(), is(2));

        TypeDeclaration appJson = body.get(0);
        assertThat(appJson.name(), is("application/json"));
        String jsonExample = appJson.example().value();
        assertThat(jsonExample, containsString("\"name\": \"liga criolla\""));
        assertThat(appJson.type().size(), is(1));
        assertThat(appJson.type().get(0), containsString("draft-03"));
        List<ValidationResult> validationResults = appJson.validate(jsonExample);
        assertThat(validationResults.size(), is(0));
        validationResults = appJson.validate("{\"liga\": \"Criolla\"}");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), containsString("missing required properties"));

        TypeDeclaration appXml = body.get(1);
        assertThat(appXml.name(), is("text/xml"));

        assertThat(appXml.schema(), is("<?xml version=\"1.0\" encoding=\"UTF-16\" ?>\n" +
                                       "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" +
                                       " elementFormDefault=\"qualified\" xmlns=\"http://mulesoft.com/schemas/soccer\"\n" +
                                       " targetNamespace=\"http://mulesoft.com/schemas/soccer\">\n" +
                                       "<xs:element name=\"league\">\n" +
                                       "  <xs:complexType>\n" +
                                       "    <xs:sequence>\n" +
                                       "      <xs:element name=\"name\" type=\"xs:string\"/>\n" +
                                       "      <xs:element name=\"description\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
                                       "    </xs:sequence>\n" +
                                       "  </xs:complexType>\n" +
                                       "</xs:element>\n" +
                                       "</xs:schema>\n"));

        validationResults = appXml.validate("<leaguee xmlns=\"http://mulesoft.com/schemas/soccer\"><name>MLS</name></leaguee>");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), containsString("Cannot find the declaration of element 'leaguee'"));
    }

    private void assertResponses(List<Response> responses)
    {
        assertThat(responses.size(), is(1));
        Response response201 = responses.get(0);
        assertThat(response201.code().value(), is("201"));
    }
}
