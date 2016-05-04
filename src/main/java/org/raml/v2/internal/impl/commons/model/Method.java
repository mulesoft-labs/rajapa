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

import java.util.ArrayList;
import java.util.List;

import org.raml.v2.impl.commons.nodes.MethodNode;
import org.raml.v2.impl.commons.nodes.ResourceNode;
import org.raml.v2.nodes.KeyValueNode;
import org.raml.v2.nodes.Node;
import org.raml.v2.internal.utils.NodeSelector;

public class Method extends CommonAttributes
{

    private MethodNode node;

    public Method(MethodNode node)
    {
        this.node = node;
    }

    @Override
    protected Node getNode()
    {
        return node.getValue();
    }

    public String method()
    {
        return node.getName();
    }

    public List<TypeDeclaration> body()
    {
        ArrayList<TypeDeclaration> result = new ArrayList<>();
        Node body = NodeSelector.selectFrom("body", node.getValue());
        if (body != null)
        {
            for (Node child : body.getChildren())
            {
                result.add(new TypeDeclaration((KeyValueNode) child));
            }
        }
        return result;
    }

    public List<BodyLike> bodyV08()
    {
        ArrayList<BodyLike> result = new ArrayList<>();
        Node body = NodeSelector.selectFrom("body", node.getValue());
        if (body != null)
        {
            for (Node child : body.getChildren())
            {
                result.add(new BodyLike((KeyValueNode) child));
            }
        }
        return result;
    }

    public List<Response> responses()
    {
        ArrayList<Response> result = new ArrayList<>();
        Node responses = NodeSelector.selectFrom("responses", node.getValue());
        if (responses != null)
        {
            for (Node child : responses.getChildren())
            {
                result.add(new Response((KeyValueNode) child));
            }
        }
        return result;
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

    public List<TypeDeclaration> queryParameters()
    {
        ArrayList<TypeDeclaration> result = new ArrayList<>();
        Node queryParamsNode = NodeSelector.selectFrom("queryParameters", node.getValue());
        if (queryParamsNode != null)
        {
            for (Node child : queryParamsNode.getChildren())
            {
                result.add(new TypeDeclaration((KeyValueNode) child));
            }
        }
        return result;
    }

    public List<TypeDeclaration> headers()
    {
        ArrayList<TypeDeclaration> result = new ArrayList<>();
        Node headersNode = NodeSelector.selectFrom("headers", node.getValue());
        if (headersNode != null)
        {
            for (Node child : headersNode.getChildren())
            {
                result.add(new TypeDeclaration((KeyValueNode) child));
            }
        }
        return result;
    }

    // TODO
    // public List<Parameter> queryParametersV08();
    // public List<Parameter> headersV08();
}
