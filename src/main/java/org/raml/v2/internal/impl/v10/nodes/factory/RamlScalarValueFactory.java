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
package org.raml.v2.internal.impl.v10.nodes.factory;


import javax.annotation.Nonnull;

import org.raml.v2.internal.framework.grammar.rule.NodeFactory;
import org.raml.v2.internal.framework.nodes.KeyValueNodeImpl;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.StringNodeImpl;
import org.raml.v2.internal.impl.commons.nodes.ObjectNodeImpl;

public class RamlScalarValueFactory implements NodeFactory
{

    @Override
    public Node create(@Nonnull Node currentNode, Object... args)
    {
        final ObjectNodeImpl result = new ObjectNodeImpl();
        result.addChild(new KeyValueNodeImpl(new StringNodeImpl("value"), currentNode.copy()));
        return result;
    }
}
