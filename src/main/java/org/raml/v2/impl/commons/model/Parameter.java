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
package org.raml.v2.impl.commons.model;

import org.raml.v2.nodes.KeyValueNode;
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.SimpleTypeNode;

public class Parameter extends BaseModelElement
{

    private KeyValueNode node;

    public Parameter(Node node)
    {
        if (!(node instanceof KeyValueNode))
        {
            throw new IllegalArgumentException("Invalid node type: " + node.getClass().getName());
        }
        this.node = (KeyValueNode) node;
    }

    @Override
    protected Node getNode()
    {
        return node.getValue();
    }

    public String name()
    {
        return ((SimpleTypeNode) node.getKey()).getLiteralValue();
    }

}