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
/*
 *
 */
package org.raml.v2.impl.commons.phase;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

import org.raml.v2.nodes.ArrayNode;
import org.raml.v2.nodes.KeyValueNode;
import org.raml.v2.nodes.Node;
import org.raml.v2.nodes.NullNode;
import org.raml.v2.nodes.ObjectNode;
import org.raml.v2.nodes.SimpleTypeNode;
import org.raml.v2.utils.NodeSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceTypesTraitsMerger
{

    private static final Logger logger = LoggerFactory.getLogger(ResourceTypesTraitsMerger.class);

    static void merge(Node baseNode, Node copyNode)
    {
        if (copyNode instanceof NullNode)
        {
            return; // nothing to do here if copyNode is null
        }
        else if (baseNode instanceof ObjectNode && copyNode instanceof ObjectNode)
        {
            merge((ObjectNode) baseNode, (ObjectNode) copyNode);
        }
        else if (baseNode instanceof ArrayNode && copyNode instanceof ArrayNode)
        {
            merge((ArrayNode) baseNode, (ArrayNode) copyNode);
        }
        else if (baseNode instanceof NullNode)
        {
            baseNode.replaceWith(copyNode);
        }
        else
        {
            throw new RuntimeException(String.format("Merging not supported for nodes of type %s and %s",
                    baseNode.getClass().getSimpleName(), copyNode.getClass().getSimpleName()));
        }
    }

    static void merge(ArrayNode baseNode, ArrayNode copyNode)
    {
        for (Node child : copyNode.getChildren())
        {
            baseNode.addChild(child);
        }
    }

    static void merge(ObjectNode baseNode, ObjectNode copyNode)
    {
        for (Node child : copyNode.getChildren())
        {
            if (!(child instanceof KeyValueNode))
            {
                throw new RuntimeException("only expecting KeyValueNode");
            }

            String key = ((KeyValueNode) child).getKey().toString();
            if (shouldIgnoreKey((KeyValueNode) child))
            {
                logger.debug("Ignoring key '{}'", key);
                continue;
            }

            boolean optional = key.endsWith("?");
            if (optional)
            {
                key = key.substring(0, key.length() - 1);
            }
            Node node = NodeSelector.selectFrom(NodeSelector.encodePath(key), baseNode);
            if (node == null && optional)
            {
                logger.debug("Ignoring optional key {}", key);
            }
            else if (node == null)
            {
                logger.debug("Adding key '{}'", key);
                baseNode.addChild(child);
            }
            else if (((KeyValueNode) child).getValue() instanceof SimpleTypeNode)
            {
                logger.debug("Scalar key already exists '{}'", key);
            }
            else
            {
                logger.debug("Merging values '{}' and '{}'", node.getParent(), child);
                merge(node, ((KeyValueNode) child).getValue());
            }
        }
    }

    private static boolean shouldIgnoreKey(KeyValueNode child)
    {
        Set<String> ignoreSet = ImmutableSet.<String> builder().add("usage").add("type").build();
        String key = child.getKey().toString();
        return ignoreSet.contains(key);
    }
}
