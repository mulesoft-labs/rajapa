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
package org.raml.types.factories;

import org.raml.grammar.rule.NodeFactory;
import org.raml.nodes.Node;
import org.raml.nodes.StringNode;
import org.raml.types.builtin.BooleanTypeNode;
import org.raml.types.builtin.FileTypeNode;
import org.raml.types.builtin.NumericTypeNode;
import org.raml.types.builtin.ObjectTypeNode;
import org.raml.types.builtin.StringTypeNode;
import org.raml.utils.NodeSelector;

public class TypeNodeFactory implements NodeFactory
{

    @Override
    public Node create(Object... args)
    {
        StringNode type = (StringNode) NodeSelector.selectFrom("type", (Node) args[0]);
        String value = type.getValue();
        switch (value)
        {
        case "object":
            return new ObjectTypeNode();
        case "string":
            return new StringTypeNode();
        case "number":
        case "integer":
            return new NumericTypeNode();
        case "boolean":
            return new BooleanTypeNode();
        case "file":
            return new FileTypeNode();
        default:
            return new StringTypeNode();
        }
    }
}
