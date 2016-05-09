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
package org.raml.v2.internal.impl.commons.model;

import java.util.List;

import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.impl.commons.nodes.MethodNode;
import org.raml.v2.internal.impl.commons.nodes.ResourceNode;

public class Method extends Operation
{

    private MethodNode node;

    public Method(MethodNode node)
    {
        super(node);
        this.node = node;
    }

    public String method()
    {
        return node.getName();
    }

    public List<TypeDeclaration> body()
    {
        return getList("body", TypeDeclaration.class);
    }

    public List<BodyLike> bodyV08()
    {
        return getList("body", BodyLike.class);
    }

    public Resource resource()
    {
        Node parent = node.getParent();
        if (parent != null)
        {
            if (parent.getParent() instanceof ResourceNode)
            {
                return new Resource((ResourceNode) parent.getParent());
            }
        }
        return null;
    }

}
