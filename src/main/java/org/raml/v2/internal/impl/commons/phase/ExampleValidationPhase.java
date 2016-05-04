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
package org.raml.v2.impl.commons.phase;

import java.util.List;

import org.raml.v2.impl.commons.nodes.ExampleTypeNode;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.nodes.Node;
import org.raml.v2.phase.Phase;
import org.raml.v2.utils.NodeValidator;

public class ExampleValidationPhase implements Phase
{

    private ResourceLoader resourceLoader;


    public ExampleValidationPhase(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public Node apply(Node tree)
    {
        final List<ExampleTypeNode> examples = tree.findDescendantsWith(ExampleTypeNode.class);
        NodeValidator validator = new NodeValidator(this.resourceLoader);
        for (ExampleTypeNode example : examples)
        {
            validator.validateExample(example);
        }
        return tree;
    }

}
