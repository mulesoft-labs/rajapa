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
package org.raml.v2.grammar.rule;

import org.raml.v2.internal.impl.v10.nodes.types.builtin.UnionTypeNode;
import org.raml.v2.nodes.KeyValueNodeImpl;
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.StringNodeImpl;

public class TypesFactory implements NodeFactory
{
    @Override
    public Node create(Object... args)
    {
        String type = (String) args[0];
        Node newNode = org.raml.v2.impl.v10.nodes.types.factories.TypeNodeFactory.createNodeFromType(type);
        if (newNode instanceof UnionTypeNode)
        {
            newNode.addChild(new KeyValueNodeImpl(new StringNodeImpl("type"), new StringNodeImpl(type)));
        }
        return newNode;
    }
}
