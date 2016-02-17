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
package org.raml.utils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.raml.nodes.ArrayNode;
import org.raml.nodes.KeyValueNode;
import org.raml.nodes.Node;
import org.raml.nodes.ObjectNode;
import org.raml.nodes.SimpleTypeNode;

public class NodeSelector
{

    public static final String PARENT_EXPR = "..";
    public static final String WILDCARD_SELECTOR = "*";

    /**
     * Resolves a path in the specified node. The path uses a very simple expression system like xpath where each element is separated by /.
     * <p><b>"name"</b> -> return the value of field with key that matches the specified name. <br/>
     * <b>..</b>        -> returns the parent <br/>
     * <b>*</b>         -> wild card selector <br/>
     * <b>number</b>    -> returns the element at that index zero base index. The number should be bigger than zero</p><br/>
     *
     * @param path The path example schemas/foo
     * @param from The source where to query
     * @return The result null if no match
     */
    @Nullable
    public static Node selectFrom(String path, Node from)
    {
        final String[] tokens = path.split("/");
        return selectFrom(Arrays.asList(tokens), from);
    }

    public static int selectIntValue(String path, Node from)
    {
        return selectType(path, from, BigInteger.ZERO).intValue();
    }

    public static String selectStringValue(String path, Node from)
    {
        return selectType(path, from, StringUtils.EMPTY);
    }

    private static <T> T selectType(String path, Node from, T defaultValue)
    {
        SimpleTypeNode<T> selectedNode = (SimpleTypeNode<T>) selectFrom(path, from);
        if (selectedNode != null)
        {
            return selectedNode.getValue();
        }
        return defaultValue;
    }

    @Nullable
    private static Node selectFrom(List<String> pathTokens, Node from)
    {
        Node currentNode = from;
        for (int i = 0; i < pathTokens.size() && currentNode != null; i++)
        {
            String token = pathTokens.get(i);
            if (token.equals(WILDCARD_SELECTOR))
            {
                if (currentNode instanceof ArrayNode)
                {
                    final List<Node> children = currentNode.getChildren();
                    final List<String> remainingTokens = pathTokens.subList(i + 1, pathTokens.size());
                    for (Node child : children)
                    {
                        final Node resolve = selectFrom(remainingTokens, child);
                        if (resolve != null)
                        {
                            currentNode = resolve;
                            break;
                        }
                    }
                    break;
                }
                // else we ignore the *
            }
            else if (token.equals(PARENT_EXPR))
            {
                currentNode = currentNode.getParent();
            }
            else if (currentNode instanceof ObjectNode)
            {
                currentNode = findValueWithName(currentNode, token);
            }
            else if (currentNode instanceof ArrayNode)
            {
                final int index = Integer.parseInt(token);
                currentNode = findElementAtIndex(currentNode, index);
            }
            else
            {
                currentNode = null;
            }
        }

        return currentNode;
    }

    @Nullable
    private static Node findElementAtIndex(final Node currentNode, int index)
    {
        Node result = null;
        final List<Node> children = currentNode.getChildren();
        if (children.size() > index)
        {
            result = children.get(index);
        }
        return result;
    }

    @Nullable
    private static Node findValueWithName(final Node currentNode, String token)
    {
        Node result = null;
        final List<Node> children = currentNode.getChildren();
        for (Node child : children)
        {
            if (child instanceof KeyValueNode)
            {
                final Node key = ((KeyValueNode) child).getKey();
                if (key instanceof SimpleTypeNode)
                {
                    if (token.equals(String.valueOf(((SimpleTypeNode) key).getValue())))
                    {
                        result = ((KeyValueNode) child).getValue();
                        break;
                    }
                }
            }
        }
        return result;
    }

}
