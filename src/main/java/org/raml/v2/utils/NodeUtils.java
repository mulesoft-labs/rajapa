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
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.ObjectNode;
import org.raml.v2.nodes.StringNode;

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

    public static Node traverseToRoot(Node node)
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

    public static boolean isSchemaType(final Node node)
    {
        return isJsonSchemaNode(node) || isXmlSchemaNode(node);
    }

    public static boolean isStringNode(Node node)
    {
        return node != null && node instanceof StringNode;
    }

    public static boolean isJsonSchemaNode(Node node)
    {
        return isStringNode(node) && nodeStartsWith((StringNode) node, "{");
    }

    public static boolean isXmlSchemaNode(Node node)
    {
        return isStringNode(node) && nodeStartsWith((StringNode) node, "<");
    }

    private static boolean nodeStartsWith(StringNode node, String prefix)
    {
        return node.getValue().startsWith(prefix);
    }

}
