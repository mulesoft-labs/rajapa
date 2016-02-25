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
package org.raml.impl.v08;

import org.raml.grammar.GrammarPhase;
import org.raml.loader.ResourceLoader;
import org.raml.nodes.Node;
import org.raml.nodes.snakeyaml.RamlNodeParser;
import org.raml.phase.Phase;
import org.raml.transformer.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Raml08Builder
{

    public Node build(String stringContent, ResourceLoader resourceLoader, String resourceLocation, int maxPhaseNumber) throws IOException
    {
        Node rootNode = RamlNodeParser.parse(stringContent);
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
        final TransformationPhase first = new TransformationPhase(new IncludeResolver(resourceLoader, resourceLocation), new StringTemplateExpressionTransformer(),
                new TypesTransformer());
        // Overlays and extensions.

        // Runs Schema. Applies the Raml rules and changes each node for a more specific. Annotations Library TypeSystem
        final GrammarPhase second = new GrammarPhase(new Raml08Grammar().raml());
        // Detect invalid references. Library resourceTypes and Traits. This point the nodes are good enough for Editors.

        // Normalize resources and detects duplicated ones and more than one use of url parameters. ???

        // Applies resourceTypes and Traits Library
        final TransformationPhase third = new TransformationPhase(new ResourceTypesTraitsTransformer());

        // Schema Types example validation
        return Arrays.asList(first, second, third);

    }
}
