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
package org.raml.v2.impl.v10.nodes.types.builtin;

import javax.annotation.Nonnull;

import org.raml.v2.nodes.Node;

public class FloatTypeNode extends NumericTypeNode
{

    public FloatTypeNode()
    {
    }

    public FloatTypeNode(FloatTypeNode node)
    {
        super(node);
    }

    @Override
    public <T> T visit(TypeNodeVisitor<T> visitor)
    {
        return visitor.visitFloat(this);
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new FloatTypeNode(this);
    }
}
