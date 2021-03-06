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
package org.raml.v2.internal.impl.commons.phase;

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.grammar.rule.Rule;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.phase.TransformationPhase;
import org.raml.v2.internal.framework.phase.Transformer;
import org.raml.v2.internal.impl.commons.nodes.RamlTypedFragmentNode;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.phase.LibraryLinkingTransformation;

import static org.raml.v2.internal.utils.PhaseUtils.applyPhases;

public class RamlFragmentGrammarTransformer implements Transformer
{

    private ResourceLoader resourceLoader;

    public RamlFragmentGrammarTransformer(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public boolean matches(Node node)
    {
        return node instanceof RamlTypedFragmentNode;
    }

    @Override
    public Node transform(Node node)
    {
        final RamlTypedFragmentNode ramlTypedFragmentNode = (RamlTypedFragmentNode) node;
        final Rule rule = ramlTypedFragmentNode.getFragment().getRule(new Raml10Grammar());
        node = rule.apply(node);
        final Node apply = applyPhases(node, new TransformationPhase(new LibraryLinkingTransformation(resourceLoader)));
        if (apply instanceof RamlTypedFragmentNode)
        {
            // Hack!!!!
            ((RamlTypedFragmentNode) apply).resolveLibraryReference();
        }
        return apply;
    }
}
