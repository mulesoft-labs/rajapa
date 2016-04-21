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
package org.raml.v2.impl.commons.nodes;

import java.util.List;

import org.raml.v2.impl.v10.nodes.types.builtin.TypeNodeVisitor;
import org.raml.v2.nodes.StringNodeImpl;

public class PayloadNode extends ExampleTypeNode
{

    private String typeName;

    private boolean isArray = false;


    public PayloadNode(String typeName, String value)
    {
        this.typeName = typeName;
        this.setSource(new StringNodeImpl(value));
    }

    public PayloadNode(String typeName, String value, boolean isArray)
    {
        this(typeName, value);
        this.isArray = isArray;
    }

    public <T> T visitProperties(TypeNodeVisitor<T> visitor, List<PropertyNode> properties, boolean allowsAdditionalProperties)
    {
        return visitor.visitExample(properties, allowsAdditionalProperties);
    }

    public String getTypeName()
    {
        return typeName;
    }


    public boolean isArrayExample()
    {
        return isArray;
    }

}
