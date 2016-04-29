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
package org.raml.v2.utils;

import org.raml.v2.impl.commons.nodes.RamlDocumentNode;
import org.raml.v2.impl.v10.nodes.types.builtin.ObjectTypeNode;
import org.raml.v2.impl.v10.nodes.types.builtin.TypeNode;
import org.raml.v2.nodes.ErrorNode;
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.ObjectNode;
import org.raml.v2.nodes.StringNode;
import org.raml.v2.nodes.snakeyaml.SYIncludeNode;

import javax.annotation.Nullable;

public class NodeUtils
{

    @Nullable
    public static Node getGrandParent(Node node)
    {
        return getAncestor(node, 2);
    }

    @Nullable
    public static Node getAncestor(Node node, int level)
    {
        int i = 1;
        Node parent = node.getParent();
        while (i < level && parent != null)
        {
            parent = parent.getParent();
            i++;
        }
        return parent;
    }

    private static Node traverseToRoot(Node node)
    {
        if (node == null || node instanceof RamlDocumentNode)
        {
            return node;
        }
        else if (node.getParent() == null)
        {
            return node;
        }
        else
        {
            return traverseToRoot(node.getParent());
        }
    }

    public static ObjectNode getTypesRoot(final Node node)
    {
        final Node typesRoot = traverseToRoot(node).get("types");
        return typesRoot instanceof ObjectNode ? (ObjectNode) typesRoot : null;
    }

    public static boolean isStringNode(Node node)
    {
        return node != null && node instanceof StringNode;
    }

    public static Node getType(Node node)
    {
        return node.get("type") != null ? node.get("type") : node.get("schema");
    }

    public static boolean isErrorResult(Node node)
    {
        return node != null && (node instanceof ErrorNode || node.findDescendantsWith(ErrorNode.class).size() > 0);
    }

    public static TypeNode getType(String typeName, Node node)
    {
        Node definitionContext = getNodeContext(node);
        if (definitionContext == null)
        {
            return null;
        }
        else if (typeName != null && typeName.contains("."))
        {
            return getTypeFromContext(typeName, definitionContext);
        }
        else if (definitionContext.get("types") != null)
        {
            Node type = definitionContext.get("types").get(typeName);
            return type instanceof TypeNode ? (TypeNode) type : null;
        }
        return null;
    }

    private static TypeNode getTypeFromContext(String typeName, Node definitionContext)
    {
        Node localContext = definitionContext.get("uses");
        if (localContext == null)
        {
            return null;
        }
        else
        {
            Node resolution = localContext;
            String objectName = typeName.substring(typeName.lastIndexOf(".") + 1);
            String navigationPath = typeName.substring(0, typeName.lastIndexOf("."));
            if (!navigationPath.contains("."))
            {
                return resolution != null && resolution.get(navigationPath) != null && resolution.get(navigationPath).get("types") != null &&
                       resolution.get(navigationPath).get("types").get(objectName) instanceof TypeNode ? (TypeNode) resolution.get(navigationPath).get("types").get(objectName) : null;
            }
            for (String path : navigationPath.split("."))
            {
                if (resolution == null)
                {
                    return null;
                }
                else
                {
                    resolution = resolution.get(path);
                }
            }
            return resolution != null && resolution.get("types") != null && resolution.get("types").get(objectName) instanceof TypeNode ? (TypeNode) resolution.get("types").get(objectName) : null;
        }

    }

    private static Node getNodeContext(Node node)
    {
        if (node == null || node instanceof RamlDocumentNode)
        {
            return node;
        }
        else if (node.getSource() != null && node.getSource() instanceof SYIncludeNode)
        {
            return node;
        }
        else if (node.getParent() == null)
        {
            return node;
        }
        else
        {
            return getNodeContext(node.getParent());
        }
    }
}
