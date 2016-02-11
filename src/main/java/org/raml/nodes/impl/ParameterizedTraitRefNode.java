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
package org.raml.nodes.impl;

import java.util.Map;

import org.raml.nodes.Node;
import org.raml.nodes.ParameterizedReferenceNode;

public class ParameterizedTraitRefNode extends TraitRefNode implements ParameterizedReferenceNode
{

    public ParameterizedTraitRefNode(TraitRefNode node)
    {
        super(node);
    }

    public ParameterizedTraitRefNode(String name)
    {
        super(name);
    }

    @Override
    public Node copy()
    {
        return new ParameterizedTraitRefNode(this);
    }

    @Override
    public Map<String, String> getParameters()
    {
        return getParameters(this);
    }
}
