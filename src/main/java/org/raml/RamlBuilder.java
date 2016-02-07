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
package org.raml;

import org.raml.grammar.GrammarPhase;
import org.raml.grammar.Raml10Grammar;
import org.raml.loader.*;
import org.raml.nodes.Node;
import org.raml.nodes.snakeyaml.RamlNodeParser;
import org.raml.phase.Phase;
import org.raml.transformer.ResourceTypesTraitsTransformer;
import org.raml.transformer.StringTemplateExpressionTransformer;
import org.raml.transformer.TransformationPhase;
import org.raml.transformer.IncludeResolver;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * RamlBuilder create a Node representation of your raml.
 * @see Node
 */
public class RamlBuilder
{

    public static int FIRST_PHASE = 1;

    private int maxPhaseNumber;

    public RamlBuilder()
    {
        maxPhaseNumber = Integer.MAX_VALUE;
    }

    public RamlBuilder(int maxPhaseNumber)
    {
        this.maxPhaseNumber = maxPhaseNumber;
    }

    public Node build(File ramlFile) throws FileNotFoundException
    {
        final ResourceLoader resourceLoader = new CompositeResourceLoader(new UrlResourceLoader(),
                new ClassPathResourceLoader(),
                new FileResourceLoader("."),
                new FileResourceLoader(ramlFile.getParent()));
        return build(new FileReader(ramlFile), resourceLoader, "");
    }

    public Node build(String content)
    {
        final ResourceLoader resourceLoader = new CompositeResourceLoader(new UrlResourceLoader(),
                new ClassPathResourceLoader(),
                new FileResourceLoader(".")
                );
        return build(new StringReader(content), resourceLoader, "");
    }


    public Node build(Reader content, ResourceLoader resourceLoader, String resourceLocation)
    {
        Node rootNode = RamlNodeParser.parse(content);
        final List<Phase> phases = createPhases(resourceLoader, resourceLocation);
        for (int i = 0; i < phases.size(); i++)
        {
            if (i < maxPhaseNumber)
            {
                Phase phase = phases.get(i);
                rootNode = phase.apply(rootNode);
            }
        }
        return rootNode;
    }


    private List<Phase> createPhases(ResourceLoader resourceLoader, String resourceLocation)
    {
        // The first phase expands the includes.
        final TransformationPhase first = new TransformationPhase(new IncludeResolver(resourceLoader, resourceLocation), new StringTemplateExpressionTransformer());
        // Overlays and extensions.

        // Runs Schema. Applies the Raml rules and changes each node for a more specific. Annotations Library TypeSystem
        final GrammarPhase second = new GrammarPhase(new Raml10Grammar().raml());
        // Detect invalid references. Library resourceTypes and Traits. This point the nodes are good enough for Editors.

        // Normalize resources and detects duplicated ones and more than one use of url parameters. ???

        // Applies resourceTypes and Traits Library
        final TransformationPhase third = new TransformationPhase(new ResourceTypesTraitsTransformer());

        // Schema Types example validation
        return Arrays.asList(first, second, third);

    }


}
